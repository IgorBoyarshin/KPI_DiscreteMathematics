package lab5;

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
import java.util.*;

/**
 * Created by Igorek on 19-Apr-16 at 11:17 PM.
 */
public class OperationsWindow implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Label resultLabel;

    private static Graph graph;

    public void process(Graph _graph) throws IOException {
        graph = _graph;

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
//        window.setOnCloseRequest(Event::consume);
        window.setTitle("Operations");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("operationsWindow.fxml"))));
        window.showAndWait();
    }

    @FXML
    private void paintGraph() {
        int colorIndex = 0;
        List<Integer> absentEdgesWith;
        for (int index = 0; index < graph.getVertexCount(); index++) {
            if (!graph.isVertexPainted(index)) {
                absentEdgesWith = new ArrayList<>();
                absentEdgesWith.add(index);
                graph.paintVertex(index, colorIndex);
                for (int index2 = 0; index2 < graph.getVertexCount(); index2++) {
                    if (!graph.isVertexPainted(index2)) {
                        final int finalIndex2 = index2;
                        if (absentEdgesWith.stream().noneMatch(vertex -> graph.edgeBetween(vertex, finalIndex2))) {
                            graph.paintVertex(index2, colorIndex);
                            absentEdgesWith.add(index2);
                        }
                    }
                }
                colorIndex++;
            }
        }

        render();
    }

    private void render() {
        graph.drawOnCanvas(canvas.getGraphicsContext2D(), canvas.getWidth());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultLabel.setText("");

        render();
    }

    private class Edge {
        protected final Integer out;
        protected final Integer in;

        protected Edge(Integer out, Integer in) {
            this.out = out;
            this.in = in;
        }
    }
}
