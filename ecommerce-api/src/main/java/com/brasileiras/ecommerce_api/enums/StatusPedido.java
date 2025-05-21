package com.brasileiras.ecommerce_api.enums;

import lombok.Getter;

/**
 * Enum que representa os diferentes status de um pedido em um sistema de 'e-commerce'.
 * Cada status tem uma descrição associada.
 * <p>
 * Os status incluem:
 * - AGUARDANDO_PAGAMENTO: O pedido foi criado, mas o pagamento ainda não foi recebido.
 * - PAGAMENTO_APROVADO: O pagamento foi aprovado e o pedido está em processamento.
 * - EM_SEPARACAO: O pedido é separado para envio.
 * - ENVIADO: O pedido foi enviado ao cliente.
 * - ENTREGUE: O pedido foi entregue ao cliente.
 * - CANCELADO: O pedido foi cancelado.
 * - REEMBOLSADO: O pedido foi reembolsado.
 */

@Getter
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
    PAGAMENTO_APROVADO("Pagamento Aprovado"),
    EM_SEPARACAO("Em Separação"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado"),
    REEMBOLSADO("Reembolsado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }
    public static StatusPedido fromDescricao(String descricao) {
        for (StatusPedido status : values()) {
            if (status.getDescricao().equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Descrição de status inválida: " + descricao);
    }
}
