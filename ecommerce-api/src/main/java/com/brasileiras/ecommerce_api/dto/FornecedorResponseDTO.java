package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.model.Fornecedor;

public record FornecedorResponseDTO(
        Long id,
        String razaoSocial,
        String cnpj,
        String email,
        String telefone,
        EnderecoResponseDTO endereco
) {
    // método estático factory para criar um FornecedorResponseDTO a partir de uma entidade Fornecedor
    public static FornecedorResponseDTO fromEntity(Fornecedor fornecedor) {
        if (fornecedor == null) {
            return null;
        }
        return new FornecedorResponseDTO(
                fornecedor.getId(),
                fornecedor.getRazaoSocial(),
                fornecedor.getCnpj(),
                fornecedor.getEmail(),
                fornecedor.getTelefone(),
                EnderecoResponseDTO.fromEntity(fornecedor.getEndereco())
        );
    }
}
