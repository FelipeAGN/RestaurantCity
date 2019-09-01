package ventanas;

import datos.Cajero;
import datos.Mesa;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ventanas.menuVentas.AtenderMesaController;

public class MenuVentasController implements Initializable
{
    @FXML
    private AnchorPane panel;
    @FXML
    private Group root;
    
    private Cajero sistema;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
 
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
        
        ArrayList<Mesa> lista=sistema.getMesas();
        Label[] mesas=new Label[lista.size()];
        
        for (int i=0; i<lista.size(); i++)
        {
            Mesa dato=lista.get(i);
            mesas[i]=crearMesa(dato.getNumeroMesa(),dato.isDisponible(),dato.isReservada(),dato.getPosicionX(),dato.getPosicionY());
        }

        root.getChildren().addAll(mesas);
    }
    
    private Label crearMesa(int numero, boolean disponible, boolean reservada, double posicionX, double posicionY)
    {
        Label mesa=new Label();
        mesa.setId(Integer.toString(numero));
        mesa.setFont(Font.font("Liberation Sans", FontWeight.BOLD,60));
        mesa.setTextFill(Color.WHITE);
        mesa.setMinSize(98,70);
        mesa.setLayoutX(posicionX);
        mesa.setLayoutY(posicionY);
        mesa.setCursor(Cursor.HAND);
        mesa.setOnMouseClicked(mesaOnMouseClicked);
        
        //Se setea al texto del label el numero de mesa
        if (numero<10)
            mesa.setText("  "+Integer.toString(numero));
        else
            mesa.setText(" "+Integer.toString(numero));
        
        //Se define el color del label segun su estado, verde = disponible, amarillo = reservada y rojo = ocupada
        if (disponible)
            mesa.setStyle("-fx-background-color:#00FF00; -fx-background-radius:15;");
        else if (reservada)
            mesa.setStyle("-fx-background-color:#FFD800; -fx-background-radius:15;");
        else
            mesa.setStyle("-fx-background-color:#F01320; -fx-background-radius:15;");
        
        
        return mesa;
    }
    
    private void reservarMesa(Label mesa, int numeroMesa)
    {
        if (sistema.isMesaReservada(numeroMesa))
        {
            mesa.setStyle("-fx-background-color:#00FF00; -fx-background-radius:15;");
            sistema.cancelarReservacion(numeroMesa);
            return;
        }
        
        if (sistema.reservarMesa(numeroMesa,"Juan Perez"))
            mesa.setStyle("-fx-background-color:#FFD800; -fx-background-radius:15;");
        else
        {
            Alert ventana=new Alert(Alert.AlertType.ERROR);
        
            ventana.setTitle("¡ERROR!");
            ventana.setHeaderText("ERROR: N° de Mesa: "+numeroMesa);
            ventana.setContentText("La mesa ya se encuentra ocupada.");
            ventana.initStyle(StageStyle.UTILITY);
            java.awt.Toolkit.getDefaultToolkit().beep();
            ventana.showAndWait();
        }
    }
        
    //Carga la ventana de AtenderMesa con un identificador de mesa en específico
    private void cargarMesa(int numeroMesa)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/menuVentas/AtenderMesa.fxml"));
            Parent tableViewParent=loader.load();
            Scene escena=new Scene(tableViewParent);

            //Obtiene el controlador de AtenderMesa para enviar Cajero por parámetros
            AtenderMesaController controlador=(AtenderMesaController) loader.getController();
            controlador.inicializar(sistema,numeroMesa);

            Stage ventana=(Stage) panel.getScene().getWindow();
            ventana.setScene(escena);
            ventana.show();		
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }
    }
    
    @FXML
    private void mostrarAyuda(ActionEvent evento)
    {
        Alert ventana = new Alert(AlertType.INFORMATION);
        
        ventana.setTitle("Ayuda");
        ventana.setHeaderText("Información para el manejo de mesas");
        ventana.setContentText("Click Izquierdo: Realizar pedido en mesa.\n\nClick Derecho:  Reservar mesa.");
        ventana.showAndWait();
    }
        
    //Carga la ventana anterior, es decir, el Login
    @FXML
    private void cargarVentanaAnterior(ActionEvent event)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/Login.fxml"));
            Parent tableViewParent=loader.load();
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
    
    //Evento del mouse que al clickear con boton izquierdo en una mesa obtiene su ID (numero de mesa)
    //y llama al método cargarMesa() para cargar la nueva escena con la mesa correspondiente.
    //Al clickear con boton derecho sobre una mesa, llama al metodo reservarMesa() para reservarla si es que se puede
    EventHandler<MouseEvent> mesaOnMouseClicked=new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent evento)
        {
            int numeroMesa=Integer.parseInt(((Label)evento.getSource()).getId());
            
            if (evento.getButton().equals(MouseButton.PRIMARY))
                cargarMesa(numeroMesa);

            if (evento.getButton().equals(MouseButton.SECONDARY))
                reservarMesa((Label) evento.getSource(),numeroMesa); 
        }
    };
    
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
