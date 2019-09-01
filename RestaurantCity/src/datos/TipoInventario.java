package datos;

public enum TipoInventario
{
    ALIMENTO("Alimento"), BEBESTIBLE("Bebestible"), INGREDIENTE("Ingrediente"), PRODUCTO("Producto"), STOCK("Stock");
    
    private String metodo;
    
    private TipoInventario(String metodo)
    {
        this.metodo=metodo;
    }
    
    public String getTipoInventario()
    {
        return metodo;
    }
}