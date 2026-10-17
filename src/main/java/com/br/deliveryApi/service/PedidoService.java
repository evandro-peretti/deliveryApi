package com.br.deliveryApi.service;

import com.br.deliveryApi.dto.EnderecoEntregaDTO;
import com.br.deliveryApi.dto.ItemPedidoResponse;
import com.br.deliveryApi.dto.PedidoRequest;
import com.br.deliveryApi.dto.PedidoResponse;
import com.br.deliveryApi.model.*;
import com.br.deliveryApi.repository.ClienteRepository;
import com.br.deliveryApi.repository.PedidoRepository;
import com.br.deliveryApi.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Set<StatusPedido> STATUS_FINAIS = EnumSet.of(StatusPedido.ENTREGUE, StatusPedido.CANCELADO);

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository){
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long clienteId, Long id) {
        return toResponse(buscarPedidoDoCliente(clienteId, id));
    }

    @Transactional
    public PedidoResponse criar(Long clienteId, PedidoRequest request) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cliente não encontrado"));

        Map<Long, Integer> quantidePorProduto = new LinkedHashMap<>();
        request.itens().forEach(i -> quantidePorProduto.put(i.produtoId(), i.quantidade()));

        Map<Long, Produto> produtos = produtoRepository.findAllById(quantidePorProduto.keySet())
                .stream().collect(Collectors.toMap(Produto::getId, Function.identity()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataPedido(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setEnderecoEntrega(toEndereco(request.enderecoEntrega()));

        quantidePorProduto.forEach((produtoId, quantidade) -> {
            Produto produto = produtos.get(produtoId);
            if (produto == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto não encontrado: " + produtoId);
            }
            pedido.adicionarItem(new ItemPedido(produto, quantidade, produto.getValor()));
        });

        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse atualizarStatus(Long clienteId, Long id, StatusPedido novoStatus) {
        Pedido pedido = buscarPedidoDoCliente(clienteId, id);

        if (STATUS_FINAIS.contains(pedido.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pedido já está " + pedido.getStatus() + " e não pode mais ser alterado");
        }
        pedido.setStatus(novoStatus);
        return toResponse(pedidoRepository.saveAndFlush(pedido));
    }

    // --- helpers ---
    private Pedido buscarPedidoDoCliente(Long clienteId, Long id) {
        return pedidoRepository.findByIdAndClienteId(id, clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private EnderecoEntrega toEndereco(EnderecoEntregaDTO dto) {
        EnderecoEntrega e = new EnderecoEntrega();
        e.setLogradouro(dto.logradouro());
        e.setNumero(dto.numero());
        e.setComplemento(dto.complemento());
        e.setBairro(dto.bairro());
        e.setCidade(dto.cidade());
        e.setEstado(dto.estado().toUpperCase());
        e.setCep(dto.cep());
        return e;
    }

    private PedidoResponse toResponse(Pedido p) {
        List<ItemPedidoResponse> itens = p.getItens().stream()
                .map(i -> new ItemPedidoResponse(i.getId(), i.getProduto().getId(), i.getProduto().getNome(),
                        i.getQuantidade(), i.getValorUnitario(), i.getSubTotal()))
                .toList();
        EnderecoEntrega e = p.getEnderecoEntrega();
        EnderecoEntregaDTO endereco = new EnderecoEntregaDTO(e.getLogradouro(), e.getNumero(),
                e.getComplemento(), e.getBairro(), e.getCidade(), e.getEstado(), e.getCep());

        return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNome(),
                itens, p.getValorTotal(), p.getDataPedido(), p.getStatus(), endereco);
    }
}
