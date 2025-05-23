package com.brasileiras.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe que representa um item de pedido em um sistema de e-commerce.
 * Um item de pedido é uma associação entre um produto e um pedido, contendo informações sobre a quantidade
 * e o preço unitário do produto no momento da compra.
 * A classe ItemPedido é crucial em qualquer sistema de vendas.
 * A principal característica dela é "congelar" o preço do produto no momento da compra
 * e associar uma quantidade a um pedido específico.
 */


@Entity
@Table(name = "item_pedido")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"pedido", "produto"}) //boa prática para coleções
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com a entidade Pedido
    @NotNull(message = "Pedido não pode ser nulo")
    @ManyToOne (fetch = FetchType.LAZY) // ItemPedido sempre pertence a um Pedido
    @JoinColumn(name = "pedido_id", nullable = false)
    @ToString.Exclude // Evitar recursão da lista de ItensPedido em Pedido
    private Pedido pedido;


    @NotNull(message = "Produto não pode ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    @ToString.Exclude // Segurança para evitar recursão.
    private Produto produto;

    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    @Column(nullable = false)
    private int quantidade;

    @NotNull(message = "Preço unitário não pode ser nulo")
    @Positive(message = "Preço unitário deve ser positivo") // Geralmente não é zero
    @Column(nullable = false, precision = 10, scale = 2) // Ex: 12345678.90
    private BigDecimal precoUnitario; // Preço do produto no momento da inclusão no pedido

    // --- Construtores ---

    /**
     * Construtor usado para criar um ItemPedido,
     * capturando o preço de venda atual do produto.
     */
    public ItemPedido(Pedido pedido, Produto produto, int quantidade) {
        if (pedido == null) throw new IllegalArgumentException("Pedido não pode ser nulo.");
        if (produto == null) throw new IllegalArgumentException("Produto não pode ser nulo.");
        if (quantidade < 1) throw new IllegalArgumentException("Quantidade deve ser no mínimo 1.");

        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;

        /*
          O preço unitário é capturado no momento da criação do ItemPedido.
          Garante que, mesmo se o preço do produto mudar no futuro,
          o preço registrado no pedido permanecerá o mesmo da hora da compra.
         */
        BigDecimal valorVendaProduto = produto.getValorVenda();
        if(valorVendaProduto == null) {
            throw new IllegalStateException("Produto " + produto.getId()
                    + " (" + produto.getDescricao() + " não possui valor de venda definido.");
        }
        this.precoUnitario = valorVendaProduto.setScale(2, RoundingMode.HALF_UP);

    }

    // --- Métodos auxiliares ---

    /**
     * Calcula o subtotal para este item do pedido (quantidade * precoUnitario).
     * @return O valor do subtotal como BigDecimal.
     */
    @Transient // JPA não tente mapeá-lo para uma coluna no banco de dados, pois é um valor calculado.
    public BigDecimal getSubtotal() {
        if (this.precoUnitario == null) {
            // Isso não deve acontecer devido às validações @NotNull e no construtor
            return BigDecimal.ZERO;
        }
        return this.precoUnitario.multiply(new BigDecimal(this.quantidade)).setScale(2, RoundingMode.HALF_UP);
    }
}
