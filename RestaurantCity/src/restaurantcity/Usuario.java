package restaurantcity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.codec.digest.DigestUtils;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

public class Usuario
{   
    private final String usuario;
    private String nombre;
    private String contraseña;
    private boolean administrador;
    
    public Usuario(String nombre, String usuario, String contraseña, boolean administrador)
    {
        this.nombre=nombre;
        this.usuario=usuario;
        this.contraseña=contraseña;
        this.administrador=administrador;
    }
    
    public String getNombre()
    {
        return nombre;
    }
    
    public String getUsuario()
    {
        return usuario;
    }
    
    public String getContraseña()
    {
        return contraseña;
    }
    
    public String getAdministrador()
    {
        if (administrador)
            return "Si";
        
        return "No";
    }
    
    public static ArrayList<Usuario> cargarDatos()
    {
        try
        {
            File archivo=new File("src/archivos/cuentas.ods");  
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            ArrayList<Usuario> lista=new ArrayList<>();
            MutableCell celda;
            String nombre, usuario, contraseña;
            boolean administrador;
           
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {   
                //Obtiene el nombre
                celda=hojaCalculo.getSheet(0).getCellAt(0,i);
                nombre=celda.getTextValue();
                
                //Obtiene el username
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                usuario=celda.getTextValue();
                
                //Obtiene el password
                celda=hojaCalculo.getSheet(0).getCellAt(2,i);
                contraseña=celda.getTextValue();
                celda=hojaCalculo.getSheet(0).getCellAt(3,i);
                
                //Obtiene el privilegio de la cuenta
                if (Integer.parseInt(celda.getTextValue())==1)
                    administrador=true;
                else
                    administrador=false;
                
                Usuario dato=new Usuario(nombre,usuario,contraseña,administrador);
                lista.add(dato);
            }
            
            return lista;
        }
        catch (IOException e) {}
        
        return null;
    }
    
    
    public static boolean iniciarSesion(String username, String password)
    {
        return verificarCredenciales(username,password);
    }
    
    private static boolean verificarCredenciales(String username, String password)
    {
        try
        {
            File archivo=new File("src/archivos/cuentas.ods");
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            String usuario;
            String contraseña;
            password=DigestUtils.md5Hex(password);
           
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {   
                //Obtiene el username
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                usuario=celda.getTextValue();
                
                //Obtiene el password
                celda=hojaCalculo.getSheet(0).getCellAt(2,i);
                contraseña=celda.getTextValue();
                
                //Comprueba si el username y el password coinciden con algunos de la tabla
                if ((usuario.compareTo(username)==0) && (contraseña.compareTo(password)==0))
                    return true;
            }
        }
        catch (IOException e) {}
        
        return false;
    }
    
    
    //Retorna true si la cuenta tiene privilegios de administrador, false en caso contrario
    public static boolean isAdmin(String username)
    {
        try
        {
            File archivo=new File("src/archivos/cuentas.ods");
            SpreadSheet hojaCalculo=SpreadSheet.createFromFile(archivo);
            MutableCell celda;
            String usuario;
            
            //Ciclo donde va recorriendo fila por fila la hoja de cálculo
            for (int i=1; i<hojaCalculo.getSheet(0).getRowCount(); i++)
            {   
                //Obtiene el username
                celda=hojaCalculo.getSheet(0).getCellAt(1,i);
                usuario=celda.getTextValue();
                
                //Comprueba si el username coincide con alguno de la tabla
                if (usuario.compareTo(username)==0)
                {
                    celda=hojaCalculo.getSheet(0).getCellAt(3,i);
                    
                    //Comprueba si la cuenta tiene privilegios, si es así retorna true
                    if (Integer.parseInt(celda.getTextValue())==1)
                        return true;
            
                    return false;
                }
                    
            }
        }
        catch (IOException e) {}
        
        return false;
    }
} 