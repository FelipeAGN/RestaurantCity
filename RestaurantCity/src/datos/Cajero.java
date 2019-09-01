package datos;

import excepciones.IDFormatoNoValidoException;
import excepciones.PrecioNoValidoException;
import excepciones.StockNoValidoException;
import java.util.ArrayList;
import org.jfree.chart.JFreeChart;

public class Cajero
{
    private final String usuario;
    private final ListaAlimentos alimentos=new ListaAlimentos();
    private final ListaBebestibles bebestibles=new ListaBebestibles();
    private final ListaIngredientes ingredientes=new ListaIngredientes();
    private final ListaMesas mesas=new ListaMesas();
    //private final ListaVentas ventas=new ListaVentas();

    public Cajero(String usuario)
    {
        this.usuario=usuario;
    }
    
    //Retorna el username del que inicio sesión en el cajero
    public String getUsuario()
    {
        return usuario;
    }
   
    // METODOS RELACIONADOS CON ALIMENTO
    
    //Agrega un alimento a ListaAlimentos
    public void agregarAlimento(Alimento dato)
    {
        alimentos.agregarAlimento(dato);
    }
    
    //Elimina un alimento de ListaAlimentos
    public void eliminarAlimento(String id)
    {
        alimentos.eliminarAlimento(id);
    }
    
    //Modifica el nombre de un alimento en específico de ListaAlimentos
    public void modificarNombreAlimento(String id, String nombre)
    {
        alimentos.modificarNombreAlimento(id,nombre);
    }
    
    //Modifica el precio de un alimento en específico de ListaAlimentos
    public void modificarPrecioAlimento(String id, int precio) throws PrecioNoValidoException
    {
        alimentos.modificarPrecioAlimento(id,precio);
    }
    
    //Agrega un ingrediente a un alimento en específico de ListaAlimentos
    public boolean agregarIngredienteAlimento(String id, Stock ingrediente) throws IDFormatoNoValidoException, StockNoValidoException
    {
        return alimentos.agregarIngredienteAlimento(id,ingrediente);
    }
    
    //Elimina un ingrediente de un alimento en específico de ListaAlimentos
    public void eliminarIngredienteAlimento(String id, String idIngrediente)
    {
        alimentos.eliminarIngredienteAlimento(id,idIngrediente);
    }
    
    //Modifica la cantidad de un ingrediente en específico de un alimento en específico de ListaAlimentos
    public void modificarCantidadIngredienteAlimento(String id, String idIngrediente, int cantidad) throws StockNoValidoException
    {
        alimentos.modificarCantidadIngredienteAlimento(id,idIngrediente,cantidad);
    }
    
    //Retorna un ArrayList de todos los alimentos de ListaAlimentos
    public ArrayList<Alimento> getAlimentos()
    {
        return alimentos.getAlimentos();
    }
        
    //Retorna todos los ingredientes de un alimento en específico de ListaAlimento
    public ArrayList<Stock> getIngredientesAlimento(String id)
    {
        return ingredientes.getIngredientes(alimentos.getIngredientes(id));
    }
    
    //Retorna null si se puede preparar el alimento, es decir, existe el stock de ingredientes necesarios para su preparación
    //en caso contrario, retorna un ArrayList con los nombres de los ingredientes que no tienen suficiente stock
    public boolean verificarAlimento(String id) throws IDFormatoNoValidoException
    {
        return ingredientes.getDisponible(alimentos.getIngredientes(id));
    }
   
    
    // METODOS RELACIONADOS CON BEBESTIBLE
    
    //Agrega un dato de tipo bebestible a ListaBebestible
    public void agregarBebestible(Bebestible dato)
    {
        bebestibles.agregarBebestible(dato);
    }
    
    //ELimina un bebestible en específico de ListaBebestible
    public void eliminarBebestible(String id)
    {
        bebestibles.eliminarBebestible(id);
    }
    
    //Modifica el nombre de un bebestible en específico de ListaBebestibles
    public void modificarNombreBebestible(String id, String nombre)
    {
        bebestibles.modificarNombreBebestible(id,nombre);
    }
    
    //Modifica el precio de un bebestible en específico de ListaBebestibles
    public void modificarPrecioBebestible(String id, int precio) throws PrecioNoValidoException
    {
        bebestibles.modificarPrecioBebestible(id, precio);
    }
    
    //Modifica el stock de un bebestible en específico de ListaBebestibles
    public void modificarStockBebestible(String id, int stock) throws StockNoValidoException
    {
        bebestibles.modificarStockBebestible(id,stock);
    }
    
    //Retorna un ArrayList con todos los bebestibles de ListaBebestible
    public ArrayList<Bebestible> getBebestibles()
    {
        return bebestibles.getBebestibles();
    }
    
    //Utiliza un bebestible de ListaBebestible, es decir, le resta uno al stock original
    public void utilizarBebestible(String id) throws StockNoValidoException
    {
        bebestibles.utilizar(id);
    }
    
    //Devuelve un bebestible a ListaBebestible, es decir, le suma uno al stock original
    public void devolverBebestible(String id) throws StockNoValidoException
    {
        bebestibles.devolver(id);
    }
    
    //Retorna true, si existe stock de un bebestible en específico
    public boolean verificarBebestible(String id)
    {
        return bebestibles.getDisponible(id);
    }
    

    // METODOS RELACIONADOS CON INGREDIENTE
    
    //Agrega un dato ingrediente a ListaIngredientes
    public void agregarIngrediente(Ingrediente dato)
    {
        ingredientes.agregarIngrediente(dato);
    }
    
    //Elimina un ingrediente en específico de ListaIngredientes
    public void eliminarIngrediente(String id)
    {
        ingredientes.eliminarIngrediente(id);
    }
    
    //Modifica el nombre de un ingrediente en específico de ListaIngredientes
    public void modificarNombreIngrediente(String id, String nombre)
    {
        ingredientes.modificarNombreIngrediente(id,nombre);
    }
    
    //Modifica el stock de un ingrediente en específico de ListaIngredientes
    public void modificarStockIngrediente(String id, int stock) throws StockNoValidoException
    {
        ingredientes.modificarStockIngrediente(id,stock);
    }

    //Retorna un ArrayList con todos los ingredientes que hay en ListaIngrediente
    public ArrayList<Ingrediente> getIngredientes()
    {
        return ingredientes.getIngredientes();
    }
    
    //Retorna un ArrayList con los nombres de los alimentos en donde se encuentra el ingrediente, 
    //si no se encuentra en ningun alimento retorna null
    public ArrayList<String> verificarIngrediente(String id)
    {
        return alimentos.verificarIngrediente(id);
    }
    
    //Utiliza todos los ingredientes de un alimento en específico, es decir, resta cada una 
    //de las cantidades  de ingredientes correspondientes (del alimento) al stock original
    public void utilizarIngredientesAlimento(String id) throws IDFormatoNoValidoException
    {
        alimentos.agregarVenta(id);
        ingredientes.utilizar(alimentos.getIngredientes(id));
    }
    
    //Devuelve todos los ingredientes de un alimento en específico, es decir, suma cada una 
    //de las cantidades  de ingredientes correspondientes (del alimento) al stock original
    public void devolverIngredientesAlimento(String id)
    {
        alimentos.devolver(id);
        ingredientes.devolver(alimentos.getIngredientes(id));
    }
    
    //METODOS RELACIONADOS CON MESA
    
    //Agrega una mesa al HashMap
    public void agregarMesa(Mesa dato)
    {
        mesas.agregarMesa(dato);
    }
    
    //Agrega un pedido (alimento o bebestible) a una mesa en específico
    public void agregarPedido(int numeroMesa, String id, int precio)
    {
        mesas.agregarPedido(numeroMesa,id,precio);
    }
    
    public void eliminarMesa(int numeroMesa)
    {
        mesas.eliminarMesa(numeroMesa);
    }
    
    //Agrega un pedido (alimento o bebestible) de una mesa en específico
    public void eliminarPedido(int numeroMesa, String id, int precio)
    {
        mesas.eliminarPedido(numeroMesa,id,precio);
    }
    
    //Modifica el número de una mesa en específico de ListaMesas
    public boolean modificarNumeroMesa(int numeroMesa, int nuevoNumeroMesa)
    {
        return mesas.modificarNumeroMesa(numeroMesa,nuevoNumeroMesa);
    }
    
    //Modifica la posición en la pantalla de una mesa en específico de ListaMesas
    public void modificarPosicionMesa(int numeroMesa, double posicionX, double posicionY)
    {
        mesas.modificarPosicionMesa(numeroMesa,posicionX,posicionY);
    }
        
    //Retorna un ArrayList con todos los alimentos de una mesa en específico
    public ArrayList<Alimento> getAlimentosMesa(int numeroMesa)
    {
        return alimentos.getAlimentos(mesas.getAlimentos(numeroMesa));
    }
    
    //Retorna un ArrayList con todos los bebestibles de una mesa en específico
    public ArrayList<Bebestible> getBebestiblesMesa(int numeroMesa)
    {
        return bebestibles.getBebestibles(mesas.getBebestibles(numeroMesa));
    }
    
    //Retorna un boolean de si está disponible una mesa en específico de ListaMesas
    public boolean isMesaDisponible(int numeroMesa)
    {
        return mesas.isMesaDisponible(numeroMesa);
    }
    
    //Retorna un boolean de si está reservada una mesa en específico de ListaMesas
    public boolean isMesaReservada(int numeroMesa)
    {
        return mesas.isMesaReservada(numeroMesa);
    }
    
    //Retorna la cantidad de productos que hay en una mesa en específico de ListaMesas
    public int getCantidadProductosMesa(int numeroMesa)
    {
        return mesas.getCantidadProductos(numeroMesa);
    }
    
    //Retorna un ArrayList con todas las mesas de ListaMesas
    public ArrayList<Mesa> getMesas()
    {
        return mesas.getMesa();
    }
    
    //Retorna el número siguiente que corresponde 
    public int getProximoNumeroMesa()
    {
        return mesas.getProximoNumeroMesa();
    }
    
    //Retorna el subtotal actual de una mesa en específico de ListaMesas
    public int getSubTotalMesa(int numeroMesa)
    {
        return mesas.getSubTotal(numeroMesa);
    }

    //Reserva al nombre del ciente de una mesa en específico de ListaMesas
    public boolean reservarMesa(int numeroMesa, String nombre)
    {
        return mesas.reservarMesa(numeroMesa,nombre);
    }
    
    //Cancela la reservación de una mesa en específico de ListaMesas
    public boolean cancelarReservacion(int numeroMesa)
    {
        return mesas.cancelarReservacion(numeroMesa);
    }
    
    //METODOS RELACIONADOS CON VENTA
    
    //Realiza la venta de una mesa en específico, es decir, agrega un dato venta y vacía la mesa
    public void realizarVenta(int numeroMesa, int propina, MetodoPago metodo)
    {
        mesas.realizarVenta(numeroMesa,usuario,propina, metodo);
    }
    
    //Realiza la venta de una mesa en específico con algunos productos en especifico (esto ocurre cuando se divide
    //la cuenta entre varias personas), es decir, agrega un dato venta y quita de la mesa los productos seleccionados
    public void realizarVenta(int numeroMesa, ArrayList<String> productos, int subTotal, int propina, MetodoPago metodo)
    {
        alimentos.vender(productos);
        bebestibles.vender(productos);
        mesas.realizarVenta(numeroMesa,usuario,productos,subTotal,propina,metodo);
    }
    
    //Cancela la venta, es decir vacía una mesa en específico de ListaMesas
    public void cancelarVenta(int numeroMesa)
    {
        mesas.cancelarVenta(numeroMesa);
    }
    
    //Guarda todos los datos en diferentes archivos de planilla
    public void guardarDatos()
    {
        alimentos.guardarDatos();
        bebestibles.guardarDatos();
        ingredientes.guardarDatos();
        mesas.guardarDatos();
    }
    
    //Guarda en un archivo PDF el inventario de todos los alimentos
    public void guardarInvertarioAlimentos(String ruta)
    {
        alimentos.guardarInventario(ruta);
    }
    
    //Guarda en un archivo PDF el inventario de todos los bebestibles
    public void guardarInvertarioBebestibles(String ruta)
    {
        bebestibles.guardarInventario(ruta);
    }
    
    //Guarda en un archivo PDF el inventario de todos los ingredientes
    public void guardarInvertarioIngredientes(String ruta)
    {
        ingredientes.guardarInventario(ruta);
    }
    
    //Genera un gráfico con los alimentos más vendidos
    public JFreeChart generarReporteAlimentos()
    {
        return alimentos.generarReporte();
    }
    
    //Genera un gráfico con los bebestibles más vendidos
    public JFreeChart generarReporteBebestibles()
    {
        return bebestibles.generarReporte();
    }
    
    //Genera un gráfico con las ventas de mesas
    public JFreeChart generarReporteMesas()
    {
        return mesas.generarReporte();
    }
    
    //Guarda el gráfico de alimentos en un archivo PDF
    public void guardarReporteAlimentos(String ruta)
    {
        alimentos.guardarReporte(ruta);
    }
    
     //Guarda el gráfico de bebestibles en un archivo PDF
    public void guardarReporteBebestibles(String ruta)
    {
        bebestibles.guardarReporte(ruta);
    }
    
    //Guarda el gráfico de mesas en un archivo PDF
    public void guardarReporteMesas(String ruta)
    {
        mesas.guardarReporte(ruta);
    }
          
    //Envia el archivo PDF del gráfico de alimentos por e-mail
    public void enviarReporteAlimentos(String destino)
    {
        alimentos.enviarReporte(destino);
    }
    
    //Envia el archivo PDF del gráfico de alimentos por e-mail
    public void enviarReporteBebestibles(String destino)
    {
        bebestibles.enviarReporte(destino);
    }
    
    //Envia el archivo PDF del gráfico de alimentos por e-mail
    public void enviarReporteMesas(String destino)
    {
        mesas.enviarReporte(destino);
    }
}