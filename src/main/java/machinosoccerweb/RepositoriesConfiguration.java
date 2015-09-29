package machinosoccerweb;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class RepositoriesConfiguration {
  @Autowired
  private DataSourceProperties properties;

  @Bean(destroyMethod = "close")
  public DataSource dataSource() {
    DataSourceBuilder builder = DataSourceBuilder
        .create(this.properties.getClassLoader())
        .url(this.properties.getUrl())
        .username(this.properties.getUsername())
        .password(this.properties.getPassword());

    return builder.build();
  }
}
