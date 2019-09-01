package datos;

import excepciones.IDFormatoNoValidoException;
import excepciones.PrecioNoValidoException;
import excepciones.StockNoValidoException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Alimento extends Producto
{
    private final LinkedList<Stock> ingredientes;   //LinkedList de ingredientes del alimento, almacena las IDs y las cantidades requeridas de cada ingrediente
    
    public Alimento(String nombre, int precio) throws PrecioNoValidoException
    {
        super(nombre,precio);
        this.ingredientes=new LinkedList<>();
    }
    
    //Constructor para leer directamente desde archivo, es decir, posee todos los datos
    public Alimento(String nombre, String id, int precio, int vendidos, LinkedList<Stock> ingredientes) throws PrecioNoValidoException, IDFormatoNoValidoException
    {
        super(nombre,id,precio,vendidos);
        this.ingredientes=ingredientes;
    }
    
    @Override
    public TipoInventario identificarse()
    {
        return TipoInventario.ALIMENTO;
    }
    
    //Agrega un ingrediente junto a su cantidad requeridad para la preparación en el ArrayList
    public boolean agregarIngrediente(String id, int cantidad) throws IDFormatoNoValidoException, StockNoValidoException
    {
        if (buscarIngrediente(id))
            return false;
        
        Stock dato=new Stock(id,cantidad);
        return ingredientes.add(dato);
    }
    
    //Retorna true si el ingrediente (según ID) se encuentra en la LinkedList
    public boolean buscarIngrediente(String id)
    {
        if (!ingredientes.isEmpty())
        {
            for (int i=0; i<ingredientes.size(); i++)
            {
                if (ingredientes.get(i).getId().compareTo(id)==0)
                    return true;
            }
        }
        
        return false;
    }
    
    //Elimina el ingrediente (según ID) del ArrayList, retorna true si se eliminó sastifactoriamente
    public boolean eliminarIngrediente(String id)
    {
        if (!buscarIngrediente(id))
            return false;
        
        if (ingredientes.remove(getPosicionIngrediente(id))==null)
            return false;
        
        return true;
    }
    
    //Modifica la cantidad de ingrediente requerido para la preparación del alimento
    //retorna true si se realizó con éxito el cambio
    public boolean modificarCantidadIngrediente(String id, int cantidad) throws StockNoValidoException
    {
        if (!buscarIngrediente(id))
            return false;
        
        ingredientes.get(getPosicionIngrediente(id)).setCantidad(cantidad);
        return true;
    }
          
    public int getCantidadIngredientes()
    {
        return ingredientes.size();
    }
    
    public ArrayList<Stock> getIngredientes()
    {
        ArrayList<Stock> lista=new ArrayList<>(ingredientes.size());
        
        for (int i=0; i<ingredientes.size(); i++)
            lista.add(ingredientes.get(i));
        
        return lista;
    }
    
    //Retorna la posición del Ingrediente (según ID) en la LinkedList
    private int getPosicionIngrediente(String id)
    {
        int i;
        
        if (!buscarIngrediente(id))
            return 0;
        
        for (i=0; i<ingredientes.size(); i++)
        {
            if (ingredientes.get(i).getId().compareTo(id)==0)
                break;
        }
        
        return i;
    }
}