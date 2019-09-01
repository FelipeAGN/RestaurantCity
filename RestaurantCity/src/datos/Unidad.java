package datos;

public enum Unidad
{
    GRAMO("Gramos"), MILILITRO("Mililitros"), UNITARIO("Unidades");
    
    private String unidad;
    
    private Unidad(String unidad)
    {
        this.unidad=unidad;
    }
    
    public String getUnidad()
    {
        return unidad;
    }
}