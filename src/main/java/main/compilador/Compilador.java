package main.compilador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Compilador extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader root = new FXMLLoader(Compilador.class.getResource("mainscreen.fxml"));
        Scene scene = new Scene(root.load(), 768, 420);

        stage.setTitle("Compilador");
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.resizableProperty().set(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
