package controllers;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import Utils.FileWrapper;
import data.DetailsSort;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import logic.SolutionDetails;
import logic.SolutionWithData;

public class AppController {

    @FXML private Button btnSortTwo;
    @FXML private TableView<List<String>> tableTwo;

    @FXML private Button btnSortOne;
    @FXML private TableView<List<String>> tableOne;

    @FXML private Spinner<Integer> spinnerCount;
    @FXML private Button btnCompare;
    @FXML private TableView<DetailsSort> tableInfo;

    @FXML private ComboBox<String> namesFiles;

    @FXML private TextField indexStart;
    @FXML private TextField indexEnd;
    @FXML private Button btnWrite;
    
    @FXML private TableView<List<String>> tableInterval;
    @FXML private TableColumn<DetailsSort, String> columnName;
    @FXML private TableColumn<DetailsSort, Double> columnTime;
    @FXML private TableColumn<DetailsSort, Double> columnRead;
    @FXML private TableColumn<DetailsSort, Double> columnWrite;
    @FXML private TableColumn<DetailsSort, Double> columnCompare;
    
    @FXML
    void initialize() {
        setMouseEvent();
        setSettingsForColumns();
        setSettingsForComboBox();
        spinnerCount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 200, 5, 15));
    }

    private void setSettingsForComboBox() {
        namesFiles.setItems(FXCollections.observableArrayList(List.of("Начальный файл", "После двухфазной", "После однофазной")));
        namesFiles.setValue("Начальный файл");
    }

    private void setSettingsForColumns() {
        columnName.setCellValueFactory(new PropertyValueFactory<DetailsSort, String>("name"));
        columnTime.setCellValueFactory(new PropertyValueFactory<DetailsSort, Double>("time"));
        columnRead.setCellValueFactory(new PropertyValueFactory<DetailsSort, Double>("countRead"));
        columnWrite.setCellValueFactory(new PropertyValueFactory<DetailsSort, Double>("countWrite"));
        columnCompare.setCellValueFactory(new PropertyValueFactory<DetailsSort, Double>("countCompare"));
    }

    //обработчики кнопок
    private void setMouseEvent() {        
        btnSortTwo.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> sortTwo());
        btnSortOne.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> sortOne());
        btnCompare.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> printInfoInTable());
        btnWrite.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> printNumbersInInterval());
    }

    //двухфазная сортировка
    private void sortTwo() {
        List<List<String>> allSteps = null;
        try {
            FileWrapper file = fillFile(15);
            allSteps = SolutionWithData.externalSortTwoStageSimpleMerge(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fillTable(tableTwo, allSteps);
    }

    //однофазная сортировка
    private void sortOne() {
        List<List<String>> allSteps = null;
        try {
            FileWrapper file = fillFile(15);
            allSteps = SolutionWithData.externalSortOneStageSimpleMerge(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fillTable(tableOne, allSteps);
    }

    //вывод информации по сортировкам
    private void printInfoInTable() {
        int count = spinnerCount.getValue();
        try {
            FileWrapper file = fillFile(count);
            DetailsSort details1 = DetailsSort.createByDetails("двухфазная", SolutionDetails.sortTwoStage(file));
            DetailsSort details2 = DetailsSort.createByDetails("однофазная", SolutionDetails.sortOneStage(file));

            tableInfo.getItems().clear();
            tableInfo.setItems(FXCollections.observableArrayList(List.of(details1, details2)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //вывод чисел в интервале
    private void printNumbersInInterval() {
        int start;
        try{
            start = Integer.parseInt(indexStart.getText());
        } catch(NumberFormatException ex) {
            showErrorAlert("Левая граница интервала должна быть неотрицательным числом", "Введите неотрицательное число");
            return;
        }

        int end;
        try{
            end = Integer.parseInt(indexEnd.getText());
        } catch(NumberFormatException ex) {
            showErrorAlert("Правая граница интервала должна быть неотрицательным числом", "Введите неотрицательное число");
            return;
        }

        if(start > end) {
            showErrorAlert("Левая граница должна быть не больше правой границы", "Проверьте корректность интервала");
            return;
        }

        if(tableInfo.getItems().size() == 0) {
            showErrorAlert("Нет данных", "Сначала необходимо сравнить сортировки");
            return;
        }

        List<List<String>> allNumbers = new ArrayList<List<String>>();

        try {
            FileWrapper file = new FileWrapper("other//data.txt");
            FileWrapper file1 = new FileWrapper("other//files1//a.txt");
            FileWrapper file2 = new FileWrapper("other//files2//a.txt");

            file.open();
            file1.open();
            file2.open();

            int size = file.getSize();
            if(end >= size || start >= size) {
                showErrorAlert("В файле всего " + size+ " чисел", "Проверьте корректность интервала");
                return;
            }

            List<String> nums = file.getNumbersInInterval(start, end);
            nums.add(0, "a");
            List<String> nums1 = file1.getNumbersInInterval(start, end);
            nums1.add(0, "a1");
            List<String> nums2 = file2.getNumbersInInterval(start, end);
            nums2.add(0, "a2");

            allNumbers.add(nums);
            allNumbers.add(nums1);
            allNumbers.add(nums2);

            file.close();
            file1.close();
            file2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fillTable(tableInterval, allNumbers);
    }

    //диалог с ошибкой
    private void showErrorAlert(String headerText, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    //заполнить файл числами
    private FileWrapper fillFile(int count) throws IOException {
        FileWrapper file = new FileWrapper("other//data.txt");
        file.open();
        file.fillByNumbers(count);
        file.close();
        return file;
    }

    //заполнение таблицы
    private void fillTable(TableView<List<String>> table, List<List<String>> allSteps) {
        table.getColumns().clear();
        ObservableList<List<String>> data = FXCollections.observableArrayList(allSteps);
        for(int i = 0; i < allSteps.get(0).size(); i++) {
            TableColumn tc = new TableColumn<>("");
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
