package datos;

import excepciones.IDFormatoNoValidoException;
import excepciones.StockNoValidoException;

public final class Ingrediente extends Inventario
{
    private int stock;            //Almacena el stock del ingrediente
    private Unidad unidad;        //Almacena el tipo de unidad en que se almacena el stock, gramos o unidades

        
    public Ingrediente(String nombre, int stock, Unidad unidad) throws StockNoValidoException
    {
        super(nombre);
        this.unidad=unidad;
        setStock(stock);
    }
    
    //Constructor para leer directamente desde archivo, es decir, posee todos los datos
    public Ingrediente(String nombre, String id, int stock, Unidad unidad) throws StockNoValidoException, IDFormatoNoValidoException
    {
        super(nombre,id);
        this.unidad=unidad;
        setStock(stock);
    }

    @Override
    public TipoInventario identificarse()
    {
        return TipoInventario.INGREDIENTE;
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
  
    public boolean getDisponible()
    {
        if (stock>0)
            return true;
        
        return false;
    }
    
    public boolean getDisponible(int cantidad)
    {
        if (cantidad>stock)
            return false;
        
        return true;
    }
    
    public String getUnidad()
    {
        return unidad.getUnidad();
    }
    
    public boolean utilizar(int cantidad)
    {
        if (cantidad>stock)
            return false;
        
        stock-=cantidad;
        return true;
    }
    
    public void devolver(int cantidad)
    {
       if (cantidad<0)
           return;
       
       stock+=cantidad;
    }
}