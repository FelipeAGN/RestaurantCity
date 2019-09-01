package datos;

import java.time.LocalDateTime;

public class Venta
{
    private String cajero;
    private final int subTotal;
    private final int propina;
    private final LocalDateTime fecha;
    private final MetodoPago metodoPago;
    
    
    public Venta(String cajero, int subTotal, int propina, MetodoPago metodoPago)
    {
        this.subTotal=subTotal;
        this.propina=propina;
        this.metodoPago=metodoPago;
        fecha=LocalDateTime.now();
    }
    
    //Constructor completo para leer desde archivo
    public Venta(String cajero, LocalDateTime fecha, int subTotal, int propina, MetodoPago metodoPago)
    {
        this.cajero=cajero;
        this.fecha=fecha;
        this.subTotal=subTotal;
        this.propina=propina;
        this.metodoPago=metodoPago;
    }
    
    //Retorna el username del cajero que realizó la venta
    public String getCajero()
    {
        return cajero;
    }
    
    //Retorna la fecha en que se realizó la venta en formato dd/mm/yyyy
    public String getFecha()
    {
        String formato;
        
        if (fecha.getDayOfMonth()<10)
            formato="0"+fecha.getDayOfMonth()+"/";
        else
            formato=fecha.getDayOfMonth()+"/";
        
        if (fecha.getMonthValue()<10)
            formato+="0"+fecha.getMonthValue()+"/";
        else
            formato+=fecha.getMonthValue()+"/";
        
        return formato+fecha.getYear();
    }

    //Retorn la hora en que se realizó la venta en formato hh/mm/ss
    public String getHora()
    {
        String formato;
        
        if (fecha.getHour()<10)
            formato="0"+fecha.getHour()+":";
        else
            formato=fecha.getHour()+":";
        
        if (fecha.getMinute()<10)
            formato+="0"+fecha.getMinute()+":";
        else
            formato+=fecha.getMinute()+":";
        
        if (fecha.getSecond()<10)
            formato+="0"+fecha.getSecond();
        else
            formato+=fecha.getSecond();
        
        return formato;
    }
    
    //Retorna el método de pago con el cuál se canceló la venta
    public String getMetodoPago()
    {
        return metodoPago.getMetodoPago();
    }
    
    
    //Retorna el total de propina dejado por esta venta
    public int getPropina()
    {
        return propina;
    }
    
    //Retorna el SubTotal de la venta
    public int getSubTotal()
    {
        return subTotal;
    }
    
    //Retorna el total de la venta, es decir, la suma del subtotal mas la propina
    public int getTotal()
    {
        return subTotal+propina;
    }
}