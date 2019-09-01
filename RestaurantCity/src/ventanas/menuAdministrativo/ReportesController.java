package ventanas.menuAdministrativo;

import datos.Cajero;
import datos.TipoInventario;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartCanvas;
import ventanas.MenuAdministradorController;

public class ReportesController implements Initializable
{
    @FXML
    private AnchorPane panel;
    
    private Cajero sistema;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    public void inicializar(Cajero sistema)
    {
        this.sistema=sistema;
    }
    
    //Carga en la pantalla el gráfico de alimentos
    @FXML
    private void cargarGraficoAlimentos()
    {
        cargarVisualizarReporte(TipoInventario.ALIMENTO,sistema.generarReporteAlimentos());
    }
    
    //Carga en la pantalla el gráfico de bebestibles
    @FXML
    private void cargarGraficoBebestibles()
    {
        cargarVisualizarReporte(TipoInventario.BEBESTIBLE,sistema.generarReporteBebestibles());
    }

    //Carga en la pantalla el gráfico de ventas
    @FXML
    private void cargarGraficoMesas()
    {
        cargarVisualizarReporte(TipoInventario.PRODUCTO,sistema.generarReporteMesas());
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
    
    private void cargarVisualizarReporte(TipoInventario tipo,JFreeChart reporte)
    {
        try
        {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("VisualizarReporte.fxml"));
            Parent root=(Parent) loader.load();
            Stage stage=new Stage();

            //Obtiene el controlador de VisualizarReporte para enviar el reporte por parámetros
            VisualizarReporteController controlador=(VisualizarReporteController) loader.getController();
            controlador.inicializar(sistema,tipo,new ChartCanvas(reporte));

            //Se ajusta, carga y muestra la ventana emergente
            stage.setResizable(false);
            stage.setTitle("Visualizar Reporte");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();	
        }
        catch (IOException | IllegalStateException excepcion)
        {
            alertaExcepcion(excepcion);
        }  
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
