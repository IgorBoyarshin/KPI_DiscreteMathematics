package lab2;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Igorek on 03-Apr-16 at 12:47 PM.
 */
public class MainWindow extends Application {

    private Stage window;

    private static Set<Person> setA;
    private static Set<Person> setB;

    @FXML
    private Label infoLabel;
    @FXML
    private TextField numberText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        setA = new HashSet<>();
        setB = new HashSet<>();

        this.window = primaryStage;
        window.setTitle("Lab2 by Igor Boiarshyn");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("mainWindow.fxml"))));
        window.show();
//        processAlgorithmWindow1();
    }

    @FXML
    public void processOperationsWindow() {
        try {
            new OperationsWindow().process(setA, setB);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create the Operations window!");
        }
    }

    @FXML
    public void processSetInputWindow() {
        try {
            new SetInputWindow().process(setA, setB);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create the SetInput window!");
        }
    }

    @FXML
    public void setInfoLabel() {
        int number = Integer.parseInt(numberText.getText());
        infoLabel.setText("Варіант: " + ((number % 30) + 1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
