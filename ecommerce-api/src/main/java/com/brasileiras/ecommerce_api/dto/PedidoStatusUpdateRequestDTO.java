package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.enums.StatusPedido;

public record PedidoStatusUpdateRequestDTO(
        StatusPedido novoStatus
) {
}
