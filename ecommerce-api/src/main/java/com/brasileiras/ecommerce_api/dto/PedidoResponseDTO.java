package com.brasileiras.ecommerce_api.dto;


import com.brasileiras.ecommerce_api.enums.FormaPagamento;
import com.brasileiras.ecommerce_api.enums.StatusPedido;
import com.brasileiras.ecommerce_api.model.Cliente;
import com.brasileiras.ecommerce_api.model.Endereco;
import com.brasileiras.ecommerce_api.model.ItemPedido;
import com.brasileiras.ecommerce_api.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections; // Para lista vazia segura
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//Este DTO representa um pedido que é enviado de volta pela API (após criação ou ao buscar detalhes).

public record PedidoResponseDTO(
        Long id,
        String numeroPedido,
        LocalDateTime dataPedido,
        BigDecimal valorTotal,
        StatusPedido status,
        ClienteResponseDTO cliente,
        EnderecoResponseDTO enderecoEntrega,
        List<ItemPedidoResponseDTO> itens,
        Set<FormaPagamento> formasPagamento
) {
    public static PedidoResponseDTO fromEntity(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getDataPedido(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                mapClienteToDTO(pedido.getCliente()),
                mapEnderecoToDTO(pedido.getEnderecoEntrega()),
                mapItensToDTO(pedido.getItens()),

                /* Garantir que, mesmo se o Set na entidade for nulo
                  (o que não deveria acontecer com a inicialização new HashSet<>()),
                  o DTO receba um conjunto vazio em vez de nulo,
                  o que é geralmente mais seguro para os consumidores da API.
                  */
               pedido.getFormasPagamento() != null ? pedido.getFormasPagamento() : Collections.emptySet() // Garantir que não seja nulo
        );
    }

    // Método auxiliar para mapear Cliente para ClienteResponseDTO
    private static ClienteResponseDTO mapClienteToDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return ClienteResponseDTO.fromEntity(cliente);
    }

    // Método auxiliar para mapear Endereco para EnderecoResponseDTO
    private static EnderecoResponseDTO mapEnderecoToDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return EnderecoResponseDTO.fromEntity(endereco);
    }

    // Método auxiliar para mapear List<ItemPedido> para List<ItemPedidoResponseDTO>
    // Retorna uma lista vazia imutável se a lista de itens for nula ou vazia
    // Isso evita a necessidade de verificar nulo em cada chamada
    // e garante que o consumidor da API sempre receba uma lista válida.
    private static List<ItemPedidoResponseDTO> mapItensToDTO(List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            return Collections.emptyList(); // Retorna uma lista vazia imutável
        }
        return itens.stream()
                .map(ItemPedidoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}