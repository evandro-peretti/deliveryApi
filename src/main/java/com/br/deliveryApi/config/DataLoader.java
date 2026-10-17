package com.br.deliveryApi.config;

import com.br.deliveryApi.model.Cliente;
import com.br.deliveryApi.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(ClienteRepository clientes, PasswordEncoder encoder) {
        return args -> {
            if (clientes.count() == 0) {
                clientes.saveAll(List.of(
                        new Cliente("Maria Souza", "maria@exemplo.com", encoder.encode("maria123")),
                        new Cliente("Pedo de Lima", "pedro@exemplo.com", encoder.encode("pedro123"))
                ));
            }
        };
    }
}
