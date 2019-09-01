package datos;

import excepciones.IDFormatoNoValidoException;
import excepciones.PrecioNoValidoException;

public class Producto extends Inventario
{
    private int precio;
    private int vendidos;
    
    public Producto(String nombre, int precio) throws PrecioNoValidoException
    {
        super(nombre);
        this.vendidos=0;
        setPrecio(precio);
    }
    
    public Producto(String nombre, String id, int precio, int vendidos) throws PrecioNoValidoException, IDFormatoNoValidoException
    {
        super(nombre,id);
        this.vendidos=vendidos;
        setPrecio(precio);
    }
    
    @Override
    public TipoInventario identificarse()
    {
        return TipoInventario.PRODUCTO;
    }
    
    public int getPrecio()
    {
        return precio;
    }
    
    public int getVendidos()
    {
        return vendidos;
    }
    
    public void setPrecio(int precio) throws PrecioNoValidoException
    {
        if (precio>0)
            this.precio=precio;
        else
            throw new PrecioNoValidoException();
    }
    
    public void devolver()
    {
        vendidos--;
    }
    
    public void vender()
    {
        vendidos++;
    }
}
