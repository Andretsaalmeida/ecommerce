package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.model.Fornecedor;
import com.brasileiras.ecommerce_api.model.Produto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoRequestDTO {

    @NotBlank(message = "O código de barras interno do produto (SKU) não pode estar em branco")
    @Size(max = 50, message = "O código de barras interno do produto (SKU) deve ter no máximo 50 caracteres")
    private String codigoBarrasProduto;

    @NotBlank(message = "A descrição não pode estar em branco")
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String descricao;

    @NotBlank(message = "O código de barras da Nota Fiscal não pode estar em branco")
    @Size(min = 8, max = 14, message = "O código de barras da Nota Fiscal deve ter entre 8 e 14 caracteres.")
    private String codigoBarras; // O código de barras da NF (EAN-8, EAN-13, UPC-A, GTIN-14)

    @NotNull(message = "O valor de compra não pode ser nulo")
    @PositiveOrZero(message = "O valor de compra deve ser positivo ou zero")
    @Digits(integer = 8, fraction = 2, message = "Valor de compra inválido. Formato: até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal valorCompra;

    @NotNull(message = "O valor de venda não pode ser nulo")
    @Positive(message = "O valor de venda deve ser positivo.")
    @Digits(integer = 8, fraction = 2, message = "Valor de venda inválido. Formato: até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal valorVenda;

    @Min(value = 0, message = "O estoque não pode ser negativo")
    private Integer estoque;

    @NotNull(message = "O ID do fornecedor não pode ser nulo")
    private Long fornecedorId;

    public Produto toEntity(Fornecedor fornecedor) {
        Produto produto = Produto.builder()
                .codigoBarrasProduto(this.codigoBarrasProduto)
                .descricao(this.descricao)
                .codigoBarras(this.codigoBarras)
                .valorCompra(this.valorCompra)
                .valorVenda(this.valorVenda)
                .fornecedor(fornecedor)
                .build();
        produto.setEstoque(this.estoque != null ? this.estoque : 0);
        return produto;
    }

    public void updateEntity(Produto produtoExistente, Fornecedor novoFornecedor) {
        if (this.codigoBarrasProduto != null) produtoExistente.setCodigoBarrasProduto(this.codigoBarrasProduto);
        if (this.descricao != null) produtoExistente.setDescricao(this.descricao);
        if (this.codigoBarras != null) produtoExistente.setCodigoBarras(this.codigoBarras);
        if (this.valorCompra != null) produtoExistente.setValorCompra(this.valorCompra);
        if (this.valorVenda != null) produtoExistente.setValorVenda(this.valorVenda);
        if (this.estoque != null) produtoExistente.setEstoque(this.estoque);
        if (novoFornecedor != null) {
            produtoExistente.setFornecedor(novoFornecedor);
        }
    }
}