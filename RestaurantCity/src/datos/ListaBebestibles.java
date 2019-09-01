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

public class ListaBebestibles implements Almacenamiento, Reportable
{
    private final HashMap<String,Bebestible> bebestibles;
    
    public ListaBebestibles()
    {
        bebestibles=new HashMap<>();
        cargarDatos();              //Carga los datos del archivo, si es que existe
    }
    
    //Agrega un bebestible al HashMap
    public boolean agregarBebestible(Bebestible dato)
    {
        if (bebestibles.put(dato.getId(),dato)==null)
            return false;
        
        return true;
    }
        
    //Elimina un bebestible del HashMap
    public boolean eliminarBebestible(String id)
    {
        if (bebestibles.remove(id)==null)
            return false;
        
        return true;
    }
    
    //Modifica el nombre de un bebestible en específico del HashMap
    public void modificarNombreBebestible(String id, String nombre)
    {
        if (!bebestibles.containsKey(id))
            return;
        
        bebestibles.get(id).setNombre(nombre);
    }
    
    //Modifica el precio de un bebestible en específico del HashMap
    public void modificarPrecioBebestible(String id, int precio) throws PrecioNoValidoException
    {
        if (!bebestibles.containsKey(id))
            return;
        
        bebestibles.get(id).setPrecio(precio);
    }
    
    //Modifica el stock de un bebestible en específico del HashMap
    public void modificarStockBebestible(String id, int stock) throws StockNoValidoException
    {
        if (!bebestibles.containsKey(id))
            return;
        
        bebestibles.get(id).setStock(stock);
    }
    
    //Retorna un ArrayList con todos los bebestibles del HashMap
    public ArrayList<Bebestible> getBebestibles()
    {
        ArrayList<Bebestible> lista=new ArrayList<>(bebestibles.size());
        
        for (Bebestible dato: bebestibles.values())
            lista.add(dato);
        
        return lista;
    }
    
    //Dado un ArrayList de IDs (String), retorna un ArrayList de Bebestibles correspondientes
    public ArrayList<Bebestible> getBebestibles(ArrayList<String> arreglo)
    {
        ArrayList<Bebestible> lista=new ArrayList<>(arreglo.size());
        
        for (int i=0; i<arreglo.size(); i++)
            lista.add(bebestibles.get(arreglo.get(i)));
        
        return lista;
    }
    
    //Retorna la cantidad de Bebestibles del HashMap
    public int getCantidad()
    {
        return bebestibles.size();
    }
    
    //Retorna true si se encuentra el bebestible según ID, false en caso contrario
    public boolean getDisponible(String id)
    {
        if (!bebestibles.containsKey(id))
            return false;
        
        return bebestibles.get(id).getDisponible(1);
    }
 
    //Dado un ArrayList de Stock de Bebestibles retorna true si existe stock para satisfacer la venta
    public boolean getDisponible(ArrayList<Stock> lista)
    {
        for (int i=0; i<lista.size(); i++)
        {
            //Retorna false si el ID del bebestible no se encuentra en la colección
            if (!bebestibles.containsKey(lista.get(i).getId()))
                return false;
            
            //Retorna false si no existe stock suficiente para realizar el Pedido
            if (!bebestibles.get(lista.get(i).getId()).getDisponible(lista.get(i).getCantidad()))
                return false;
        }
        
        return true;
    }
    
    //Retorna true si el bebestible fue restado del stock y de la ventas correctamente, false en caso contrario
    public boolean utilizar(String id) throws StockNoValidoException
    {
        if (!bebestibles.containsKey(id))
            return false;
        
        Bebestible dato=(Bebestible) bebestibles.get(id);
        
        dato.vender();
        dato.setStock(dato.getStock()-1);
        return true;
    }
    
    //Aumenta el contador de ventas de este alimento en uno
    public void vender(ArrayList<String> productos)
    {
        String dato;
        
        for (int i=0; i<productos.size(); i++)
        {
            dato=productos.get(i);
            
            if (dato.charAt(0)=='B')
                bebestibles.get(dato).vender();
        }
    }
    
    //Devuelve el bebestible al stock, es decir, se le suma uno al stock original y a la cantidad de ventas
    public boolean devolver(String id) throws StockNoValidoException
    {
        if (!bebestibles.containsKey(id))
            return false;
        
        Bebestible dato=(Bebestible) bebestibles.get(id);
        
        dato.devolver();
        dato.setStock(dato.getStock()+1);
        return true;
    }
     
    //Metodo encargado de almacenar todos los datos de tipo Bebestible en el HashMap leídos desde un archivo
    @Override
    public final void cargarDatos()
    {
        try
        {
            File archivo=new File("src/archivos/bebestibles.ods"); 
            
            //Si el archivo no existe no carga los datos
            if (!archivo.exists())
                return;
            
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            String id, nombre;
            int precio, stock, vendidos;
        
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
                stock=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(4,i);
                vendidos=Integer.parseInt(celda.getTextValue());
                Bebestible dato=new Bebestible(nombre,id,precio,stock,vendidos);
                bebestibles.put(id,dato);
            }
        }
        catch (IOException e) {} 
        catch (PrecioNoValidoException | StockNoValidoException | IDFormatoNoValidoException ex)
        {
            Logger.getLogger(ListaBebestibles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Guarda toda la lista de bebestibles con sus respectivos datos en una tabla para luego ser guardado en un archivo de planilla
    @Override
    public void guardarDatos()
    {
        try
        {
            //Crea e inicializa un Array de String de columnas y una matriz de String para los datos
            String[] columnas=new String[] {"Nombre","ID","Precio","Stock","Vendidos"};
            String[][] datos=new String[bebestibles.size()][5];
            int i=0;
        
            for (Bebestible dato: bebestibles.values())
            {
                datos[i]=new String[]{dato.getNombre(),dato.getId(),Integer.toString(dato.getPrecio()),Integer.toString(dato.getStock()),Integer.toString(dato.getVendidos())};
                i++;
            }
        
            //Crea una tabla y la inicializa con los datos y columnas anteriormente creadas
            File archivo=new File("src/archivos/bebestibles.ods");
            TableModel tabla=new DefaultTableModel(datos,columnas);
        
            //Guarda la tabla anterior en un archivo de planilla tipo .ods
            SpreadSheet.createEmpty(tabla).saveAs(archivo);
        }
        catch (IOException e) {}
    }
    
    //Genera un gráfico con los bebestibles más vendidos y lo retorna    
    @Override
    public JFreeChart generarReporte()
    {
        //Crea e inicializa una categoría para almacenar los datos para gráficar
        DefaultCategoryDataset categoria=new DefaultCategoryDataset();
        ArrayList<Bebestible> lista=getBebestibles();
                
        //Dado un ArrayList de bebestibles, guarda los datos útiles de los bebestibles en la categoria,
        //es decir, el nombre y la cantidad de veces que se vendió
        for (int i=0; i<lista.size(); i++)
            categoria.setValue(lista.get(i).getVendidos(),lista.get(i).getNombre(),"");
        
        //Con los datos recolectados crea y retorna un gráfico de barras
        return ChartFactory.createBarChart("BEBESTIBLES MAS VENDIDOS","Bebestibles","Numero de Ventas",categoria);
    }
    
    @Override
    public void guardarInventario(String ruta)
    {
        Document documento=new Document();
         
        try
        {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();
            documento.addTitle("Inventario Bebestibles");
            documento.addAuthor("Restaurant City");
            PdfPTable tabla=new PdfPTable(4);
             
            //Agrega las celdas correspondientes
            tabla.addCell("Nombre");
            tabla.addCell("ID");
            tabla.addCell("Precio");
            tabla.addCell("Stock");
            
            for (Bebestible dato: bebestibles.values())
            {
                tabla.addCell(dato.getNombre());
                tabla.addCell(dato.getId());
                tabla.addCell(Integer.toString(dato.getPrecio()));
                tabla.addCell(Integer.toString(dato.getStock()));
            }

            //Se crea una celda final
            PdfPCell celdaFinal=new PdfPCell(new Paragraph("Cantidad total de bebestibles:  "+bebestibles.size()));
            celdaFinal.setColspan(4);
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

    //Crea un archivo PDF a partir del gráfico generado por JFreeChart
    private PDFDocument generarPDF()
    {
        //Crea e inicializa un nuevo archivo PDF
        PDFDocument archivo=new PDFDocument();
        Page pagina=archivo.createPage(new Rectangle(612, 468));    
        PDFGraphics2D g2=pagina.getGraphics2D();
        archivo.setTitle("Reporte Bebestibles");
        archivo.setAuthor("Restaurant City");
        
        //Dibuja el gráfico generado anteriormente en el archivo pdf
        generarReporte().draw(g2, new Rectangle(0, 0, 612, 468)); 
        return archivo;
    }
    
    //Guarda en el disco el archivo PDF creado
    @Override
    public void guardarReporte(String ruta)
    {
        //Crea e inicializa un nuevo archivo PDF
        PDFDocument archivo=new PDFDocument();
        Page pagina=archivo.createPage(new Rectangle(612, 468));    
        PDFGraphics2D g2=pagina.getGraphics2D();
        archivo.setTitle("Reporte Bebestibles");
        archivo.setAuthor("Restaurant City");
        
        //Dibuja el gráfico generado anteriormente en el archivo pdf
        generarReporte().draw(g2, new Rectangle(0, 0, 612, 468));       
        archivo.writeToFile(new File(ruta));
    }

    //Envia el archivo PDF generado por correo electrónico
    @Override
    public void enviarReporte(String destino)
    {
        try
        {
            Email.enviarEmail(destino,generarPDF(),"reporte bebestibles.pdf");
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
