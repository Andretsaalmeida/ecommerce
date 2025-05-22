package com.brasileiras.ecommerce_api.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorRequestDTO {

    @NotBlank(message = "Razão Social não pode estar em branco")
    @Size(max = 100, message = "Razão Social deve ter no máximo 100 caracteres")
    private String razaoSocial;

    @NotBlank(message = "CNPJ não pode estar em branco")
    @Size(min = 14, max = 18, message = "CNPJ inválido (forneça com ou sem formatação)")
    private String cnpj;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @Size(min = 10, max = 15, message = "Telefone inválido (forneça com ou sem formatação)")
    private String telefone;

    @NotNull(message = "Endereço do fornecedor é obrigatório")
    @Valid // Para validar os campos aninhados do EnderecoRequestDTO
    private EnderecoRequestDTO endereco;

}
