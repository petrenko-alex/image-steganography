package ru.petrenko_alex.image_steganography;


import java.lang.reflect.Constructor;

public class SteganographyAlgorithmFactory {

    /**
     * Get object of the class with the name className.
     *
     * @param className name of the class
     * @return object of the class
     * @throws Exception thrown if got errors while searching for a class and making a constructor
     */
    public static SteganographyAlgorithm createSteganographyAlgorithm( String className ) throws Exception {

        /* Preparing the class name */
        String tmpClassName = PATH_TO_ALGORITHMS_PACKAGE;
        tmpClassName = tmpClassName.concat( className );
        Object algoObject = null;

        /* Getting an object of the class */
        Class c = Class.forName( tmpClassName );
        Constructor< ? > constr = c.getConstructor();
        algoObject = constr.newInstance();

        return ( SteganographyAlgorithm ) algoObject;
    }

    /* The path to image_steganography algorithms classes */
    private final static String PATH_TO_ALGORITHMS_PACKAGE = "ru.petrenko_alex.image_steganography.";
}
