package datos;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import javax.mail.MessagingException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import restaurantcity.Email;

public class ListaMesas implements Almacenamiento, Reportable
{
    private final HashMap<Integer,Mesa> mesas;
    //private final ListaVentas ventas;
    
    public ListaMesas()
    {
        mesas=new HashMap<>();
        cargarDatos();              //Carga los datos del archivo, si es que existe
    }
    
    public void agregarMesa(Mesa dato)
    {
        mesas.put(dato.getNumeroMesa(),dato);
    }
    
    //Agrega un pedido (alimento o bebestible) a una mesa en especifico, además de sumar el precio al subtotal de la mesa
    public boolean agregarPedido(int numeroMesa, String id, int precio)
    {
        if (!mesas.containsKey(numeroMesa))
            return false;
        
        if (id.charAt(0)=='A')
            mesas.get(numeroMesa).agregarAlimento(id,precio);
        else
            mesas.get(numeroMesa).agregarBebestible(id,precio);
        
        return true;
    }
    
    public boolean eliminarMesa(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return false;
        
        mesas.remove(numeroMesa);
        return true;
    }
    
    //Elimina un pedido (alimento o bebestible) de una mesa en especifico, además de restar el precio al subtotal de la mesa
    public boolean eliminarPedido(int numeroMesa, String id, int precio)
    {
        if (!mesas.containsKey(numeroMesa))
            return false;
        
        if (id.charAt(0)=='A')
            mesas.get(numeroMesa).eliminarAlimento(id,precio);
        else
            mesas.get(numeroMesa).eliminarBebestible(id,precio);
        
        return true;
    }
    
    public boolean modificarNumeroMesa(int numeroMesa, int nuevoNumeroMesa)
    {
        if (mesas.containsKey(nuevoNumeroMesa))
            return false;
        
        Mesa dato=mesas.remove(numeroMesa);
        
        dato.setNumeroMesa(nuevoNumeroMesa);
        mesas.put(nuevoNumeroMesa,dato);
        return true;
    }
    
    //Modifica la posición en la pantalla de una mesa en específico del HashMap
    public void modificarPosicionMesa(int numeroMesa, double posicionX, double posicionY)
    {
        if (!mesas.containsKey(numeroMesa))
            return;
        
        mesas.get(numeroMesa).setPosicion(posicionX,posicionY);
    }
    
    //Retorna un ArrayList con todos los alimentos actuales de una mesa en especifico
    public ArrayList<String> getAlimentos(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return null;
        
        return mesas.get(numeroMesa).getAlimentos();
    }
    
    //Retorna un ArrayList con todos los bebestibles actuales de una mesa en especifico
    public ArrayList<String> getBebestibles(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return null;
        
        return mesas.get(numeroMesa).getBebestibles();
    }
    
    
    public int getCantidadProductos(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return 0;
        
        return mesas.get(numeroMesa).getCantidadProductos();
    }
    
    
    //Retorna un boolean si está disponible una mesa en específico
    public boolean isMesaDisponible(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return false;
        
        return mesas.get(numeroMesa).isDisponible();
    }
    
    //Retorna un boolean si está reservada una mesa en específico
    public boolean isMesaReservada(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return false;
        
        return mesas.get(numeroMesa).isReservada();
    }
    
    
    public ArrayList<Mesa> getMesa()
    {
        ArrayList<Mesa> lista=new ArrayList<>(mesas.size());
        
        for (Mesa dato: mesas.values())
            lista.add(dato);
        
        return lista;
    }
    
    public Mesa getMesa(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return null;
        
        return mesas.get(numeroMesa);
    }
    
    //Obtiene el proximo numero de mesa correspondiente, para agregar una nueva mesa a futuro
    public int getProximoNumeroMesa()
    {
        for (int i=1; i<=mesas.size(); i++)
        {
            if (!mesas.containsKey(i))
                return i;
        }
        
        return mesas.size()+1;
    }
    
    //Retorna el subtotal actual de una mesa en especifico
    public int getSubTotal(int numeroMesa)
    {
        if (!mesas.containsKey(numeroMesa))
            return 0;
        
        return mesas.get(numeroMesa).getSubTotal();
    }
        
    //Agrega un nuevo dato de tipo venta al ArrayList ventas y vacía las LinkedLists de alimentos y bebestibles
    //reseteando todos los atributos por default de una mesa en especifico
    public void realizarVenta(int numeroMesa, String cajero,int propina, MetodoPago metodoPago)
    {
        mesas.get(numeroMesa).realizarVenta(cajero,propina,metodoPago);
    }
    
    public void realizarVenta(int numeroMesa, String cajero, ArrayList<String> productos, int subTotal, int propina, MetodoPago metodo)
    {
        mesas.get(numeroMesa).realizarVenta(cajero,productos,subTotal,propina,metodo);
    }
    
    //Vacía las LinkedList de alimentos y bebestibles, reseteando todos los atributos por default de una mesa en especifico
    public void cancelarVenta(int numeroMesa)
    {
        mesas.get(numeroMesa).cancelarVenta();
    }
    
    
    public boolean reservarMesa(int numeroMesa, String nombre)
    {
        return mesas.get(numeroMesa).reservarMesa(nombre);
    }
    
    public boolean cancelarReservacion(int numeroMesa)
    {
        return mesas.get(numeroMesa).cancelarReservacion();
    }
    
    //Metodo encargado de almacenar todos los datos de tipo Mesas en el ArrayList leídos desde un archivo
    @Override
    public final void cargarDatos()
    {
        try
        {
            File archivo=new File("src/archivos/mesas.ods"); 
            
            //Si el archivo no existe no carga los datos
            if (!archivo.exists())
                return;
            
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            int numeroMesa;
            double posicionX, posicionY;
        
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {
                celda=hojaCalculo.getSheet(0).getCellAt(0,i);
                numeroMesa=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                posicionX=Double.parseDouble(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(2,i);
                posicionY=Double.parseDouble(celda.getTextValue());
                Mesa dato=new Mesa(numeroMesa,posicionX,posicionY);
                mesas.put(numeroMesa,dato);
            }
        }
        catch (IOException e) {}
    }
    
    //Guarda toda la lista de mesas con sus respectivos datos en una tabla para luego ser guardado en un archivo de planilla
    @Override
    public void guardarDatos()
    {
        try
        {
            //Crea e inicializa un Array de String de columnas y una matriz de String para los datos
            String[] columnas=new String[] {"Numero","X","Y"};
            String[][] datos=new String[mesas.size()][3];
            int i=0;
        
            for (Mesa dato: mesas.values())
            {
                datos[i]=new String[]{Integer.toString(dato.getNumeroMesa()),Double.toString(dato.getPosicionX()),Double.toString(dato.getPosicionY())};
                i++;
            }
        
            //Crea una tabla y la inicializa con los datos y columnas anteriormente creadas
            File archivo=new File("src/archivos/mesas.ods");
            TableModel tabla=new DefaultTableModel(datos,columnas);
        
            //Guarda la tabla anterior en un archivo de planilla tipo .ods
            SpreadSheet.createEmpty(tabla).saveAs(archivo);
        }
        catch (IOException e) {}
    }

    @Override
    public JFreeChart generarReporte()
    {
        //Crea e inicializa una categoría para almacenar los datos para gráficar
        DefaultCategoryDataset categoria=new DefaultCategoryDataset();
        ArrayList<Mesa> lista=getMesa();
                
        //Dado un ArrayList de bebestibles, guarda los datos útiles de los bebestibles en la categoria,
        //es decir, el nombre y la cantidad de veces que se vendió
        for (int i=0; i<lista.size(); i++)
            categoria.setValue(lista.get(i).getTotalVentas(),"Mesa "+lista.get(i).getNumeroMesa(),"");
        
        //Con los datos recolectados crea y retorna un gráfico de barras
        return ChartFactory.createBarChart("TOTAL VENTAS MESAS","Mesas","Total",categoria);
    }
    
    //Crea un archivo PDF a partir del gráfico generado por JFreeChart
    private PDFDocument generarPDF()
    {
        //Crea e inicializa un nuevo archivo PDF
        PDFDocument archivo=new PDFDocument();
        Page pagina=archivo.createPage(new Rectangle(612, 468));    
        PDFGraphics2D g2=pagina.getGraphics2D();
        archivo.setTitle("Reporte Ventas");
        archivo.setAuthor("Restaurant City");
        
        //Dibuja el gráfico generado anteriormente en el archivo pdf
        generarReporte().draw(g2, new Rectangle(0, 0, 612, 468)); 
        return archivo;
    }
    
    @Override
    public void guardarReporte(String ruta)
    {
        generarPDF().writeToFile(new File(ruta));
    }

    //Envia el archivo PDF generado por correo electrónico
    @Override
    public void enviarReporte(String destino)
    {
        try
        {
            Email.enviarEmail(destino,generarPDF(),"reporte ventas.pdf");
        }
        catch (MessagingException excepcion)
        {
            Alert ventana=new Alert(Alert.AlertType.ERROR);
            ventana.setTitle("¡ERROR!");
            ventana.setHeaderText("ERROR: No se pudo enviar el correo");
            ventana.initStyle(StageStyle.UTILITY);
            java.awt.Toolkit.getDefaultToolkit().beep();
            ventana.showAndWait();
        }
    }

    @Override
    public void guardarInventario(String ruta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
