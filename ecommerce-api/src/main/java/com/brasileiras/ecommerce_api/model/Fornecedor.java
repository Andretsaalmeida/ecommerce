package com.brasileiras.ecommerce_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fornecedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fornecedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Razão Social não pode estar em branco")
    @Size(max = 100, message = "Razão Social deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 100)
    private String razaoSocial;

    @NotBlank(message = "CNPJ não pode estar em branco")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos (sem formatação)")
    @Column(unique = true, nullable = false, length = 14)
    private String cnpj;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(unique = true, length = 100)
    private String email;

    @Pattern(regexp = "\\d{11}", message = "Telefone deve conter 11 dígitos (DDD + número, sem formatação)")
    @Column(length = 11) // Armazenar apenas DDD + número (ex: 11987654321)
    private String telefone;

    // Relacionamento One-to-One com Endereco: Um fornecedor tem um endereço principal.
    // CascadeType.ALL: Operações no Fornecedor (persistir, remover, etc.) são cascateadas para o Endereco.
    // orphanRemoval = true: Se o Endereco for desassociado do Fornecedor (ex: fornecedor.setEndereco(null)),
    // ele será removido do banco. Útil se o endereço só existe no contexto do fornecedor.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id") // Chave estrangeira na tabela 'fornecedores'
    @NotNull(message = "Endereço do fornecedor não pode ser nulo") // Um fornecedor SEMPRE tem endereço
    private Endereco endereco;

    // Relacionamento One-to-Many com Produto: Um fornecedor pode ter muitos produtos.
    // mappedBy = "fornecedor": O lado "mestre" do relacionamento é o Produto, que tem a chave estrangeira.
    // CascadeType.ALL: Operações no Fornecedor (persistir, remover, etc.) são cascateadas para os Produtos.

    @OneToMany(
            mappedBy = "fornecedor", cascade = {
                    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}
            // Evita CascadeType.REMOVE por padrão, a menos que seja desejado
    )
    private List<Produto> produtos = new ArrayList<>();

    /**
     * Retorna o CNPJ formatado (XX.XXX.XXX/XXXX-XX).
     * Assume que o CNPJ está armazenado apenas com dígitos.
     */
    public String getCnpjFormatado() {
        if (this.cnpj != null && this.cnpj.matches("\\d{14}")) {
            return String.format("%s.%s.%s/%s-%s",
                    this.cnpj.substring(0, 2),
                    this.cnpj.substring(2, 5),
                    this.cnpj.substring(5, 8),
                    this.cnpj.substring(8, 12),
                    this.cnpj.substring(12, 14));
        }
        return this.cnpj; // Retorna como está se não for o formato esperado ou for nulo
    }

    /**
     * Define o CNPJ, removendo caracteres não numéricos antes de atribuir.
     */
    public void setCnpj(String cnpj) {
        if (cnpj != null) {
            this.cnpj = cnpj.replaceAll("[^0-9]", ""); // Remove tudo que não for dígito
        } else {
            this.cnpj = null;
        }
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

    // Se precisar de métodos para adicionar/remover produtos de forma segura (bidirecionalidade)
    public void addProduto(Produto produto) {
        this.produtos.add(produto);
        produto.setFornecedor(this); // Mantém a consistência do lado do Produto
    }

    public void removeProduto(Produto produto) {
        this.produtos.remove(produto);
        produto.setFornecedor(null); // Mantém a consistência
    }
}
