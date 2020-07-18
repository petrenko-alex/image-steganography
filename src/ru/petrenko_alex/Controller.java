package ru.petrenko_alex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.petrenko_alex.image_steganography.ImagesComparisonTools;
import ru.petrenko_alex.image_steganography.SteganographyAlgorithm;
import ru.petrenko_alex.image_steganography.SteganographyAlgorithmFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private ImageView sourceImage = new ImageView();

    @FXML
    private ImageView outputImage = new ImageView();

    @FXML
    private TextField encodingText;

    @FXML
    private Button encodeBtn;

    @FXML
    private Button openImageBtn;

    @FXML
    private Button saveImageBtn;

    @FXML
    private Button readImageBtn;

    @FXML
    private ComboBox< String > algorithmList;

    private Stage _stage;
    private Image _sourceImage;
    private String _sourceImageFileName;
    private String _sourceImagePath;
    private WritableImage _outputImage;


    /**
     * Initialize controller state.<br>
     * <p>
     * Set a stage, disable some buttons and etc.
     *
     * @param primaryStage controller stage
     */
    public void init( Stage primaryStage ) {

        _stage = primaryStage;

        setButtonsDisabled( true, saveImageBtn, encodeBtn );
        setComboBox();
    }

    /**
     * On open image button clicked.<br>
     * <p>
     * Open choose file dialog, load image and show it.
     *
     * @param event action event
     */
    public void onOpenImageBtn( ActionEvent event ) {

        /* Opening a file chooser dialog */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Выберите файл с изображением..." );
        fileChooser.setInitialDirectory( new File( "." ) );
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter( "Изображения", "*.png" ) );
        File file = fileChooser.showOpenDialog( _stage );

        /* Showing an image */
        if( file != null ) {

            clearImageWidgets();

            /* Saving file name and directory */
            _sourceImageFileName = file.getName();
            _sourceImagePath = getFileDirectory( file );

            _sourceImage = new Image( file.toURI().toString() );
            sourceImage.setImage( _sourceImage );

            setButtonsDisabled( false, encodeBtn );
        }
    }

    /**
     * On save image button clicked.<br>
     * <p>
     * Open choose file dialog, save image and show it.
     *
     * @param event
     */
    public void onSaveImageBtn( ActionEvent event ) {

        /* Opening a file chooser dialog */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Сохранить файл с изображением..." );
        fileChooser.setInitialDirectory( new File( _sourceImagePath ) );

        /* Making a new file name based on loaded file name */
        String newFileName = getFileNameForEncodedFile( _sourceImageFileName );
        fileChooser.setInitialFileName( newFileName );

        File file = fileChooser.showSaveDialog( _stage );

        /* Saving an image */
        if( file != null ) {

            BufferedImage tmpImage = SwingFXUtils.fromFXImage( _outputImage, null );

            try {

                String fileExtension = getFileExtension( newFileName );
                boolean isSuccessful = ImageIO.write( tmpImage, fileExtension, file );

            } catch( IOException e ) {

                e.printStackTrace();
            }
        }

    }

    public void onReadImageBtn( ActionEvent event ) {

        /* Opening a file chooser dialog */
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Выберите файл с изображением..." );
        fileChooser.setInitialDirectory( new File( "." ) );
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter( "Изображения", "*.png" ) );
        File file = fileChooser.showOpenDialog( _stage );

        String algorithmName = algorithmList.getValue();

        if( file != null && algorithmName != null ) {

            Image tmpImage = new Image( file.toURI().toString() );
            BufferedImage tmpBufferedImage = SwingFXUtils.fromFXImage( tmpImage, null );

            /* Getting an object of the class depending on chosen algorithm */
            SteganographyAlgorithm algorithm = null;
            try {

                algorithm = SteganographyAlgorithmFactory.createSteganographyAlgorithm( algorithmName );

            } catch( Exception e ) {

                e.printStackTrace();
                return;
            }

            /* Decoding the text from the image */
            String text = null;
            try {

                text = algorithm.decode( tmpBufferedImage );

            } catch( Exception e ) {

                showSimpleMessage( Alert.AlertType.ERROR, "Ощибка", e.getMessage() );
                return;
            }

            showSimpleMessage( Alert.AlertType.INFORMATION, "Результат чтения", text );

        } else if( file != null && algorithmName == null ) {

            showSimpleMessage( Alert.AlertType.WARNING, "Ошибка", "Не задан алгоритм для чтения изображения." );
        }
    }

    /**
     * On encode button clicked.<br>
     * <p>
     * Get an encoding text from a text field and put it into a current loaded image.
     *
     * @param event action event
     */
    public void onEncodeBtn( ActionEvent event ) {

        String text = encodingText.getText();
        String algorithmName = algorithmList.getValue();

        /* If image is loaded and text field isn't empty */
        if( _sourceImage != null && !text.isEmpty() && algorithmName != null ) {

            /* Image to BufferedImage */
            BufferedImage inputBufferedImage = SwingFXUtils.fromFXImage( _sourceImage, null );

            /* Getting an object of the class depending on chosen algorithm */
            SteganographyAlgorithm algorithm = null;
            try {

                algorithm = SteganographyAlgorithmFactory.createSteganographyAlgorithm( algorithmName );

            } catch( Exception e ) {

                e.printStackTrace();
                return;
            }

            /* Encoding the text into the image */
            BufferedImage outputBufferedImage = null;
            try {

                outputBufferedImage = algorithm.encode( text, inputBufferedImage );

            } catch( Exception e ) {

                showSimpleMessage( Alert.AlertType.ERROR, "Ощибка", e.getMessage() );
                return;
            }

            /* Showing the result */
            if( outputBufferedImage != null ) {

                showSimpleMessage( Alert.AlertType.INFORMATION, "Успешно!", "Текст \"" + text + "\" вставлен в изображение.\n" +
                        "Теперь его можно сохранить." );

                _outputImage = SwingFXUtils.toFXImage( outputBufferedImage, null );
                outputImage.setImage( _outputImage );

                setButtonsDisabled( false, saveImageBtn );

                System.out.println("********** Сравнение исходного изображения с закодированным **********");

                System.out.print("Метрика MSE: ");
                double mse = ImagesComparisonTools.calculateMSE( inputBufferedImage,outputBufferedImage );
                System.out.println(mse);

                System.out.print("Метрика RMSE: ");
                double rmse = ImagesComparisonTools.calculateRMSE( inputBufferedImage,outputBufferedImage );
                System.out.println(rmse);

                System.out.print("Метрика PSNR: ");
                double psnr = ImagesComparisonTools.calculatePSNR( inputBufferedImage,outputBufferedImage );
                System.out.println(psnr);

                System.out.println("**********************************************************************");
            }

        } else {

            /* If image isn't loaded */
            if( _sourceImage == null ) {

                showSimpleMessage( Alert.AlertType.WARNING, "Ошибка", "Не возможно выполнить операцию.\n" +
                        "Файл с изображением не загружен." );

            /* If text field is empty */
            } else if( text.isEmpty() ) {

                showSimpleMessage( Alert.AlertType.WARNING, "Ошибка", "Не возможно выполнить операцию.\n" +
                        "Текст для вставки в изображение не введен." );

            /* If algorithm is not set */
            } else if( algorithmName == null ) {

                showSimpleMessage( Alert.AlertType.WARNING, "Ошибка", "Не возможно выполнить операцию.\n" +
                        "Не задан алгоритм для вставки текста в изображение." );
            }
        }
    }

    /**
     * Set buttons enabled or disabled.<br>
     *
     * @param isDisabled disabled or enabled
     * @param btns       array of buttons
     */
    private void setButtonsDisabled( boolean isDisabled, Button... btns ) {

        for( Button b : btns ) {

            b.setDisable( isDisabled );
        }
    }

    /**
     * Show simple alert message without header text.
     *
     * @param type  type of an alert dialog
     * @param title title text of an alert dialog
     * @param text  information text of an alert dialog
     */
    private void showSimpleMessage( Alert.AlertType type, String title, String text ) {

        Alert alert = new Alert( type );
        alert.setTitle( title );
        alert.setHeaderText( null );
        alert.setContentText( text );
        alert.showAndWait();
    }

    /**
     * Make a file name for encoded image.<br>
     * <p>
     * Simply get a source file name and insert "[encoded]" before file extension.
     *
     * @param oldFileName source file name (i.e. file.jpg)
     *
     * @return new file name for encoded image (i.e. file[encoded].jpg)
     */
    private String getFileNameForEncodedFile( String oldFileName ) {

        String algorithmName = algorithmList.getValue();
        String toInsert = "[encoded," + algorithmName + "]";
        int indexOfDot = _sourceImageFileName.indexOf( "." );
        StringBuilder newFileName = new StringBuilder( _sourceImageFileName );
        newFileName.insert( indexOfDot, toInsert );
        return newFileName.toString();
    }

    /**
     * Get file extension from a file name.<br>
     *
     * @param fileName name of a file with extension (i.e. file.jpg)
     *
     * @return file extension (i.e. jpg)
     */
    private String getFileExtension( String fileName ) {

        return fileName.substring( fileName.lastIndexOf( "." ) + 1 );
    }

    /**
     * Get absolute path of a directory where file is located.<br>
     *
     * @param file file
     *
     * @return absolute path of a file directory
     */
    private String getFileDirectory( File file ) {

        int startDeleteIndex = file.getAbsolutePath().lastIndexOf( "\\" ) + 1;
        StringBuilder stringBuilder = new StringBuilder( file.getAbsolutePath() );
        int stringLength = stringBuilder.length();
        stringBuilder.delete( startDeleteIndex, stringLength );

        return stringBuilder.toString();
    }

    /**
     * Clear image widgets
     */
    private void clearImageWidgets() {

        sourceImage.setImage( null );
        outputImage.setImage( null );
    }

    /**
     * Set the algorithm combo box.
     */
    private void setComboBox() {

        ObservableList< String > options = FXCollections.observableArrayList(

                "KutterJordanBossen",
                "LeastSignificantBit"
        );

        algorithmList.setItems( options );
        //algorithmList.setValue( "LeastSignificantBit" );
    }
}
