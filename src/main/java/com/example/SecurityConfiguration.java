package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
        /**
         * To use default schema and configure user on the fly
         */
               /* .withDefaultSchema()
                .withUser(User.withUsername("user")
                            .password("pass")
                            .roles("USER"))
                .withUser(User.withUsername("admin")
                        .password("pass")
                        .roles("ADMIN"))*/
                /**
                 * If you use different schema from the default spring security schema
                 * you can ovveride the table names and column names
                 */
               .usersByUsernameQuery("select username,password,enabled"
                                        + "from user "
                                        + "where username = ?")
                .authoritiesByUsernameQuery("select username,authority"
                                        + "from authorities "
                                        + "where username = ?");

    }

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * For using with DB and JPA
     * PS. comment one of the methods
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * Put most restrictive first and less restrictive on the way down
         */
        http.authorizeRequests()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/", "static/css", "static/js", "/h2-console", "/h2-console/login.do**").permitAll()
                .and().formLogin();

   /*     http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin();*/
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
