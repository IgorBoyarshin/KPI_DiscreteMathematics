package lab4;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.web.WebHistory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Igorek on 15-May-16 at 3:41 PM.
 */
public class Graph {

    private int size;
    private int[][] weightField;
    private final boolean oriented;

    private List<Edge> markedEdges;

    private final int maxVertexCount = 20;
    private final int minVertexCount = 3;

    public Graph(boolean oriented, int initialVertexCount) {
        this.oriented = oriented;
        this.size = initialVertexCount;

        weightField = new int[size][size];
        markedEdges = new ArrayList<>();

//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < i; j++) {
//                weightField[i][j] = 10;
//            }
//        }

//        markedEdges.add(new Edge(1, 3));
//        markedEdges.add(new Edge(3, 2));

//        weightField[2][1] = 4;
//        weightField[1][2] = 4;
//        weightField[3][1] = 5;
//        weightField[0][3] = 5;
    }

    public int getWeightAt(int row, int column) {
        if (row < 0 || column < 0 || row >= size || column >= size) {
            return 0;
        }

        return weightField[row][column];
    }

    public int getVertexCount() {
        return size;
    }

    public void markEdge(int out, int in) {
        markedEdges.add(new Edge(out, in));
    }

//    public void unmarkEdge(int out, int in) {
//
//    }

    public void clearEdgeMarking() {
        markedEdges = new ArrayList<>();
    }

    public void addEdge(int out, int in, int edgeWeight) {
        if (out == in) {
            return;
        }

        if (edgeWeight == 0) {
            weightField[out][in] = 0;
            if (!oriented) {
                weightField[in][out] = 0;
            }
            return;
        }

        if (oriented) {
            weightField[out][in] = edgeWeight;
            if (weightField[in][out] != 0) {
                weightField[in][out] = edgeWeight;
            }
        } else {
            weightField[out][in] = edgeWeight;
            weightField[in][out] = edgeWeight;
        }
    }

    public void addVertex() {
        if (size >= maxVertexCount) {
            return;
        }

        int[][] newWeightField = new int[size + 1][size + 1];
        for (int row = 0; row < size; row++) {
            System.arraycopy(weightField[row], 0, newWeightField[row], 0, size);
        }

        weightField = newWeightField;
        size++;
    }

    public void removeVertex() {
        if (size <= minVertexCount) {
            return;
        }

        int[][] newWeightField = new int[size - 1][size - 1];
        for (int row = 0; row < size - 1; row++) {
            System.arraycopy(weightField[row], 0, newWeightField[row], 0, size - 1);
        }

        markedEdges.removeIf(e -> e.out >= size - 1 || e.in >= size - 1);

        weightField = newWeightField;
        size--;
    }

    public void drawOnCanvas(GraphicsContext gc, double canvasSize) {
//        gc.clearRect(0.0, 0.0, canvasSize, canvasSize);
        gc.setFill(Color.WHITE);
        gc.fillRect(0.0, 0.0, canvasSize, canvasSize);

        gc.setFont(new Font("Calibri", 20));

        drawVertices(gc, canvasSize);
        drawEdges(gc, canvasSize);
    }

    private Vector getVertexShift(int index, double canvasSize) {
        final double angle = 1.0d * index / size * 2 * Math.PI;
        final double radius = canvasSize / 2.0 * 0.85;
        return new Vector(Math.cos(angle) * radius, Math.sin(angle) * radius);
    }

    private void drawVertices(GraphicsContext gc, double canvasSize) {
        final double vertexRadius = 7.0;
        final double center = canvasSize / 2.0;
        for (int i = 0; i < size; i++) {
            Vector shift = getVertexShift(i, canvasSize);
            gc.setFill(Color.GREEN);
            drawVertex(gc, new Vector(center + shift.x, center - shift.y), vertexRadius);
            gc.setFill(Color.BLACK);
            drawVertexText(gc, i, new Vector(center + shift.x * 1.1, center - shift.y * 1.1));
        }
    }

    private void drawVertex(GraphicsContext gc, Vector pos, double radius) {
        gc.fillOval(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius);
    }

    private void drawVertexText(GraphicsContext gc, int vertexNumber, Vector pos) {
        gc.fillText("V" + vertexNumber, pos.x - 8.0, pos.y + 2.0);
    }

    private void drawEdges(GraphicsContext gc, double canvasSize) {
        for (int out = 0; out < size; out++) {
            for (int in = 0; in < size; in++) {
                if (out == in) {
                    continue;
                }

                boolean symmetrical = weightField[out][in] == weightField[in][out];
                if (symmetrical && out > in) {
                    continue;
                }

                if (weightField[out][in] != 0) {
                    Vector outShift = getVertexShift(out, canvasSize);
                    Vector inShift = getVertexShift(in, canvasSize);
                    Vector verts = new Vector(out, in);
                    final boolean marked = markedEdges.stream().anyMatch(
                            e -> (e.out == verts.x && e.in == verts.y) || (e.out == verts.y && e.in == verts.x)
                    );
//                    gc.setStroke(marked ? Color.RED : Color.BLACK);
//                    gc.setFill(marked ? Color.RED : Color.BLACK);
                    drawEdge(gc, outShift, inShift, oriented && (!symmetrical), marked, canvasSize / 2.0, weightField[out][in]);
                }
            }
        }
    }

    private void drawEdge(GraphicsContext gc, Vector out, Vector in, boolean isArrow, boolean marked, double center, int weight) {
        if (marked) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(6.0);
        } else {
            gc.setLineWidth(4.0);
            if (isArrow) {
                gc.setStroke(new LinearGradient(center + out.x, center - out.y, center + in.x, center - in.y, false, CycleMethod.REFLECT,
                        new Stop(0.0, Color.DEEPSKYBLUE), new Stop(1.0, Color.RED)));
            } else {
                gc.setStroke(Color.DEEPSKYBLUE);
            }
        }

        gc.strokeLine(center + out.x, center - out.y, center + in.x, center - in.y);

        Vector dir = new Vector(in.x - out.x, -(in.y - out.y));

//        if (isArrow) {
//            final double rectSize = 14.0;
////            final double length = Math.sqrt((in.x - out.x) * (in.x - out.x) + (in.y - out.y) * (in.y - out.y));
//            final double dirAmpl = 0.08;
//            gc.fillRect(center + in.x - dir.x * dirAmpl - rectSize / 2.0, center - (in.y + dir.y * dirAmpl) - rectSize / 2.0,
//                    rectSize, rectSize);
//        }

        // Weight
        final double dirAmpl = 0.8;
        final double textSize = 19.0;
        gc.setFill(Color.AQUA);
        gc.fillRect(
                center + out.x + dir.x * dirAmpl - 1.5 * textSize / 2.0,
                center - out.y + dir.y * dirAmpl - textSize / 2.0,
                1.5 * textSize, textSize);
        gc.setFill(Color.BLACK);
        gc.fillText(weight + "", center + out.x + dir.x * dirAmpl - 1.5 * 6.0, center - out.y + dir.y * dirAmpl + 6.0);
    }

    private class Edge {
        protected final int out;
        protected final int in;

        protected Edge(int out, int in) {
            this.out = out;
            this.in = in;
        }
    }

    private class Vector {
        protected final double x;
        protected final double y;

        protected Vector(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
