package datos;

import excepciones.IDFormatoNoValidoException;
import java.time.LocalDateTime;

public abstract class Inventario
{
    private String nombre;
    private final String id;
    
    public Inventario(String nombre)
    {
        this.nombre=nombre;
        id=generarID();
    }
    
    public Inventario(String nombre, String id) throws IDFormatoNoValidoException
    {
        this.nombre=nombre;
        
        if (verificarID(id))
            this.id=id;
        else
            throw new IDFormatoNoValidoException();
    }
    
    //Metodo abstracto para identificarse a que instancia de Inventario corresponde el objeto
    public abstract TipoInventario identificarse();
    
    //genera la ID del elemento para que sea única
    private String generarID()
    {   
        LocalDateTime fecha=LocalDateTime.now();
        String codigo;
        int numero;
        
        //Patrón de diseño TemplateMethod
        switch (identificarse())
        {
            case ALIMENTO:
                codigo="A-";
                break;
            
            case BEBESTIBLE:
                codigo="B-";
                break;
            
            default:
                codigo="I-";
                break;
        }
        
        //Concatena el día del año actual al codigo
        if ((numero=fecha.getDayOfYear())<10)
            codigo=codigo+"00"+Integer.toString(numero);
        else if ((numero=fecha.getDayOfYear())<100)
            codigo=codigo+"0"+Integer.toString(numero);
        else
            codigo=codigo+Integer.toString(numero);
        
        
        //Concatena la hora actual al codigo, es decir, hora/minutos/segundos
        if ((numero=fecha.getHour())<10)
            codigo=codigo+"0"+Integer.toString(numero);
        else
            codigo=codigo+Integer.toString(numero);
        
        if ((numero=fecha.getMinute())<10)
            codigo=codigo+"0"+Integer.toString(numero);
        else
            codigo=codigo+Integer.toString(numero);
        
        if ((numero=fecha.getSecond())<10)
            codigo=codigo+"0"+Integer.toString(numero);
        else
            codigo=codigo+Integer.toString(numero);
        
        return codigo;
    }
    
    //Verifica si el formato del ID está correcto
    private boolean verificarID(String codigo)
    {
        if (codigo.length()!=11)
            return false;

        if (codigo.charAt(0)!='A' && codigo.charAt(0)!='B' && codigo.charAt(0)!='I' && codigo.charAt(1)!='-')
            return false;
        
        for (int i=2; i<11; i++)
        {
            if (!Character.isDigit(codigo.charAt(i)))
                return false;              
        }
        
        return true;
    }
    
    public String getNombre()
    {
        return nombre;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setNombre(String nombre)
    {
        this.nombre=nombre;
    }
}
