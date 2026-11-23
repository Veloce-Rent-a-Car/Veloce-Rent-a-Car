package Veloce_Rent_a_Car.config;

import Veloce_Rent_a_Car.security.CustomUserDetailsService;
import Veloce_Rent_a_Car.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Define a segurança HTTP e a autenticação da aplicação, com sessões stateless,
 * validação por JWT, regras de acesso aos endpoints e senhas codificadas com BCrypt.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Configura a cadeia principal de segurança, desabilitando CSRF e sessões com estado,
     * liberando os endpoints públicos definidos e exigindo autenticação nas demais rotas.
     * O filtro JWT é executado antes do filtro padrão de autenticação por usuário e senha.
     *
     * @param http configuração de segurança HTTP fornecida pelo Spring
     * @return cadeia principal de filtros de segurança da aplicação
     * @throws Exception se a configuração da cadeia de segurança falhar
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clients/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/carros/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rentals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/payments/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cria o provedor de autenticação baseado no serviço de usuários da aplicação e
     * configura o mesmo codificador BCrypt exposto por esta classe.
     *
     * @return provedor de autenticação DAO configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expõe o gerenciador de autenticação construído pela configuração do Spring Security.
     *
     * @param config configuração de autenticação fornecida pelo Spring
     * @return gerenciador de autenticação da aplicação
     * @throws Exception se o gerenciador não puder ser obtido da configuração
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Fornece o codificador de senhas utilizado pela autenticação da aplicação.
     *
     * @return implementação de codificação de senhas baseada em BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}