package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.enums.EstadoBrasileiro;
import com.brasileiras.ecommerce_api.model.Endereco;
import lombok.Builder;

@Builder
public record EnderecoResponseDTO(
        Long id,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        EstadoBrasileiro estado,
        String cep // Pode ser o CEP formatado
) {

    // método estático factory para criar um EnderecoResponseDTO a partir de uma entidade Endereco
    public static EnderecoResponseDTO fromEntity(Endereco endereco) {
        if(endereco == null) {
            return null;
        }
        return EnderecoResponseDTO.builder()
                .id(endereco.getId())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .cep(endereco.getCepFormatado())
                .build();
    }
}

