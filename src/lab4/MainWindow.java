package lab4;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Igorek on 19-Apr-16 at 9:04 PM.
 */
public class MainWindow extends Application {
    private Stage window;

    @FXML
    private Label infoLabel;
    @FXML
    private TextField numberText;

    private static Graph graph;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        window.setTitle("Lab4 by Igor Boiarshyn");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("mainWindow.fxml"))));
        window.show();

        // TODO: REMOVE
        graph = new Graph(true, 7);
        graph.addEdge(0, 1, 8);
        graph.addEdge(0, 2, 4);
        graph.addEdge(2, 1, 3);
        graph.addEdge(1, 4, 3);
        graph.addEdge(1, 3, 8);
        graph.addEdge(2, 3, 10);
        graph.addEdge(2, 5, 12);
        graph.addEdge(4, 3, 2);
        graph.addEdge(3, 5, 3);
        graph.addEdge(4, 6, 10);
        graph.addEdge(5, 6, 4);

        processOperationsWindow();
//        processInputWindow();
    }

    @FXML
    private void processOperationsWindow() {
        if (graph == null) {
            return;
        }

        try {
            new OperationsWindow().process(graph);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create the Operations window!");
        }
    }

    @FXML
    private void processInputWindow() {
        try {
            new InputWindow().process(graph);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to create the SetInput window!");
        }
    }

    @FXML
    private void setInfoLabel() {
        int number = Integer.parseInt(numberText.getText());
        infoLabel.setText("Варіант: " + ((number % 8) + 1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
