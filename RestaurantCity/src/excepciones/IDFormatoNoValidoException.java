package excepciones;

public class IDFormatoNoValidoException extends Exception
{
    public IDFormatoNoValidoException()
    {
        super("El código identificador del inventario no posee el formato correcto.");
    }
}
