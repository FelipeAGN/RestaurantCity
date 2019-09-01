package datos;

import org.jfree.chart.JFreeChart;

public interface Almacenamiento
{
    public void cargarDatos();
    public void guardarDatos();
    public void guardarInventario(String ruta);
}
