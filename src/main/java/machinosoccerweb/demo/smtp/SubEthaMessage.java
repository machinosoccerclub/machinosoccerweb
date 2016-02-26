package machinosoccerweb.demo.smtp;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class SubEthaMessage {
  private LocalDateTime receivedAt;
  private String from;
  private String recipient;
  private String content;
  private String rawData;
}
