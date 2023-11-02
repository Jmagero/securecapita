package io.jmagero.securecapita.configuration;

import io.jmagero.securecapita.handler.CustomAuthenticationEntryPoint;
import io.jmagero.securecapita.handler.CustomerAccessDeniedHandler;
import io.jmagero.securecapita.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final String[] PUBLIC_URLS = {"/users/login/**"};
    private final BCryptPasswordEncoder encoder;
    private final CustomerAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    public  final CustomUserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.cors().disable();
       http.sessionManagement( sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
       http.authorizeHttpRequests().requestMatchers(PUBLIC_URLS).permitAll();
       http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAnyAuthority("DELETE:USER");
       http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/customer/delete/**").hasAnyAuthority("DELETE:CUSTOMER");
       http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(customAuthenticationEntryPoint);
       http.authorizeHttpRequests().anyRequest().authenticated();
       return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }

}

