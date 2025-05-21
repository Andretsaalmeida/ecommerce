package com.brasileiras.ecommerce_api.enums;

import lombok.Getter;

/**
 * Enumeração que representa as diferentes formas de pagamento disponíveis em um sistema de 'e-commerce'.
 * Cada forma de pagamento possui uma descrição associada.
 * <p>
 * As formas de pagamento incluem:
 * - CARTAO_CREDITO: Pagamento via cartão de crédito.
 * - CARTAO_DEBITO: Pagamento via cartão de débito.
 * - BOLETO_BANCARIO: Pagamento via boleto bancário.
 * - PIX: Pagamento via Pix.
 * - DINHEIRO: Pagamento em dinheiro.
 * - TRANSFERENCIA_BANCARIA: Pagamento via transferência bancária.
*/

@Getter
public enum FormaPagamento {
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    BOLETO_BANCARIO("Boleto Bancário"),
    PIX("Pix"),
    DINHEIRO("Dinheiro"),
    TRANSFERENCIA_BANCARIA("Transferência Bancária");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public static FormaPagamento fromDescricao(String descricao) {
        for (FormaPagamento forma : values()) {
            if (forma.getDescricao().equalsIgnoreCase(descricao)) {
                return forma;
            }
        }
        throw new IllegalArgumentException("Descrição de forma de pagamento inválida: " + descricao);
    }
}
