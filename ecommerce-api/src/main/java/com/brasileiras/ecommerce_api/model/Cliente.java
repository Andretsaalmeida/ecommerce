package com.brasileiras.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "enderecos") // Evitar recursão no equals/hashCode
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 150 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "CPF não pode estar em branco")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos (sem formatação)")
    @Column(unique = true, nullable = false, length = 11)
    //O banco de dados aplicará uma restrição de unicidade (UNIQUE constraint) nesta coluna.
    private String cpf;

    @NotBlank(message = "Email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(unique = true, nullable = false, length = 100)
    private String email; // Email deve ser único

    @Pattern(regexp = "\\d{11}", message = "Telefone deve conter 11 dígitos (DDD + número, sem formatação)")
    @Column(length = 11)
    private String telefone;

    // A senha NUNCA deve ser armazenada em texto e sim o HASH da senha.
    // A lógica de hashing acontece na camada de serviço, antes de save.
    @NotBlank(message = "Senha não pode estar em branco")
    @Column(nullable = false, length = 60) // BCrypt hashes são tipicamente de 60 caracteres
    @ToString.Exclude // Para não incluir a senha no toString gerado pelo Lombok
    private String senha; // Deveria ser armazenada com hash


    // Relação Muitos-para-Muitos com Endereco
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "cliente_endereco", // Nome da tabela de junção
            joinColumns = @JoinColumn(name = "cliente_id"), // Coluna que referencia Cliente
            inverseJoinColumns = @JoinColumn(name = "endereco_id") // Coluna que referencia Endereco
    )
    private Set<Endereco> enderecos = new HashSet<>();

    /**
     * Define o CPF, removendo caracteres não numéricos antes de atribuir.
     * Este método será usado pelo Lombok para o 'setter' setCpf.
     */
    public void setCpf(String cpf) {
        if (cpf != null) {
            this.cpf = cpf.replaceAll("[^0-9]", "");
        } else {
            this.cpf = null;
        }
    }

    /**
     * Retorna o CPF formatado (XXX.XXX.XXX-XX).
     * Este é um método ADICIONAL, não substitui o getCpf() gerado pelo Lombok.
     */
    public String getCpfFormatado() {
        if (this.cpf != null && this.cpf.matches("\\d{11}")) {
            return String.format("%s.%s.%s-%s",
                    this.cpf.substring(0, 3),
                    this.cpf.substring(3, 6),
                    this.cpf.substring(6, 9),
                    this.cpf.substring(9, 11));
        }
        return this.cpf;
    }

    /**
     * Define o Telefone, removendo caracteres não numéricos antes de atribuir.
     */
    public void setTelefone(String telefone) {
        if (telefone != null) {
            this.telefone = telefone.replaceAll("[^0-9]", "");
        } else {
            this.telefone = null;
        }
    }

    // Métodos para gerenciar a lista de endereços de forma segura (bidirecionalidade)
    public void addEndereco(Endereco endereco) {
        this.enderecos.add(endereco);
        if( endereco != null && endereco.getClientes() != null){
            endereco.getClientes().add(this); // Mantém a consistência do lado do Endereco
        } else if (endereco != null) {
            endereco.setClientes(new HashSet<>()); // Inicializa a lista de clientes se for nula
            endereco.getClientes().add(this); // Mantém a consistência do lado do Endereco
        }
        assert endereco != null;
        endereco.getClientes().add(this); // Mantém a consistência do lado do Endereco
    }

    public void removeEndereco(Endereco endereco) {
        this.enderecos.remove(endereco);
        if(endereco != null && endereco.getClientes() != null) {
            endereco.getClientes().remove(this); // Mantém a consistência do lado do Endereco
        } else if (endereco != null) {
            endereco.setClientes(new HashSet<>()); // Inicializa a lista de clientes se for nula
        }
        assert endereco != null;
        endereco.getClientes().remove(this); // Mantém a consistência
    }
}
