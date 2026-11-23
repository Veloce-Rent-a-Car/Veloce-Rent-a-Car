package Veloce_Rent_a_Car.services;

import Veloce_Rent_a_Car.dto.LoginRequest;
import Veloce_Rent_a_Car.dto.RegisterRequest;
import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Centraliza os fluxos de autenticação por CPF e senha, emissão de JWT e cadastro de clientes.
 */
@Service
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClientService clientService;

    /**
     * Cria a fachada de autenticação com os serviços responsáveis pelas credenciais, tokens e clientes.
     *
     * @param authenticationManager gerenciador que valida CPF e senha
     * @param jwtTokenProvider gerador do token emitido após a autenticação
     * @param clientService serviço responsável por validar e persistir novos clientes
     */
    public AuthFacade(AuthenticationManager authenticationManager,
                      JwtTokenProvider jwtTokenProvider,
                      ClientService clientService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientService = clientService;
    }

    /**
     * Autentica o CPF e a senha informados e gera um JWT para o usuário aceito pelo Spring Security.
     *
     * @param request credenciais de login contendo CPF e senha
     * @return token JWT assinado com as autoridades do usuário autenticado
     * @throws org.springframework.security.core.AuthenticationException se as credenciais forem rejeitadas
     */
    public String autenticar(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCpf(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtTokenProvider.generateToken(userDetails);
    }

    /**
     * Converte os dados de cadastro em um cliente e delega sua criação ao serviço de clientes,
     * que verifica o CPF e codifica a senha antes da persistência.
     *
     * @param request dados pessoais e credenciais do novo cliente
     * @return cliente persistido com a senha codificada
     */
    public Client registrar(RegisterRequest request) {
        Client client = Client.builder()
                .name(request.getNome())
                .cpf(request.getCpf())
                .phone(request.getTelefone())
                .email(request.getEmail())
                .address(request.getEndereco())
                .password(request.getPassword())
                .build();
        return clientService.create(client);
    }
}
