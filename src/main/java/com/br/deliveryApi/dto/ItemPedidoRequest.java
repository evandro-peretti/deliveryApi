package com.br.deliveryApi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequest(
        @NotNull Long produtoId,
        @NotNull @Min(1) @Max(99) Integer quantidade
) {
}
