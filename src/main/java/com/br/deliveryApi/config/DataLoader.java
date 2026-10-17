package com.br.deliveryApi.config;

import com.br.deliveryApi.model.Cliente;
import com.br.deliveryApi.model.Produto;
import com.br.deliveryApi.repository.ClienteRepository;
import com.br.deliveryApi.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seed(ClienteRepository clientes, ProdutoRepository produtos, PasswordEncoder encoder) {
        return args -> {
            if (clientes.count() == 0) {
                clientes.saveAll(List.of(
                        new Cliente("Maria Souza", "maria@exemplo.com", encoder.encode("maria123")),
                        new Cliente("Pedo de Lima", "pedro@exemplo.com", encoder.encode("pedro123"))
                ));
            }
            if (produtos.count() == 0) {
                produtos.saveAll(List.of(
                        new Produto("Pizza Margherita", "Molho de tomate, mussarela e manjericão", new BigDecimal("49.90")),
                        new Produto("Hambúrguer Artesanal", "180g de carne, queijo cheddar e bacon", new BigDecimal("32.50")),
                        new Produto("Refrigerante Lata", "350ml", new BigDecimal("6.00"))
                ));
            }
        };
    }
}
