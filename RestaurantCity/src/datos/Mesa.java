package datos;

import java.util.ArrayList;
import java.util.LinkedList;

public class Mesa
{
    private final LinkedList<String> alimentos;     //Almacena los IDs y cantidades del pedido actual de la mesa en alimentos
    private final LinkedList<String> bebestibles;   //Almacena los IDs y cantidades del pedido actual de la mesa en bebestibles
    private int numeroMesa;                         //Almacena el número de mesa
    private int subTotal;                           //Almacena el subTotal actual de la mesa
    private String reservacion;                     //Almacena el nombre del cliente que reservo la mesa
    private double posicionX;                       //Almacena la coordenada X de la posicion en la pantalla
    private double posicionY;                       //Almacena la coordenada Y de la posicion en la pantalla
    private ListaVentas ventas;                 
    
    public Mesa(int numeroMesa, double posicionX, double posicionY)
    {
        this.numeroMesa=numeroMesa;
        this.posicionX=posicionX;
        this.posicionY=posicionY;
        alimentos=new LinkedList<>();
        bebestibles=new LinkedList<>();
        ventas=new ListaVentas(numeroMesa);
        subTotal=0;
        reservacion=null;
    }
    
    //Agrega un alimento a la LinkedList alimentos y aumenta el subTotal
    public void agregarAlimento(String id, int precio)
    {
        if (isReservada())
           reservacion=null;
        
        alimentos.add(id);
        subTotal+=precio;
    }
    
    //Agrega un bebestible a la LinkedList bebestibles y aumenta el subTotal
    public void agregarBebestible(String id, int precio)
    {
        if (isReservada())
            reservacion=null;
        
        bebestibles.add(id);
        subTotal+=precio;
    }
    
    //Elimina un alimento de la LinkedList alimentos y decrementa el subTotal
    public void eliminarAlimento(String id, int precio)
    {
        for (int i=0; i<alimentos.size(); i++)
        {
            if (alimentos.get(i).compareTo(id)==0)
            {
                alimentos.remove(i);
                break;
            }
        }
        
        subTotal-=precio;
    }
    
    //Elimina un bebestible de la LinkedList bebestibles y decrementa el subTotal
    public void eliminarBebestible(String id, int precio)
    {
        for (int i=0; i<bebestibles.size(); i++)
        {
            if (bebestibles.get(i).compareTo(id)==0)
            {
                bebestibles.remove(i);
                break;
            }
        }
        
        subTotal-=precio;
    }
    
    //Retorna un ArrayList con todos los alimentos de la LinkedList alimentos de la mesa
    public ArrayList<String> getAlimentos()
    {
        ArrayList<String> lista=new ArrayList<>();
   
        for (int i=0; i<alimentos.size(); i++)
            lista.add(alimentos.get(i));
        
        return lista;
    }
    
    //Retorna un ArrayList con todos los bebestibles de la LinkedList bebestibles de la mesa
    public ArrayList<String> getBebestibles()
    {
        ArrayList<String> lista=new ArrayList<>();
        
        for (int i=0; i<bebestibles.size(); i++)
            lista.add(bebestibles.get(i));
        
        return lista;
    }
    
    //Retorna el numero de mesa
    public int getNumeroMesa()
    {
        return numeroMesa;
    }
    
    //Retorna la coordenada X de la posicion de la mesa en la pantalla
    public double getPosicionX()
    {
        return posicionX;
    }
    
    //Retorna la coordenada Y de la posicion de la mesa en la pantalla
    public double getPosicionY()
    {
        return posicionY;
    }
    
    public int getCantidadProductos()
    {
        return alimentos.size()+bebestibles.size();
    }
    
    //Retorna true si la mesa está disponible, es decir, si no hay ningún pedido de alimento ni bebestible
    //en las listas correspondientes y además que no se encuentre resrvada, retorna false en caso contrario
    public boolean isDisponible()
    {
        if (((alimentos.isEmpty()) && (bebestibles.isEmpty())) && (!isReservada()))
            return true;
        
        return false;
    }
    
    public boolean isReservada()
    {
        if (reservacion==null)
            return false;
        
        return true;
    }
    
    //Retorna el subTotal actual de la mesa
    public int getSubTotal()
    {
        return subTotal;
    }
        
    //Modifica el numero de mesa
    public void setNumeroMesa(int numeroMesa)
    {
        this.numeroMesa=numeroMesa;
    }
    
    //Modifica las Coordenadas X e Y de la posicion de la mesa en la pantalla
    public void setPosicion(double posicionX, double posicionY)
    {
        this.posicionX=posicionX;
        this.posicionY=posicionY;
    }
    
    //Reserva la mesa, solo si la mesa no está reservada ni ocupada con anterioridad
    public boolean reservarMesa(String nombre)
    {
        if ((!isDisponible()) || (nombre==null))
            return false;
        
        this.reservacion=nombre;
        return true;
    }
    
    public boolean cancelarReservacion()
    {
        if (isDisponible())
            return false;
        
        this.reservacion=null;
        return true;
    }
    
    //Agrega un nuevo dato de tipo venta al ArrayList ventas y vacía las LinkedLists
    //de alimentos y bebestiblesreseteando todos los atributos por default
    public void realizarVenta(String cajero,int propina, MetodoPago metodoPago)
    {
        ArrayList<String> productos=new ArrayList<>(alimentos.size()+bebestibles.size());
               
        //Agrega los alimentos a la lista de productos
        for (int i=0; i<alimentos.size(); i++)
            productos.add(alimentos.get(i));
        
        //Agrega los bebestibles a la lista de productos
        for (int i=0; i<bebestibles.size(); i++)
            productos.add(bebestibles.get(i));
        
        //Venta dato=new Venta(numeroMesa,subTotal,propina,metodoPago,productos);
        ventas.realizarVenta(cajero, subTotal, propina, metodoPago);
        alimentos.clear();
        bebestibles.clear();
        subTotal=0;
        reservacion=null;
    }
    
    //Agrega un nuevo dato de tipo venta al ArrayList ventas y quita los productos seleccionados
    //de las LinkedLists de alimentos y bebestibles correspondientes, es decir, una venta parcial
    public void realizarVenta(String cajero,ArrayList<String> productos, int subTotal, int propina, MetodoPago metodoPago)
    {
        //Se eliminan los productos seleccionados de las LinkedList alimentos y bebestibles correspondientes
        for (int i=0; i<productos.size(); i++)
        {
            if (productos.get(i).charAt(0)=='A')
            {
                for (int j=0; j<alimentos.size(); j++)
                {
                    if (productos.get(i).compareTo(alimentos.get(j))==0)
                    {
                        alimentos.remove(j);
                        break;
                    }
                }
            }
            else
            {
                for (int j=0; j<bebestibles.size(); j++)
                {
                    if (productos.get(i).compareTo(bebestibles.get(j))==0)
                    {
                        bebestibles.remove(j);
                        break;
                    }
                }
            }  
        }
        
        //Si se desocupa la mesa con esta venta parcial, la mesa queda como no reservada
        if ((alimentos.isEmpty()) && (bebestibles.isEmpty()))
            this.reservacion=null;
        
        ventas.realizarVenta(cajero,subTotal,propina,metodoPago);
        this.subTotal-=subTotal;
    }
    
    //Vacía las LinkedList de alimentos y bebestibles, reseteando todos los atributos por default
    public void cancelarVenta()
    {
        alimentos.clear();
        bebestibles.clear();
        subTotal=0;
        reservacion=null;
    }
    
    //Retorna el total de todas las ventas realizadas por esta mesa
    public int getTotalVentas()
    {
        return ventas.getTotalVentas();
    }
}
