package datos;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.Page;
import excepciones.IDFormatoNoValidoException;
import excepciones.PrecioNoValidoException;
import excepciones.StockNoValidoException;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
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


public class ListaAlimentos implements Almacenamiento, Reportable
{
    private final HashMap<String,Alimento> alimentos;
    
    public ListaAlimentos()
    {
        alimentos=new HashMap<>();
        cargarDatos();              //Carga todos los alimentos desde un archivo por defecto
    }
    
    //Agrega un dato alimento al HashMap, retornando true si se agrego correctamente
    public boolean agregarAlimento(Alimento dato)
    {
        if (alimentos.put(dato.getId(),dato)==null)
            return false;
        
        return true;
    }
    
    //Elimina un alimento (según ID), retornando true si se elimino correctamente
    public boolean eliminarAlimento(String id)
    {
        if (alimentos.remove(id)==null)
            return false;
        
        return true;
    }
    
    //Modifica el nombre de un alimento en específico del HashMap
    public void modificarNombreAlimento(String id, String nombre)
    {
        if (!alimentos.containsKey(id))
            return;
        
        alimentos.get(id).setNombre(nombre);
    }
    
    //Modifica el precio de un alimento en específico del HashMap
    public void modificarPrecioAlimento(String id, int precio) throws PrecioNoValidoException
    {
        if (!alimentos.containsKey(id))
            return;
        
        alimentos.get(id).setPrecio(precio);
    }
    
    //Agrega un ingrediente a un alimento en específico (según ID), retornando true si se agrego correctamente
    public boolean agregarIngredienteAlimento(String id, Stock ingrediente) throws IDFormatoNoValidoException, StockNoValidoException
    {
        if (!alimentos.containsKey(id))
            return false;
        
        return alimentos.get(id).agregarIngrediente(ingrediente.getId(),ingrediente.getCantidad());
    }
    
    //Elimina un ingrediente en específico de un alimento en específico, retornando true si se llevo a cabo correctamente
    public boolean eliminarIngredienteAlimento(String id, String idIngrediente)
    {
        if (!alimentos.containsKey(id))
            return false;
        
        alimentos.get(id).eliminarIngrediente(idIngrediente);
        return true;
    }
    
    public void modificarCantidadIngredienteAlimento(String id, String idIngrediente, int cantidad) throws StockNoValidoException
    {
        if (!alimentos.containsKey(id))
            return;
        
        System.out.println("Se va a modificar");
        alimentos.get(id).modificarCantidadIngrediente(idIngrediente,cantidad);
    }
    
    //Retorna un ArrayList con todos los alimen throws IDFormatoNoValidoExceptiontos del HashMap
    public ArrayList<Alimento> getAlimentos()
    {
        ArrayList<Alimento> lista=new ArrayList<>(alimentos.size());
        
        for (Alimento dato: alimentos.values())
            lista.add(dato);
        
        return lista;
    }
    
    //Dado un ArrayList de IDs (String), retorna un ArrayList de Alimentos correspondientes
    public ArrayList<Alimento> getAlimentos(ArrayList<String> arreglo)
    {
        ArrayList<Alimento> lista=new ArrayList<>(arreglo.size());
        
        for (int i=0; i<arreglo.size(); i++)
            lista.add(alimentos.get(arreglo.get(i)));
        
        return lista;
    }
    
    //Retorna un ArrayList con los ingredientes de un alimento en específico (según ID)
    public ArrayList<Stock> getIngredientes(String id)
    {
        if (!alimentos.containsKey(id))
            return null;
        
        return alimentos.get(id).getIngredientes();
    }
            
    //Retorna un ArrayList con los nombres de los alimentos en donde se encuentra el ingrediente, 
    //si no se encuentra en ningun alimento retorna null
    public ArrayList<String> verificarIngrediente(String id)
    {
        ArrayList<String> lista;
        
        if (alimentos.isEmpty())
            return null;
        
        lista=new ArrayList<>(alimentos.size());
        
        for (Alimento dato: alimentos.values())
        {
            if (dato.buscarIngrediente(id))
                lista.add(dato.getNombre());
        }
        
        if (lista.size()>0)
            return lista;
        
        return null;
    }
    
    //Aumenta el contador de ventas en uno de un alimento en específico
    public boolean agregarVenta(String id)
    {
        if (!alimentos.containsKey(id))
            return false;
        
        alimentos.get(id).vender();
        return true;
    }
    
    //Decrementa el contador de ventas en uno de un alimento en específico
    public boolean devolver(String id)
    {
        if (!alimentos.containsKey(id))
            return false;
        
        alimentos.get(id).devolver();
        return true;
    }
    
    //Aumenta el contador de ventas de este alimento en uno
    public void vender(ArrayList<String> productos)
    {
        String dato;
        
        for (int i=0; i<productos.size(); i++)
        {
            dato=productos.get(i);
            
            if (dato.charAt(0)=='A')
                alimentos.get(dato).vender();
        }
    }
    
    //Metodo encargado de almacenar todos los datos de tipo Alimentos en el HashMap leídos desde un archivo
    @Override
    public final void cargarDatos()
    {
        try
        {
            File archivo=new File("src/archivos/alimentos.ods");        
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            String id, nombre, codigo;
            int precio, cantidad, cantidadIngredientes, vendidos;
        
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {
                celda=hojaCalculo.getSheet(0).getCellAt(0,i);
                nombre=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                id=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(2,i);
                precio=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(3,i);
                vendidos=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(4,i);
                cantidadIngredientes=Integer.parseInt(celda.getTextValue());
                LinkedList<Stock> lista=new LinkedList<>();
                
                //Recorre las siguientes N columnas, de en dos en dos obteniendo el ingrediente y su cantidad
                // y los va almacenando en un ArrayList
                          
                for (int j=5; j<(cantidadIngredientes*2)+5; j+=2)
                {
                    celda=hojaCalculo.getSheet(0).getCellAt(j,i);
                    codigo=celda.getTextValue();
                    celda=hojaCalculo.getSheet(0).getCellAt(j+1,i);
                    cantidad=Integer.parseInt(celda.getTextValue());
                    Stock ingrediente=new Stock(codigo,cantidad);
                    lista.add(ingrediente);
                }
                
                Alimento dato=new Alimento(nombre,id,precio,vendidos,lista);
                alimentos.put(id,dato);
            }
        }
        catch (IOException e) {} 
        catch (PrecioNoValidoException | IDFormatoNoValidoException | StockNoValidoException ex)
        {
            Logger.getLogger(ListaAlimentos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Obtiene la cantidad de columnas necesarias para realizar la tabla y guardar los datos en un archivo
    private int obtenerCantidadColumnas()
    {
        int mayor=0;
        
        for (Alimento dato: alimentos.values())
        {
            if (dato.getCantidadIngredientes()>mayor)
                mayor=dato.getCantidadIngredientes();
        }
        
        return (mayor*2)+5;
    }

    //Guarda toda la lista de alimentos con sus respectivos datos en una tabla para luego ser guardado en un archivo de planilla
    @Override
    public void guardarDatos()
    {
        try
        {   
            //Crea e inicializa un Array de String de columnas y una matriz de String para los datos
            String[] columnas=new String[obtenerCantidadColumnas()];
            String[][] datos=new String[alimentos.size()][];
            ArrayList<Stock> lista;
            int i=0;
            
            //Asigna los valores respectivos a las columnas 
            columnas[0]="Nombre";
            columnas[1]="ID";
            columnas[2]="Precio";
            columnas[3]="Vendidos";
            columnas[4]="Cantidad";
            columnas[5]="Ingredientes";
            columnas[6]="Cantidades";
            
            for (int j=7; j<obtenerCantidadColumnas(); j++)
                columnas[j]="";
            
            //Ciclo donde agrega todos los datos de un alimento en una fila de la matriz
            for (Alimento dato: alimentos.values())
            {
                lista=dato.getIngredientes();
                datos[i]=new String[(lista.size()*2)+5];
                datos[i][0]=dato.getNombre();
                datos[i][1]=dato.getId();
                datos[i][2]=Integer.toString(dato.getPrecio());
                datos[i][3]=Integer.toString(dato.getVendidos());
                datos[i][4]=Integer.toString(dato.getCantidadIngredientes());
                
                //Agrega los ingredientes y sus cantidades a los datos
                for (int j=5, k=0; k<lista.size(); j+=2, k++)
                {
                    datos[i][j]=lista.get(k).getId();
                    datos[i][j+1]=Integer.toString(lista.get(k).getCantidad());
                }
                
                i++;
            }
            
            //Crea una tabla y la inicializa con los datos y columnas anteriormente creadas
            File archivo=new File("src/archivos/alimentos.ods");
            TableModel tabla=new DefaultTableModel(datos,columnas);
        
            //Guarda la tabla anterior en un archivo de planilla tipo .ods
            SpreadSheet.createEmpty(tabla).saveAs(archivo);
        }
        catch (IOException e) {}
    }
    
    @Override
    public void guardarInventario(String ruta)
    {
        Document documento=new Document();
         
        try
        {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();
            documento.addTitle("Inventario Alimentos");
            documento.addAuthor("Restaurant City");
            PdfPTable tabla=new PdfPTable(3);
             
            //Agrega las celdas correspondientes
            tabla.addCell("Nombre");
            tabla.addCell("ID");
            tabla.addCell("Precio");
            
            for (Alimento dato: alimentos.values())
            {
                tabla.addCell(dato.getNombre());
                tabla.addCell(dato.getId());
                tabla.addCell(Integer.toString(dato.getPrecio()));
            }

            //Se crea una celda final
            PdfPCell celdaFinal=new PdfPCell(new Paragraph("Cantidad total de alimentos:  "+alimentos.size()));
            celdaFinal.setColspan(3);
            tabla.addCell(celdaFinal);  
            
            //Se guarda la tabla en el documento
            documento.add(tabla);
            documento.close();
        }
        catch(Exception excepcion)
        {
            Alert ventana=new Alert(Alert.AlertType.ERROR);
            ventana.setTitle("¡ERROR!");
            ventana.setHeaderText("ERROR: No se pudo guardar el archivo PDF");
            ventana.initStyle(StageStyle.UTILITY);
            java.awt.Toolkit.getDefaultToolkit().beep();
            ventana.showAndWait();
        }
    }
    
    //Genera un gráfico con los alimentos más vendidos y lo retorna
    @Override
    public JFreeChart generarReporte()
    {
        //Crea e inicializa una categoría para almacenar los datos para gráficar
        DefaultCategoryDataset categoria=new DefaultCategoryDataset();
        ArrayList<Alimento> lista=getAlimentos();
                
        //Dado un ArrayList de alimentos, guarda los datos útiles de los alimentos en la categoria,
        //es decir, el nombre y la cantidad de veces que se vendió
        for (int i=0; i<lista.size(); i++)
            categoria.setValue(lista.get(i).getVendidos(),lista.get(i).getNombre(),"");
        
        //Con los datos recolectados crea y retorna un gráfico de barras
        return ChartFactory.createBarChart("ALIMENTOS MAS VENDIDOS","Alimentos","Numero de Ventas",categoria);
    }
    
    //Crea un archivo PDF a partir del gráfico generado por JFreeChart
    private PDFDocument generarPDF()
    {
        //Crea e inicializa un nuevo archivo PDF
        PDFDocument archivo=new PDFDocument();
        Page pagina=archivo.createPage(new Rectangle(612, 468));    
        PDFGraphics2D g2=pagina.getGraphics2D();
        archivo.setTitle("Reporte Alimentos");
        archivo.setAuthor("Restaurant City");
        
        //Dibuja el gráfico generado anteriormente en el archivo pdf
        generarReporte().draw(g2, new Rectangle(0, 0, 612, 468));
        return archivo;
    }
    
    //Guarda en el disco el archivo PDF creado
    @Override
    public void guardarReporte(String ruta)
    {   
        generarPDF().writeToFile(new File(ruta));
    }

    //Envia el archivo PDF creado por correo electrónico
    @Override
    public void enviarReporte(String destino)
    {        
        try
        {
            Email.enviarEmail(destino,generarPDF(),"reporte alimentos.pdf");
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
}