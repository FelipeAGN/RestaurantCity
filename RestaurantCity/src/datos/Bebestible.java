package datos;

import excepciones.IDFormatoNoValidoException;
import excepciones.PrecioNoValidoException;
import excepciones.StockNoValidoException;

public final class Bebestible extends Producto
{
    private int stock;

    public Bebestible(String nombre, int precio, int stock) throws PrecioNoValidoException, StockNoValidoException
    {
        super(nombre,precio);
        setStock(stock);
    }
    
    //Constructor para leer directamente desde archivo, es decir, posee todos los datos
    public Bebestible(String nombre, String id, int precio, int stock, int vendidos) throws PrecioNoValidoException, StockNoValidoException, IDFormatoNoValidoException
    {
        super(nombre,id,precio,vendidos);
        setStock(stock);
    }
    
    @Override
    public TipoInventario identificarse()
    {
        return TipoInventario.BEBESTIBLE;
    }
    
    public boolean getDisponible(int cantidad)
    {
        if (cantidad>stock)
            return false;
        
        return true;
    }
    
    public int getStock()
    {
        return stock;
    }
    
    public void setStock(int stock) throws StockNoValidoException
    {
        if (stock>=0)
            this.stock=stock;
        else
            throw new StockNoValidoException();
    }
}
