package iso.e02.planify.services;

// imports de java
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

// imports de javax.mail
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Clase para enviar correos electrónicos utilizando el protocolo SMTP.
 * Se utiliza para enviar correos de recuperación de contraseña.
 */
public class EmailSMTP {

    /**
     * Logger para la clase EmailSMTP.
     */
    private static final Logger LOGGER = Logger.getLogger(EmailSMTP.class.getName());

    /**
     * Envía un correo electrónico al usuario con un enlace para cambiar la
     * contraseña.
     *
     * @param userEmail Dirección de correo electrónico del destinatario.
     * @param token     Token único para la recuperación de contraseña.
     * @throws AddressException   Si hay un problema con la dirección de correo
     *                            electrónico.
     * @throws MessagingException Si hay un problema al enviar el correo
     *                            electrónico.
     */
    public void sendEmail(String userEmail, String token) throws javax.mail.MessagingException {
        String emailFrom = System.getProperty("EMAIL_FROM");
        String emailTo = userEmail;
        String subject = "Cambio de contraseña";
        String passwordFrom = System.getProperty("EMAIL_PASSWORD");
        //String resetLink = "http://localhost:4200/change-password?token=" + token;
        String resetLink = "https://planify-2024.web.app/change-password?token=" + token; //usado para producción
        String htmlCode = "";
        // Cargar el contenido del correo electrónico desde un archivo HTML
        try {
            htmlCode = new String(Files
                    .readAllBytes(Paths.get(getClass().getClassLoader().getResource("emailContent.html").toURI())));
        } catch (IOException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE,"context", e);
        }
        htmlCode = htmlCode.replace("${userEmail}", userEmail);
        htmlCode = htmlCode.replace("${resetLink}", resetLink);

        String content = htmlCode;
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587"); // Puerto para STARTTLS
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Habilitar STARTTLS
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Configurar protocolos SSL

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailFrom, passwordFrom);
            }
        });

        // Crear y enviar el mensaje
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException e) {
             LOGGER.log(Level.SEVERE,"context", e);
        }
    }
}