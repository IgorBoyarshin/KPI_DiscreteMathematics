package lab2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Igorek on 03-Apr-16 at 6:18 PM.
 */
public class SetInputWindow implements Initializable {

    @FXML
    private ListView<HBox> setAListMale;
    @FXML
    private ListView<HBox> setAListFemale;
    @FXML
    private ListView<HBox> setBListMale;
    @FXML
    private ListView<HBox> setBListFemale;

    private static Set<Person> setA;
    private static Set<Person> setB;

    private static Stage window;

    private void updateSetFromCheckBoxes(ListView<HBox> listViewMale, ListView<HBox> listViewFemale, Set<Person> set) {
        set.clear();

        for (HBox hbox : listViewMale.getItems()) {
            CheckBox checkBox = (CheckBox) hbox.getChildren().get(2);
            if (checkBox.isSelected()) {
                String name = ((Label) hbox.getChildren().get(0)).getText();
                set.add(new Person(name, true)); // if not in male => female
            }
        }

        for (HBox hbox : listViewFemale.getItems()) {
            CheckBox checkBox = (CheckBox) hbox.getChildren().get(2);
            if (checkBox.isSelected()) {
                String name = ((Label) hbox.getChildren().get(0)).getText();
                set.add(new Person(name, false)); // if not in male => female
            }
        }
    }

    @FXML
    public void loadSetAFromFile() {
        loadSetFromFile(setA, "src//lab2//A.txt");
        updateListViewCheckBoxes(setAListMale, setA);
        updateListViewCheckBoxes(setAListFemale, setA);
    }

    @FXML
    public void loadSetBFromFile() {
        loadSetFromFile(setB, "src//lab2//B.txt");
        updateListViewCheckBoxes(setBListMale, setB);
        updateListViewCheckBoxes(setBListFemale, setB);
    }

    @FXML
    public void saveSetAToFile() {
        updateSetFromCheckBoxes(setAListMale, setAListFemale, setA);
        saveSetToFile(setA, "src//lab2//A.txt");
    }

    @FXML
    public void saveSetBToFile() {
        updateSetFromCheckBoxes(setBListMale, setBListFemale, setB);
        saveSetToFile(setB, "src//lab2//B.txt");
    }

    @FXML
    public void clearSetA() {
        setA.clear();
        updateListViewCheckBoxes(setAListFemale, setA);
        updateListViewCheckBoxes(setAListMale, setA);
    }

    @FXML
    public void clearSetB() {
        setB.clear();
        updateListViewCheckBoxes(setBListFemale, setB);
        updateListViewCheckBoxes(setBListMale, setB);
    }

    private void saveSetToFile(Set<Person> set, String fileName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            set.forEach(p -> out.println((p.isMale ? "Ч" : "Ж") + " " + p.name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSetFromFile(Set<Person> set, String fileName) {
        set.clear();

        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            while (in.ready()) {
                String input = in.readLine();
                set.add(new Person(input.split(" ")[1], input.split(":")[0].charAt(0) == 'Ч'));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Populates the given sets with values inputted by the user
    public void process(Set<Person> _setA, Set<Person> _setB) throws IOException {
        setA = _setA;
        setB = _setB;

        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(Event::consume);
        window.setTitle("Set input");
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("setInputWindow.fxml"))));
        window.showAndWait();
    }

    private void populateListViewWithNames(ListView<HBox> listView, List<String> names, Set<Person> selected) {
        ObservableList<HBox> items = FXCollections.observableArrayList();

        for (String name : names) {
            HBox element = new HBox();

            Label label = new Label(name);

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(selected.stream().anyMatch(p -> p.name.equals(name)));

            element.getChildren().addAll(label, region, checkBox);

            items.add(element);
        }

        listView.setItems(items);
    }

    private void updateListViewCheckBoxes(ListView<HBox> listView, Set<Person> set) {
        for (HBox hbox : listView.getItems()) {
            String name = ((Label) hbox.getChildren().get(0)).getText();
            CheckBox checkBox = (CheckBox) hbox.getChildren().get(2);

            checkBox.setSelected(set.stream().anyMatch(p -> p.name.equals(name)));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> maleNames = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("src//lab2//males.txt"))) {
            while (in.ready()) {
                maleNames.add(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> femaleNames = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("src//lab2//females.txt"))) {
            while (in.ready()) {
                femaleNames.add(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        populateListViewWithNames(setAListMale, maleNames, setA);
        populateListViewWithNames(setAListFemale, femaleNames, setA);
        populateListViewWithNames(setBListMale, maleNames, setB);
        populateListViewWithNames(setBListFemale, femaleNames, setB);
    }

    @FXML
    private void exit() {
        updateSetFromCheckBoxes(setAListMale, setAListFemale, setA);
        updateSetFromCheckBoxes(setBListMale, setBListFemale, setB);

        window.close();
    }



//    public void magic() {
//        Set<Person> a = new HashSet<>();
//        loadSetFromFile(a, "src//lab2//A.txt");
//        Set<Person> b = new HashSet<>();
//        loadSetFromFile(b, "src//lab2//B.txt");
//
//        List<Person> people = new ArrayList<>();
//        people.addAll(a);
//        people.addAll(b);
//
//        try (PrintWriter out = new PrintWriter(new FileWriter("src//lab2//males.txt"))) {
//            people.stream()
//                    .filter(p -> p.isMale)
//                    .map(p -> p.name)
//                    .distinct()
//                    .sorted().collect(Collectors.toList())
//                    .forEach(out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try (PrintWriter out = new PrintWriter(new FileWriter("src//lab2//females.txt"))) {
//            people.stream()
//                    .filter(p -> !p.isMale)
//                    .map(p -> p.name)
//                    .distinct()
//                    .sorted().collect(Collectors.toList())
//                    .forEach(out::println);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
