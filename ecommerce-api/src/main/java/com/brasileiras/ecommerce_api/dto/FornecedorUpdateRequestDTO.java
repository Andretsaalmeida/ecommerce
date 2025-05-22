package com.brasileiras.ecommerce_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorUpdateRequestDTO {

    // o ID aqui virá no path da URL

//    @Size(max = 100, message = "Razão Social deve ter no máximo 100 caracteres")
//    private String razaoSocial;
//
//    // A atualização de CNPJ pode ser complexa devido à sua unicidade.
//    // Geralmente, CNPJ não muda. Se precisar mudar, a validação de unicidade deve ser refeita.
//    // Para simplificar, vamos permitir, mas o service precisa tratar a unicidade.
//    @Size(min = 14, max = 18, message = "CNPJ inválido")
//    private String cnpj;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email; // Service precisa tratar unicidade se o email mudar

    @Size(min = 10, max = 15, message = "Telefone inválido")
    private String telefone;

    @NotNull(message = "Endereço do fornecedor não pode ser nulo na atualização")
    @Valid // Para validar os campos aninhados do EnderecoRequestDTO
    private EnderecoRequestDTO endereco; // Assume que o endereço completo é enviado para atualização
}
