package ru.petrenko_alex.image_steganography;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class ImagesComparisonTools {

    /* Number of bits used to store the mantissa in the 64-bit IEEE-754 standard */
    public static final int DOUBLE_MANTISSA_BITS = 52;

    /* Number of bits used to store the mantissa in the 32-bit IEEE-754 standard */
    public static final int FLOAT_MANTISSA_BITS = 23;

    public static final byte POSITIVE = 0;
    public static final byte NEGATIVE = 1;

    public static final int BLACK = 0;
    public static final int WHITE = 1;

    public static final int INSIGNIFICANT = 0;
    public static final int SIGNIFICANT = 1;


    /**
     * Calculate the PSNR(peak signal-to-noise ratio) of two images.<br> Images must have the same dimensions and types.
     *
     * @param im1 first image
     * @param im2 second image
     *
     * @return PSNR(peak signal-to-noise ratio)
     */
    public static double calculatePSNR( BufferedImage im1, BufferedImage im2 ) {

        assert ( im1.getType() == im2.getType()
                && im1.getHeight() == im2.getHeight()
                && im1.getWidth() == im2.getWidth() );

        double MSE = calculateMSE( im1, im2 );

        int maxVal = 255;
        double x = Math.pow( maxVal, 2 ) / MSE;
        double PSNR = 10.0 * logbase10( x );

        return PSNR;
    }

    /**
     * Calculate the MSE(mean squared error) of two images.<br> Images must have the same dimensions and types.
     *
     * @param im1 first image
     * @param im2 second image
     *
     * @return MSE(mean squared error)
     */
    public static double calculateMSE( BufferedImage im1, BufferedImage im2 ) {

        assert ( im1.getType() == im2.getType()
                && im1.getHeight() == im2.getHeight()
                && im1.getWidth() == im2.getWidth() );

        double MSE = 0;

        int width = im1.getWidth();
        int height = im1.getHeight();

        Raster r1 = im1.getRaster();
        Raster r2 = im2.getRaster();

        for( int j = 0; j < height; j++ ) {

            for( int i = 0; i < width; i++ ) {

                MSE += Math.pow( r1.getSample( i, j, 0 ) - r2.getSample( i, j, 0 ), 2 );
            }
        }

        MSE /= ( double ) ( width * height );

        return MSE;
    }

    /**
     * Calculate the RMSE(root mean squared error).<br> Images must have the same dimensions and types.
     *
     * @param im1 first image
     * @param im2 second image
     *
     * @return RMSE(root mean squared error)
     */
    public static double calculateRMSE( BufferedImage im1, BufferedImage im2 ) {

        double MSE = calculateMSE( im1, im2 );
        return Math.sqrt( MSE );

    }

    /**
     * Calculate the base-10 logarithm of the number.
     *
     * @param x number to calculate logarithm of
     *
     * @return base-10 logarithm of the number
     */
    private static double logbase10( double x ) {

        return Math.log( x ) / Math.log( 10 );
    }

}
