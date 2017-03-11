package lab2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Igorek on 03-Apr-16 at 6:17 PM.
 */
public class OperationsWindow implements Initializable {

//    private Set<Relation> relationR;
//    private Set<Relation> relationS;

    private static boolean[][] relationS;
    private static boolean[][] relationR;
    private static boolean[][] relationSUR;
    private static boolean[][] relationR1;
    private static List<String> relationSxR;
    private static List<String> relationSxR1;

    private static List<Person> setA;
    private static List<Person> setB;

    @FXML
    private GridPane gridS;
    @FXML
    private GridPane gridR;
    @FXML
    private GridPane gridResult;
    @FXML
    private ListView<Label> listResult;
    @FXML
    private ToggleGroup relationOperation1;
    @FXML
    private ToggleGroup relationOperation2;
    @FXML
    private Label infoLabel;

    // Not supposed to change sets
    public void process(Set<Person> _setA, Set<Person> _setB) throws IOException {
        setA = _setA.stream().collect(Collectors.toList());
        setB = _setB.stream().collect(Collectors.toList());

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
//        window.setOnCloseRequest(Event::consume);
        window.setTitle("Operations");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("operationsWindow.fxml"))));
        window.showAndWait();
    }

    private void populateList(ListView<Label> listView, List<String> content) {
        ObservableList<Label> items = FXCollections.observableArrayList();
        items.addAll(content.stream().map(Label::new).collect(Collectors.toList()));
        listView.setItems(items);
    }

    private void populateGrid(GridPane grid, String title, boolean[][] relationField, boolean transposed) {
        grid.getChildren().clear();
        grid.add(new Label(title), 0, 0);
        int index;

        index = 1;
        for (Person person : (transposed ? setB : setA)) {
            Label label = new Label(person.name);
            label.setTextFill(person.isMale ? new Color(0.0, 0.0, 0.8, 1.0) : new Color(0.8, 0.0, 0.0, 1.0));
            grid.add(label, 0, index);
            index++;
        }

        index = 1;
        for (Person person : (transposed ? setA : setB)) {
            Label label = new Label(person.name);
            label.setTextFill(person.isMale ? new Color(0.0, 0.0, 0.8, 1.0) : new Color(0.8, 0.0, 0.0, 1.0));
            grid.add(label, index, 0);
            index++;
        }

        for (int row = 0; row < (transposed ? setB : setA).size(); row++) {
            for (int column = 0; column < (transposed ? setA : setB).size(); column++) {
                grid.add(new Label(relationField[row][column] ? "1" : "0"), column + 1, row + 1);
            }
        }
    }

    private void createRelations(boolean[][] relationS, boolean[][] relationR, List<Person> setA, List<Person> setB) {
        int[] girlsInA = new int[(int) setA.stream().filter(p -> !p.isMale).count()];
        int[] boysInB = new int[(int) setB.stream().filter(p -> p.isMale).count()];

        // Init arrays that contain indices of people we work with
        int index = 0;
        int count = 0;
        for (Person p : setA) {
            if (!p.isMale) {
                girlsInA[index] = count;
                index++;
            }

            count++;
        }

        index = 0;
        count = 0;
        for (Person p : setB) {
            if (p.isMale) {
                boysInB[index] = count;
                index++;
            }

            count++;
        }

        // Starting now
        // Wife first - relationR
        int currentBoy = 0; // amount of boys used already
        for (int indexGirl : girlsInA) {
            if (currentBoy < boysInB.length) { // if we have boys left to marry
                relationR[indexGirl][boysInB[currentBoy]] = true;
                currentBoy++;
            } else {
                break;
            }
        }

        // Now tesha - relationS
        for (int indexGirl : girlsInA) {
            int timesTesha = 0; // how many times?
            final int maxTimesTesha = 2;

            for (int indexBoy : boysInB) {
                if (timesTesha >= maxTimesTesha) {
                    break;
                }

                if (!relationR[indexGirl][indexBoy]) { // if not wife
                    boolean anotherTeshaExists = false;
                    for (int possibleAnotherTesha : girlsInA) {
                        if (relationS[possibleAnotherTesha][indexBoy]) {
                            anotherTeshaExists = true;
                            break;
                        }
                    }
                    if (anotherTeshaExists) {
                        break;
                    }

                    // Try
                    relationS[indexGirl][indexBoy] = true;
                    if (cycleExist(relationS, relationR, girlsInA, boysInB, indexGirl, indexBoy)) {
                        // if we break anything then revert the changes
                        relationS[indexGirl][indexBoy] = false;
                    } else {
                        // if we're good, go on
                        timesTesha++;
                    }
                }
            }
        }
    }

    // Checks if there is a cycle for (row,column) in relation R
    // row gives us the girl, column needed only to make sure we pick the right boy at once and not waste time
    // startRow is needed for recursion - when to exit
    // At first call startRow must be equal to row
    private boolean cycleExist(boolean[][] s, boolean[][] r, int[] girls, int[] boys, int startRow, int column) {
//        System.out.println("Called for " + setA.get(girls[startRow]).name + " and " + setB.get(boys[column]).name);
        // find wife of this 'column'(zyat)
        int wife = -1;
        for (int possibleWife : girls) {
            if (r[possibleWife][column]) { // if found his wife
                // Then she's the new current tesha
                wife = possibleWife;
                break;
            }
        }

        if (wife == -1) { // if no wife found
            return false; // cycle broke
        }
        // Exit for recursion
        // The only case when the cycle exist(the second 'return true' is from deeper recursion)
        if (wife == startRow) { // we came back to were we started
            // So the cycle does exist
            return true;
        }

        // Prepare the new recursion call

        // Find the zyat now
        for (int possibleZyat : boys) {
            // We will check all of them for possible cycles
            if (s[wife][possibleZyat]) { // found him
                // TODO::::
                // not SR but wife
                if (cycleExist(s, r, girls, boys, startRow, possibleZyat)) {
                    return true;
                }
            }
        }

        // There were no cycles for this wife || there were no connections at all
        // So no cycle
        return false;
    }

    private boolean[][] createRelationSUR(boolean[][] relationS, boolean[][] relationR) {
        boolean[][] result = new boolean[setA.size()][setB.size()];

        for (int row = 0; row < setA.size(); row++) {
            for (int column = 0; column < setB.size(); column++) {
                result[row][column] = relationS[row][column] || relationR[row][column];
            }
        }

        return result;
    }

    private boolean[][] createRelationR1(boolean[][] relationR) {
        boolean[][] result = new boolean[setB.size()][setA.size()];

        for (int row = 0; row < setA.size(); row++) {
            for (int column = 0; column < setB.size(); column++) {
                result[column][row] = relationR[row][column];
            }
        }

        return result;
    }

    private List<String> createSxR(boolean[][] relationS, boolean[][] relationR) {
        List<String> result = new ArrayList<>();
        List<String> r = new ArrayList<>();

        for (int row = 0; row < setA.size(); row++) {
            for (int column = 0; column < setB.size(); column++) {
                if (relationR[row][column]) {
                    r.add("<" + setA.get(row).name + ", " + setB.get(column).name + ">");
                }
            }
        }

        for (int row = 0; row < setA.size(); row++) {
            for (int column = 0; column < setB.size(); column++) {
                if (relationS[row][column]) {
                    for (String fromR : r) {
                        result.add("("
                                + "<" + setA.get(row).name + ", " + setB.get(column).name + ">, "
                                + fromR
                                + ")"
                        );
                    }
                }
            }
        }

        return result;
    }

    private List<String> createSxR1(boolean[][] relationS, boolean[][] relationR) {
        List<String> result = new ArrayList<>();
        List<String> r1 = new ArrayList<>();

        for (int row = 0; row < setA.size(); row++) {
            for (int column = 0; column < setB.size(); column++) {
                if (relationR[row][column]) {
                    r1.add("<" + setB.get(column).name + ", " + setA.get(row).name + ">");
                }
            }
        }

        for (int row = 0; row < setA.size(); row++) {
            for (int column = 0; column < setB.size(); column++) {
                if (relationS[row][column]) {
                    for (String fromR : r1) {
                        result.add("("
                                + "<" + setA.get(row).name + ", " + setB.get(column).name + ">, "
                                + fromR
                                + ")"
                        );
                    }
                }
            }
        }

        return result;
    }

    private void printInfoInSFor(int rRow, int rColumn) {
        infoLabel.setText(setA.get(rRow).name + " is " + (relationS[rRow][rColumn] ? "" : "not ")
                + "tesha of " + setB.get(rColumn).name);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO: remove
        setA.clear();
        setB.clear();
        setA.add(new Person("Kate", false));
        setA.add(new Person("Margy", false));
        setA.add(new Person("Jinny", false));
        setA.add(new Person("Cristy", false));
        setA.add(new Person("Igor", true));
        setB.add(new Person("Robert", true));
        setB.add(new Person("Bob", true));
        setB.add(new Person("Jack", true));
        setB.add(new Person("Marta", false));

        relationS = new boolean[setA.size()][setB.size()];
        relationR = new boolean[setA.size()][setB.size()];

        // Data creation
        createRelations(relationS, relationR, setA, setB);
        relationSUR = createRelationSUR(relationS, relationR);
        relationR1 = createRelationR1(relationR);
        relationSxR = createSxR(relationS, relationR);
        relationSxR1 = createSxR1(relationS, relationR);

        // Init
        populateGrid(gridS, "aSb", relationS, false);
        populateGrid(gridR, "aRb", relationR, false);
        populateGrid(gridResult, "", relationSUR, false); // the default chosen radio
        populateList(listResult, relationSxR); // the default chosen radio

        // Radio buttons
        relationOperation1.getToggles().get(0).setUserData("union");
        relationOperation1.getToggles().get(1).setUserData("r1");
        relationOperation1.selectedToggleProperty().addListener((v, o, n) -> {
            switch (n.getUserData().toString()) {
                case "union":
                    populateGrid(gridResult, "", relationSUR, false);
                    break;
                case "r1":
                    populateGrid(gridResult, "", relationR1, true);
                    break;
            }
        });

        relationOperation2.getToggles().get(0).setUserData("sr");
        relationOperation2.getToggles().get(1).setUserData("sr1");
        relationOperation2.selectedToggleProperty().addListener((v, o, n) -> {
            switch (n.getUserData().toString()) {
                case "sr":
                    populateList(listResult, relationSxR);
                    break;
                case "sr1":
                    populateList(listResult, relationSxR1);
                    break;
            }
        });

        // Adding functionality to gridR
        gridR.addEventFilter(MouseEvent.MOUSE_PRESSED,
                e -> gridR.getChildren().stream()
                        .filter(node -> node instanceof Label)
                        .filter(node -> node.getBoundsInParent().contains(
                                e.getSceneX() - gridR.localToScene(node.getBoundsInLocal()).getMinX(),
                                e.getSceneY() - gridR.localToScene(node.getBoundsInLocal()).getMinY()))
                        .filter(node -> (GridPane.getRowIndex(node) > 0 && GridPane.getColumnIndex(node) > 0))
                        .forEach(node -> printInfoInSFor(GridPane.getRowIndex(node) - 1, GridPane.getColumnIndex(node) - 1))
        );
    }
}
