package machinosoccerweb;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = Application.class, lazyInit = true)
@ImportResource({"classpath*:applicationContext.xml"})
public class Application {
  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
