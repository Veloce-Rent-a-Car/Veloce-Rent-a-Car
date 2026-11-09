package Veloce_Rent_a_Car.security;

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

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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

        // Gera nosso próprio JWT usando o email como username
        String token = jwtTokenProvider.generateTokenFromEmail(email);

        // Retorna o token no response body (JSON) em vez de redirecionar
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"token\":\"%s\",\"type\":\"Bearer\"}", token));
    }
}