package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.model.Cliente;
import com.brasileiras.ecommerce_api.model.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF deve ser válido")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "Telefone deve conter 11 dígitos (DDD + número, sem formatação)")
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotEmpty(message = "Pelo menos um endereço deve ser fornecido")
    @Valid // Para validar os campos dentro de cada EnderecoRequestDTO
    private List<EnderecoRequestDTO> enderecos;

    // Método para converter o DTO em uma entidade Cliente
    public static Cliente toEntity(ClienteRequestDTO clienteRequestDTO) {
        if(clienteRequestDTO == null) {
            return null;
        }
        Cliente cliente = Cliente.builder()
                .nome(clienteRequestDTO.getNome())
                .cpf(clienteRequestDTO.getCpf())
                .email(clienteRequestDTO.getEmail())
                .telefone(clienteRequestDTO.getTelefone())
                .senha(clienteRequestDTO.getSenha()) // ATENÇÃO: SENHA EM TEXTO PLANO! HASHEAR NO SERVIÇO!
                .build();

        // Mapear e associar endereços
        if (clienteRequestDTO.getEnderecos() != null && !clienteRequestDTO.getEnderecos().isEmpty()) {
            List<Endereco> enderecosEntidades = clienteRequestDTO.getEnderecos().stream()
                    .map(EnderecoRequestDTO::toEntity)
                    .collect(Collectors.toList());

            // Esta é uma forma de fazer a associação se o 'setter' de Cliente.setEnderecos
            // ou o construtor de Cliente (via builder) já tratar a bidirecionalidade.
            cliente.setEnderecos(enderecosEntidades);
        } else {
            cliente.setEnderecos(Collections.emptyList());
        }
        return cliente;
    }

}
