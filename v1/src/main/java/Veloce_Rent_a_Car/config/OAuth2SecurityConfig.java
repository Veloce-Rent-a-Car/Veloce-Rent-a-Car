package Veloce_Rent_a_Car.config;

import Veloce_Rent_a_Car.repositories.ClientRepository;
import Veloce_Rent_a_Car.security.JwtTokenProvider;
import Veloce_Rent_a_Car.security.OAuth2LoginSuccessHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configura o login OAuth2 do Google quando a propriedade de identificação do cliente
 * está presente, incluindo o tratador que emite o token após uma autenticação bem-sucedida.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "spring.security.oauth2.client.registration.google.client-id")
public class OAuth2SecurityConfig {

    /**
     * Disponibiliza o tratador de sucesso do login OAuth2 com as dependências necessárias
     * para consultar clientes e gerar tokens JWT.
     *
     * @param jwtTokenProvider componente responsável pela geração de tokens JWT
     * @param clientRepository repositório usado para localizar o cliente autenticado
     * @return tratador de sucesso configurado para o fluxo OAuth2
     */
    @Bean
    public OAuth2LoginSuccessHandler oauth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider, ClientRepository clientRepository) {
        return new OAuth2LoginSuccessHandler(jwtTokenProvider, clientRepository);
    }

    /**
     * Constrói a cadeia de segurança restrita aos caminhos do fluxo OAuth2. As requisições
     * desses caminhos são permitidas, o CSRF é desabilitado, as sessões são stateless e o
     * tratador informado é acionado após o login bem-sucedido.
     *
     * @param http configuração de segurança HTTP fornecida pelo Spring
     * @param oauth2LoginSuccessHandler tratador executado após a autenticação OAuth2
     * @return cadeia de filtros de segurança específica do fluxo OAuth2
     * @throws Exception se a configuração da cadeia de segurança falhar
     */
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