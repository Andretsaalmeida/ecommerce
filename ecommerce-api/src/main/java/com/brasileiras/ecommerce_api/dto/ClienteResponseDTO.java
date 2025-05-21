package com.brasileiras.ecommerce_api.dto;


import com.brasileiras.ecommerce_api.model.Cliente;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Builder
public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        List<EnderecoResponseDTO> enderecos
) {

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        List<EnderecoResponseDTO> enderecosResponse;
        if(cliente.getEnderecos() != null){
            enderecosResponse = cliente.getEnderecos()
                    .stream()
                    .map(EnderecoResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        } else {
            enderecosResponse = Collections.emptyList();
        }

        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .enderecos(enderecosResponse)
                .build();
    }
}
