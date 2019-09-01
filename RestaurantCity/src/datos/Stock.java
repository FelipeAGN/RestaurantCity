package datos;

import excepciones.IDFormatoNoValidoException;
import excepciones.StockNoValidoException;

public final class Stock extends Inventario
{
    private int cantidad;   //Almacena la cantidad requerida para el pedido
    
    public Stock(String id, int cantidad) throws IDFormatoNoValidoException, StockNoValidoException
    {
        super(null,id);
        setCantidad(cantidad);
    }
    
    public Stock(String nombre, String id, int cantidad) throws IDFormatoNoValidoException, StockNoValidoException
    {
        super(nombre,id);
        setCantidad(cantidad);
    }
    
    @Override
    public TipoInventario identificarse()
    {
        return TipoInventario.STOCK;
    }
    
    public int getCantidad()
    {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) throws StockNoValidoException
    {
        if (cantidad>0)
            this.cantidad=cantidad;
        else
            throw new StockNoValidoException();
    }
}
