/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladorgui;

import static compiladorgui.CompiladorController.MensajeDeConsola;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class ErrorController {
    
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private HBox buttonBox;
    @FXML
    private Button continueButton;
    @FXML
    private TextArea errorMessage;
    @FXML
    private Button quitButton;
    
    @FXML
    void continueAction(ActionEvent event) {
        quitButton.getScene().getWindow().hide();
        MensajeDeConsola = "";
    }
    
    @FXML
    void quitAction(ActionEvent event) {
        System.exit(1);
    }
    
    @FXML
    void initialize() {


        //  exception.printStackTrace(new PrintWriter(error));
//        if(MensajeDeConsola.isEmpty() || MensajeDeConsola.trim().isEmpty()){
//            MensajeDeConsola = "Compilado exitosamente 0 Errores";
//        }
        errorMessage.setText(CompiladorController.MensajeDeConsola);
        
    }
}
