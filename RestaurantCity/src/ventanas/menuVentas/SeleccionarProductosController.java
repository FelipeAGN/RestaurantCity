package ventanas.menuVentas;

import datos.Cajero;
import datos.Producto;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class SeleccionarProductosController implements Initializable
{
    @FXML
    private TableView<Producto> tabla;
    @FXML
    private TableColumn<Producto,String> columnaNombre;
    @FXML
    private TableColumn<Producto,Integer> columnaPrecio;
    @FXML
    private ComboBox listaProductos;
    @FXML
    private Label labelSubTotal;
    
    private Cajero sistema;
    private int numeroMesa;
    private int divisionCuenta;
    private final ArrayList<String> productos=new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}    
    
 
    public void inicializar(Cajero sistema, int numeroMesa, int divisionCuenta)
    {
        this.sistema=sistema;
        this.numeroMesa=numeroMesa;
        this.divisionCuenta=divisionCuenta;
        
        //Se inicializa el label SubTotal en 0, ya que no hay productos en la tabla
        labelSubTotal.setText("0");
        
        //Se inicializa el ComboBox productos y lo configuramos para que solo muestre el nombre
        listaProductos.setItems(FXCollections.observableArrayList(sistema.getAlimentosMesa(numeroMesa)));
        listaProductos.getItems().addAll(sistema.getBebestiblesMesa(numeroMesa));
        listaProductos.setCellFactory(factoryProducto);
        listaProductos.setButtonCell(factoryProducto.call(null));
        listaProductos.getSelectionModel().selectFirst();

        //Se ajusta la tabla productos y sus columnas
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }
    
    //Actualiza los items de la lista alimentos (ComboBox) para que solo muestre el atributo nombre
    Callback<ListView<Producto>,ListCell<Producto>> factoryProducto=lista-> new ListCell<Producto>()
    {
        @Override
        protected void updateItem(Producto dato, boolean empty)
        {
            super.updateItem(dato,empty);
            setText(empty ? "" : dato.getNombre());
        }
    };
    
    //Agrega un producto a la tabla, lo quita del ComboBox y actualiza el SubTotal
    @FXML
    private void agregarProducto()
    {
        Producto dato;
        int subTotal=Integer.parseInt(labelSubTotal.getText());
        
        if (listaProductos.getItems().isEmpty())
            return;
        
        dato=(Producto) listaProductos.getValue();
        tabla.getItems().add(dato);
        productos.add(dato.getId());
        listaProductos.getItems().remove(dato);
        listaProductos.getSelectionModel().selectFirst();
        labelSubTotal.setText(Integer.toString(subTotal+dato.getPrecio()));
    }
    
    //Elimina un producto de la tabla, lo agrega al ComboBox y actualiza el SubTotal
    @FXML
    private void eliminarProducto()
    {
        Producto dato;
        int subTotal=Integer.parseInt(labelSubTotal.getText());
        
        if ((tabla.getItems().isEmpty()) || (tabla.getSelectionModel().isEmpty()))
            return;
        
        dato=(Producto) tabla.getSelectionModel().getSelectedItem();
        tabla.getItems().remove(dato);
        productos.remove(dato.getId());
        listaProductos.getItems().add(dato);
        listaProductos.getSelectionModel().selectFirst();
        labelSubTotal.setText(Integer.toString(subTotal-dato.getPrecio()));
    }
    
    @FXML
    private void continuarPago(ActionEvent event)
    {
        //Lanzar Alerta acá
        if ((divisionCuenta==1) && (!listaProductos.getItems().isEmpty()))
            return;
        
        if (!productos.isEmpty())
            cargarPagar(event);
        else
        {
            Alert ventana=new Alert(Alert.AlertType.ERROR);
            ventana.setTitle("¡ERROR!");
            ventana.setHeaderText("ERROR: Imposible continuar con el pago");
            ventana.setContentText("No se ha seleccionado ningún producto de la lista para continuar con el pago.");
            ventana.initStyle(StageStyle.UTILITY);
            java.awt.Toolkit.getDefaultToolkit().beep();
            ventana.showAndWait();
        }
    }
   
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
            controlador.inicializar(sistema,numeroMesa,productos,Integer.parseInt(labelSubTotal.getText()),divisionCuenta);

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
