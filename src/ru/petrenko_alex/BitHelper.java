package ru.petrenko_alex;


import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class BitHelper {

    /**
     * Get a bit from the byte on the pos.
     *
     * @param val byte to get bit of
     * @param pos pos of a bit in the byte
     *
     * @return bit from the byte on the pos
     */
    public static byte getBit( byte val, int pos ) {

        return ( byte ) ( ( val & ( 1 << pos ) ) >> pos );
    }

    /**
     * Get a byte array representation of the string in US_ASCII.
     *
     * @param val string to get bytes of
     *
     * @return byte array representation of the string
     */
    public static byte[] getBytes( String val ) {

        return val.getBytes( StandardCharsets.US_ASCII );
    }

    /**
     * Get a string representation of the US_ASCII byte array.
     *
     * @param array US_ASCII byte array
     *
     * @return string representation of the byte array
     */
    public static String getString( byte[] array ) {

        return new String( array, StandardCharsets.US_ASCII );
    }

    /**
     * Get a byte array representation of the integer number.
     *
     * @param integer integer number
     *
     * @return byte array representation of the integer number
     *
     * @throws IOException thrown if errors while converting are occurred
     */
    public static byte[] intToByteArray( final int integer ) throws IOException {

        return BigInteger.valueOf( integer ).toByteArray();
    }


    /**
     * Get a length of the byte array as a byte array.
     *
     * @param array byte array to get length of
     *
     * @return length of the byte array as a byte array
     */
    public static byte[] getByteArrayLength( byte[] array ) {

        byte[] lenBytes = new byte[ 4 ];

        lenBytes[ 0 ] = ( byte ) ( ( array.length >>> 24 ) & 0xFF );
        lenBytes[ 1 ] = ( byte ) ( ( array.length >>> 16 ) & 0xFF );
        lenBytes[ 2 ] = ( byte ) ( ( array.length >>> 8 ) & 0xFF );
        lenBytes[ 3 ] = ( byte ) ( array.length & 0xFF );

        return lenBytes;
    }
}
