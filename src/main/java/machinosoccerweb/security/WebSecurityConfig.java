package machinosoccerweb.security;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String ConfirmURL = "/emailConf";

  private static final String[] AccessPermittedPaths = {
    "/", ConfirmURL, "/css/**", "/favicon.ico", "/webjars/**", "/error"
  };

  private static final Object[] NoArgs = {};

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
        .failureUrl("/")
        .permitAll()
        .and()
      .logout()
        .permitAll();

    setPostOnlyToFalse(http);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(createUserDetailsService());
  }

  private UserDetailsService createUserDetailsService() {
    return  (String username) ->
        new LoginUser(
            username,
            username,
            Arrays.asList(new SimpleGrantedAuthority("user")),
            username.hashCode());  // temporary
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