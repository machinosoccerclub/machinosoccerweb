package machinosoccerweb.security;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String ConfirmURL = "/emailConf";

  private static final String[] AccessPermittedPaths = {
    "/", "/ping", ConfirmURL, "/css/**", "/favicon.ico", "/webjars/**", "/error",
    "/requestLoginLink", "/loginLinkSent"
  };

  private static final Object[] NoArgs = {};

  @Autowired
  private LoginUserDetailsService loginUserDetailsService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
        .antMatchers(AccessPermittedPaths).permitAll()
        .antMatchers("/upload", "/conf", "/completed").permitAll()
        .anyRequest().authenticated()
        .and()
      .formLogin()
        .loginPage("/")
        .usernameParameter("a")
        .passwordParameter("k")
        .loginProcessingUrl(ConfirmURL)
        .defaultSuccessUrl("/mypage")
        .failureUrl("/loginError")
        .permitAll()
        .and()
      .logout()
        .permitAll();

    setPostOnlyToFalse(http);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(loginUserDetailsService);
  }

  private void setPostOnlyToFalse(HttpSecurity http) throws Exception {
    @SuppressWarnings("unchecked")
    FormLoginConfigurer<HttpSecurity> config = http.getConfigurer(FormLoginConfigurer.class);

    // because of the filter is encapsulated in the configurator object ...
    Method method =
        AbstractAuthenticationFilterConfigurer.class.getDeclaredMethod("getAuthenticationFilter");
    method.setAccessible(true);

    UsernamePasswordAuthenticationFilter filter =
        (UsernamePasswordAuthenticationFilter) method.invoke(config, NoArgs);
    filter.setPostOnly(false);
    // replace the Request Matcher with no specific http method (default was "POST")
    filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(ConfirmURL));
  }
}
