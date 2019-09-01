package ventanas.menuVentas;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import datos.Cajero;
import datos.MetodoPago;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class PagarController implements Initializable
{
    @FXML
    private JFXRadioButton botonCheque;
    @FXML
    private JFXRadioButton botonCredito;
    @FXML
    private JFXRadioButton botonDebito;
    @FXML
    private JFXRadioButton botonEfectivo;
    @FXML
    private JFXRadioButton botonPropinaSugerida;
    @FXML
    private JFXRadioButton botonPropinaOtroMonto;
    @FXML
    private JFXToggleButton botonAgregarPropina;
    @FXML
    private Label labelOpcion;
    @FXML
    private Label labelPropina;
    @FXML
    private Label labelTotal;
    @FXML
    private JFXTextField campoMontoPropina;
    
    private Cajero sistema;
    private int numeroMesa;
    private int subTotal;
    private int divisionCuenta;
    private ArrayList<String> productos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}

    public void inicializar(Cajero sistema, int numeroMesa, ArrayList<String> productos, int subTotal, int divisionCuenta)
    {
        int propina=(int) (subTotal*0.1);
        
        this.sistema=sistema;
        this.numeroMesa=numeroMesa;
        this.productos=productos;
        this.subTotal=subTotal;
        this.divisionCuenta=divisionCuenta;
        
        botonCheque.setTooltip(new Tooltip("Cheque"));
        botonCredito.setTooltip(new Tooltip("Tarjeta de Crédito"));
        botonDebito.setTooltip(new Tooltip("Tarjeta de Débito"));
        botonEfectivo.setTooltip(new Tooltip("Efectivo"));
        botonPropinaSugerida.setDisable(true);
        botonPropinaOtroMonto.setDisable(true);
        campoMontoPropina.setDisable(true);
        campoMontoPropina.setPromptText("Otro Monto");
        labelPropina.setText(Integer.toString(propina));
        labelTotal.setText(Integer.toString(subTotal));
        
        //Asignar al campo monto propina la propiedad de aceptar solo un input númerico
        campoMontoPropina.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("([\\-])?\\d{0,11}"))
                    campoMontoPropina.setText(oldValue);
            }
        });
    }
    
    @FXML
    public void pagar(ActionEvent event) throws IOException
    {
        MetodoPago metodo;
        int propina=0;
        
        //Verifica que método de pago se seleccionó
        if (botonCheque.isSelected())
            metodo=MetodoPago.CHEQUE;
        else if (botonCredito.isSelected())
            metodo=MetodoPago.CREDITO;
        else if (botonDebito.isSelected())
            metodo=MetodoPago.DEBITO;
        else
            metodo=MetodoPago.EFECTIVO;
        
        //Asigna el valor de propina correspondiente
        if ((botonAgregarPropina.isSelected()) && (botonPropinaSugerida.isSelected()))
           propina=(int) (subTotal*0.1);
        else if ((botonAgregarPropina.isSelected()) && (botonPropinaOtroMonto.isSelected()))
           propina=Integer.parseInt(campoMontoPropina.getText());

        //Se realiza el pago
        if (divisionCuenta==1)
        {
            sistema.realizarVenta(numeroMesa,propina,metodo);
            cerrarVentana(event);
        }
        else
        {
            sistema.realizarVenta(numeroMesa,productos,subTotal,propina, metodo);
            cargarSeleccionarProductos(event);
        }
    }
    
    //Habilita y deshabilita según sea el caso, los botones PropinaSugerida y PropinaOtroMonto
    @FXML
    public void accionAgregarPropina(ActionEvent evento)
    {
        int propina=(int) (subTotal*0.1);
        
        if (botonAgregarPropina.isSelected())
        {
            labelOpcion.setText("Si");
            labelOpcion.setTextFill(Paint.valueOf("#00ff00"));
            labelTotal.setText(Integer.toString(subTotal+propina));
            botonPropinaSugerida.setDisable(false);
            botonPropinaOtroMonto.setDisable(false);
        }
        else
        {
            labelOpcion.setText("No");
            labelOpcion.setTextFill(Paint.valueOf("#f01320"));
            labelTotal.setText(Integer.toString(subTotal));
            botonPropinaSugerida.setSelected(true);
            botonPropinaSugerida.setDisable(true);
            botonPropinaOtroMonto.setSelected(false);
            botonPropinaOtroMonto.setDisable(true);
            campoMontoPropina.setDisable(true);
        }
    }
    
    
    //Selecciona el botón Cheque y deselecciona los demás botones para que exista una solo opción
    @FXML
    public void seleccionarBotonCheque()
    {
        botonCheque.setSelected(true);
        botonCredito.setSelected(false);
        botonDebito.setSelected(false);
        botonEfectivo.setSelected(false);
    }
     
    //Selecciona el botón Credito y deselecciona los demás botones para que exista una solo opción
    @FXML
    public void seleccionarBotonCredito()
    {
        botonCredito.setSelected(true);
        botonCheque.setSelected(false);
        botonDebito.setSelected(false);
        botonEfectivo.setSelected(false);
    }
    
    //Selecciona el botón Debito y deselecciona los demás botones para que exista una solo opción
    @FXML
    public void seleccionarBotonDebito()
    {
        botonDebito.setSelected(true);
        botonCheque.setSelected(false);
        botonCredito.setSelected(false);
        botonEfectivo.setSelected(false);
    }
    
    //Selecciona el botón Efectivo y deselecciona los demás botones para que exista una solo opción
    @FXML
    public void seleccionarBotonEfectivo()
    {
        botonEfectivo.setSelected(true);
        botonCheque.setSelected(false);
        botonCredito.setSelected(false);
        botonDebito.setSelected(false);
    }
    
    //Selecciona el boton PropinaSugerida, deselecciona el boton PropinaOtroMonto y desahabilita su campo de texto para que exista una sola opción de propina
    @FXML
    public void seleccionarPropinaSugerida()
    {
        botonPropinaSugerida.setSelected(true);
        botonPropinaOtroMonto.setSelected(false);
        campoMontoPropina.setDisable(true);
    }
    
    //Selecciona el boton PropinaOtroMonto, habilita el campo MontoPropina y deselecciona el boton PropinaSugerida para que exista una sola opción de propina
    @FXML
    public void seleccionarPropinaOtroMonto()
    {
        botonPropinaOtroMonto.setSelected(true);
        botonPropinaSugerida.setSelected(false);
        campoMontoPropina.setDisable(false);
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
            controlador.inicializar(sistema,numeroMesa,divisionCuenta-1);
        
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
