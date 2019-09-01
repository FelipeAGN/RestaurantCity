package ventanas.menuVentas;

import datos.Cajero;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class DividirCuentaController implements Initializable
{
    @FXML
    private Label labelCantidad;
    
    private Cajero sistema;
    private int numeroMesa;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}    
    
    public void inicializar(Cajero sistema, int numeroMesa)
    {
        this.sistema=sistema;
        this.numeroMesa=numeroMesa;
    }
   
     
    //Aumenta la cantidad de personas en que se repartirá la cuenta, considerar que esta división debe ser menor o igual a la cantidad de productos vendidos
    @FXML
    private void aumentarCantidad(ActionEvent event)
    {
        int numero=Integer.parseInt(labelCantidad.getText())+1;
        
        if (numero>sistema.getCantidadProductosMesa(numeroMesa))
            return;
        
        if (numero<10)
            labelCantidad.setText("0"+numero);
        else
            labelCantidad.setText((Integer.toString(numero)));
    }
    
    //Disminuye la cantidad de personas en que se repartirá la cuenta, considerar que esta división no puede ser menor que 1
    @FXML
    private void disminuirCantidad(ActionEvent event)
    {
        int numero=Integer.parseInt(labelCantidad.getText())-1;
        
        if (numero<1)
            return;
        
        if (numero<10)
            labelCantidad.setText("0"+numero);
        else
            labelCantidad.setText((Integer.toString(numero)));
    }
    
    @FXML
    private void continuarPago(ActionEvent event)
    {
        if (Integer.parseInt(labelCantidad.getText())==1)
            cargarPagar(event);
        else
            cargarSeleccionarProductos(event);
    }
  
    //Carga la ventana SeleccionarProductos, si la división de la cuenta fue mayor a 1,
    //se abrirá recursivamente hasta que se hallan vendidos todos los productos de la mesa
    @FXML
    private void cargarSeleccionarProductos(ActionEvent event)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/ventanas/menuVentas/SeleccionarProductos.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de SeleccionarProductos para enviar Cajero por parámetros
            SeleccionarProductosController controlador=(SeleccionarProductosController) loader.getController();
            controlador.inicializar(sistema,numeroMesa,Integer.parseInt(labelCantidad.getText()));

            Stage ventana=(Stage)((Node)event.getSource()).getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();	
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Si la división de la cuenta fue 1, se carga la ventana Pagar para pagar toda la cuenta en un solo paso
    @FXML
    private void cargarPagar(ActionEvent event)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/ventanas/menuVentas/Pagar.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de Pagar para enviar Cajero por parámetros
            PagarController controlador=(PagarController) loader.getController();
            controlador.inicializar(sistema,numeroMesa,null,sistema.getSubTotalMesa(numeroMesa),1);

            Stage ventana=(Stage)((Node)event.getSource()).getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Cierra la ventana emergente
    @FXML
    private void cerrarVentana(ActionEvent event)
    {
        Stage ventana=(Stage)((Node)event.getSource()).getScene().getWindow();
        ventana.close();
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
