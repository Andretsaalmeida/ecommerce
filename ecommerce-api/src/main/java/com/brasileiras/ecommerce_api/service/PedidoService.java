package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.PedidoRequestDTO;
import com.brasileiras.ecommerce_api.enums.StatusPedido;
import com.brasileiras.ecommerce_api.model.Cliente;
import com.brasileiras.ecommerce_api.model.ItemPedido;
import com.brasileiras.ecommerce_api.model.Pedido;
import com.brasileiras.ecommerce_api.model.Produto;
import com.brasileiras.ecommerce_api.repository.ClienteRepository;
import com.brasileiras.ecommerce_api.repository.EnderecoRepository;
import com.brasileiras.ecommerce_api.repository.PedidoRepository;
import com.brasileiras.ecommerce_api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private EnderecoRepository enderecoRepository; // Para buscar endereço
    private final ProdutoService produtoService; // Para atualizar estoque

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, ClienteRepository clienteRepository, EnderecoRepository enderecoRepository, ProdutoService produtoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoService = produtoService;
    }

    @Transactional
    public Pedido criarPedido(PedidoRequestDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(UUID.randomUUID().toString());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.valueOf("AGUARDANDO_PAGAMENTO"));

        Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        pedido.setCliente(cliente);

        // Lógica para buscar o endereço de entrega selecionado pelo cliente
        // Aqui, para simplificar, vamos assumir que o ID do endereço está no DTO
        // e que ele pertence ao cliente. Uma validação robusta é necessária.
        // Endereco enderecoEntrega = enderecoRepository.findByIdAndClienteId(pedidoDTO.getEnderecoEntregaId(), cliente.getId())
        // .orElseThrow(() -> new RuntimeException("Endereço de entrega inválido ou não pertence ao cliente"));
        // pedido.setEnderecoEntrega(enderecoEntrega);

        // Para simplificar, vamos pegar o primeiro endereço do cliente (NÃO FAZER ISSO EM PRODUÇÃO)
        if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
            throw new RuntimeException("Cliente não possui endereço cadastrado.");
        }
        pedido.setEnderecoEntrega(cliente.getEnderecos().iterator().next());


        List<ItemPedido> itens = pedidoDTO.getItens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto ID " + itemDto.getProdutoId() + " não encontrado."));

            if(produto.getEstoque() < itemDto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getDescricao());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getValorVenda()); // Preço atual do produto
            return itemPedido;
        }).collect(Collectors.toList());

        pedido.setItens(itens);

        BigDecimal valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(valorTotal);

        // Salva o pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Atualiza o estoque
        for (ItemPedido item : pedidoSalvo.getItens()) {
            produtoService.atualizarEstoque(item.getProduto().getId(), item.getQuantidade());
        }

        // Aqui viria a lógica para gerar Contas a Receber baseado na forma de pagamento
        // e interagir com gateways de pagamento.

        return pedidoSalvo;
    }

    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }
}
