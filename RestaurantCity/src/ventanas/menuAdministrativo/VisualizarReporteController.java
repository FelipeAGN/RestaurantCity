package ventanas.menuAdministrativo;

import datos.Cajero;
import datos.TipoInventario;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.chart.fx.ChartCanvas;

public class VisualizarReporteController implements Initializable
{
    @FXML
    private AnchorPane panel;
    
    private Cajero sistema;
    private TipoInventario tipo;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){}

    public void inicializar(Cajero sistema, TipoInventario tipo, ChartCanvas grafico)
    {
        this.sistema=sistema;
        this.tipo=tipo;
        panel.getChildren().add(grafico);
        grafico.widthProperty().bind(panel.widthProperty());
        grafico.heightProperty().bind(panel.heightProperty());
    }
    
    @FXML
    private void enviarReporte(ActionEvent event)
    {
        TextInputDialog dialogo=new TextInputDialog("example@mail.com");
        dialogo.setTitle("Enviar Reporte");
        dialogo.setHeaderText("Por favor ingrese el correo a enviar el reporte!");
        dialogo.setContentText("E-mail:");

        //Se obtiene el correo a enviar el reporte
        Optional<String> resultado=dialogo.showAndWait();
        
        if (resultado.isPresent())
        {
            switch (tipo)
            {
                case ALIMENTO:
                    sistema.enviarReporteAlimentos(resultado.get());
                    break;
                    
                case BEBESTIBLE:
                    sistema.enviarReporteBebestibles(resultado.get());
                    break;
                
                case PRODUCTO:
                    sistema.enviarReporteMesas(resultado.get());
                    break;
                    
                default:
                    break;
            }
        }
    }
    
    @FXML
    private void guardarReporte(ActionEvent event)
    {
        FileChooser fileChooser=new FileChooser();
        
        //Ajusta el filtro de extension solo para archivos PDF
        FileChooser.ExtensionFilter extension=new FileChooser.ExtensionFilter("PDF - Portable Document Format (.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extension);
        fileChooser.setInitialFileName("reporte.pdf");
        File archivo=fileChooser.showSaveDialog(new Stage());
        
        if (archivo!=null)
        {
            switch (tipo)
            {
                case ALIMENTO:
                    sistema.guardarReporteAlimentos(archivo.getAbsolutePath());
                    break;
                    
                case BEBESTIBLE:
                    sistema.guardarReporteBebestibles(archivo.getAbsolutePath());
                    break;
                
                case PRODUCTO:
                    sistema.guardarReporteMesas(archivo.getAbsolutePath());
                    break;
                    
                default:
                    break;
            }
        }
    }
}
