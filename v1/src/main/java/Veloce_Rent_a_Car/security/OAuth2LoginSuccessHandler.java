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

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRepository clientRepository;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider, ClientRepository clientRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientRepository = clientRepository;
    }

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