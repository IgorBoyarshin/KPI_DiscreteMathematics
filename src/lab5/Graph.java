package lab5;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by Igorek on 15-May-16 at 3:41 PM.
 */
public class Graph {

    // Not oriented graph

    private int size;
    private boolean[][] vertexField;

    private final Color defaultColor = Color.BLACK;
    private final Color[] COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.AQUA, Color.BROWN,
            Color.DARKGREEN, Color.INDIGO, Color.HONEYDEW, Color.PINK, Color.NAVY, Color.VIOLET};

    private Color[] verticesColors;

    private final int maxVertexCount = 20;
    private final int minVertexCount = 3;

    public Graph(int initialVertexCount) {
        this.size = initialVertexCount;

        vertexField = new boolean[size][size];
        verticesColors = new Color[size];
    }

    public boolean edgeBetween(int v1, int v2) {
        if (v1 < 0 || v2 < 0 || v1 >= size || v2 >= size) {
            return false;
        }
        if (v1 == v2) {
            return false;
        }

        return vertexField[v1][v2];
    }

    public int getColorsCount() {
        return COLORS.length;
    }

    public void paintVertex(int index, int colorIndex) {
        if (index < 0 || index >= size || colorIndex < 0 || colorIndex >= COLORS.length) {
            return;
        }

        verticesColors[index] = COLORS[colorIndex];
    }

    public boolean isVertexPainted(int index) {
        if (index < 0 || index >= size) {
            return false;
        }

        return verticesColors[index] != null;
    }

    public int getVertexCount() {
        return size;
    }

    public void addEdge(int out, int in) {
        if (out == in || out < 0 || in < 0 || out >= size || in >= size) {
            return;
        }

        vertexField[out][in] = true;
        vertexField[in][out] = true;
    }

    public void removeEdge(int out, int in) {
        if (out == in || out < 0 || in < 0 || out >= size || in >= size) {
            return;
        }

        vertexField[out][in] = false;
        vertexField[in][out] = false;
    }

    public void addVertex() {
        if (size >= maxVertexCount) {
            return;
        }

        boolean[][] newVertexField = new boolean[size + 1][size + 1];
        for (int row = 0; row < size; row++) {
            System.arraycopy(vertexField[row], 0, newVertexField[row], 0, size);
        }
        vertexField = newVertexField;

        Color[] newVerticesColors = new Color[size + 1];
        System.arraycopy(verticesColors, 0, newVerticesColors, 0, size);
        verticesColors = newVerticesColors;

        size++;
    }

    public void removeVertex() {
        if (size <= minVertexCount) {
            return;
        }

        boolean[][] newVertexField = new boolean[size - 1][size - 1];
        for (int row = 0; row < size - 1; row++) {
            System.arraycopy(vertexField[row], 0, newVertexField[row], 0, size - 1);
        }
        vertexField = newVertexField;

        Color[] newVerticesColors = new Color[size - 1];
        System.arraycopy(verticesColors, 0, newVerticesColors, 0, size - 1);
        verticesColors = newVerticesColors;

        size--;
    }

    public void drawOnCanvas(GraphicsContext gc, double canvasSize) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0.0, 0.0, canvasSize, canvasSize);

        gc.setFont(new Font("Calibri", 20));

        drawEdges(gc, canvasSize);
        drawVertices(gc, canvasSize);
    }

    private Vector getVertexShift(int index, double canvasSize) {
        final double angle = 1.0d * index / size * 2 * Math.PI;
        final double radius = canvasSize / 2.0 * 0.85;
        return new Vector(Math.cos(angle) * radius, Math.sin(angle) * radius);
    }

    private void drawVertices(GraphicsContext gc, double canvasSize) {
        final double vertexRadius = 9.0;
        final double center = canvasSize / 2.0;
        for (int i = 0; i < size; i++) {
            Vector shift = getVertexShift(i, canvasSize);
            drawVertex(gc, new Vector(center + shift.x, center - shift.y), vertexRadius, verticesColors[i]);

            gc.setFill(Color.BLACK);
            drawVertexText(gc, i, new Vector(center + shift.x * 1.1, center - shift.y * 1.1));
        }
    }

    private void drawVertex(GraphicsContext gc, Vector pos, double radius, Color color) {
        gc.setFill(color);
        gc.fillOval(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius);
    }

    private void drawVertexText(GraphicsContext gc, int vertexNumber, Vector pos) {
        gc.fillText("V" + vertexNumber, pos.x - 8.0, pos.y + 2.0);
    }

    private void drawEdges(GraphicsContext gc, double canvasSize) {
        for (int out = 0; out < size; out++) {
            for (int in = 0; in < out; in++) {
                if (vertexField[out][in]) {
                    Vector outShift = getVertexShift(out, canvasSize);
                    Vector inShift = getVertexShift(in, canvasSize);
                    drawEdge(gc, outShift, inShift, canvasSize / 2.0);
                }
            }
        }
    }

    private void drawEdge(GraphicsContext gc, Vector out, Vector in, double center) {
        gc.setLineWidth(4.0);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(center + out.x, center - out.y, center + in.x, center - in.y);
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
