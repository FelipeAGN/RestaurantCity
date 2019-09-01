package ventanas.menuAdministrativo;

import com.jfoenix.controls.JFXButton;
import datos.Alimento;
import datos.Cajero;
import datos.Ingrediente;
import datos.Stock;
import excepciones.IDFormatoNoValidoException;
import excepciones.StockNoValidoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

public class ModificarIngredientesController implements Initializable
{
    @FXML
    private TableView<Stock> tabla;
    @FXML
    private TableColumn<Alimento,String> columnaNombre;
    @FXML
    private TableColumn<Stock,String> columnaID;
    @FXML
    private TableColumn<Stock,Integer> columnaCantidad;
    @FXML
    private Label nombreAlimento;
    @FXML
    private ComboBox ingredientes;
    @FXML
    private TextField cantidad;
    @FXML
    private JFXButton botonEliminar;

    
    private Cajero sistema;
    private String idAlimento;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}

    //Actualiza los items de la lista con ingredientes para que solo muestre el atributo nombre
    Callback<ListView<Ingrediente>,ListCell<Ingrediente>> factory=lista-> new ListCell<Ingrediente>()
    {
        @Override
        protected void updateItem(Ingrediente dato, boolean empty)
        {
            super.updateItem(dato,empty);
            setText(empty ? "" : dato.getNombre());
        }
    };
    
    public void inicializar(Cajero sistema, String id, String nombre)
    {
        this.sistema=sistema;
        idAlimento=id;
        nombreAlimento.setText(nombre);
        
        //Deshabilitar el botón eliminar hasta que el usuario seleccione un ingrediente de la tabla
        botonEliminar.setDisable(true);
        
        //Inicializamos el ComboBox ingredientes y lo configuramos para que solo muestre el nombre
        ingredientes.setItems(FXCollections.observableArrayList(sistema.getIngredientes()));
        ingredientes.setCellFactory(factory);
        ingredientes.setButtonCell(factory.call(null));
        ingredientes.getSelectionModel().selectFirst();
        
        //Inicializa la tabla y columnas con los datos cargados
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        tabla.setItems(FXCollections.observableArrayList(sistema.getIngredientesAlimento(id)));
        
        //Deja los campos nombre y precio modificables para el administrador
        tabla.setEditable(true);
        columnaCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
        //Asignar al campo cantidad la propiedad de aceptar solo un input númerico
        cantidad.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("([\\-])?\\d{0,11}"))
                    cantidad.setText(oldValue);
            }
        });
    }
    
    //Habilita el botón eliminar si el usuario seleccionó un ingrediente de la tabla
    @FXML
    private void habilitarBotonEliminar()
    {
        if ((!tabla.getItems().isEmpty()) && (!tabla.getSelectionModel().isEmpty()))
        {
            botonEliminar.setDisable(false);
        }
    }
    
    @FXML
    public void agregarIngrediente()
    {
        //Verifica si en el campo de texto cantidad se ha ingresado un valor 
        if (cantidad.getText().isEmpty())
        {
            alertaError("El campo cantidad se encuentran en blanco.");
            return;
        }
                
        Ingrediente ingrediente=(Ingrediente) ingredientes.getValue();
       
        try
        {
            Stock dato=new Stock(ingrediente.getNombre(),ingrediente.getId(),Integer.parseInt(cantidad.getText()));
            
            if (sistema.agregarIngredienteAlimento(idAlimento,dato))
            {
                tabla.getItems().add(dato);
                cantidad.setText("");
            }
            else
                alertaError("El ingrediente ya se encuentra en el alimento.");
            
        }
        catch (StockNoValidoException | IDFormatoNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Elimina el ingrediente seleccionado de la tabla de la lista de ingredientes del alimento
    @FXML
    private void eliminarIngrediente()
    {
        Stock dato=(Stock) tabla.getSelectionModel().getSelectedItem();
        
        if (alertaEliminar(dato))
        {    
            tabla.getItems().remove(dato);
            sistema.eliminarIngredienteAlimento(idAlimento,dato.getId());
        }
    }
     
    //Modifica la cantidad de ingrediente, tanto en la tabla como en los datos reales
    @FXML
    private void modificarCantidad(CellEditEvent celda)
    {       
        Stock dato=tabla.getSelectionModel().getSelectedItem();
        
        try
        {
            dato.setCantidad(Integer.parseInt(celda.getNewValue().toString()));
            sistema.modificarCantidadIngredienteAlimento(idAlimento,dato.getId(),Integer.parseInt(celda.getNewValue().toString()));
        }
        catch (StockNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Carga la ventana anterior, es decir, AdministrarAlimentos
    @FXML
    private void cargarVentanaAnterior(ActionEvent event)
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

            Stage ventana=(Stage)((Node)event.getSource()).getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    //Muestra un cuadro de dialogo, donde pide confirmación para eliminar el ingrediente, retornando un booleano
    private boolean alertaEliminar(Stock dato)
    {
        Alert ventana=new Alert(Alert.AlertType.CONFIRMATION);
        String contenido="Ingrediente: "+dato.getNombre()+" ( "+dato.getId()+" )";
        
        ventana.setTitle("Confirmar Eliminar Ingrediente");
        ventana.setHeaderText(contenido);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Realmente quieres eliminar este ingrediente?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
            return true;
        
        return false;
    }
    
    //Muestra un cuadro de dialogo de error, con un mensaje del porqué ocurrió dicho error
    private void alertaError(String mensaje)
    {
        Alert ventana=new Alert(Alert.AlertType.ERROR);
        ventana.setTitle("¡ERROR!");
        ventana.setHeaderText("ERROR: Imposible agregar el ingrediente");
        ventana.setContentText(mensaje);
        ventana.initStyle(StageStyle.UTILITY);
        java.awt.Toolkit.getDefaultToolkit().beep();
        ventana.showAndWait();
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