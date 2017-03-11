package lab4;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Exchanger;

/**
 * Created by Igorek on 19-Apr-16 at 9:42 PM.
 */
public class InputWindow implements Initializable {

    @FXML
    private TextField outputVertex;
    @FXML
    private TextField inputVertex;
    @FXML
    private TextField vertexWeight;
    @FXML
    private Canvas canvas;

    private static Graph graph;

    private static Stage window;

    public Graph process(Graph _graph) throws IOException {
        graph = _graph == null ? new Graph(true, 5) : _graph;

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(Event::consume);
        window.setTitle("Input");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("inputWindow.fxml"))));
        window.showAndWait();

        return graph;
    }

    @FXML
    private void removeVertex() {
        graph.removeVertex();
        render();
    }

    @FXML
    private void addVertex() {
        graph.addVertex();
        render();
    }

    @FXML
    private void processEdge() {
        try {
            int out = Integer.parseInt(outputVertex.getText());
            int in = Integer.parseInt(inputVertex.getText());
            int weight = Integer.parseInt(vertexWeight.getText());

            graph.addEdge(out, in, weight);
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void render() {
        graph.drawOnCanvas(canvas.getGraphicsContext2D(), canvas.getWidth());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        render();
    }

    @FXML
    private void exit() {
        if (true) {
            window.close();
        }
    }
}
