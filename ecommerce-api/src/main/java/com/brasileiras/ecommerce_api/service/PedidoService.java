package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.ItemPedidoRequestDTO;
import com.brasileiras.ecommerce_api.dto.PedidoRequestDTO;
import com.brasileiras.ecommerce_api.dto.PedidoResponseDTO;
import com.brasileiras.ecommerce_api.enums.StatusPedido;
import com.brasileiras.ecommerce_api.exception.BusinessRuleException;
import com.brasileiras.ecommerce_api.exception.ResourceNotFoundException;
import com.brasileiras.ecommerce_api.model.*;
import com.brasileiras.ecommerce_api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Para gerar número de pedido único

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ProdutoRepository produtoRepository;
    // ItemPedidoRepository não estritamente pois em Pedido.itens = CascadeType.ALL
    // Outros serviços para serem injetados futuramente ex: ContasAPagarService, ContasAReceberService, NotificacaoService

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         EnderecoRepository enderecoRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO) {
        logger.info("Iniciando processo de criação de pedido para o cliente ID: {}", pedidoRequestDTO.getClienteId());

        Cliente cliente = clienteRepository.findById(pedidoRequestDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + pedidoRequestDTO.getClienteId()));

        // Valida se o endereço pertence ao cliente
        Endereco enderecoEntrega = enderecoRepository.findByIdAndClienteId(pedidoRequestDTO.getEnderecoEntregaId(), cliente.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço de entrega não encontrado com ID: " + pedidoRequestDTO.getEnderecoEntregaId() + " para o cliente especificado."));

        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);
        novoPedido.setEnderecoEntrega(enderecoEntrega);
        novoPedido.setFormasPagamento(pedidoRequestDTO.getFormasPagamento());
        novoPedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO); // Status inicial
        novoPedido.setDataPedido(LocalDateTime.now());
        novoPedido.setNumeroPedido(gerarNumeroPedidoUnico()); // Implementar geração de número único

        List<ItemPedido> itensDoPedido = new ArrayList<>();
        for (ItemPedidoRequestDTO itemReq : pedidoRequestDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + itemReq.getProdutoId()));

            if (!produto.temEstoqueSuficiente(itemReq.getQuantidade())) {
                throw new BusinessRuleException("Estoque insuficiente para o produto: " + produto.getDescricao() +
                        ". Solicitado: " + itemReq.getQuantidade() +
                        ", Disponível: " + produto.getEstoque());
            }

            // O construtor de ItemPedido já "congela" o preço do produto no momento da compra
            ItemPedido itemPedido = new ItemPedido(novoPedido, produto, itemReq.getQuantidade());
            itensDoPedido.add(itemPedido);

        }

        novoPedido.setItens(itensDoPedido); // Isso também recalcula o valorTotal
        // novoPedido.recalcularValorTotal(); // Chamado dentro de setItens ou adicionarItem

        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);
        logger.info("Pedido {} criado com sucesso para o cliente {}.", pedidoSalvo.getNumeroPedido(), cliente.getNome());

        // Lógica Pós-Criação
        // processarAtualizacaoEstoque(pedidoSalvo);
        // gerarLancamentosContasAReceber(pedidoSalvo);
        // enviarNotificacaoConfirmacaoPedido(pedidoSalvo);

        return PedidoResponseDTO.fromEntity(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidos(Pageable pageable) {
        // Adicionar filtros se necessário (ex: por clienteId, status)
        return pedidoRepository.findAll(pageable).map(PedidoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidosPorCliente(Long clienteId, Pageable pageable) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId);
        }
        return pedidoRepository.findByClienteId(clienteId, pageable).map(PedidoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
        return PedidoResponseDTO.fromEntity(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorNumero(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com número: " + numeroPedido));
        return PedidoResponseDTO.fromEntity(pedido);
    }


    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long pedidoId, StatusPedido novoStatus) {
        logger.info("Tentando atualizar status do pedido ID: {} para {}", pedidoId, novoStatus);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        // Adiciona validações de transição de status (ex: não pode ir de CANCELADO para ENVIADO)
        // Exemplo simples:
        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            if (novoStatus != pedido.getStatus()) { // Permite "reatualizar" para o mesmo status
                throw new BusinessRuleException("Não é possível alterar o status de um pedido " + pedido.getStatus().getDescricao());
            }
        }

        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        logger.info("Status do pedido ID: {} atualizado para {}", pedidoId, novoStatus);

        // Lógica Pós-Atualização de Status
        if (novoStatus == StatusPedido.PAGAMENTO_APROVADO) {
            processarAtualizacaoEstoque(pedidoAtualizado); // Com a venda finalizada, o sistema deve realizar a atualização de estoque
            // Gerar lançamentos em Contas a Receber
            gerarLancamentosContasAReceber(pedidoAtualizado); //Cada venda deve gerar, pelo menos um lançamento no contas a receber
        } else if (novoStatus == StatusPedido.ENVIADO) {
            // Iniciar processo de entrega, notificar cliente, etc.
        } else if (novoStatus == StatusPedido.CANCELADO) {
            // Reverter estoque (se já havia sido debitado), cancelar contas a receber, etc.
            reverterAtualizacaoEstoque(pedidoAtualizado);
        }

        return PedidoResponseDTO.fromEntity(pedidoAtualizado);
    }

    private String gerarNumeroPedidoUnico() {
        // Estratégia simples: timestamp + parte de UUID
        return "PED-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void processarAtualizacaoEstoque(Pedido pedido) {
        logger.info("Processando atualização de estoque para o pedido: {}", pedido.getNumeroPedido());
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            try {
                produto.removerEstoque(item.getQuantidade());
                produtoRepository.save(produto); // Salva o produto com estoque atualizado
                logger.info("Estoque do produto ID {} ({}) atualizado. Removido: {}. Novo estoque: {}",
                        produto.getId(), produto.getDescricao(), item.getQuantidade(), produto.getEstoque());
            } catch (IllegalStateException e) {
                // Lida com a situação de estoque ficar insuficiente DEPOIS da criação do pedido.
                // Isso não deveria acontecer se a verificação na criação do pedido for robusta e transacional.
                // Pode ser necessário cancelar o item, notificar, ou até mesmo o pedido.
                logger.error("Erro ao atualizar estoque para produto ID {}: {}", produto.getId(), e.getMessage());
                // Lançar uma exceção customizada
                throw new BusinessRuleException("Falha crítica ao atualizar estoque para o produto " + produto.getDescricao() +
                        ". Pedido pode precisar de revisão manual. Detalhe: " + e.getMessage());
            }
        }
    }

    private void reverterAtualizacaoEstoque(Pedido pedido) {
        logger.info("Revertendo atualização de estoque para o pedido cancelado: {}", pedido.getNumeroPedido());
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.adicionarEstoque(item.getQuantidade());
            produtoRepository.save(produto);
            logger.info("Estoque do produto ID {} ({}) revertido. Adicionado: {}. Novo estoque: {}",
                    produto.getId(), produto.getDescricao(), item.getQuantidade(), produto.getEstoque());
        }
    }

    private void gerarLancamentosContasAReceber(Pedido pedido) {
        // "Caso boleto, pix, cartão de débito o lançamento deve ser único, com data de lançamento do dia da venda e valor total da venda."
        // "Caso seja cartão de crédito, crediário etc, o sistema deve considerar a quantidade de vezes que o cliente optou em dividir
        // a compra e fazer os respectivos lançamentos..."
        logger.info("Gerando lançamentos em Contas a Receber para o pedido: {}", pedido.getNumeroPedido());
        // Lógica para interagir com um possível ContasAReceberService
        // Exemplo muito simplificado:
        // if (pedido.getFormasPagamento().contains(FormaPagamento.BOLETO) || ...) {
        //     // Criar lançamento único
        // } else if (pedido.getFormasPagamento().contains(FormaPagamento.CARTAO_CREDITO)) {
        //     // Criar lançamentos parcelados (precisaria de info de parcelas no Pedido ou FormaPagamento)
        // }
        logger.warn("Implementação de gerarLancamentosContasAReceber pendente.");
    }
}