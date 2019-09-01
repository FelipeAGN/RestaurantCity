package datos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

public class ListaVentas implements Almacenamiento
{
    private final ArrayList<Venta> ventas;
    private int numeroMesa;
    
    public ListaVentas(int numeroMesa)
    {
        this.numeroMesa=numeroMesa;
        ventas=new ArrayList<>();
        cargarDatos();
    }
    
    //Agrega una venta
    public void realizarVenta(String cajero, int subTotal, int propina, MetodoPago metodo)
    {
        Venta dato=new Venta(cajero, subTotal,propina,metodo);
        ventas.add(dato);
        guardarDatos();
    }
    
    //Retorna el total de todas las ventas realizadas
    public int getTotalVentas()
    {
        int total=0;
        
        for (int i=0; i<ventas.size(); i++)
            total+=ventas.get(i).getTotal();
        
        return total;
    }
    
    //Retorna un dato tipo LocalDateTime, dado dos strings fecha y hora con un formato determinado
    private LocalDateTime transformarFecha(String fecha, String tiempo)
    {
        int dia, mes, año, hora, minutos, segundos;

        dia=Integer.parseInt(fecha.substring(0,2));
        mes=Integer.parseInt(fecha.substring(3,5));
        año=Integer.parseInt(fecha.substring(6,10));
        hora=Integer.parseInt(tiempo.substring(0,2));
        minutos=Integer.parseInt(tiempo.substring(3,5));
        segundos=Integer.parseInt(tiempo.substring(6,8));
        return LocalDateTime.of(año,mes,dia,hora,minutos,segundos);
    }

    @Override
    public final void cargarDatos() 
    {
        try
        {
            String ruta="src/archivos/ventas/mesa-"+Integer.toString(numeroMesa)+".ods";
            File archivo=new File(ruta);            
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            String cajero,fecha,hora;
            int subTotal, propina;
            MetodoPago metodo;
            
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {
                celda=hojaCalculo.getSheet(0).getCellAt(0,i);
                cajero=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                fecha=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(2,i);
                hora=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(3,i);
                subTotal=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(4,i);
                propina=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(5,i);
                
                if (celda.getTextValue().compareTo("Cheque")==0)
                    metodo=MetodoPago.CHEQUE;
                else if (celda.getTextValue().compareTo("Tarjeta de Crédito")==0)
                    metodo=MetodoPago.CREDITO;
                else if (celda.getTextValue().compareTo("Tarjeta de Débito")==0)
                    metodo=MetodoPago.DEBITO;
                else
                    metodo=MetodoPago.EFECTIVO;
                         
                Venta dato=new Venta(cajero,transformarFecha(fecha,hora),subTotal,propina,metodo);
                ventas.add(dato);
            }
        }
        catch (IOException e){}
    }
    

    @Override
    public void guardarDatos() 
    {
        //Guardar los datos en un fichero de planilla
        try
        {   
            //Crea e inicializa un Array de String de columnas y una matriz de String para los datos
            String[] columnas=new String[] {"Cajero","Fecha","Hora","SubTotal","Propina","Metodo de Pago"};
            String[][] datos=new String[ventas.size()][];
            Venta dato;
                 
            //Ciclo donde agrega todos los datos de una venta en una fila de la matriz
            for (int i=0; i<ventas.size(); i++)
            {
                dato=ventas.get(i);
                datos[i]=new String[]{dato.getCajero(),dato.getFecha(),dato.getHora(),Integer.toString(dato.getSubTotal()),Integer.toString(dato.getPropina()),dato.getMetodoPago()};
            }
            
            //Crea una tabla y la inicializa con los datos y columnas anteriormente creadas
            String ruta="src/archivos/ventas/mesa-"+Integer.toString(numeroMesa)+".ods";
            File archivo=new File(ruta);
            TableModel tabla=new DefaultTableModel(datos,columnas);
        
            //Guarda la tabla anterior en un archivo de planilla tipo .ods
            SpreadSheet.createEmpty(tabla).saveAs(archivo);
        }
        catch (IOException e) {}
    }

    @Override
    public void guardarInventario(String ruta){}
}
