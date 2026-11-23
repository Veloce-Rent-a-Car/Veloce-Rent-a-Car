package Veloce_Rent_a_Car.security;

import Veloce_Rent_a_Car.models.Client;
import Veloce_Rent_a_Car.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Carrega clientes pelo CPF e os adapta ao modelo de usuário exigido pelo Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Busca um cliente pelo CPF e expõe sua senha codificada e o perfil {@code ROLE_USER}.
     *
     * @param cpf CPF usado como nome de usuário na autenticação
     * @return usuário do Spring Security com CPF, senha codificada e autoridade de cliente
     * @throws UsernameNotFoundException se não existir cliente com o CPF informado
     */
    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Client client = clientRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado com CPF: " + cpf));

        return User.builder()
                .username(client.getCpf())
                .password(client.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}