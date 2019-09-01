package ventanas.menuAdministrativo;

import com.jfoenix.controls.JFXButton;
import datos.Cajero;
import datos.Ingrediente;
import datos.Unidad;
import excepciones.StockNoValidoException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;
import ventanas.MenuAdministradorController;

public class AdministrarIngredientesController implements Initializable
{
    @FXML
    private TableView<Ingrediente> tabla;
    @FXML
    private TableColumn<Ingrediente,String> columnaNombre;
    @FXML
    private TableColumn<Ingrediente,String> columnaID;
    @FXML
    private TableColumn<Ingrediente,Integer> columnaStock;
    @FXML
    private TableColumn<Ingrediente,Integer> columnaUnidad;
    @FXML
    private TextField nombre;
    @FXML
    private TextField stock;
    @FXML
    private ComboBox unidades;
    @FXML
    private JFXButton botonEliminar;
       
    private Cajero sistema;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}
    
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
        
        //Deshabilitar el botón eliminar hasta que el usuario seleccione un ingrediente de la tabla
        botonEliminar.setDisable(true);
        
        //Inicializamos el ComboBox de unidades
        unidades.setItems(FXCollections.observableArrayList(Unidad.GRAMO,Unidad.MILILITRO,Unidad.UNITARIO));
        unidades.getSelectionModel().selectFirst();
        
        //Inicializa la tabla y columnas con los datos cargados
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        columnaUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        tabla.setItems(FXCollections.observableArrayList(sistema.getIngredientes()));
        
        //Deja los campos nombre y stock modificables para el administrador
        tabla.setEditable(true);
        columnaNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaStock.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
        //Asignar al campo stock la propiedad de aceptar solo un input númerico
        stock.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("([\\-])?\\d{0,11}"))
                    stock.setText(oldValue);
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
    
    //Agrega un nuevo ingrediente a la coleccion, tanto en la tabla como en los datos reales
    @FXML
    private void agregarIngrediente()
    {   
        //Verifica si el campo nombre se encuentra vaciío
        if (nombre.getText().isEmpty())
        {
            alertaError("El campo nombre se encuentra en blanco.");
            return;
        }
        
        //Verifica si el campo stock se encuentra vacío
        if (stock.getText().isEmpty())
        {
            alertaError("El campo nombre se encuentra en blanco.");
            return;
        }
        
        try
        {
            Ingrediente dato=new Ingrediente(nombre.getText(),Integer.parseInt(stock.getText()),(Unidad) unidades.getValue());
            tabla.getItems().add(dato);
            sistema.agregarIngrediente(dato);
            nombre.setText("");
            stock.setText("");
        }
        catch (StockNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Elimina un dato ingrediente (si es que se puede) de la coleccion, tanto en la tabla como en los datos reales
    @FXML
    private void eliminarIngrediente()
    {
        Ingrediente dato=(Ingrediente) tabla.getSelectionModel().getSelectedItem();
        ArrayList<String> listaAlimentos=sistema.verificarIngrediente(dato.getId());
            
        //Verifica si el ingrediente se puede eliminar, si no es así finaliza el metódo y muestra un cuadro de alerta
        if (listaAlimentos!=null)
        {
            alertaErrorEliminar(dato,listaAlimentos);
            return;
        }
            
        if (alertaEliminar(dato))
        {
            tabla.getItems().remove(dato);
            sistema.eliminarIngrediente(dato.getId());
        }
    }
    
    //Modifica el nombre del ingrediente, tanto en la tabla como en los datos reales
    @FXML
    private void modificarNombre(TableColumn.CellEditEvent celda)
    {
        Ingrediente dato=tabla.getSelectionModel().getSelectedItem();
        dato.setNombre(celda.getNewValue().toString());
        sistema.modificarNombreIngrediente(dato.getId(),celda.getNewValue().toString());
    }
    
    //Modifica el stock del ingrediente, tanto en la tabla como en los datos reales
    @FXML
    private void modificarStock(TableColumn.CellEditEvent celda)
    {
        Ingrediente dato=tabla.getSelectionModel().getSelectedItem();
        
        try
        {
            dato.setStock(Integer.parseInt(celda.getNewValue().toString()));
            sistema.modificarStockIngrediente(dato.getId(),Integer.parseInt(celda.getNewValue().toString()));
        }
        catch (StockNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }

    //Abre la ventana anterior, es decir, el MenuAdministrador
    @FXML
    private void cargarVentanaAnterior(ActionEvent event)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/MenuAdministrador.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de MenuAdministrador para enviar Cajero por parámetros
            MenuAdministradorController controlador=(MenuAdministradorController) loader.getController();
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
    
    @FXML
    private void guardarInventario(ActionEvent event)
    {
        FileChooser fileChooser=new FileChooser();
        
        //Ajusta el filtro de extension solo para archivos PDF
        FileChooser.ExtensionFilter extension=new FileChooser.ExtensionFilter("PDF - Portable Document Format (.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extension);
        fileChooser.setInitialFileName("ingredientes.pdf");
        File archivo=fileChooser.showSaveDialog(new Stage());
        
        if (archivo!=null)
            sistema.guardarInvertarioIngredientes(archivo.getAbsolutePath());
    }
    
    //Muestra un cuadro de dialogo, donde pide confirmación para eliminar el ingrediente, retornando un booleano
    private boolean alertaEliminar(Ingrediente dato)
    {
        Alert ventana=new Alert(AlertType.CONFIRMATION);
        String contenido="Ingrediente: "+dato.getNombre()+" ( "+dato.getId()+" )";
        
        ventana.setTitle("Confirmar Eliminar Ingrediente");
        ventana.setHeaderText(contenido);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Realmente Quieres Eliminar este Ingrediente?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
            return true;
        
        return false;
    }
    
    //Muestra un cuadro de dialogo de error, con un mensaje del porqué ocurrió dicho error
    private void alertaError(String mensaje)
    {
        Alert ventana=new Alert(Alert.AlertType.ERROR);
        ventana.setTitle("¡ERROR Ingrediente!");
        ventana.setHeaderText("ERROR: Imposible realizar la acción");
        ventana.setContentText(mensaje);
        ventana.initStyle(StageStyle.UTILITY);
        java.awt.Toolkit.getDefaultToolkit().beep();
        ventana.showAndWait();
    }
            
    //Muestra un cuadro de dialogo de error, donde muestra el porqué no se pudo eliminar
    //el ingrediente, es decir, imprime todos los alimentos que contenian al ingrediente
    private void alertaErrorEliminar(Ingrediente dato, ArrayList<String> lista)
    {
        Alert ventana=new Alert(AlertType.ERROR);
        String contenido="Los siguientes alimentos contienen este ingrediente:\n\n";
        
        for (int i=0; i<lista.size(); i++)
            contenido=contenido+lista.get(i)+"\n";
        
        ventana.setTitle("¡ERROR!");
        ventana.setHeaderText("ERROR: No se puede eliminar "+dato.getNombre()+" ( "+dato.getId()+" )");
        ventana.setContentText(contenido);
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
