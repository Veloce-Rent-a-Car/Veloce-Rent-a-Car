package Veloce_Rent_a_Car.security;

import Veloce_Rent_a_Car.exception.RecursoNaoEncontradoException;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Conclui o login OAuth2 validando o e-mail do provedor e emitindo um JWT próprio da aplicação.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRepository clientRepository;

    /**
     * Cria o manipulador com as dependências necessárias para localizar clientes e gerar tokens.
     *
     * @param jwtTokenProvider gerador dos tokens JWT da aplicação
     * @param clientRepository repositório usado para localizar o cliente pelo e-mail
     */
    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider, ClientRepository clientRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientRepository = clientRepository;
    }

    /**
     * Valida o e-mail e sua confirmação pelo provedor, exige um cliente já cadastrado e escreve
     * o token Bearer em JSON na resposta, sem redirecionar o usuário.
     *
     * @param request requisição HTTP associada ao login concluído
     * @param response resposta HTTP na qual o token ou o erro será escrito
     * @param authentication autenticação concluída, cujo principal contém os atributos OAuth2
     * @throws IOException se ocorrer uma falha ao escrever a resposta
     * @throws ServletException se ocorrer um erro no processamento do servlet
     * @throws RecursoNaoEncontradoException se o e-mail verificado não pertencer a um cliente cadastrado
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Extrai email do provedor OAuth2 (Google)
        String email = oauth2User.getAttribute("email");
        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email não fornecido pelo provedor OAuth2");
            return;
        }

        // Verifica email_verified - obrigatório true para prosseguir
        Boolean emailVerified = oauth2User.getAttribute("email_verified");
        if (emailVerified == null || !emailVerified) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Email não verificado pelo provedor OAuth2");
            return;
        }

        // Verifica se o email já existe como Client
        // Comportamento: se não existe, retorna 404 com mensagem clara (não cria usuário automaticamente)
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não cadastrado. Email " + email + " não encontrado no sistema. " +
                        "Faça o cadastro via /api/auth/register antes de usar login OAuth2."
                ));

        // Gera nosso próprio JWT usando o email como username
        String token = jwtTokenProvider.generateTokenFromEmail(email);

        // Retorna o token no response body (JSON) em vez de redirecionar
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"token\":\"%s\",\"type\":\"Bearer\"}", token));
    }
}