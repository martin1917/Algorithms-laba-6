import java.io.IOException;

import Utils.FileWrapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.SolutionDetails;

public class App /*extends Application*/ {
    public static void main(String[] args) throws IOException  {
        // launch(args);
        FileWrapper file = new FileWrapper("other//data.txt");
        file.setCount(9);
        // file.open();
        // file.fillByNumbers(6);
        // file.close();

        double[] details = SolutionDetails.externalSortOneStageSimpleMerge(file);
        System.out.println();
    }

    // @Override
    // public void start(Stage stage) throws Exception {
    //     Parent root = FXMLLoader.load(getClass().getResource("resources/App.fxml"));
    //     Scene scene = new Scene(root);

    //     stage.setScene(scene);
         
    //     stage.setTitle("Внешняя сортировка");
    //     stage.sizeToScene();
    //     stage.setResizable(false);

    //     stage.show();
    // }
}