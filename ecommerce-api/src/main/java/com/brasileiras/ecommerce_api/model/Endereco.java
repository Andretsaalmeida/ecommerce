package com.brasileiras.ecommerce_api.model;

import com.brasileiras.ecommerce_api.enums.EstadoBrasileiro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "enderecos") // Boa prática nomear a tabela explicitamente no plural
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder //útil para criar objetos com muitos campos de forma mais legível.
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Logradouro não pode estar em branco")
    @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
    @Column(nullable = false) // nullable = false por padrão
    private String logradouro;

    // Número pode ser "S/N" (sem número) ou conter letras (Lote, Quadra),
    // então String é apropriado. Pode ser nulo ou ter um tamanho máximo.
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(length = 20) // nullable = true por padrão
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String complemento;

    @NotBlank(message = "Bairro não pode estar em branco")
    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String bairro;

    @NotBlank(message = "Cidade não pode estar em branco")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String cidade;

    @NotNull(message = "Estado não pode ser nulo")
    @Enumerated(EnumType.STRING) // Armazena o NOME do enum ("SP", "RJ") no banco
    @Column(nullable = false, length = 2) // O nome do enum (a sigla) terá 2 caracteres
    private EstadoBrasileiro estado;

    @NotBlank(message = "CEP não pode estar em branco")
    // Armazenar apenas os 8 dígitos e formatar na apresentação.
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos numéricos (sem hífen)")
    @Column(nullable = false, length = 8) // ou length = 9 se armazenar com hífen
    private String cep;

    // Relacionamento com Cliente
    @NotNull(message = "Cliente não pode ser nulo") // Garante que um endereço sempre pertence a um cliente
    @ManyToOne(fetch = FetchType.LAZY) // LAZY é geralmente melhor para performance
    // Todo endereço deve estar associado a um cliente / FK
    @JoinColumn(name = "cliente_id", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    // Método para formatar o CEP com hífen (se necessário)
    public String getCepFormatado() {
        if (this.cep == null || this.cep.length() != 8) {
            return this.cep; // Retorna como está se for inválido ou nulo
        }
        return String.format("%s-%s",
                this.cep.substring(0, 5),
                this.cep.substring(5, 8));
    }

    public void setCep(String cep) {
        if (cep != null) {
            this.cep = cep.replaceAll("[^0-9]", ""); // Remove tudo que não for dígito
        } else {
            this.cep = null;
        }
    }
}