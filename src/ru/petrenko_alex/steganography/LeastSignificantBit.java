package ru.petrenko_alex.steganography;


import ru.petrenko_alex.BitHelper;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LeastSignificantBit extends SteganographyAlgorithm {

    /**
     * Encode text into a buffered image using Least Significant Bit algorithm.<br>
     *
     * @param text   text to encode
     * @param bitmap buffered image
     *
     * @return encoded buffered image
     */
    @Override
    public BufferedImage encode( String text, BufferedImage bitmap ) throws Exception {

        int i = 0;
        int j = 8;

        /* Creating a result image */
        BufferedImage result = makeImageCopy( bitmap );

        /* Preparing the text to encode */
        byte[] encode = prepareTextToEncode( text );


        /* Checking if the image is big enough for the text */
        if( ( result.getHeight() * result.getWidth() ) < ( encode.length * 8 ) ) {

            throw new Exception( "Изображение слишком мало для заданного текста." );
        }

        for( int y = 0; y < result.getHeight(); y++ ) {

            for( int x = 0; x < result.getWidth(); x++ ) {

                Color pixel = new Color( result.getRGB( x, y ) );

                int R = ( j == 8 ) ? pixel.getRed() & 254 : pixel.getRed() & 254 | +BitHelper.getBit( encode[ i ], --j );
                int G = pixel.getGreen() & 254 | +BitHelper.getBit( encode[ i ], --j );
                int B = pixel.getBlue() & 254 | +BitHelper.getBit( encode[ i ], --j );

                Color newPixel = new Color( R, G, B );
                result.setRGB( x, y, newPixel.getRGB() );

                if( j == 0 ) {

                    j = 8;
                    i++;
                }

                if( i == encode.length ) {

                    break;
                }
            }

            if( i == encode.length ) {
                break;
            }
        }
        return result;
    }

    /**
     * Decode text from a buffered image using Least Significant Bit algorithm.
     *
     * @param bitmap encoded buffered image
     *
     * @return decoded text
     */
    @Override
    public String decode( BufferedImage bitmap ) {

        int textLength = 0;
        boolean containsMessage = false;
        StringBuilder information = new StringBuilder();

        StringBuilder tmp = new StringBuilder();

        for( int y = 0; y < bitmap.getHeight(); y++ ) {

            for( int x = 0; x < bitmap.getWidth(); x++ ) {

                Color pixel = new Color( bitmap.getRGB( x, y ) );

                tmp.append( pixel.getRed() % 2 );
                tmp.append( pixel.getGreen() % 2 );
                tmp.append( pixel.getBlue() % 2 );

                if( tmp.length() == 9 ) {

                    try {

                        Integer tmpInt = Integer.parseInt( tmp.toString(), 2 );
                        information.append( BitHelper.getString( BitHelper.intToByteArray( tmpInt ) ) );

                        if( !containsMessage && tmpInt == 32 ) {

                            containsMessage = true;
                            information.deleteCharAt( information.length() - 1 );
                            textLength = Integer.parseInt( information.toString() );
                            information.setLength( 0 );
                        }

                        if( !containsMessage && tmpInt == 0 ) {

                            throw new Exception();
                        }

                        tmp.setLength( 0 );

                    } catch( Exception e ) {

                        containsMessage = true;
                        information.setLength( 0 );

                        information.append( "Ошибка в процессе декодирования. Убедитесь, что изображение содержит текст." );
                    }
                }

                if( containsMessage && information.length() >= textLength ) {

                    break;
                }
            }

            if( containsMessage && information.length() >= textLength ) {

                break;
            }
        }
        return information.toString();
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

        String textToEncode = String.format( "%1$s %2$s", text.length(), text );
        byte[] encode = BitHelper.getBytes( textToEncode );

        return encode;
    }
}
