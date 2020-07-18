package ru.petrenko_alex.image_steganography;


import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class SteganographyAlgorithm {

    /**
     * Encode the text into the buffered image.
     *
     * @param text   text to encode
     * @param bitmap buffered image
     *
     * @return encoded buffered image
     */
    public abstract BufferedImage encode( String text, BufferedImage bitmap ) throws Exception;

    /**
     * Decode the text from the buffered image.
     *
     * @param bitmap encoded buffered image
     *
     * @return decoded text
     */
    public abstract String decode( BufferedImage bitmap ) throws Exception;

    /**
     * Prepare the text to encode.<br> Make a byte array containing bytes of the source text and a length of the source
     * text.
     *
     * @param text source text
     *
     * @return byte array of a source text and a length of it
     */
    abstract byte[] prepareTextToEncode( String text );

    /**
     * Make a copy of the image.
     *
     * @param imageToCopy image to make copy of
     *
     * @return copy of the image
     */
    BufferedImage makeImageCopy( BufferedImage imageToCopy ) {

        BufferedImage result = new BufferedImage( imageToCopy.getWidth(), imageToCopy.getHeight(), imageToCopy.getType() );
        Graphics g = result.getGraphics();
        g.drawImage( imageToCopy, 0, 0, null );
        return result;
    }
}
