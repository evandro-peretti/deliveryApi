package com.br.deliveryApi.controller;

import com.br.deliveryApi.dto.AtualizarStatusRequest;
import com.br.deliveryApi.dto.PedidoRequest;
import com.br.deliveryApi.dto.PedidoResponse;
import com.br.deliveryApi.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PedidoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return service.listar(clienteId(jwt));
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.buscarPorId(clienteId(jwt), id);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        PedidoResponse criado = service.criar(clienteId(jwt), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PatchMapping("/{id}/status")
    public PedidoResponse atualizarStatus(@PathVariable Long id,
                                          @Valid @RequestBody AtualizarStatusRequest request) {
        return service.atualizarStatus(id, request.status());
    }

    /** O "subject" do token é o id do cliente. */
    private Long clienteId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
