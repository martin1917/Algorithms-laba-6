import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws IOException  {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/App.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
         
        stage.setTitle("Внешняя сортировка");
        stage.sizeToScene();
        stage.setResizable(false);

        stage.show();
    }
}