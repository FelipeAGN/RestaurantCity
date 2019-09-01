package excepciones;

public class StockNoValidoException extends Exception
{
    public StockNoValidoException()
    {
        super("El valor Stock ingresado no es un valor númerico positivo.");
    }
}
