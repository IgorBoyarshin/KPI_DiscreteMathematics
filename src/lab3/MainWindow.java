package lab3;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Igorek on 19-Apr-16 at 9:04 PM.
 */
public class MainWindow extends Application {
    private Stage window;

    private static int valueN;
    private static int valueM;

    @FXML
    private Label infoLabel;
    @FXML
    private TextField numberText;

    @Override
    public void start(Stage primaryStage) throws Exception {
//        valueN = 3;
//        valueM = 9;

        this.window = primaryStage;
        window.setTitle("Lab3 by Igor Boiarshyn");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("mainWindow.fxml"))));
        window.show();
//        processOperationsWindow();
    }

    @FXML
    public void processOperationsWindow() {
        try {
            new OperationsWindow().process(valueN, valueM);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create the Operations window!");
        }
    }

    @FXML
    public void processSetInputWindow() {
        try {
            int[] nAndM = new InputWindow().process();
            valueN = nAndM[0];
            valueM = nAndM[1];
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create the SetInput window!");
        }
    }

    @FXML
    public void setInfoLabel() {
        int number = Integer.parseInt(numberText.getText());
        infoLabel.setText("Варіант: " + ((number % 26) + 1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
