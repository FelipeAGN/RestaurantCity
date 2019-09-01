package ventanas.menuAdministrativo;

import com.jfoenix.controls.JFXButton;
import datos.Alimento;
import datos.Cajero;
import excepciones.IDFormatoNoValidoException;
import excepciones.PrecioNoValidoException;
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

public class AdministrarAlimentosController implements Initializable
{   
    @FXML
    private TableView<Alimento> tabla;
    @FXML
    private TableColumn<Alimento,String> columnaNombre;
    @FXML
    private TableColumn<Alimento,String> columnaID;
    @FXML
    private TableColumn<Alimento,Integer> columnaPrecio;
    @FXML
    private TableColumn<Alimento,Integer> columnaIngredientes;
    @FXML
    private TextField nombre;
    @FXML
    private TextField precio;
    @FXML
    private JFXButton botonEliminar;
    @FXML
    private JFXButton botonModificar;

    private Cajero sistema;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}    
    
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
        
        //Deshabilitar el botón eliminar y modificar hasta que se seleccione un alimento de la tabla
        botonEliminar.setDisable(true);
        botonModificar.setDisable(true);
        
        //Inicializa la tabla y columnas con los datos cargados
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaIngredientes.setCellValueFactory(new PropertyValueFactory<>("cantidadIngredientes"));
        tabla.setItems(FXCollections.observableArrayList(sistema.getAlimentos()));
        
        //Deja los campos nombre y precio modificables para el administrador
        tabla.setEditable(true);
        columnaNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        columnaPrecio.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
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
    }
    
    //Habilita el botón modificar y eliminar si el usuario seleccionó un alimento de la tabla
    @FXML
    private void habilitarBotones()
    {        
        if ((!tabla.getItems().isEmpty()) && (!tabla.getSelectionModel().isEmpty()))
        {
            botonEliminar.setDisable(false);
            botonModificar.setDisable(false);
        }
    } 
    
    @FXML
    private void agregarAlimento()
    {   
        //Verifica el campo nombre si es que no está en blanco
        if (nombre.getText().isEmpty())
        {
            alertaError("El campo nombre se encuentra en blanco.");
            return;
        }
        
        //Verifica el campo precio si es que no está en blanco
        if (precio.getText().isEmpty())
        {
            alertaError("El campo precio se encuentra en blanco.");
            return;
        }
   
        //Documentar
        try
        {
            Alimento dato=new Alimento(nombre.getText(),Integer.parseInt(precio.getText()));
            tabla.getItems().add(dato);
            sistema.agregarAlimento(dato);
            nombre.setText("");
            precio.setText("");
        }
        catch (PrecioNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Elimina un dato alimento de la colección, tanto en la tabla como en los datos reales
    @FXML
    private void eliminarAlimento()
    {
        Alimento dato=(Alimento) tabla.getSelectionModel().getSelectedItem();
        
        if (alertaEliminar(dato))
        {
            tabla.getItems().remove(dato);
            sistema.eliminarAlimento(dato.getId());
        }
    }
    
    //Modifica el nombre del alimento, tanto en la tabla como en los datos reales
    @FXML
    private void modificarNombre(TableColumn.CellEditEvent celda)
    {
        Alimento dato=tabla.getSelectionModel().getSelectedItem();
        dato.setNombre(celda.getNewValue().toString());
        sistema.modificarNombreAlimento(dato.getId(),celda.getNewValue().toString());
    }
    
    //Modifica el precio del alimento, tanto en la tabla como en los datos reales
    @FXML
    private void modificarPrecio(TableColumn.CellEditEvent celda)
    {
        Alimento dato=tabla.getSelectionModel().getSelectedItem();
        
        try
        {
            dato.setPrecio(Integer.parseInt(celda.getNewValue().toString()));
            sistema.modificarPrecioAlimento(dato.getId(),Integer.parseInt(celda.getNewValue().toString()));
        }
        catch (PrecioNoValidoException excepcion)
        {
            alertaError(excepcion.getMessage());
        }
    }
    
    //Obtiene el ID del alimento, para luego cargar la ventana ModificarIngrediente
    @FXML 
    private void modificarIngredientes(ActionEvent event) throws IOException, IDFormatoNoValidoException
    {
        Alimento dato=tabla.getSelectionModel().getSelectedItem();
        cargarModificarIngredientes(event,dato.getId(),dato.getNombre());
    }
    
    //Carga la ventana ModificarIngredientes con modificación de un ingrediente en especifico
    private void cargarModificarIngredientes(ActionEvent event, String id, String nombre)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/menuAdministrativo/ModificarIngredientes.fxml"));
            Parent root=loader.load();
            Scene escena=new Scene(root);

            //Obtiene el controlador de ModificarIngredientes para enviar Cajero por parámetros
            ModificarIngredientesController controlador=(ModificarIngredientesController) loader.getController();
            controlador.inicializar(sistema,id,nombre);

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
        fileChooser.setInitialFileName("alimentos.pdf");
        File archivo=fileChooser.showSaveDialog(new Stage());
        
        if (archivo!=null)
            sistema.guardarInvertarioAlimentos(archivo.getAbsolutePath());
    }
    
    //Carga la ventana anterior, es decir, el MenuAdministrador
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
    
    //Muestra un cuadro de dialogo, donde pide confirmación para eliminar el alimento, retornando un booleano
    private boolean alertaEliminar(Alimento dato)
    {
        Alert ventana=new Alert(Alert.AlertType.CONFIRMATION);
        String contenido="Alimento: "+dato.getNombre()+" ( "+dato.getId()+" )";
        
        ventana.setTitle("Confirmar Eliminar Alimento");
        ventana.setHeaderText(contenido);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Realmente quieres eliminar este alimento?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
            return true;
        
        return false;
    }
    
    //Muestra un cuadro de dialogo de error, con un mensaje del porqué ocurrió dicho error
    private void alertaError(String mensaje)
    {
        Alert ventana=new Alert(Alert.AlertType.ERROR);
        ventana.setTitle("¡ERROR Alimento!");
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