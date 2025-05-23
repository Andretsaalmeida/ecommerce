package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.model.Produto;
import java.math.BigDecimal;


public record ProdutoResponseDTO(
        Long id,
        String codigoBarrasProduto,
        String descricao,
        String codigoBarras,
        BigDecimal valorCompra,
        BigDecimal valorVenda,
        Integer estoque,
        FornecedorResponseDTO fornecedor
) {
    /**
     * Cria um ProdutoResponseDTO a partir de uma entidade Produto.
     * @param produto A entidade Produto.
     * @return Um ProdutoResponseDTO.
     */
    public static ProdutoResponseDTO fromEntity(Produto produto) {
        if (produto == null) {
            return null;
        }

        FornecedorResponseDTO fornecedorDTO = null;
        if (produto.getFornecedor() != null) {
            fornecedorDTO = FornecedorResponseDTO.fromEntity(produto.getFornecedor());
        }

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigoBarrasProduto(),
                produto.getDescricao(),
                produto.getCodigoBarras(),
                produto.getValorCompra(),
                produto.getValorVenda(),
                produto.getEstoque(),
                fornecedorDTO
        );
    }
}