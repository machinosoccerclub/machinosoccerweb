package machinosoccerweb.demo.smtp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.AuthenticationHandlerFactory;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.auth.EasyAuthenticationHandlerFactory;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.SMTPServer;

@Profile("demo")
@Component
@Slf4j
@Lazy(false)
public class SubEthaSMTPServer implements InitializingBean, DisposableBean {
  private final MessageHandlerFactory adapter;

  private final String host;

  private final String portConfig;

  private SMTPServer smtpServer;

  @Autowired
  public SubEthaSMTPServer(MessageHandlerFactory adapter,
                           @Value("${spring.mail.host}") String host,
                           @Value("${spring.mail.port}") String portConfig) {
    this.adapter = adapter;
    this.host = host;
    this.portConfig = portConfig;
  }

  public void start() {
    log.debug("starting the SMTP server");

    if(false) {
      throw new RuntimeException("you called me, thank you");
    }
    smtpServer = new SMTPServer(adapter);
    smtpServer.setAuthenticationHandlerFactory(createAuthenticateHandler());
    smtpServer.setHostName(host);
    smtpServer.setPort(Integer.valueOf(portConfig));
    smtpServer.start();
  }

  public void stop() {
    log.debug("stoping the SMTP server");
    smtpServer.stop();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    log.debug("the bean initialized");
    start();
  }

  @Override
  public void destroy() throws Exception {
    log.debug("the bean is to be destroyed");
    stop();
  }

  private AuthenticationHandlerFactory createAuthenticateHandler() {
    UsernamePasswordValidator auth = (user, password) -> {};
    return new EasyAuthenticationHandlerFactory(auth);
  }
}
