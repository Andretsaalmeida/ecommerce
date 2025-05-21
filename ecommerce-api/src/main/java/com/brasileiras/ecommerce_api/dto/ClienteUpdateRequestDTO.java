package com.brasileiras.ecommerce_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * Data Transfer Object (DTO) para atualizar os dados de um cliente.
 * Utilizado para receber os dados da requisição de atualização.
 */

@Data
@Builder
public class ClienteUpdateRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres.")
    private String nome;

    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 100, message = "E-mail não pode exceder 100 caracteres.")
    private String email;

    @Pattern(regexp = "\\d{11}", message = "Telefone deve conter 11 dígitos (DDD + número, sem formatação)")
    @Size(min = 10, max = 11, message = "Telefone deve ter entre 10 e 11 dígitos.")
    private String telefone;

    @NotEmpty(message = "Pelo menos um endereço deve ser fornecido")
    @Valid
    private List<EnderecoRequestDTO> enderecos;
}
