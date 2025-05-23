package com.brasileiras.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/*
 * Classe que representa um produto no sistema de e-commerce BRASILEIRAS.
 * Um produto possui um código interno, descrição, código de barras (da nota fiscal),
 * valor de compra, valor de venda, estoque e está associado a um fornecedor associado.
 */

@Entity
@Table(name = "produtos", uniqueConstraints = { // Adicionado para garantir unicidade no nível do BD
        @UniqueConstraint(name = "uk_produto_codigo_barras", columnNames = "produto_codigo_barras"),
        @UniqueConstraint(name = "uk_nf_codigo_barras", columnNames = "nf_codigo_barras")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "fornecedor")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código do produto não pode estar em branco")
    @Size(max = 50, message = "Código do produto deve ter no máximo 50 caracteres")
    @Column(name= "produto_codigo_barras",unique = true, nullable = false, length = 50)
    private String codigoBarrasProduto;

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String descricao;

    @NotBlank(message = "Código de barras não pode estar em branco")
    @Size(min = 8, max = 14, message = "Código de barras deve ter entre 8 e 14 caracteres.") // Para EAN-8, EAN-13, UPC-A, GTIN-14
    @Column(name = "nf_codigo_barras", length = 13, nullable = false)
    private String codigoBarras;

    @NotNull(message = "Valor de compra não pode ser nulo")
    @PositiveOrZero(message = "Valor de compra deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Valor de compra inválido. Formato esperado: até 8 dígitos inteiros e 2 decimais.")
    @Column(name= "valor_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCompra; // Ex: 12345678.90

    @NotNull(message = "Valor de venda não pode ser nulo")
    @Positive(message = "Valor de venda deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Valor de venda inválido. Formato esperado: até 8 dígitos inteiros e 2 decimais.")
    @Column(name= "valor_venda",nullable = false, precision = 10, scale = 2)
    private BigDecimal valorVenda;

    @NotNull(message = "Estoque não pode ser nulo") // Embora int primitivo não possa ser nulo
    @Min(value = 0, message = "Estoque não pode ser negativo")
    @Column(nullable = false)
    @Builder.Default // Garante que o builder use O valor padrão
    private Integer estoque = 0;

    // Relacionamento com a tabela de fornecedores
    // Um produto pode ter vários fornecedores
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false) // Coluna FK na tabela de produtos
    @NotNull(message = "Fornecedor não pode ser nulo" )// Se um produto sempre deve ter um fornecedor
    @ToString.Exclude
    private Fornecedor fornecedor;


    /**
     * Adiciona uma quantidade ao estoque.
     * @param quantidade A quantidade a ser adicionada (deve ser positiva).
     * @throws IllegalArgumentException se a quantidade for não positiva.
     */
    public void adicionarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para adicionar ao estoque deve ser positiva.");
        }
        this.estoque += quantidade;
    }

    /**
     * Remove uma quantidade do estoque.
     * @param quantidade A quantidade a ser removida (deve ser positiva).
     * @throws IllegalArgumentException se a quantidade for não positiva.
     * @throws IllegalStateException se não houver estoque suficiente.
     */
    public void removerEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para remover do estoque deve ser positiva.");
        }
        if (this.estoque < quantidade) {
            throw new IllegalStateException("Estoque insuficiente para o produto " + this.descricao +
                    ". Solicitado: " + quantidade + ", Disponível: " + this.estoque);
        }
        this.estoque -= quantidade;
    }

    /**
     * Verifica se há estoque disponível para uma determinada quantidade.
     * @param quantidadeDesejada A quantidade desejada.
     * @return true se houver estoque suficiente, false caso contrário.
     */
    public boolean temEstoqueSuficiente(int quantidadeDesejada) {
        if (quantidadeDesejada <= 0) { // Não faz sentido verificar estoque para 0 ou negativo
            return true; // Ou false, dependendo da interpretação. Se for para venda, quantidadeDesejada > 0.
        }
        return this.estoque >= quantidadeDesejada;
    }
}
