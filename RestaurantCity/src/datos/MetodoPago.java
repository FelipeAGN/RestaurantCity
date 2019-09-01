package datos;

public enum MetodoPago
{
    CHEQUE("Cheque"), CREDITO("Tarjeta de Crédito"), DEBITO("Tarjeta de Débito"), EFECTIVO("Efectivo");
    
    private String metodo;
    
    private MetodoPago(String metodo)
    {
        this.metodo=metodo;
    }
    
    public String getMetodoPago()
    {
        return metodo;
    }
}