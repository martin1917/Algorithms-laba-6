package controllers;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

import Utils.FileWrapper;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import logic.SolutionWithData;

public class AppController {

    @FXML
    private Button btnSortTwo;

    @FXML
    private TableView<List<Integer>> tableTwo;

    @FXML
    private Button btnSortOne;

    @FXML
    private TableView<List<Integer>> tableOne;

    @FXML
    void initialize() {
        btnSortTwo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            List<List<Integer>> allSteps = null;
            try {
                FileWrapper file = fillFile();
                allSteps = SolutionWithData.externalSortTwoStageSimpleMerge(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fillTable(tableTwo, allSteps);
        });

        btnSortOne.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            List<List<Integer>> allSteps = null;
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

    private void fillTable(TableView<List<Integer>> table, List<List<Integer>> allSteps) {
        table.getColumns().clear();
        ObservableList<List<Integer>> data = FXCollections.observableArrayList(allSteps);
        for(int i = 0; i < allSteps.get(0).size(); i++) {
            TableColumn tc = new TableColumn<>((i+1) + "");
            tc.setSortable(false);
            int number = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<Integer>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(CellDataFeatures<List<Integer>, Integer> arg) {
                    if (number >= arg.getValue().size()) {
                        return new SimpleIntegerProperty(-1).asObject();    
                    } else {
                        return new SimpleIntegerProperty(arg.getValue().get(number)).asObject();
                    }
                }
            });
            table.getColumns().add(tc);
        }
        table.setItems(data);
    }
}
