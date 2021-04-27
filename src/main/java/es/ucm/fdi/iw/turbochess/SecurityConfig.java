package es.ucm.fdi.iw.turbochess;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
    public void configure(WebSecurity web) throws Exception {
        // allows access to h2 console iff running under debug mode
        web.ignoring().antMatchers("/h2/**");
    }

}