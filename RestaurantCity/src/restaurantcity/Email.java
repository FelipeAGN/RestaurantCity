package restaurantcity;

import com.orsonpdf.PDFDocument;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class Email
{
    public static void enviarEmail(String destino, PDFDocument archivo, String nombreArchivo) throws MessagingException
    {
        Properties propiedades=new Properties();
        
        System.out.println("Preparando mensaje!...");
        
        //Se ajustan las propiedades del servidor
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port","587");
        
        Session sesion=Session.getInstance(propiedades,new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication("restaurantcity.cl@gmail.com","admin123987");
            }        
        });
        
        Message mensaje=prepararMensaje(sesion,"restaurantcity.cl@gmail.com",destino,archivo,nombreArchivo);
        Transport.send(mensaje);
        System.out.println("Mensaje enviado correctamente!");
    }
    
    private static Message prepararMensaje(Session sesion, String correo, String destino, PDFDocument archivo, String nombreArchivo) throws AddressException, MessagingException
    {
        try
        {
            Message mensaje=new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(correo));
        
            mensaje.setSubject("Reporte solicitado");
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText("¡Do not reply!\n");
              
            //BodyPart2 contendrá el archivo PDF
            BodyPart messageBodyPart2 = new MimeBodyPart();            
            DataSource source=new ByteArrayDataSource(archivo.getPDFBytes(),"application/octet-stream");
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(nombreArchivo);
            
            Multipart multipart=new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);
            
            mensaje.setContent(multipart);
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(destino));

            return mensaje;
        }
        catch (MessagingException excepcion)
        {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE,null,excepcion);
        }
        
        return null;
    }
}