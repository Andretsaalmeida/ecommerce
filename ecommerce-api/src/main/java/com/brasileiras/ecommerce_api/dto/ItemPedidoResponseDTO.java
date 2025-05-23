package com.brasileiras.ecommerce_api.dto;

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
}
