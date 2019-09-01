package ventanas;

import com.jfoenix.controls.JFXButton;
import datos.Cajero;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javax.mail.MessagingException;
import ventanas.menuAdministrativo.AdministrarAlimentosController;
import ventanas.menuAdministrativo.AdministrarBebestiblesController;
import ventanas.menuAdministrativo.AdministrarIngredientesController;
import ventanas.menuAdministrativo.AdministrarMesasController;
import ventanas.menuAdministrativo.ReportesController;

public class MenuAdministradorController implements Initializable
{   
    @FXML
    private AnchorPane panel;
    @FXML
    private JFXButton botonGuardar;
    
    private Cajero sistema;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}    
    
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
    }
    
    //Guarda los cambios en los archivos de planilla
    @FXML
    private void guardarCambios(ActionEvent event) throws MessagingException
    {
        //sistema.guardarDatos();
        botonGuardar.setDisable(true);
        
        /*
        AnchorPane caja=new AnchorPane();
        ProgressIndicator indicador=new ProgressIndicator();
        Label texto=new Label("Enviando correo ...");
        texto.setFont(Font.font("Liberation Sans", FontWeight.BOLD,25));
        texto.setTextFill(Color.WHITE);
        texto.setLayoutX(90);
        texto.setLayoutY(20);
        indicador.setMinSize(120,120);
        indicador.setLayoutX(140);
        indicador.setLayoutY(60);
        caja.setStyle("-fx-background-color: #222222;");
        caja.getChildren().add(texto);
        caja.getChildren().add(indicador);
        
        Stage ventana=new Stage();
        ventana.initStyle(StageStyle.UNDECORATED);
        ventana.setResizable(false);
        ventana.setTitle("Enviando ...");
        ventana.setScene(new Scene(caja,400,200));
        ventana.show();
        */
    }
    
    //Finaliza el programa
    @FXML
    private void finalizarPrograma(ActionEvent event)
    {
        if (botonGuardar.isDisable())
            System.exit(0);
        
        //Se crea una alerta de confirmación al salir
        Alert alerta=new Alert(AlertType.WARNING);
        alerta.setTitle("");
        alerta.setHeaderText("¿Guardar los cambios antes de salir?");
        alerta.setContentText("Se perderán todos los cambios realizados.");

        //Se crean y personalizan los botones para las opciones a elegir
        ButtonType opcionGuardar=new ButtonType("Guardar");
        ButtonType opcionSalir=new ButtonType("Salir");
        ButtonType opcionCancelar=new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(opcionGuardar,opcionCancelar,opcionSalir);

        Optional<ButtonType> resultado=alerta.showAndWait();
        
        if (resultado.get()==opcionGuardar)
            sistema.guardarDatos();
        else if (resultado.get()==opcionSalir)
            System.exit(0);
    }
        
    //Carga la ventana AdministrarAlimentos
    @FXML
    private void cargarAdministrarAlimentos()
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/menuAdministrativo/AdministrarAlimentos.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de AdministrarAlimentos para enviar Cajero por parámetros
            AdministrarAlimentosController controlador=(AdministrarAlimentosController) loader.getController();
            controlador.inicializar(sistema);

            Stage ventana=(Stage) panel.getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();   
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Carga la ventana AdministrarBebestibles
    @FXML
    private void cargarAdministrarBebestibles()
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/menuAdministrativo/AdministrarBebestibles.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de AdministrarBebestibles para enviar Cajero por parámetros
            AdministrarBebestiblesController controlador=(AdministrarBebestiblesController) loader.getController();
            controlador.inicializar(sistema);

            Stage ventana=(Stage) panel.getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
     
    //Carga la ventana AdministrarIngredientes
    @FXML
    private void cargarAdministrarIngredientes()
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/menuAdministrativo/AdministrarIngredientes.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de AdministrarIngredientes para enviar Cajero por parámetros
            AdministrarIngredientesController controlador=(AdministrarIngredientesController) loader.getController();
            controlador.inicializar(sistema);

            Stage ventana=(Stage) panel.getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();    
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Carga la ventana AdministrarMesas
    @FXML
    private void cargarAdministrarMesas()
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/menuAdministrativo/AdministrarMesas.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de AdministrarMesas para enviar Cajero por parámetros
            AdministrarMesasController controlador=(AdministrarMesasController) loader.getController();
            controlador.inicializar(sistema);

            Stage ventana=(Stage) panel.getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Carga la ventana Reportes
    @FXML
    private void cargarReportes()
    {
        try
        {
           FXMLLoader loader=new FXMLLoader();
           loader.setLocation(getClass().getResource("/ventanas/menuAdministrativo/Reportes.fxml"));
           Parent root=loader.load();
           Scene escena=new Scene(root);

           //Obtiene el controlador de Reportes para enviar Cajero por parámetros
           ReportesController controlador=(ReportesController) loader.getController();
           controlador.inicializar(sistema);

           Stage ventana=(Stage) panel.getScene().getWindow();
           ventana.setScene(escena);
           ventana.show();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }  
    }
    
    //Carga la ventana anterior, es decir, el Login
    @FXML
    private void cargarVentanaAnterior(ActionEvent event)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/Login.fxml"));
            Parent tableViewParent;
            tableViewParent = loader.load();
            Scene escena=new Scene(tableViewParent);        
            Stage ventana=(Stage)((Node)event.getSource()).getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Muestra una alerta con toda la información detallada de la excepción
    private void alertaExcepcion(Exception excepcion)
    {
        Alert alerta=new Alert(AlertType.ERROR);
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
