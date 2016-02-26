package machinosoccerweb.infra.mail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class Mailer {

  private final MailSender mailSender;

  private final String fromAddress;

  @Autowired
  public Mailer(MailSender mailSender,
                @Value("${machinosoccerweb.mail.from}") String fromAddress) {
    this.mailSender = mailSender;
    this.fromAddress = fromAddress;
  }

  public void send(String to, String subject, String body) {
    SimpleMailMessage message = messageTemplate();
    message.setTo(to);
    message.setCc(fromAddress);

    message.setSubject(subject);
    message.setText(body);

    mailSender.send(message);
  }

  public void send(List<String> bccs, String subject, String body) {
    SimpleMailMessage message = messageTemplate();
    message.setTo(fromAddress);
    message.setBcc(bccs.toArray(new String[bccs.size()]));

    message.setSubject(subject);
    message.setText(body);

    mailSender.send(message);
  }

  public String getFromAddress() {
    return fromAddress;
  }

  private SimpleMailMessage messageTemplate() {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);

    return message;
  }
}
