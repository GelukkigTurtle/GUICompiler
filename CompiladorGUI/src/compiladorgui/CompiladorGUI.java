/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorgui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPaneBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author turtle
 */
public class CompiladorGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/compiladorgui/Compilador.fxml"));
        } catch (Exception e) {
            root = AnchorPaneBuilder.create().id("mainWindow").prefWidth(800).prefHeight(600).build();
        }
        Scene scene = new Scene(root);

        primaryStage.setTitle("Compildador - creado por Freddy Ayala");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
