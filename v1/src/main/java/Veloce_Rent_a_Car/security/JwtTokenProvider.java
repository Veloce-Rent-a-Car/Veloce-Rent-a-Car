package Veloce_Rent_a_Car.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Gera e valida tokens JWT assinados com a chave secreta configurada para a aplicação.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:VeloceRentACarSecretKey2024VeloceRentACarSecretKey2024VeloceRentACarSecretKey2024}")
    private String secret;

    @Value("${app.jwt.expiration:86400000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Gera um token para o usuário autenticado, usando seu nome de usuário como assunto e
     * incluindo todas as suas autoridades na claim {@code role}.
     *
     * @param userDetails usuário cujas credenciais e autoridades serão representadas no token
     * @return token JWT assinado, com datas de emissão e expiração
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Gera um token para um login OAuth2, usando o e-mail como assunto e atribuindo
     * exclusivamente a autoridade {@code ROLE_USER}.
     *
     * @param email e-mail validado pelo provedor OAuth2
     * @return token JWT assinado para o e-mail informado
     */
    public String generateTokenFromEmail(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", List.of("ROLE_USER"));
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Recupera o assunto do token, que identifica o usuário por CPF ou e-mail.
     *
     * @param token token JWT assinado
     * @return identificador do usuário armazenado na claim de assunto
     * @throws io.jsonwebtoken.JwtException se o token for inválido ou não puder ser validado
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Recupera a data e o horário de expiração registrados no token.
     *
     * @param token token JWT assinado
     * @return data de expiração do token
     * @throws io.jsonwebtoken.JwtException se o token for inválido ou não puder ser validado
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Valida o token, lê suas claims e aplica a função informada para obter um valor específico.
     *
     * @param <T> tipo do valor extraído das claims
     * @param token token JWT assinado
     * @param claimsResolver função que seleciona ou transforma a claim desejada
     * @return valor produzido pela função sobre as claims do token
     * @throws io.jsonwebtoken.JwtException se o token for inválido ou não puder ser validado
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Confere a assinatura e a validade do token e compara seu assunto com o usuário informado.
     *
     * @param token token JWT recebido na requisição
     * @param userDetails usuário carregado para comparação com o assunto do token
     * @return {@code true} se o assunto coincidir e o token não estiver expirado; caso contrário, {@code false}
     * @throws io.jsonwebtoken.JwtException se o token for inválido ou não puder ser validado
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}