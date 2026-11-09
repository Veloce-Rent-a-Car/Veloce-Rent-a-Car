package Veloce_Rent_a_Car.config;

import Veloce_Rent_a_Car.security.OAuth2LoginSuccessHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.oauth2.client.registration.google.client-id")
public class OAuth2SecurityConfig {

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http, OAuth2LoginSuccessHandler oauth2LoginSuccessHandler) throws Exception {
        http
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2LoginSuccessHandler)
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));

        return http.build();
    }
}