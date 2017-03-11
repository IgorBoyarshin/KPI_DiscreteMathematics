package lab3;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Igorek on 19-Apr-16 at 9:42 PM.
 */
public class InputWindow implements Initializable {

    @FXML
    private TextField inputN;
    @FXML
    private TextField inputM;
    @FXML
    private Label errorN;
    @FXML
    private Label errorM;

    private static int[] nAndM;
    private static boolean correctN;
    private static boolean correctM;

    private static Stage window;

    public int[] process() throws IOException {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(Event::consume);
        window.setTitle("Input");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("inputWindow.fxml"))));
        window.showAndWait();

        return nAndM;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        correctN = false;
        correctM = false;
        nAndM = new int[2];

        inputN.textProperty().addListener(((observable, oldValue, newValue) -> {
            correctN = true;

            try {
                nAndM[0] = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                errorN.setText("Invalid value");
                correctN = false;
            }

            if (correctN) {
                errorN.setText("");
            }
        }));

        inputM.textProperty().addListener(((observable, oldValue, newValue) -> {
            correctM = true;

            try {
                nAndM[1] = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                errorM.setText("Invalid value");
                correctM = false;
            }

            if (nAndM[1] > nAndM[0]) { // m <= n -- has to be
                errorM.setText("Invalid value");
                correctM = false;
            }

            if (correctM) {
                errorM.setText("");
            }
        }));
    }

    @FXML
    private void exit() {
        if (correctN && correctM) {
            window.close();
        }
    }
}
