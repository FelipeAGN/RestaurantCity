package excepciones;

public class PrecioNoValidoException extends Exception
{
    public PrecioNoValidoException()
    {
        super("El valor Precio ingresado no es un valor númerico mayor a cero.");
    }
}
