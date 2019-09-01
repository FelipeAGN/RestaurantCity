package datos;

import org.jfree.chart.JFreeChart;

public interface Reportable
{
    public JFreeChart generarReporte();
    public void guardarReporte(String ruta);
    public void enviarReporte(String destino);
}
