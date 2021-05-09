package com.behsa.sdp.mcwmi.config;


import com.behsa.sdp.mcwmi.filters.AuthenticationFilter;
import com.behsa.sdp.mcwmi.filters.AuthorizationFilter;
import com.behsa.sdp.mcwmi.filters.BindFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private AuthenticationFilter authenticationFilter;
    @Autowired
    private AuthorizationFilter authorizationFilter;
    @Autowired
    private DsdpAuthenticationProvider authProvider;

    @Autowired
    private BindFilter bindFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
// We don't need CSRF for this example
        httpSecurity.csrf().disable()
// dont authenticate this particular request
                .authorizeRequests().
                antMatchers("/api/call/**").hasAuthority("SERVICE_ACCESS").
                antMatchers("/web/call/**").hasAuthority("SERVICE_ACCESS").
                antMatchers("/serviceToken").authenticated().
                antMatchers("/authenticate").permitAll().

// all other requests need to be authenticated
        anyRequest().authenticated().and().
/**
 *    make sure we use stateless session; session won't be used to
 *   store user's state.
 */
        exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

/**
 *   Add a filter to validate the tokens with every request
 */
        httpSecurity.addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterAt(bindFilter, AuthenticationFilter.class);
        httpSecurity.addFilterAfter(authorizationFilter, BindFilter.class);

    }

}
