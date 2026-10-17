package com.br.deliveryApi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(
        @NotEmpty List<@NotNull @Valid ItemPedidoRequest> itens,
        @NotNull @Valid EnderecoEntregaDTO enderecoEntrega) {
}
