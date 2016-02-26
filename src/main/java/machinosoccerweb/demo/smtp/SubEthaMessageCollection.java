package machinosoccerweb.demo.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;

@Configuration
@Component
public class SubEthaMessageCollection {

  private final List<SubEthaMessage> messages = Collections.synchronizedList(new ArrayList<>());

  public List<SubEthaMessage> getMessages() {
    return Collections.unmodifiableList(messages);
  }

  public SubEthaMessage getMessage(int index) {
    return messages.get(index);
  }

  @Profile("demo")
  @Bean
  public SimpleMessageListenerAdapter simpleMessageListenerAdapter() {
    return new SimpleMessageListenerAdapter(new SubEthaMessageListener(messages));
  }

  @Slf4j
  public static class SubEthaMessageListener implements SimpleMessageListener {

    private final List<SubEthaMessage> messages;
    private final Session session;

    public SubEthaMessageListener(List<SubEthaMessage> messages) {
      this.messages = messages;
      session = Session.getDefaultInstance(new Properties());
    }

    @Override
    public boolean accept(String from, String recipient) {
      return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data)
        throws TooMuchDataException, IOException {
      log.debug("receiving an email from: {}", from);

      try {
        MimeMessage message = new MimeMessage(session, data);
        String content = message.getContent().toString();

        //String pattern = "http?://[:graph:]+";
        //String pattern = "(.*://[^<>[:space:]]+[[:alnum:]/])";
        String pattern = "<(.*)>";
        String linkEnabledContent =
          content.replaceAll(pattern, "<a href=\"$1\" target=\"_blank\">$1</a>");

        StringBuilder rawData = new StringBuilder();
        Enumeration headerLines = message.getAllHeaderLines();
        while(headerLines.hasMoreElements()) {
          rawData.append(headerLines.nextElement().toString()).append('\n');
        }
        try (InputStream rawStream = message.getRawInputStream()) {
          BufferedReader reader = new BufferedReader(new InputStreamReader(rawStream));
          rawData.append(reader.lines().collect(Collectors.joining("\n")));
          messages.add(
              new SubEthaMessage(LocalDateTime.now(),
                  from, recipient, linkEnabledContent, rawData.toString()));
        }

      } catch (MessagingException e) {
        throw new RuntimeException(e);
      }

    }
  }
}
