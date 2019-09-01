package ventanas;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import datos.Cajero;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import restaurantcity.Usuario;

public class LoginController implements Initializable
{   
    @FXML
    private AnchorPane anchorRoot;
    @FXML
    private StackPane parentContainer;
    @FXML
    private JFXTextField usuario;
    @FXML
    private JFXPasswordField contraseña;
    @FXML
    private Label advertencia;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    //Finaliza el programa
    @FXML
    private void finalizarPrograma(ActionEvent evento)
    {
        System.exit(0);
    }
    
    @FXML
    private void verificarDatos(ActionEvent evento)
    {
        //Verifica si los datos ingresados coinciden con una cuenta
        if (!Usuario.iniciarSesion(usuario.getText(),contraseña.getText()))
        {
            advertencia.setText("Los datos ingresados no son válidos.");
            return;
        }
        
        //Verifica que privilegios tiene la cuenta, si es administrador carga la ventana MenuAdministrador,
        //en caso contrario, carga la ventana MenuVentas
        if (Usuario.isAdmin(usuario.getText()))
            cargarMenuAdministrador();
        else
            cargarMenuVentas();
    }
    
    //Carga la ventana del MenuAdministrador con una animación de barrido horizontal
    private void cargarMenuAdministrador()
    {
        try
        {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/ventanas/MenuAdministrador.fxml"));
            Parent root=(Parent) loader.load();
            Scene scene=anchorRoot.getScene();

            //Obtiene el controlador de MenuAdministrador para inicializar la clase Cajero y enviarla por parámetros
            MenuAdministradorController controlador=(MenuAdministradorController) loader.getController();
            controlador.inicializar(new Cajero(usuario.getText()));

            //Obtiene el ancho y largo del panel para realizar la transición horizontal hacia la siguiente escena
            root.translateXProperty().set(scene.getWidth());
            parentContainer.getChildren().add(root);
            Timeline timeline=new Timeline();
            KeyValue value=new KeyValue(root.translateXProperty(),0,Interpolator.EASE_IN);
            KeyFrame frame=new KeyFrame(Duration.seconds(1),value);
            timeline.getKeyFrames().add(frame);
            timeline.setOnFinished((ActionEvent evento) -> {
                parentContainer.getChildren().remove(anchorRoot);
            });
            timeline.play();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Carga la ventana del MenuVentas con una animación de barrido horizontal
    private void cargarMenuVentas()
    {
        try
        {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/ventanas/MenuVentas.fxml"));
            Parent root=(Parent) loader.load();
            Scene scene=anchorRoot.getScene();

            //Obtiene el controlador de MenuVentas para inicializar la clase Cajero y enviarla por parámetros
            MenuVentasController controlador=(MenuVentasController) loader.getController();
            controlador.inicializar(new Cajero(usuario.getText()));

            //Obtiene el ancho y largo del panel para realizar la transición horizontal hacia la siguiente escena
            root.translateXProperty().set(scene.getWidth());
            parentContainer.getChildren().add(root);
            Timeline timeline=new Timeline();
            KeyValue value=new KeyValue(root.translateXProperty(),0,Interpolator.EASE_IN);
            KeyFrame frame=new KeyFrame(Duration.seconds(1),value);
            timeline.getKeyFrames().add(frame);
            timeline.setOnFinished((ActionEvent evento) -> {
                parentContainer.getChildren().remove(anchorRoot);
            });
            timeline.play();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Muestra una alerta con toda la información detallada de la excepción
    private void alertaExcepcion(Exception excepcion)
    {
        Alert alerta=new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Alerta Excepción");
        alerta.setHeaderText(excepcion.getMessage());
        alerta.setContentText(excepcion.toString());

        //Se imprime todo el stacktrace de la excepción en un cajón expandible de texto
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        excepcion.printStackTrace(pw);
        TextArea texto=new TextArea(sw.toString());
        texto.setEditable(false);
        texto.setWrapText(true);
        texto.setMaxWidth(Double.MAX_VALUE);
        texto.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(texto,Priority.ALWAYS);
        GridPane.setHgrow(texto,Priority.ALWAYS);
        GridPane contenido=new GridPane();
        contenido.setMaxWidth(Double.MAX_VALUE);
        contenido.add(new Label("El Stacktrace de la excepción fue:"),0,0);
        contenido.add(texto,0, 1);

        //Se ajusta el texto en la alerta y se muestra por pantalla
        alerta.getDialogPane().setExpandableContent(contenido);
        alerta.showAndWait();
    }
}
