package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.model.ItemPedido;
import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record ItemPedidoResponseDTO(
        Long id,
        Long produtoId,
        Long pedidoId,
        String produtoDescricao,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subTotal
) {

    /**
     * Cria um ItemPedidoResponseDTO a partir de um ItemPedido.
     * @param itemPedido O ItemPedido.
     * @return Um ItemPedidoResponseDTO.
     */
    public static ItemPedidoResponseDTO fromEntity(ItemPedido itemPedido) {
        if (itemPedido == null) {
            return null;
        }

        return new ItemPedidoResponseDTO (
                itemPedido.getId(),
                itemPedido.getProduto().getId(),
                itemPedido.getPedido().getId(),
                itemPedido.getProduto().getDescricao(),
                itemPedido.getQuantidade(),
                itemPedido.getPrecoUnitario(),
                itemPedido.getSubtotal()
        );

    }
}
