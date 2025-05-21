package com.brasileiras.ecommerce_api.dto;


import com.brasileiras.ecommerce_api.model.Cliente;
import lombok.Builder;

import java.util.List;


@Builder
public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        List<EnderecoResponseDTO> enderecos
) {
    // Método estático factory para criar um ClienteResponseDTO a partir de uma entidade Cliente.
    public static ClienteResponseDTO toClienteResponseDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .enderecos(cliente.getEnderecos().stream()
                        .map(EnderecoResponseDTO::fromEntity)
                        .toList())
                .build();
    }
}
