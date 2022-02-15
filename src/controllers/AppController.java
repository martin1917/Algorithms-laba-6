package controllers;

import java.util.List;
import java.io.IOException;

import Utils.FileWrapper;
import data.DetailsSort;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import logic.SolutionWithData;

public class AppController {

    @FXML
    private Button btnSortTwo;

    @FXML
    private TableView<List<String>> tableTwo;

    @FXML
    private Button btnSortOne;

    @FXML
    private TableView<List<String>> tableOne;

    @FXML
    private Spinner<Integer> spinnerCount;

    @FXML
    private Button btnCompare;

    @FXML
    private TableView<DetailsSort> tableInfo;

    @FXML
    private ComboBox<String> namesFiles;

    @FXML
    private TextField indexStart;

    @FXML
    private TextField indexEnd;

    @FXML
    private Button btnWrite;

    @FXML
    void initialize() {
        setMouseEvent();
        spinnerCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 200, 5, 15));
        namesFiles.setItems(FXCollections.observableArrayList(List.of("A", "B", "C")));
        namesFiles.setValue("A");
    }

    private void setMouseEvent() {
        btnSortTwo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            List<List<String>> allSteps = null;
            try {
                FileWrapper file = fillFile();
                allSteps = SolutionWithData.externalSortTwoStageSimpleMerge(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fillTable(tableTwo, allSteps);
        });

        btnSortOne.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            List<List<String>> allSteps = null;
            try {
                FileWrapper file = fillFile();
                allSteps = SolutionWithData.externalSortOneStageSimpleMerge(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fillTable(tableOne, allSteps);
        });
    }
    
    private FileWrapper fillFile() throws IOException {
        FileWrapper file = new FileWrapper("other//data.txt");
        file.open();
        file.fillByNumbers(15);
        file.close();
        return file;
    }

    private void fillTable(TableView<List<String>> table, List<List<String>> allSteps) {
        table.getColumns().clear();
        ObservableList<List<String>> data = FXCollections.observableArrayList(allSteps);
        for(int i = 0; i < allSteps.get(0).size(); i++) {
            TableColumn tc = new TableColumn<>((i+1) + "");
            tc.setSortable(false);
            int number = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<String>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(CellDataFeatures<List<String>, String> arg) {
                    if (number >= arg.getValue().size()) {
                        return new SimpleStringProperty("");    
                    } else {
                        return new SimpleStringProperty(arg.getValue().get(number));
                    }
                }
            });
            table.getColumns().add(tc);
        }
        table.setItems(data);
    }
}
