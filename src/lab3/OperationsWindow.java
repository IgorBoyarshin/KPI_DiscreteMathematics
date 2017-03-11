package lab3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by Igorek on 19-Apr-16 at 11:17 PM.
 */
public class OperationsWindow implements Initializable {

    private static int valueN;
    private static int valueM;

    @FXML
    private Label nAndMValueLabel;
    @FXML
    private Label startingValueLabel;
    @FXML
    private ListView<Label> listViewLexi;
    @FXML
    private ListView<Label> listViewAntiLexi;

    public void process(int _valueN, int _valueM) throws IOException {
        valueN = _valueN;
        valueM = _valueM;

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
//        window.setOnCloseRequest(Event::consume);
        window.setTitle("Operations");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("operationsWindow.fxml"))));
        window.showAndWait();
    }

    // Cuts off the hidden digit
    private String valueToString(boolean[] value, boolean lexicographical) {
        StringBuilder stringBuilder = new StringBuilder();

        for (boolean bit : value) {
            stringBuilder.append(bit ? '1' : '0');
        }
        stringBuilder.deleteCharAt(lexicographical ? 0 : stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    private boolean[] generateRandomValue(int length) {
        boolean[] result = new boolean[length];
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        for (int i = 0; i < length; i++) {
            result[i] = random.nextInt() % 2 == 0;
        }

        return result;
    }

    @FXML
    private void regenerateStartingValue() {
        boolean[] newStartingValueLexi = generateRandomValue(valueN + 1);
        newStartingValueLexi[0] = false;

        startingValueLabel.setText(valueToString(newStartingValueLexi, true));

        boolean[] lastValue = populateList(listViewLexi, newStartingValueLexi, valueM, true);

        // Prepare the value(reverse, shift and set the hidden value)
        boolean[] startingValueAntiLexi = new boolean[valueN + 1];
        for (int index = valueN; index >= 1; index--) {
            startingValueAntiLexi[valueN - index] = lastValue[index];
        }
        startingValueAntiLexi[valueN] = true;

        populateList(listViewAntiLexi, startingValueAntiLexi, valueM, false);
    }

    // returns the last element
    private boolean[] populateList(ListView<Label> listView, boolean[] currentChain, int amount, boolean lexicographical) {
        ObservableList<Label> items = FXCollections.observableArrayList();

        boolean[] storage = currentChain;
        while (amount > 0 && (lexicographical ? !currentChain[0] : currentChain[valueN])) {
            items.add(new Label(valueToString(currentChain, lexicographical)));
            storage = Arrays.copyOf(currentChain, currentChain.length);

//            if (lexicographical) {
//                for (int index = valueN; index > 0; index--) {
//                    currentChain[index] = false;
//                    if (!currentChain[index - 1]) {
//                        currentChain[index - 1] = true;
//
//                        break;
//                    }
//                }
//            } else {
//                for (int index = 0; index < valueN; index++) {
//                    currentChain[index] = true;
//                    if (currentChain[index + 1]) {
//                        currentChain[index + 1] = false;
//
//                        break;
//                    }
//                }
//            }

            if (lexicographical) {
                for (int index = valueN; index >= 0; index--) {
                    if (!currentChain[index]) {
                        currentChain[index] = true;
                        for (int i = valueN; i > index; i--) {
                            currentChain[i] = false;
                        }

                        break;
                    }
                }
            } else {
                for (int index = 0; index <= valueN; index++) {
                    if (currentChain[index]) {
                        currentChain[index] = false;
                        for (int i = 0; i < index; i++) {
                            currentChain[i] = true;
                        }

                        break;
                    }
                }
            }

            amount--;
        }

        listView.setItems(items);
        return storage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nAndMValueLabel.setText("Значення N: " + valueN + ". Значення M: " + valueM);
        regenerateStartingValue(); // and populate lists
    }
}
