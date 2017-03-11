package lab1;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * Created by Igorek on 06-Mar-16 at 3:57 PM.
 */
public class Lab1 extends Application {

    private Stage window;

    private Label infoLabel = new Label("");

    private int taskCode = 0;
    private Set<String> setA = new HashSet<>();
    private Set<String> setB = new HashSet<>();
    private Set<String> setC = new HashSet<>();
    private Set<String> setBeingFormatted = setA;
    private GridPane gridBeingFormatted;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        window.setTitle("Lab1 by Igor Boiarshyn");

//        scene.getStylesheets().add("main.css");
        window.setScene(new Scene(setupMainScene()));
        window.show();
    }

    private void populateGridWithSet(GridPane grid, Set<String> set, String head) {
        final int gridSize = 16;
        String[] setElements = null;
        grid.getChildren().clear();
        if (set != null) {
            setElements = set.toArray(new String[set.size()]);
        }

        for (int y = 0; y < gridSize + 1; y++) {
            for (int x = 0; x < gridSize + 1; x++) {
                Label label = new Label();
                label.setMinWidth(18);

                if (x == 0 && y == 0) {
                    label.setText(head);
                } else if (y == 0 && x > 0) { // border
                    label.setText(" " + toHex(x - 1));
                    label.setStyle("-fx-background-color: #BBBBBB");
                } else if (x == 0 && y > 0) { // border
                    label.setText(toHex(y - 1) + "0");
                    label.setStyle("-fx-background-color: #BBBBBB");
                } else { // normal fields
                    if (set == null) {
                        label.setText(toHex(y - 1) + toHex(x - 1));
                    } else {
                        final int id = (y - 1) * gridSize + (x - 1);
                        label.setText(id < setElements.length ? setElements[id] : "    ");
                    }
                    label.setStyle("-fx-background-color: white");
                    label.setPadding(new Insets(0.5, 0.5, 0.5, 0.5));
                }

                grid.add(label, x, y);
            }
        }
    }

    private String toHex(int number) {
        if (number >= 0 && number <= 9) {
            return ("" + number);
        } else {
            switch (number) {
                case 10:
                    return "A";
                case 11:
                    return "B";
                case 12:
                    return "C";
                case 13:
                    return "D";
                case 14:
                    return "E";
                case 15:
                    return "F";
                default:
                    return "?";
            }
        }
    }

    private void saveSetToFile(Set<String> set, String fileName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            set.forEach(out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSetFromFile(Set<String> set, String fileName) {
        set.clear();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            while (in.ready()) {
                set.add(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateGridContent(GridPane grid, Set<String> set, String head) {
        populateGridWithSet(grid, set, head);
    }

    private void setSetBeingFormatted(Set<String> set, GridPane grid) {
        setBeingFormatted = set;
        gridBeingFormatted = grid;
    }

    private void addElementToSelectedGrid(String element) {
        boolean containedAlready = !setBeingFormatted.add(element);

        char setLetter = ' ';
        if (setBeingFormatted == setA) {
            setLetter = 'A';
        } else if (setBeingFormatted == setB) {
            setLetter = 'B';
        } else if (setBeingFormatted == setC) {
            setLetter = 'C';
        }

        if (containedAlready) {
            infoLabel.setText("Елемент " + element + " уже є в множині " + setLetter);
//            System.out.println("Contained: " + element);
        } else {
            updateGridContent(gridBeingFormatted, setBeingFormatted, setLetter + "");
//            System.out.println("Added: " + element);
        }
    }

    private void processInputWindow() {
        Stage inputWindow = new Stage();
        inputWindow.initModality(Modality.APPLICATION_MODAL);
        inputWindow.setTitle("Ввід данних");

        inputWindow.setScene(new Scene(setupInputScene()));
        inputWindow.showAndWait();
    }

    private void processOperationsWindow() {
        Stage operationsWindow = new Stage();
        operationsWindow.initModality(Modality.APPLICATION_MODAL);
        operationsWindow.setTitle("Виконання операцій над множинами А і В");

        operationsWindow.setScene(new Scene(setupOperationsScene()));
        operationsWindow.showAndWait();
    }

    private Parent setupInputScene() {
//        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setFont(Font.font(null, FontWeight.BOLD, 16));
        infoLabel.setTextFill(Color.web("#EE3333"));

        GridPane gridA;
        GridPane gridB;
        GridPane gridC;

        VBox mainVbox = new VBox();
        mainVbox.setAlignment(Pos.CENTER);
        mainVbox.setPadding(new Insets(1, 5, 1, 5));
        mainVbox.setStyle("-fx-background-color: #CCCCFF");
        mainVbox.setSpacing(5);
        {
            HBox topSection = new HBox();
            topSection.setSpacing(8);
            {
                GridPane inputGrid = new GridPane();
                {
                    inputGrid.setHgap(1);
                    inputGrid.setVgap(1);
//                    inputGrid.setGridLinesVisible(true);
                    populateGridWithSet(inputGrid, null, "Код");
                }
                inputGrid.addEventFilter(MouseEvent.MOUSE_PRESSED,
                        e -> inputGrid.getChildren().stream()
                                .filter(node -> node instanceof Label)
                                .filter(node -> node.getBoundsInParent().contains(e.getSceneX(), e.getSceneY()))
                                .filter(node -> (GridPane.getRowIndex(node) > 0 && GridPane.getColumnIndex(node) > 0))
                                .forEach(node -> addElementToSelectedGrid(
                                        toHex(GridPane.getRowIndex(node) - 1) + toHex(GridPane.getColumnIndex(node) - 1))
                                )
                );

                gridA = new GridPane();
                gridBeingFormatted = gridA;
                {
                    gridA.setHgap(1);
                    gridA.setVgap(1);
//                    gridA.setGridLinesVisible(true);
                    populateGridWithSet(gridA, setA, "A");
                }

                gridB = new GridPane();
                {
                    gridB.setHgap(1);
                    gridB.setVgap(1);
//                    gridB.setGridLinesVisible(true);
                    populateGridWithSet(gridB, setB, "B");
                }

                gridC = new GridPane();
                {
                    gridC.setHgap(1);
                    gridC.setVgap(1);
//                    gridC.setGridLinesVisible(true);
                    populateGridWithSet(gridC, setC, "C");
                }

                topSection.getChildren().add(inputGrid);
                topSection.getChildren().add(gridA);
                topSection.getChildren().add(gridB);
                topSection.getChildren().add(gridC);
            }

            HBox buttonsGroups = new HBox();
            buttonsGroups.setAlignment(Pos.CENTER);
            buttonsGroups.setSpacing(10);
            buttonsGroups.setPadding(new Insets(10, 10, 10, 10));
            {
                final ToggleGroup selectedSetGroup = new ToggleGroup();

                VBox groupA = new VBox();
                groupA.setSpacing(5);
                groupA.setAlignment(Pos.CENTER);
                {
                    final String fileNameA = "A17.txt";
                    Button saveSet = new Button("Зберегти множину А");
                    Button loadSet = new Button("Завантажити множину А");
                    Button clearSet = new Button("Очистити множину А");
                    RadioButton selectSet = new RadioButton("Форматування множини A");
                    selectSet.setSelected(true);
                    selectSet.setOnAction(e -> setSetBeingFormatted(setA, gridA));
                    selectSet.setToggleGroup(selectedSetGroup);

                    saveSet.setOnAction(e -> saveSetToFile(setA, fileNameA));
                    loadSet.setOnAction(e -> {
                        loadSetFromFile(setA, fileNameA);
                        updateGridContent(gridA, setA, "A");
                    });
                    clearSet.setOnAction(e -> {
                        setA.clear();
                        updateGridContent(gridA, setA, "A");
                    });

                    groupA.getChildren().add(saveSet);
                    groupA.getChildren().add(loadSet);
                    groupA.getChildren().add(clearSet);
                    groupA.getChildren().add(selectSet);
                }

                VBox groupB = new VBox();
                groupB.setSpacing(5);
                groupB.setAlignment(Pos.CENTER);
                {
                    final String fileNameB = "B17.txt";
                    Button saveSet = new Button("Зберегти множину B");
                    Button loadSet = new Button("Завантажити множину B");
                    Button clearSet = new Button("Очистити множину B");
                    RadioButton selectSet = new RadioButton("Форматування множини B");
                    selectSet.setOnAction(e -> setSetBeingFormatted(setB, gridB));
                    selectSet.setToggleGroup(selectedSetGroup);

                    saveSet.setOnAction(e -> saveSetToFile(setB, fileNameB));
                    loadSet.setOnAction(e -> {
                        loadSetFromFile(setB, fileNameB);
                        updateGridContent(gridB, setB, "B");
                    });
                    clearSet.setOnAction(e -> {
                        setB.clear();
                        updateGridContent(gridB, setB, "B");
                    });

                    groupB.getChildren().add(saveSet);
                    groupB.getChildren().add(loadSet);
                    groupB.getChildren().add(clearSet);
                    groupB.getChildren().add(selectSet);
                }

                VBox groupC = new VBox();
                groupC.setSpacing(5);
                groupC.setAlignment(Pos.CENTER);
                {
                    final String fileNameC = "C17.txt";
                    Button saveSet = new Button("Зберегти множину C");
                    Button loadSet = new Button("Завантажити множину C");
                    Button clearSet = new Button("Очистити множину C");
                    RadioButton selectSet = new RadioButton("Форматування множини C");
                    selectSet.setOnAction(e -> setSetBeingFormatted(setC, gridC));
                    selectSet.setToggleGroup(selectedSetGroup);

                    saveSet.setOnAction(e -> saveSetToFile(setC, fileNameC));
                    loadSet.setOnAction(e -> {
                        loadSetFromFile(setC, fileNameC);
                        updateGridContent(gridC, setC, "C");
                    });
                    clearSet.setOnAction(e -> {
                        setC.clear();
                        updateGridContent(gridC, setC, "C");
                    });

                    groupC.getChildren().add(saveSet);
                    groupC.getChildren().add(loadSet);
                    groupC.getChildren().add(clearSet);
                    groupC.getChildren().add(selectSet);
                }

                buttonsGroups.getChildren().add(groupA);
                buttonsGroups.getChildren().add(groupB);
                buttonsGroups.getChildren().add(groupC);
            }

            mainVbox.getChildren().add(topSection);
            mainVbox.getChildren().add(buttonsGroups);
            mainVbox.getChildren().add(infoLabel);
        }

        return mainVbox;
    }

    private StringBuilder createStringOfElements(Set<String> set) {
        StringBuilder result = new StringBuilder();

        if (set.size() > 0) {
            set.forEach(elem -> result.append(elem).append(", "));
            result.delete(result.length() - 2, result.length());
        }

        return result;
    }

    private Parent setupOperationsScene() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15, 85, 15, 35));
        vbox.setSpacing(10.0d);

        // TODO: REMOVE
//        setA.add("A1");
//        setA.add("A2");
//        setA.add("A3");
//        setB.add("A1");
//        setB.add("A2");
//        setB.add("B1");
//        setC.add("A1");
//        setC.add("A3");
//        setC.add("B1");

        // Prepping labels
        final int fontSize1 = 18;
        final int fontSize2 = 17;

        Label setsLabel = new Label(
                "Set A: " + createStringOfElements(setA) + ";\n" +
                        "Set B: " + createStringOfElements(setB) + ";\n" +
                        "Set C: " + createStringOfElements(setC) + ";"
        );
        setsLabel.setFont(Font.font(null, FontWeight.BOLD, fontSize1));

        Label abOperations = new Label(
                "A ∩ B: " + createStringOfElements(setIntersection(setA, setB)) + ";\n" +
                        "A ∪ B: " + createStringOfElements(setUnion(setA, setB)) + ";\n" +
                        "A \\ B: " + createStringOfElements(setDifference(setA, setB)) + ";\n" +
                        "A ∆ B: " + createStringOfElements(setSymmetricalDifference(setA, setB)) + ";"
        );
        abOperations.setFont(Font.font(null, FontWeight.NORMAL, fontSize2));

        final Set<String> everything = setUnion(setUnion(setA, setB), setC);
        final Set<String> aUnionB = setIntersection(setA, setB);
        final Set<String> aUnionBUnionC = setIntersection(aUnionB, setC);
        final Set<String> uDiffAUnionBUnionC = setDifference(everything, aUnionBUnionC);

        Label answer = new Label("Answer: U \\ (A ∩ B ∩ C): " + createStringOfElements(uDiffAUnionBUnionC) + ";");
        answer.setFont(Font.font(null, FontWeight.BOLD, fontSize2));

        Label intermediate = new Label("Intermediate steps:\n" +
                "U: (A ∪ B ∪ C): " + createStringOfElements(everything) + ";\n" +
                "(A ∩ B): " + createStringOfElements(aUnionB) + ";\n" +
                "(↑ ∩ C): " + createStringOfElements(aUnionBUnionC) + ";\n" +
                "(U \\ ↑): " + createStringOfElements(uDiffAUnionBUnionC) + ";"
        );
        intermediate.setFont(Font.font(null, FontWeight.NORMAL, fontSize2));

        // Adding labels
        vbox.getChildren().add(setsLabel);
        vbox.getChildren().add(abOperations);
        vbox.getChildren().add(answer);
        vbox.getChildren().add(intermediate);

        // Save button
        Button saveButton = new Button("Зберегти до файлу");
        final String fileNameD = "D17.txt";
        saveButton.setOnAction(e -> {
            saveSetToFile(uDiffAUnionBUnionC, fileNameD);
        });
        vbox.getChildren().add(saveButton);

        return vbox;
    }

    private Parent setupMainScene() {
        final int width = 950;

        // All labels
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 15, 10, 15));
        vBox.setPrefWidth(width);

        final int bigTextSize = 20;
        final int mainTextSize = 16;
        Label[] labels = new Label[6];
        labels[0] = new Label("Національний технічний університет України 'Київський політехнічний інститут'");
        labels[0].setAlignment(Pos.CENTER);
        labels[0].setFont(Font.font(null, FontWeight.BOLD, bigTextSize));

        labels[1] = new Label("Кафедра обчислювальної техніки");
        labels[1].setAlignment(Pos.CENTER);
        labels[1].setFont(Font.font(null, FontWeight.BOLD, bigTextSize));

        labels[2] = new Label("'Дискретна математика'");
        labels[2].setAlignment(Pos.CENTER);
        labels[2].setFont(Font.font(null, FontWeight.BOLD, bigTextSize));

        labels[3] = new Label("Лабораторна робота №1");
        labels[3].setAlignment(Pos.CENTER);
        labels[3].setFont(Font.font(null, FontWeight.NORMAL, mainTextSize));

        labels[4] = new Label("Тема: 'Множини: основні властивості та операції над ними, діаграми Венна'.\n" +
                "Мета: Вивчити основні аксіоми, закони і теореми теорії множин, навчитися застосовувати їх на практиці.\n" +
                "Виконати наступні операції над множинами: доповнення множин; об'єднання, перетин, різниця, симетрична різниця.\n" +
                "Завдання: Написати программу для виконання даних операцій над множинами.");
        labels[4].setAlignment(Pos.CENTER);
        labels[4].setFont(Font.font(null, FontWeight.NORMAL, mainTextSize));

        labels[5] = new Label("Введіть номер залікової книжки:");
        labels[5].setAlignment(Pos.CENTER);
        labels[5].setFont(Font.font(null, FontWeight.NORMAL, mainTextSize + 2));

        vBox.getChildren().addAll(labels);

        // Bottom line
        Label infoLabel = new Label("");
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setFont(Font.font(null, FontWeight.BOLD, mainTextSize));
        infoLabel.setPrefWidth(width);

        // I-ono how to call it
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);

        TextField textField = new TextField();
        textField.setMinWidth(100);

        Button button = new Button("Ввід");
        button.setOnAction(e -> {
            taskCode = Integer.parseInt(textField.getText()) % 30 + 1;
            infoLabel.setText("Варіант " + taskCode);
        });

        hBox.getChildren().add(textField);
        hBox.getChildren().add(button);
        vBox.getChildren().add(hBox);

        // Menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Основне меню");

        MenuItem inputMenuItem = new MenuItem("Ввід данних");
        inputMenuItem.setOnAction(e -> processInputWindow());
        menu.getItems().add(inputMenuItem);

        MenuItem operationsMenuItem = new MenuItem("Операції");
        operationsMenuItem.setOnAction(e -> processOperationsWindow());
        menu.getItems().add(operationsMenuItem);

        menuBar.getMenus().add(menu);

        // Parent layout
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setTop(menuBar);
        borderPane.setBottom(infoLabel);

        return borderPane;
    }


    // Operations over sets
    public Set<String> setUnion(Set<String> set1, Set<String> set2) {
        Set<String> set = new HashSet<>();

        set1.forEach(set::add);
        set2.forEach(set::add);

        return set;
    }

    public Set<String> setDifference(Set<String> set1, Set<String> set2) {
        Set<String> set = new HashSet<>();

        set1.forEach(elem -> {
            if (!set2.contains(elem)) {
                set.add(elem);
            }
        });

        return set;
    }

    public Set<String> setIntersection(Set<String> set1, Set<String> set2) {
        Set<String> set = new HashSet<>();

        set1.forEach(elem -> {
            if (set2.contains(elem)) {
                set.add(elem);
            }
        });

        return set;
    }

    public Set<String> setSymmetricalDifference(Set<String> set1, Set<String> set2) {
        Set<String> set = new HashSet<>();

        set1.forEach(elem -> {
            if (!set2.contains(elem)) {
                set.add(elem);
            }
        });
        set2.forEach(elem -> {
            if (!set1.contains(elem)) {
                set.add(elem);
            }
        });

        return set;
    }
}
