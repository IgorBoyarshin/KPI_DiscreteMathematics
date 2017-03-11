package lab4;

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
    private TextField startVertexLabel;
    @FXML
    private TextField finishVertexLabel;
    @FXML
    private Canvas canvas;
    @FXML
    private Label resultLabel;

    private static Graph graph;

    private final int veryBigNumber = 999999;

    public void process(Graph _graph) throws IOException {
        graph = _graph;

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
//        window.setOnCloseRequest(Event::consume);
        window.setTitle("Operations");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("operationsWindow.fxml"))));
        window.showAndWait();
    }

    private void render() {
        graph.drawOnCanvas(canvas.getGraphicsContext2D(), canvas.getWidth());
    }

    @FXML
    private void clearPath() {
        graph.clearEdgeMarking();
        render();
    }

    @FXML
    private void generatePath() {
        resultLabel.setText("");
        graph.clearEdgeMarking();

        boolean correct = true;
        int vs = -1;
        int vf = -1;

        try {
            vs = Integer.parseInt(startVertexLabel.getText());
            vf = Integer.parseInt(finishVertexLabel.getText());
            correct = (vs >= 0 && vs < graph.getVertexCount() && vf >= 0 && vf < graph.getVertexCount());
        } catch (Exception e) {
            correct = false;
        }

        if (!correct) {
            return;
        }

        getPath(vs, vf);

        // check loops
    }

    private void getPath(final int vs, final int vf) {
        // new <-> old
        Map<Integer, Integer> vertexMapping = sortTopologically();
        if (vertexMapping == null) {
            // Loop exists
            resultLabel.setText("There is a loop in the graph. Fix it!");
            return;
        }

        // Vertices S and F according to the new mapping
        int start = -1;
        int finish = -1;
        for (int vertex : vertexMapping.keySet()) {
            final int newVertex = vertexMapping.get(vertex);
            if (newVertex == vs) {
                start = vertex;
            } else if (newVertex == vf) {
                finish = vertex;
            }
        }
        if (start == -1 || finish == -1) {
            System.out.println("Unknown error");
            return;
        }

        // Init
        Map<Integer, Integer> verticesMarks = new HashMap<>(); // new vertex mapping is used
        for (int index = 0; index < graph.getVertexCount(); index++) {
            verticesMarks.put(index, index == start ? 0 : veryBigNumber);
        }
        int currentVertexIndex = start + 1;

        // Main loop
        while (currentVertexIndex < graph.getVertexCount()) {
            final int currentVertex = vertexMapping.get(currentVertexIndex); // need old mapping, because we access weightField
            int minPath = veryBigNumber;
            for (int row = 0; row < graph.getVertexCount(); row++) { // seek for edge into current vertex
                final int edgeWeight = graph.getWeightAt(row, currentVertex);
                if (edgeWeight != 0) { // if edge exists
                    final int finalRow = row;
                    final int outVertex = vertexMapping.keySet().stream()
                            .filter(newV -> vertexMapping.get(newV) == finalRow).findAny().get(); // in new mapping
                    final int outVertexMark = verticesMarks.get(outVertex);
                    if (edgeWeight + outVertexMark < minPath) {
                        minPath = edgeWeight + outVertexMark;
                    }
                }
            }

            verticesMarks.replace(currentVertexIndex, minPath);
            currentVertexIndex++;
        }

        // Print answer
        final int resultPath = verticesMarks.get(finish);
        if (resultPath < veryBigNumber) {
            resultLabel.setText("Min path: " + resultPath);
        } else {
            resultLabel.setText("The path does not exist!");
            return;
        }

        // Reverse loop
        StringBuilder result = reverseLoop(vertexMapping, verticesMarks, start, finish).append(finish);
        String[] verticesStr = result.toString().substring(1, result.length()).split(" ");
        int[] vertices = new int[verticesStr.length];
        for (int index = 0; index < vertices.length; index++) {
            vertices[index] = vertexMapping.get(Integer.parseInt(verticesStr[index]));
        }

        for (int index = 0; index < vertices.length - 1; index++) {
            graph.markEdge(vertices[index], vertices[index + 1]);
        }
        render();
    }

    private StringBuilder reverseLoop(Map<Integer, Integer> vertexMapping, Map<Integer, Integer> verticesMarks, int startVertex, int forVertex) {
//        System.out.println();
//        System.out.println("Invokation");

        if (forVertex == startVertex) {
            return new StringBuilder(startVertex).append(" ");
        }

        final int markForThisVertex = verticesMarks.get(forVertex);
        StringBuilder answer = null;
        for (int row = 0; row < graph.getVertexCount(); row++) { // seek for output vertices into current vertex
            final int edgeWeight = graph.getWeightAt(row, vertexMapping.get(forVertex));
            if (edgeWeight > 0) { // exists edge
//                System.out.print("For vertex " + forVertex + " found edge " + edgeWeight);
                final int finalRow = row;
                final int outputVertex = vertexMapping.keySet().stream()
                        .filter(vertexNew -> vertexMapping.get(vertexNew) == finalRow) // new mapping for it
                        .findAny()
                        .get(); // new mapping
//                System.out.println(" with output " + outputVertex);
                if (edgeWeight + verticesMarks.get(outputVertex) == markForThisVertex) { // correct path
//                    System.out.println(" :: correct!");
                    StringBuilder deeperRes = reverseLoop(vertexMapping, verticesMarks, startVertex, outputVertex);
                    if (deeperRes != null) {
//                        System.out.println(" ++ deeper succ");
                        if (answer != null) {
//                            System.out.println("ERRRRR");
                        }
                        answer = new StringBuilder(deeperRes).append(outputVertex).append(" ");
                    }
                    // else => deeper search failed
                }
            }
        }

        return answer;
    }

    private Map<Integer, Integer> sortTopologically() {
        // Result
        Map<Integer, Integer> newVertexMapping = new HashMap<>();

        // Vertices
        List<Integer> vertices = new ArrayList<>();
        for (int index = 0; index < graph.getVertexCount(); index++) {
            vertices.add(index);
        }

        // Edges
        List<Edge> edges = new ArrayList<>();
        for (int row = 0; row < graph.getVertexCount(); row++) {
            for (int column = 0; column < graph.getVertexCount(); column++) {
                final int weight = graph.getWeightAt(row, column);
                if (weight != 0) {
                    edges.add(new Edge(row, column));
                }
            }
        }

        // Topo sort
        int currentId = vertices.size() - 1;
        while (currentId >= 0) {
            Integer vertexWithNoOutput = vertices.stream()
                    .filter(v -> edges.stream().noneMatch(edge -> Objects.equals(edge.out, v))).findAny().orElseGet(() -> (-1));
            if (vertexWithNoOutput == -1 && currentId != 0) {
                // loop exists
                System.out.println("LOOP!!");
                return null;
            } else {
                // new <-> old
                newVertexMapping.put(currentId, vertexWithNoOutput);
                currentId--;

                // Remove this vertex and edges that come into it
                vertices.remove(vertexWithNoOutput);
                edges.removeIf(edge -> Objects.equals(edge.in, vertexWithNoOutput));
            }
        }

        // Prints new mapping
//        for (int vertex : newVertexMapping.keySet()){
//            System.out.println(vertex + " <-> " + newVertexMapping.get(vertex));
//        }

        return newVertexMapping;
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
