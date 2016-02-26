package machinosoccerweb.infra.mail

import machinosoccerweb.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.mail.MailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.subethamail.wiser.Wiser
import spock.lang.Specification
import spock.lang.Unroll

import javax.mail.Message
import javax.mail.internet.InternetAddress

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@EnableConfigurationProperties
@ActiveProfiles(["development", "unitTest"])
@WebAppConfiguration
class MailerSpec extends Specification {

  private Wiser wiser

  @Autowired
  private MailSender mailSender

  @Value('${spring.mail.port}')
  private String port

  @Unroll
  def "email to #toAddress from #fromAddress" () {
    setup:
    wiser = new Wiser(port: Integer.valueOf(port))
    wiser.start()

    when:
    def subject = "THIS IS THE SUBJECT"
    def content = "CONTENTS HERE !!!!"
    def mailer = new Mailer(mailSender, fromAddress)
    mailer.send(toAddress, subject, content)

    then:
    wiser.messages.size() == 2

    // receiver of two envelopes are different, but messages are all the same
    // so we check the first message
    def message = wiser.messages.first().mimeMessage
    message.from == [new InternetAddress(fromAddress)]
    message.getRecipients(Message.RecipientType.TO) == [new InternetAddress(toAddress)]
    message.getRecipients(Message.RecipientType.CC) == [new InternetAddress(fromAddress)]
    message.subject == subject
    message.content.toString() startsWith content

    cleanup:
    wiser.stop()

    where:
    toAddress           | fromAddress
    "abc@example.com"   | "efd@example.com"
    "xyz@example.com"   | "BBQ@example.com"
  }

  @Unroll
  def "send the number of #numOfBcc Bcc: from #fromAddress" () {
    setup:
    wiser = new Wiser(port: Integer.valueOf(port))
    wiser.start()

    when:
    def subject = "THIS IS THE SUBJECT"
    def content = "CONTENTS HERE !!!!"
    def bccs = (1..numOfBcc).collect { i -> "dist${i}@example.com".toString() }
    def mailer = new Mailer(mailSender, fromAddress)
    mailer.send(bccs, subject, content)

    then:
    wiser.messages.size() == numOfBcc + 1

    def receivers = wiser.messages.collect {envelope -> envelope.envelopeReceiver}
    receivers.sort() == (bccs << fromAddress).sort()

    def message = wiser.messages.last().mimeMessage
    message.from == [new InternetAddress(fromAddress)]
    message.allRecipients == [new InternetAddress(fromAddress)]
    message.getRecipients(Message.RecipientType.BCC) == null  // should be hidden
    message.subject == subject
    message.content.toString() startsWith content

    cleanup:
    wiser.stop()

    where:
    numOfBcc   | fromAddress
    1          | "sender@example.com"
    3          | "sender@example.com"
    3          | "differ@example.com"
    200        | "sender@example.com"
  }

}
