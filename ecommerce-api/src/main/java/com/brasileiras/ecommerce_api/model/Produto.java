package com.brasileiras.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * Classe que representa um produto no sistema.
 * Um produto tem um código interno, descrição, código de barras,
 * valor de compra, valor de venda, estoque e um fornecedor associado.
 */

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código do produto não pode estar em branco")
    @Size(max = 50, message = "Código do produto deve ter no máximo 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String codigoProduto; // Código interno da Brasileiras

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String descricao;

    // Código de barras (EAN-13, por exemplo) pode ser opcional ou ter um formato específico
    @NotBlank(message = "Código de barras não pode estar em branco")
    @Pattern(regexp = "\\d{13}", message = "Código de barras deve conter 13 dígitos numéricos (EAN-13)")
    @Column(unique = true, length = 13, nullable = false)
    private String codigoBarras;

    @NotNull(message = "Valor de compra não pode ser nulo")
    @PositiveOrZero(message = "Valor de compra deve ser positivo ou zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCompra; // Ex: 12345678.90

    @NotNull(message = "Valor de venda não pode ser nulo")
    @PositiveOrZero(message = "Valor de venda deve ser positivo ou zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorVenda;

    @NotNull(message = "Estoque não pode ser nulo") // Embora int primitivo não possa ser nulo
    @Min(value = 0, message = "Estoque não pode ser negativo")
    @Column(nullable = false)
    private int estoque = 0;

    // Relacionamento com a tabela de fornecedores
    // Um fornecedor pode ter vários produtos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false) // Coluna FK na tabela de produtos
    @NotNull(message = "Fornecedor não pode ser nulo" )// Se um produto sempre deve ter um fornecedor
    @ToString.Exclude
    private Fornecedor fornecedor;

    /**
     * Calcula a margem de lucro bruta percentual sobre o valor de compra.
     * Retorna BigDecimal.ZERO se valorCompra for nulo ou zero para evitar divisão por zero.
     */
    @Transient // Não mapear este método como uma coluna no banco
    public BigDecimal getMargemLucroPercentual() {
        if (valorCompra == null || valorVenda == null || valorCompra.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // Margem = ((Venda - Compra) / Compra) * 100
        BigDecimal lucroBruto = valorVenda.subtract(valorCompra);
        return lucroBruto.divide(valorCompra, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

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
     * @return true se a remoção foi bem-sucedida, false se não havia estoque suficiente.
     * @throws IllegalArgumentException se a quantidade for não positiva.
     */
    public boolean retirarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para retirar do estoque deve ser positiva.");
        }
        if (this.estoque >= quantidade) {
            this.estoque -= quantidade;
            return true;
        }
        return false; // Estoque insuficiente
    }

    /**
     * Verifica se há estoque disponível para uma determinada quantidade.
     * @param quantidadeDesejada A quantidade desejada.
     * @return true se houver estoque suficiente, false caso contrário.
     */
    public boolean temEstoqueSuficiente(int quantidadeDesejada) {
        return this.estoque >= quantidadeDesejada;
    }
}
