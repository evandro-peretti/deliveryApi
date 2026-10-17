package com.br.deliveryApi.dto;

import com.br.deliveryApi.model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(@NotNull StatusPedido status) {
}
