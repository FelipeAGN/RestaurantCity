package ventanas.menuAdministrativo;

import datos.Cajero;
import datos.Mesa;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ventanas.MenuAdministradorController;

public class AdministrarMesasController implements Initializable 
{
    @FXML
    private Group root;

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
    private Cajero sistema;
       
    @Override
    public void initialize(URL url, ResourceBundle rb){}
    
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
        
        ArrayList<Mesa> lista=sistema.getMesas();
        Label[] mesas=new Label[lista.size()];
        
        
        //Crea e inicializa un grupo de labels que representan una mesa en el sistema, con su respectivo
        //numero de mesa y posicion en la pantalla para luego poder ser modificado
        for (int i=0; i<lista.size(); i++)
        {
            Mesa dato=lista.get(i);
            mesas[i]=crearMesa(dato.getNumeroMesa(),dato.getPosicionX(),dato.getPosicionY());
        }

        root.getChildren().addAll(mesas);
    }
    
    //Carga la ventana anterior, es decir, el MenuAdministrador
    @FXML
    private void cargarVentanaAnterior(ActionEvent event)
    {
        guardarPosiciones();
        
        try
        {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/ventanas/MenuAdministrador.fxml"));
            Parent raiz=loader.load();
            Scene escena=new Scene(raiz);

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
    private void mostrarAyuda(ActionEvent evento)
    {
        Alert ventana = new Alert(AlertType.INFORMATION);
        
        ventana.setTitle("Ayuda");
        ventana.setHeaderText("Información para la Administración de Mesas");
        ventana.setContentText("Click Izquierdo: Arrastrar mesa para cambiar de posición.\n\nClick Derecho:  Eliminar mesa.\n\nDoble Click:      Modficar número de mesa.");
        ventana.showAndWait();
    }
    
    //Guarda los cambios realizados a las coordenadas en la posicion de la pantalla en cada mesa (label)
    private void guardarPosiciones()
    {
        Label dato;
        double posicionX, posicionY;
        ObservableList<Node> lista=root.getChildren();
        
        for (int i=0; i<lista.size(); i++)
        {
            dato=(Label) lista.get(i);
            posicionX=dato.getLayoutX()+dato.getTranslateX();
            posicionY=dato.getLayoutY()+dato.getTranslateY();
            sistema.modificarPosicionMesa(Integer.parseInt(dato.getId()),posicionX,posicionY);
            //sistema.getMesa(Integer.parseInt(dato.getId())).setPosicion(posicionX,posicionY);
        }
    }
    
    //Agrega una mesa tanto a los datos, como al grupo de labels en pantalla
    @FXML
    private void agregarMesa(ActionEvent event)
    {
        int numeroMesa=sistema.getProximoNumeroMesa();
        Label mesa=crearMesa(numeroMesa,88.0,109.0);
        Mesa dato=new Mesa(numeroMesa,88.0,109.0);
        
        sistema.agregarMesa(dato);
        root.getChildren().add(mesa);
    }
    
    //Crea un label personalizado que simboliza una mesa, con un numero de mesa y una ubicacion en la pantalla
    private Label crearMesa(int numero, double posicionX, double posicionY)
    {
        Label mesa=new Label();
        mesa.setId(Integer.toString(numero));
        mesa.setFont(Font.font("Liberation Sans", FontWeight.BOLD,60));
        mesa.setStyle("-fx-background-color:#00FF00; -fx-background-radius:15;");
        mesa.setTextFill(Color.WHITE);
        mesa.setMinSize(98,70);
        mesa.setLayoutX(posicionX);
        mesa.setLayoutY(posicionY);
        mesa.setCursor(Cursor.OPEN_HAND);
        mesa.setOnMousePressed(mesaOnMousePressedEventHandler);
        mesa.setOnMouseDragged(mesaOnMouseDraggedEventHandler);
        mesa.setOnMouseClicked(mesaOnMouseClickedEventHandler);
        mesa.setOnMouseReleased(mesaOnMouseReleasedEventHandler);
        
        if (numero<10)
            mesa.setText("  "+Integer.toString(numero));
        else
            mesa.setText(" "+Integer.toString(numero));
        
        return mesa;
    }
    
    //Muestra un cuadro de dialogo, donde pide confirmación para eliminar la mesa,
    //si se confirma la elimina tanto de los datos como del grupo de labels
    private void alertaEliminarMesa(int numeroMesa)
    {
        Alert ventana=new Alert(Alert.AlertType.CONFIRMATION);
        
        ventana.setTitle("Confirmar Eliminar Mesa");
        ventana.setHeaderText("Mesa N°: "+numeroMesa);
        ventana.initStyle(StageStyle.UTILITY);
        ventana.setContentText("¿Realmente Quieres Eliminar esta Mesa?");
        
        
        Optional<ButtonType> opcion=ventana.showAndWait();
        
        if (opcion.get()==ButtonType.OK)
        {
            ObservableList<Node> lista=root.getChildren();
            
            for (int i=0; i<lista.size(); i++)
            {
                if (Integer.parseInt(lista.get(i).getId())==numeroMesa)
                {
                    lista.remove(i);
                    break;
                }
            }
            
            sistema.eliminarMesa(numeroMesa);
        }   
    }
    
    private void alertaModificarMesa(Label mesa,int numeroMesa)
    {
        TextInputDialog dialogo=new TextInputDialog(Integer.toString(numeroMesa));
        
        dialogo.setTitle("Modificar Mesa");
        dialogo.setHeaderText("Mesa N°: "+numeroMesa);
        dialogo.setContentText("Nuevo N° de mesa:");
        
        Optional<String> resultado=dialogo.showAndWait();
        
        if (resultado.isPresent())
        {
            if (!verificarEntradaNumerica(resultado.get()))
            {
                alertaErrorEntrada();
                return;
            }
            
            int nuevoNumero=Integer.parseInt(resultado.get());
            
            if (sistema.modificarNumeroMesa(numeroMesa,nuevoNumero))
            {
                mesa.setId(Integer.toString(nuevoNumero));
                
                if (nuevoNumero<10)
                    mesa.setText("   "+Integer.toString(nuevoNumero));
                else
                    mesa.setText("  "+Integer.toString(nuevoNumero));
            }
            else
                alertaErrorNumeroMesa(nuevoNumero);
        }
    }
    
    private void alertaErrorEntrada()
    {
        Alert ventana=new Alert(AlertType.ERROR);
        
        ventana.setTitle("¡ERROR!");
        ventana.setHeaderText("ERROR: Entrada no válida");
        ventana.setContentText("La entrada ingresada no corresponde a un valor númerico válido.");
        ventana.initStyle(StageStyle.UTILITY);
        java.awt.Toolkit.getDefaultToolkit().beep();
        ventana.showAndWait();
    }
    
    private void alertaErrorNumeroMesa(int numeroMesa)
    {
        Alert ventana=new Alert(AlertType.ERROR);
        
        ventana.setTitle("¡ERROR!");
        ventana.setHeaderText("ERROR: N° de Mesa: "+numeroMesa);
        ventana.setContentText("El numero de mesa ingresado ya lo contiene otra mesa.");
        ventana.initStyle(StageStyle.UTILITY);
        java.awt.Toolkit.getDefaultToolkit().beep();
        ventana.showAndWait();
    }
    
    
    EventHandler<MouseEvent> mesaOnMousePressedEventHandler=new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent evento)
        {
            orgSceneX=evento.getSceneX();
            orgSceneY=evento.getSceneY();
            orgTranslateX=((Label)(evento.getSource())).getTranslateX();
            orgTranslateY=((Label)(evento.getSource())).getTranslateY();
            ((Label) evento.getSource()).setCursor(Cursor.MOVE);
        }
    };
     
    EventHandler<MouseEvent> mesaOnMouseDraggedEventHandler=new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent evento)
        {
            double offsetX=evento.getSceneX()-orgSceneX;
            double offsetY=evento.getSceneY()-orgSceneY;
            double newTranslateX=orgTranslateX+offsetX;
            double newTranslateY=orgTranslateY+offsetY;
            double posicionX=((Label)evento.getSource()).getLayoutX()+newTranslateX;
            double posicionY=((Label)evento.getSource()).getLayoutY()+newTranslateY;
            
            if ((posicionX>=88) && (posicionX<=828) && (posicionY>=109) && (posicionY<=539))
            {
                ((Label)(evento.getSource())).setTranslateX(newTranslateX);
                ((Label)(evento.getSource())).setTranslateY(newTranslateY);
            }
        }
    };
    
    EventHandler<MouseEvent> mesaOnMouseClickedEventHandler=new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent evento)
        {
            int numeroMesa=Integer.parseInt(((Label) evento.getSource()).getId());
                    
            if (evento.getButton().equals(MouseButton.PRIMARY))
            {
                if (evento.getClickCount()==2)
                    alertaModificarMesa((Label) evento.getSource(),numeroMesa);
            }
            
            if (evento.getButton().equals(MouseButton.SECONDARY))
                alertaEliminarMesa(numeroMesa);
        }
    };
    
    EventHandler<MouseEvent> mesaOnMouseReleasedEventHandler=new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent evento)
        {
            ((Label) evento.getSource()).setCursor(Cursor.OPEN_HAND);
        }
    };
    
    //Verifica si la entrada de texto es un valor numerico mayor a 0
    private boolean verificarEntradaNumerica(String entrada)
    {   
        if ((entrada.matches("[0-9]+")) && (entrada.length()>0))
        {
            if ((Integer.parseInt(entrada)>0) && (Integer.parseInt(entrada)<100))
                return true;
        }
        
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
