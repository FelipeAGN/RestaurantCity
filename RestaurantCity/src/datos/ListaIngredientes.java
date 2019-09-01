package datos;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import excepciones.IDFormatoNoValidoException;
import excepciones.StockNoValidoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

public class ListaIngredientes implements Almacenamiento
{
    private final HashMap<String,Ingrediente> ingredientes;
    
    public ListaIngredientes()
    {
        ingredientes=new HashMap<>();
        cargarDatos();      //Carga los datos del archivo, si es que existe
    }
    
    //Agrega un ingrediente al HashMap
    public boolean agregarIngrediente(Ingrediente dato)
    {
        if (ingredientes.put(dato.getId(),dato)==null)
            return false;
        
        return true;
    }
        
    //Elimina un ingrediente del HashMap según id
    public boolean eliminarIngrediente(String id)
    {
        if (ingredientes.remove(id)==null)
            return false;
        
        return true;
    }
    
    //Modifica el nombre de un ingrediente en específico del HashMap
    public void modificarNombreIngrediente(String id, String nombre)
    {
        if (!ingredientes.containsKey(id))
            return;
        
        ingredientes.get(id).setNombre(nombre);
    }
    
    //Modifica el stock de un ingrediente en específico del HashMap
    public void modificarStockIngrediente(String id, int stock) throws StockNoValidoException
    {
        if (!ingredientes.containsKey(id))
            return;
        
        ingredientes.get(id).setStock(stock);
    }
    
    //Retorna un ArrayList con todos los ingredientes del HashMap
    public ArrayList<Ingrediente> getIngredientes()
    {
        ArrayList<Ingrediente> lista=new ArrayList<>(ingredientes.size());
                
        for (Ingrediente dato: ingredientes.values())
            lista.add(dato);
        
        return lista;
    }
    
    //Retorna un ArrayList de Stock de Ingredientes
    public ArrayList<Stock> getIngredientes(ArrayList<Stock> lista)
    {
        //Agrega el nombre del ingrediente correspondiente a esa ID
        for (int i=0; i<lista.size(); i++)
            lista.get(i).setNombre(ingredientes.get(lista.get(i).getId()).getNombre());
        
        return lista;
    }
    
    
    //Retorna un ingrediente según la ID
    public Ingrediente getIngrediente(String id)
    {
        if (!ingredientes.containsKey(id))
            return null;
        
        return ingredientes.get(id);
    }

    //Dado un ArrayList Pedido (que almacena las IDs de los ingredientes, junto a sus cantidades a utilizar)
    //retorna true si existen todas las cantidades de ingredientes necesarias para preparar el producto
    public boolean getDisponible(ArrayList<Stock> lista)
    {
        ArrayList<String> nombres=new ArrayList<>();
        
        for (int i=0; i<lista.size(); i++)
        {
            if (!ingredientes.get(lista.get(i).getId()).getDisponible(lista.get(i).getCantidad()))
                nombres.add(ingredientes.get(lista.get(i).getId()).getNombre());
        }
        
        if (nombres.isEmpty())
            return true;
        
        //Muestra un cuadro de alerta con todos los ingredientes que no tienen stock suficiente para ser preparados
        Alert ventana=new Alert(Alert.AlertType.ERROR);
        String contenido="Los siguientes ingredientes de este alimento no tienen stock:\n\n";
        
        for (int i=0; i<nombres.size(); i++)
            contenido+=nombres.get(i)+"\n";
        
        ventana.setTitle("¡ERROR!");
        ventana.setHeaderText("ERROR: No se puede agregar el alimento");
        ventana.setContentText(contenido);
        ventana.initStyle(StageStyle.UTILITY);
        java.awt.Toolkit.getDefaultToolkit().beep();
        ventana.showAndWait();
    
        return false;
    }
    
    //Dado un ArrayList Pedido (que almacena las IDs de los ingredientes, junto a sus cantidades a utilizar)
    //resta todas las cantidades de ingredientes del stock
    public void utilizar(ArrayList<Stock> lista)
    {        
        for (int i=0; i<lista.size(); i++)
            ingredientes.get(lista.get(i).getId()).utilizar(lista.get(i).getCantidad());
    }
    
    //Dado un ArrayList Pedido (que almacena las IDs de los ingredientes, junto a sus cantidades a devolver)
    //suma todas las cantidades de ingredientes del stock
    public void devolver(ArrayList<Stock> lista)
    {        
        for (int i=0; i<lista.size(); i++)
            ingredientes.get(lista.get(i).getId()).devolver(lista.get(i).getCantidad());
    }
    
    
    //Metodo encargado de almacenar todos los datos de tipo Ingrediente en el HashMap leídos desde un archivo
    @Override
    public final void cargarDatos()
    {
        try
        {
            File archivo=new File("src/archivos/ingredientes.ods");
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            String id;
            String nombre;
            int stock;
            Unidad unidad;
        
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {
                celda=hojaCalculo.getSheet(0).getCellAt(0,i);
                nombre=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                id=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(2,i);
                stock=Integer.parseInt(celda.getTextValue());
                celda=hojaCalculo.getSheet(0).getCellAt(3,i);
                //unidad=Unidad.valueOf(celda.getTextValue());
                
                if (celda.getTextValue().compareTo("Gramos")==0)
                    unidad=Unidad.GRAMO;
                else if (celda.getTextValue().compareTo("Mililitros")==0)
                    unidad=Unidad.MILILITRO;
                else
                    unidad=Unidad.UNITARIO;
                
                Ingrediente dato=new Ingrediente(nombre,id,stock,unidad);
                ingredientes.put(id,dato);
            }
        }
        catch (IOException e){}
        catch (StockNoValidoException | IDFormatoNoValidoException ex)
        {
            Logger.getLogger(ListaIngredientes.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //Guarda toda la lista de ingredientes con sus respectivos datos en una tabla para luego ser guardado en un archivo de planilla
    @Override
    public void guardarDatos()
    {
        try
        {
            //Inicializa un arreglo String con las columnas y una matriz de String con los datos correspondientes
            String[] columnas=new String[] {"Nombre","ID","Stock","Unidad"};
            String[][] datos=new String[ingredientes.size()][4];
            int i=0;
        
            //Ciclo donde va agregando los datos del ingrediente en una fila de la matriz
            for (Ingrediente dato: ingredientes.values())
            {
                datos[i]=new String[]{dato.getNombre(),dato.getId(),Integer.toString(dato.getStock()),dato.getUnidad()};
                i++;
            }
        
            //Crea una tabla y la inicializa con los datos y columnas anteriormente creadas
            File archivo=new File("src/archivos/ingredientes.ods");
            TableModel tabla=new DefaultTableModel(datos,columnas);
        
            //Guarda la tabla anterior en un archivo de planilla tipo .ods
            SpreadSheet.createEmpty(tabla).saveAs(archivo);
        }
        catch (IOException e){}
    }
    
    
    @Override
    public void guardarInventario(String ruta)
    {
        Document documento=new Document();
         
        try
        {
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();
            documento.addTitle("Inventario Ingredientes");
            documento.addAuthor("Restaurant City");
            PdfPTable tabla=new PdfPTable(4);
             
            //Agrega las celdas correspondientes
            tabla.addCell("Nombre");
            tabla.addCell("ID");
            tabla.addCell("Stock");
            tabla.addCell("Unidad");
            
            for (Ingrediente dato: ingredientes.values())
            {
                tabla.addCell(dato.getNombre());
                tabla.addCell(dato.getId());
                tabla.addCell(Integer.toString(dato.getStock()));
                tabla.addCell(dato.getUnidad());
            }

            //Se crea una celda final
            PdfPCell celdaFinal=new PdfPCell(new Paragraph("Cantidad total de ingredientes:  "+ingredientes.size()));
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
}
