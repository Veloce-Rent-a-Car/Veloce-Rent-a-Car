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

@Service
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClientService clientService;

    public AuthFacade(AuthenticationManager authenticationManager,
                      JwtTokenProvider jwtTokenProvider,
                      ClientService clientService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientService = clientService;
    }

    public String autenticar(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCpf(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtTokenProvider.generateToken(userDetails);
    }

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
