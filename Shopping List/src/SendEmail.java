import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class SendEmail {

    private final String userName = "shoppinglistoptimizer";
    private final String password = "4VdSBYuzQXaF3yFf";
    private String recipient;
    private List<String> shoppingList;

    public SendEmail(String recipient, List<String> shoppingList){
        this.recipient = recipient;
        this.shoppingList = shoppingList;
        sendFromGmail();
    }

    private void sendFromGmail(){
        Properties properties = System.getProperties();
        String host = "smtp.gmail.com";
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", userName);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port ", "587");
        properties.put("mail.smtp.auth", "true");


        String[] to = {recipient};
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);

        try{
            message.setFrom(new InternetAddress(userName));
            InternetAddress[] toAddress = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++){
                toAddress[i] = new InternetAddress(to[i]);
            }

            for (int i = 0; i < toAddress.length; i++){
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject("Your Shopping list, simplified");
            message.setText(createList());
            Transport transport = session.getTransport("smtp");
            transport.connect(host, userName, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ex){

        } catch ( MessagingException ex ) {

        }
    }
    private String createList(){
        String contents = "";
        for (String item : shoppingList) {
            contents += item;
        }
        return contents;
    }
}
