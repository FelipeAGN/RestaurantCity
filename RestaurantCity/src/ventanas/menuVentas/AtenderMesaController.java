package ventanas.menuVentas;

import com.jfoenix.controls.JFXButton;
import datos.Alimento;
import datos.Bebestible;
import datos.Cajero;
import excepciones.IDFormatoNoValidoException;
import excepciones.StockNoValidoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import ventanas.MenuVentasController;


public class AtenderMesaController implements Initializable 
{
    @FXML
    private ComboBox listaAlimentos;
    @FXML
    private ComboBox listaBebestibles;
    @FXML
    private TableView<Alimento> tablaAlimentos;
    @FXML
    private TableColumn<Alimento,String> columnaAlimentosNombre;
    @FXML
    private TableColumn<Alimento,Integer> columnaAlimentosPrecio;
    @FXML
    private TableView<Bebestible> tablaBebestibles;
    @FXML
    private TableColumn<Bebestible,String> columnaBebestiblesNombre;
    @FXML
    private TableColumn<Bebestible,Integer> columnaBebestiblesPrecio;
    @FXML
    private Label numero;
    @FXML
    private Label subTotal;
    @FXML
    private JFXButton botonEliminarAlimento;
    @FXML
    private JFXButton botonEliminarBebestible;
    
    
    private Cajero sistema;
    private int numeroMesa;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}
    
    //Actualiza los items de la lista alimentos (ComboBox) para que solo muestre el atributo nombre
    Callback<ListView<Alimento>,ListCell<Alimento>> factoryAlimento=lista-> new ListCell<Alimento>()
    {
        @Override
        protected void updateItem(Alimento dato, boolean empty)
        {
            super.updateItem(dato,empty);
            setText(empty ? "" : dato.getNombre());
        }
    };
    
    //Actualiza los items de la lista bebestibles (ComboBox) para que solo muestre el atributo nombre
    Callback<ListView<Bebestible>,ListCell<Bebestible>> factoryBebestible=lista-> new ListCell<Bebestible>()
    {
        @Override
        protected void updateItem(Bebestible dato, boolean empty)
        {
            super.updateItem(dato,empty);
            setText(empty ? "" : dato.getNombre());
        }
    };
        
    public void inicializar(Cajero sistema, int numeroMesa)
    {
        //Inicializa los datos esenciales de la ventana
        this.sistema=sistema;
        this.numeroMesa=numeroMesa;
        numero.setText(Integer.toString(numeroMesa));
        actualizarSubTotal();
        
        //Se deshabilitan los botones EliminarAlimento y EliminarBebestible ya que no existe ningún elemento en ambas tablas
        botonEliminarAlimento.setDisable(true);
        botonEliminarBebestible.setDisable(true);
        
        //Se inicializa el ComboBox alimentos y lo configuramos para que solo muestre el nombre
        listaAlimentos.setItems(FXCollections.observableArrayList(sistema.getAlimentos()));
        listaAlimentos.setCellFactory(factoryAlimento);
        listaAlimentos.setButtonCell(factoryAlimento.call(null));
        listaAlimentos.getSelectionModel().selectFirst();
        
        //Se inicializa el ComboBox bebestibles y lo configuramos para que solo muestre el nombre
        listaBebestibles.setItems(FXCollections.observableArrayList(sistema.getBebestibles()));
        listaBebestibles.setCellFactory(factoryBebestible);
        listaBebestibles.setButtonCell(factoryBebestible.call(null));
        listaBebestibles.getSelectionModel().selectFirst();
        
        //Inicializa la tabla alimentos y sus columnas con los datos cargados
        columnaAlimentosNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaAlimentosPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tablaAlimentos.setItems(FXCollections.observableArrayList(sistema.getAlimentosMesa(numeroMesa)));
        
        //Inicializa la tabla bebestibles y sus columnas con los datos cargados
        columnaBebestiblesNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaBebestiblesPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        tablaBebestibles.setItems(FXCollections.observableArrayList(sistema.getBebestiblesMesa(numeroMesa)));
    }
    
    //Habilita el botón eliminar si el usuario seleccionó un alimento de la tabla
    @FXML
    private void habilitarBotonEliminarAlimento()
    {
        if ((!tablaAlimentos.getItems().isEmpty()) && (!tablaAlimentos.getSelectionModel().isEmpty()))
            botonEliminarAlimento.setDisable(false);
    }
    
    //Habilita el botón eliminar si el usuario seleccionó un bebestible de la tabla
    @FXML
    private void habilitarBotonEliminarBebestible()
    {
        if ((!tablaBebestibles.getItems().isEmpty()) && (!tablaBebestibles.getSelectionModel().isEmpty()))
            botonEliminarBebestible.setDisable(false);
    }
    
    //Agrega un dato alimento, tanto a la tabla como a los datos de la mesa
    @FXML
    private void agregarAlimento() throws IDFormatoNoValidoException
    {      
        Alimento alimento=(Alimento) listaAlimentos.getValue();
        
        if (!sistema.verificarAlimento(alimento.getId()))
            return;

        //Agrega el alimento a la tabla
        tablaAlimentos.getItems().add(alimento);
        
        //Agrega el alimento al ArrayList de la mesa
        sistema.agregarPedido(numeroMesa,alimento.getId(),alimento.getPrecio());
        
        //Resta el stock de los ingredientes del alimento
        sistema.utilizarIngredientesAlimento(alimento.getId());
        actualizarSubTotal();
    }
    
    //Agrega un dato bebestible, tanto a la tabla como a los datos de la mesa
    @FXML
    private void agregarBebestible() throws StockNoValidoException
    {      
        Bebestible bebida=(Bebestible) listaBebestibles.getValue();
        
        if (!sistema.verificarBebestible(bebida.getId()))
        {
            alertaErrorAgregarBebestible(bebida.getNombre());
            return;
        }

        //Agrega el bebestible a la tabla
        tablaBebestibles.getItems().add(bebida);
        
        //Agrega el bebestible al ArrayList de la mesa
        sistema.agregarPedido(numeroMesa,bebida.getId(),bebida.getPrecio());
        
        //Resta uno al stock de bebestible
        sistema.utilizarBebestible(bebida.getId());
        actualizarSubTotal();
    }
    
    //Elimina un dato alimento, tanto de la tabla como de los datos de la mesa
    @FXML
    private void eliminarAlimento()
    {
        Alimento dato=(Alimento) tablaAlimentos.getSelectionModel().getSelectedItem();
        
        if (alertaEliminar(dato.getNombre()))
        {
            //Elimina el alimento de la tabla
            tablaAlimentos.getItems().remove(dato);
            
            //Elimina el alimento del ArrayList de la mesa
            sistema.eliminarPedido(numeroMesa, dato.getId(), dato.getPrecio());
            
            //Suma al stock las cantidades del ingrediente del alimento
            sistema.devolverIngredientesAlimento(dato.getId());
            actualizarSubTotal();
        }
    }
    
    //Elimina un dato bebestible, tanto de la tabla como de los datos mesa
    @FXML
    private void eliminarBebestible() throws StockNoValidoException
    {
        Bebestible dato=(Bebestible) tablaBebestibles.getSelectionModel().getSelectedItem();
        
        if (alertaEliminar(dato.getNombre()))
        {
            //Elimina el bebestible de la tabla
            tablaBebestibles.getItems().remove(dato);
            
            //Elimina el bebestible del ArrayList de la mesa
            sistema.eliminarPedido(numeroMesa, dato.getId(), dato.getPrecio());
            
            //Suma uno al stock de bebestibles
            sistema.devolverBebestible(dato.getId());
            actualizarSubTotal();
        }
    }
    
    //Cancela la venta devolviendo al stock todos los ingredientes y bebestibles
    @FXML
    private void cancelarVenta(ActionEvent event) throws StockNoValidoException, IDFormatoNoValidoException
    {  
        if (!alertaCancelar())
            return;
        
        ObservableList<Alimento> listaAlimento=tablaAlimentos.getItems();
        ObservableList<Bebestible> listaBebestible=tablaBebestibles.getItems();
        
        //Devuelve al stock original todos los ingredientes de cada alimento
        for (int i=0; i<listaAlimento.size(); i++)
            sistema.devolverIngredientesAlimento(listaAlimento.get(i).getId());
        
        //Devuelve al stock original todos los bebestibles
        for (int i=0; i<listaBebestible.size(); i++)
            sistema.devolverBebestible(listaBebestible.get(i).getId());
        
        sistema.cancelarVenta(numeroMesa);
        cargarVentanaAnterior(event);
    }
    
    //Abre una nueva ventana DividirCuenta, para determinar en cuantas personas se dividira la cuenta
    @FXML
    private void cargarDividirCuenta(ActionEvent event)
    {
        //Verifica que exista contenidos en las tablas, es decir, el cliente ha pedido algunos productos, falta customizar una alerta para esto
        if ((tablaAlimentos.getItems().isEmpty()) && (tablaBebestibles.getItems().isEmpty()))
            return;
        
        try
        {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/ventanas/menuVentas/DividirCuenta.fxml"));
            Parent root=(Parent) loader.load();
            Stage stage=new Stage();

            //Obtiene el controlador de DividirCuenta para enviar Cajero por parámetros
            DividirCuentaController controlador=(DividirCuentaController) loader.getController();
            controlador.inicializar(sistema,numeroMesa);

            //Se ajusta, carga y muestra la ventan emergente
            stage.setResizable(false);
            stage.setTitle("Pagar Cuenta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
            return;
        }
        
        //Se actualizan las tablas alimentos y bebestibles
        tablaAlimentos.setItems(FXCollections.observableArrayList(sistema.getAlimentosMesa(numeroMesa)));
        tablaBebestibles.setItems(FXCollections.observableArrayList(sistema.getBebestiblesMesa(numeroMesa)));
        
        //Se verifican si las tablas estan vacías, es decir, si finalizó la venta, si es así se lanza una alerta confirmando esto y se carga el MenuVentas
        if ((tablaAlimentos.getItems().isEmpty()) && (tablaBebestibles.getItems().isEmpty()))
        {
            Alert alerta=new Alert(Alert.AlertType.INFORMATION);
            
            //Se ajusta y lanza la alerta
            alerta.setTitle("Estado de Venta");
            alerta.setHeaderText("Éxito al realizar la venta");
            alerta.setContentText("Se ha completado la venta, con todos sus pagos correctamente!");
            alerta.showAndWait();
            
            //Carga el MenuVentas
            cargarVentanaAnterior(event);
        }
           
    }
       
    //Carga la ventana anterior, es decir, Menu Ventas
    @FXML
    private void cargarVentanaAnterior(ActionEvent event)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/MenuVentas.fxml"));
            Parent tableViewParent=loader.load();
            Scene escena=new Scene(tableViewParent);

            //Obtiene el controlador de MenuVentas para enviar Cajero por parámetros
            MenuVentasController controlador=(MenuVentasController) loader.getController();
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
    
    //Actualiza el label con el subTotal actual de la mesa
    private void actualizarSubTotal()
    {
        subTotal.setText(Integer.toString(sistema.getSubTotalMesa(numeroMesa)));
    }
    
    //Muestra un cuadro de dialogo, donde pide confirmación para eliminar el elemento, retornando un booleano
    private boolean alertaEliminar(String nombre)
    {
        Alert ventana=new Alert(Alert.AlertType.CONFIRMATION);
        String contenido="Pedido: "+nombre;
        
        ventana.setTitle("Confirmar Eliminar Pedido");
        ventana.setHeaderText(contenido);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Realmente Quieres Eliminar este Pedido?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
            return true;
        
        return false;
    }
    
    
    //Muestra un cuadro de dialogo de error, porque no existe stock del bebestible
    private void alertaErrorAgregarBebestible(String nombre)
    {
        Alert ventana=new Alert(Alert.AlertType.ERROR);
        ventana.setTitle("¡ERROR!");
        ventana.setHeaderText("ERROR: No se puede agregar "+nombre);
        ventana.setContentText("No se puede agregar porque no hay stock disponible.");
        ventana.initStyle(StageStyle.UTILITY);
        java.awt.Toolkit.getDefaultToolkit().beep();
        ventana.showAndWait();
    }
    
    
    //Muestra un cuadro de dialogo, donde pide confirmación de si desea cancelar la venta, retornando un booleano
    private boolean alertaCancelar()
    {
        Alert ventana=new Alert(Alert.AlertType.CONFIRMATION);
        ventana.setTitle("Confirmar Cancelar Venta");
        ventana.setHeaderText(null);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Estas seguro que desea cancelar la venta?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
            return true;
        
        return false;
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