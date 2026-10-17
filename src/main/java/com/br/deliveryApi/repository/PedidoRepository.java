package com.br.deliveryApi.repository;

import com.br.deliveryApi.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
    List<Pedido> findByClienteIdOrderByDataPedidoDesc(Long clienteId);

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
    Optional<Pedido> findByIdAndClienteId(Long id, Long clienteId);
}
