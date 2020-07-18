package ru.petrenko_alex.steganography;


import ru.petrenko_alex.BitHelper;

import java.awt.*;
import java.awt.image.BufferedImage;

public class KutterJordanBossen extends SteganographyAlgorithm {

    /*
     * Every bit is encoded for NUM_OF_REPEATS times
     * in order to improve noise stability.
     */
    private final static int NUM_OF_REPEATS = 15;

    /* The current position of an image point which has information being written into it */
    private int xPos, yPos;


    /**
     * Encode text into a buffered image using Kutter-Jordan-Bossen algorithm.
     *
     * @param text   text to encode
     * @param bitmap buffered image
     *
     * @return encoded buffered image
     *
     * @throws Exception thrown if image is small for the text
     */
    @Override
    public BufferedImage encode( String text, BufferedImage bitmap ) throws Exception {

        byte[] message = prepareTextToEncode( text );

        xPos = yPos = 3;

        /* Creating a result image */
        BufferedImage result = makeImageCopy( bitmap );


        /* Checking if the image is big enough for the text */
        if( ( message.length * 8 * NUM_OF_REPEATS )
                > ( ( bitmap.getWidth() / 4 - 1 ) * ( bitmap.getHeight() / 4 - 1 ) ) ) {

            throw new Exception( "Изображение слишком мало для заданного текста." );
        }

        /* Encoding */
        for( int i = 0; i < message.length; i++ ) {

            writeByte( result, message[ i ] );
        }

        return result;
    }

    /**
     * Decode text from a buffered image using Kutter-Jordan-Bossen algorithm.
     *
     * @param bitmap encoded buffered image
     *
     * @return decoded text
     *
     * @throws Exception thrown if data is damaged
     */
    @Override
    public String decode( BufferedImage bitmap ) throws Exception {

        xPos = yPos = 3;

        /* Getting a length of encoded text */
        byte lenByte0 = readByte( bitmap );
        byte lenByte1 = readByte( bitmap );
        byte lenByte2 = readByte( bitmap );
        byte lenByte3 = readByte( bitmap );

        /* Converting lenByte into a decimal number */
        int msgLen = ( ( lenByte0 & 0xff ) << 24 ) |
                ( ( lenByte1 & 0xff ) << 16 ) |
                ( ( lenByte2 & 0xff ) << 8 ) |
                ( lenByte3 & 0xff );

        if( ( msgLen <= 0 ) || ( ( msgLen * 8 * NUM_OF_REPEATS )
                > ( bitmap.getWidth() / 4 - 1 ) * ( bitmap.getHeight() / 4 - 1 ) ) ) {

            throw new Exception( "Ошибка в процессе декодирования. Убедитесь, что изображение содержит текст." );
        }

        /* Decoding */
        byte[] msgBytes = new byte[ msgLen ];
        for( int i1 = 0; i1 < msgLen; i1++ ) {

            msgBytes[ i1 ] = readByte( bitmap );
        }

        /* Converting byte array to string */
        String msg = new String( msgBytes );
        return msg;
    }

    /**
     * Prepare text to encode.<br> Make a byte array containing bytes of source text and a length of source text.
     *
     * @param text source text
     *
     * @return byte array of a source text and a length of it
     */
    @Override
    byte[] prepareTextToEncode( String text ) {

         /* Converting text to byte array */
        byte[] msgBytes = text.getBytes();

        /* Getting length of a byte array */
        byte[] lenBytes = BitHelper.getByteArrayLength( msgBytes );

        /* Preparing information to insert */
        byte[] message = new byte[ msgBytes.length + 4 ];
        System.arraycopy( lenBytes, 0, message, 0, lenBytes.length );
        System.arraycopy( msgBytes, 0, message, lenBytes.length, msgBytes.length );

        return message;
    }

    /**
     * Write a single byte into buffered image.
     *
     * @param img     buffered image
     * @param byteVal byte to write
     */
    private void writeByte( BufferedImage img, byte byteVal ) {

        /* Loop through 8 bits of byteVal byte */
        for( int j = 7; j >= 0; j-- ) {

            int bitVal = ( byteVal >>> j ) & 1;
            writeBit( img, bitVal );
        }
    }

    /**
     * Read a single byte from buffered image.
     *
     * @param img buffered image
     *
     * @return read byte
     */
    private byte readByte( BufferedImage img ) {

        byte byteVal = 0;

        /* Getting a byte from 8 bits */
        for( int i = 0; i < 8; i++ ) {

            /* Left shift founded bits and add a bit to the right */
            byteVal = ( byte ) ( ( byteVal << 1 ) | ( readBit( img ) & 1 ) );
        }

        return byteVal;
    }

    /**
     * Write a single bit into buffered image.
     *
     * @param img buffered image
     * @param bit bit to write
     */
    private void writeBit( BufferedImage img, int bit ) {

        /* Writing a bit for NUM_OF_REPEATS times */
        for( int i1 = 0; i1 < NUM_OF_REPEATS; i1++ ) {

            if( xPos + 4 > img.getWidth() ) {

                xPos = 3;
                yPos += 4;
            }

            writeIntoPixel( img, xPos, yPos, bit, 0.25 );
            xPos += 4;
        }
    }

    /**
     * Read a single bit from buffered image.
     *
     * @param img buffered image
     *
     * @return read bit
     */
    private int readBit( BufferedImage img ) {


        /* Probabilistic estimate of an information bit */
        float bitEstimate = 0;

        for( int i1 = 0; i1 < NUM_OF_REPEATS; i1++ ) {

            if( xPos + 4 > img.getWidth() ) {

                xPos = 3;
                yPos += 4;
            }

            bitEstimate += readFromPixel( img, xPos, yPos );
            xPos += 4;
        }
        bitEstimate /= NUM_OF_REPEATS;


        /* if more than half of NUM_OF_REPEATS read bits were 1s, so consider 1 was encoded */
        if( bitEstimate > 0.5 ) {

            return 1;

        } else {

            return 0;
        }
    }

    /**
     * Write a single bit into a current image point.
     *
     * @param image  buffered image
     * @param x      image point coordinate x
     * @param y      image point coordinate y
     * @param bit    bit to write
     * @param energy coefficient - energy of a bit
     *
     * @return modified blue component of a pixel
     */
    private static int writeIntoPixel( BufferedImage image, int x, int y, int bit, double energy ) {

        Color pixel = new Color( image.getRGB( x, y ) );
        int red = pixel.getRed();
        int green = pixel.getGreen();
        int blue = pixel.getBlue();

        int pixelBrightness = ( int ) ( 0.29890 * red + 0.58662 * green + 0.11448 * blue );

        /* Variable blue component */
        int modifiedBlueComponent;
        if( bit > 0 ) {

            modifiedBlueComponent = ( int ) ( blue + energy * pixelBrightness );

        } else {

            modifiedBlueComponent = ( int ) ( blue - energy * pixelBrightness );
        }

        if( modifiedBlueComponent > 255 ) {

            modifiedBlueComponent = 255;
        }

        if( modifiedBlueComponent < 0 ) {

            modifiedBlueComponent = 0;
        }

        Color pixelModified = new Color( red, green, modifiedBlueComponent );
        image.setRGB( x, y, pixelModified.getRGB() );

        return modifiedBlueComponent;
    }

    /**
     * Read a single bit from a current image point.
     *
     * @param image buffered image
     * @param x     image point coordinate x
     * @param y     image point coordinate y
     *
     * @return read bit
     */
    private int readFromPixel( BufferedImage image, int x, int y ) {

        /* Summing up all the blue components of surrounding points */
        int estimate = 0;

        for( int i1 = 1; i1 <= 3; i1++ ) {

            Color pixel = new Color( image.getRGB( x + i1, y ) );
            estimate += pixel.getBlue();
        }

        for( int i1 = 1; i1 <= 3; i1++ ) {

            Color pixel = new Color( image.getRGB( x - i1, y ) );
            estimate += pixel.getBlue();
        }

        for( int i1 = 1; i1 <= 3; i1++ ) {

            Color pixel = new Color( image.getRGB( x, y + i1 ) );
            estimate += pixel.getBlue();
        }

        for( int i1 = 1; i1 <= 3; i1++ ) {

            Color pixel = new Color( image.getRGB( x, y - i1 ) );
            estimate += pixel.getBlue();
        }

        /* Average */
        estimate /= 12;

        Color pixel = new Color( image.getRGB( x, y ) );
        int blue = pixel.getBlue();

        if( blue > estimate ) {

            return 1;

        } else {

            return 0;
        }
    }
}
