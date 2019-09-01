package ventanas.menuAdministrativo;

import com.jfoenix.controls.JFXButton;
import datos.Bebestible;
import datos.Cajero;
import excepciones.PrecioNoValidoException;
import excepciones.StockNoValidoException;
import java.io.File;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
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

public class AdministrarBebestiblesController implements Initializable
{
    @FXML
    private TableView<Bebestible> tabla;
    @FXML
    private TableColumn<Bebestible,String> columnaNombre;
    @FXML
    private TableColumn<Bebestible,String> columnaID;
    @FXML
    private TableColumn<Bebestible,Integer> columnaPrecio;
    @FXML
    private TableColumn<Bebestible,Integer> columnaStock;
    @FXML
    private TextField nombre;
    @FXML
    private TextField precio;
    @FXML
    private TextField stock;
    @FXML
    private JFXButton botonEliminar;
    
    private Cajero sistema;

    @Override
    public void initialize(URL url, ResourceBundle rb){}
    
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
        
        //Deshabilitar el botón eliminar hasta que el usuario seleccione un bebestible de la tabla
        botonEliminar.setDisable(true);
        
        //Inicializa la tabla y columnas con los datos cargados
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        tabla.setItems(FXCollections.observableArrayList(sistema.getBebestibles()));
        
        //Deja los campos nombre, precio y stock modificables para el administrador
        tabla.setEditable(true);
        columnaNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaPrecio.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnaStock.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
        //Asignar al campo precio la propiedad de aceptar solo un input númerico
        precio.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("([\\-])?\\d{0,11}"))
                    precio.setText(oldValue);
            }
        });
        
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
    
    //Habilita el botón eliminar si el usuario seleccionó un bebestible de la tabla
    @FXML
    private void habilitarBotonEliminar()
    {
        if ((!tabla.getItems().isEmpty()) && (!tabla.getSelectionModel().isEmpty()))
        {
            botonEliminar.setDisable(false);
        }
    }
    
    //Agrega un nuevo bebestible a la coleccion, tanto en la tabla como en los datos reales
    @FXML
    private void agregarBebestible()
    {   
        //Verifica si el campo nombre se encuentra vacío
        if (nombre.getText().isEmpty())
        {
            alertaError("El campo nombre se encuentra en blanco.");
            return;
        }
        
        //Verifica si el precio nombre se encuentra vacío
        if (precio.getText().isEmpty())
        {
            alertaError("El campo precio se encuentra en blanco.");
            return;
        }
        
        //Verifica si el campo stock se encuentra vacío
        if (stock.getText().isEmpty())
        {
            alertaError("El campo stock se encuentra en blanco.");
            return;
        }
  
        try
        {
            Bebestible dato=new Bebestible(nombre.getText(),Integer.parseInt(precio.getText()),Integer.parseInt(stock.getText()));
            tabla.getItems().add(dato);
            sistema.agregarBebestible(dato);
            nombre.setText("");
            precio.setText("");
            stock.setText("");
        }
        catch (PrecioNoValidoException | StockNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Elimina un dato bebestible de la coleccion, tanto en la tabla como en los datos reales
    @FXML
    private void eliminarBebestible()
    {
        Bebestible dato=(Bebestible) tabla.getSelectionModel().getSelectedItem();
        
        if (alertaEliminar(dato))
        {
            tabla.getItems().remove(dato);
            sistema.eliminarBebestible(dato.getId());
        }
    }
    
    //Modifica el nombre del bebestible, tanto en la tabla como en los datos reales
    @FXML
    private void modificarNombre(CellEditEvent celda)
    {
        Bebestible dato=tabla.getSelectionModel().getSelectedItem();
        dato.setNombre(celda.getNewValue().toString());
        sistema.modificarNombreBebestible(dato.getId(),celda.getNewValue().toString());
    }
    
    //Modifica el precio del bebestible, tanto en la tabla como en los datos reales
    @FXML
    private void modificarPrecio(CellEditEvent celda)
    {
        Bebestible dato=tabla.getSelectionModel().getSelectedItem();
        
        try
        {
            dato.setPrecio(Integer.parseInt(celda.getNewValue().toString()));
            sistema.modificarPrecioBebestible(dato.getId(),Integer.parseInt(celda.getNewValue().toString()));    
        }
        catch (PrecioNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Modifica el stock del bebestible, tanto en la tabla como en los datos reales
    @FXML
    private void modificarStock(CellEditEvent celda)
    {
        Bebestible dato=tabla.getSelectionModel().getSelectedItem();
        
        try
        {
            dato.setStock(Integer.parseInt(celda.getNewValue().toString()));
            sistema.modificarStockBebestible(dato.getId(),Integer.parseInt(celda.getNewValue().toString()));
        }
        catch (StockNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Carga la Ventana Anterior, es decir, MenuAdministrador
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
        fileChooser.setInitialFileName("bebestibles.pdf");
        File archivo=fileChooser.showSaveDialog(new Stage());
        
        if (archivo!=null)
            sistema.guardarInvertarioBebestibles(archivo.getAbsolutePath());
    }
    
    //Muestra un cuadro de dialogo, donde pide confirmación para eliminar el bebestible, retornando un booleano
    private boolean alertaEliminar(Bebestible dato)
    {
        Alert ventana=new Alert(Alert.AlertType.CONFIRMATION);
        String contenido="Bebestible: "+dato.getNombre()+" ( "+dato.getId()+" )";
        
        ventana.setTitle("Confirmar Eliminar Bebestible");
        ventana.setHeaderText(contenido);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Realmente quieres eliminar este bebestible?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
            return true;
        
        return false;
    }
    
    //Muestra un cuadro de dialogo de error, con un mensaje del porqué ocurrió dicho error
    private void alertaError(String mensaje)
    {
        Alert ventana=new Alert(Alert.AlertType.ERROR);
        ventana.setTitle("¡ERROR Bebestible!");
        ventana.setHeaderText("ERROR: Imposible realizar la acción");
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
