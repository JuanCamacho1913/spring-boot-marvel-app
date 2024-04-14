package com.api.marvel.config;

import com.api.marvel.config.filter.JwtTokenValidation;
import com.api.marvel.services.impl.UserDetailServiceImpl;
import com.api.marvel.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {

                    /*Define the public endpoints*/
                    //Endpoinds para registrar
                    //Endpoints para iniciar sesion

                    /*Define the private endpoints*/
                    // request.requestMatchers(HttpMethod.GET, "/character/find").hasAuthority("READ_CHARACTER");
                    // request.requestMatchers(HttpMethod.GET, "/character/find/{characterId}").hasAuthority("READ_CHARACTER");

                    request.requestMatchers(HttpMethod.GET, "/character/find").hasAnyRole("ADMIN", "USER");
                    request.requestMatchers(HttpMethod.GET, "/character/find/{characterId}").hasRole("ADMIN");

                    request.requestMatchers(HttpMethod.GET, "/comic/find").hasAuthority("READ_COMIC");
                    request.requestMatchers(HttpMethod.GET, "/comic/findAll").hasAuthority("READ_COMIC");
                    request.requestMatchers(HttpMethod.GET, "/comic/find/{comicId}").hasAuthority("READ_COMIC");

                    /*The rest of endpoints*/
                    request.anyRequest().denyAll();
                });

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}