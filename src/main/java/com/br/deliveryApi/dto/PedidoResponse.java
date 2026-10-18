package com.br.deliveryApi.dto;

import com.br.deliveryApi.model.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        List<ItemPedidoResponse> itens,
        BigDecimal valorTotal,
        Instant dataPedido,
        StatusPedido status,
        EnderecoEntregaDTO enderecoEntrega,
        Instant updatedAt
) {
}
