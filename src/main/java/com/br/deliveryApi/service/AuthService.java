package com.br.deliveryApi.service;

import com.br.deliveryApi.dto.ClienteResponse;
import com.br.deliveryApi.dto.LoginRequest;
import com.br.deliveryApi.dto.RegisterRequest;
import com.br.deliveryApi.dto.TokenResponse;
import com.br.deliveryApi.model.Cliente;
import com.br.deliveryApi.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ClienteResponse registrar(RegisterRequest request) {
        String email = normalizar(request.email());
        if (clienteRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }
        Cliente cliente = clienteRepository.save(
                new Cliente(request.nome().trim(), email, passwordEncoder.encode(request.senha()))
        );
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }

    public TokenResponse login(LoginRequest request) {
        Cliente cliente = clienteRepository.findByEmail(normalizar(request.email()))
                .filter(c -> passwordEncoder.matches(request.senha(), c.getSenha()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"));
        return jwtService.gerarToken(cliente);
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}
