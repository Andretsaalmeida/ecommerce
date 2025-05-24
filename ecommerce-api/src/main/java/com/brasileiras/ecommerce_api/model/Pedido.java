package com.brasileiras.ecommerce_api.model;

import com.brasileiras.ecommerce_api.enums.FormaPagamento;
import com.brasileiras.ecommerce_api.enums.StatusPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"cliente", "enderecoEntrega", "itens"})
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Número do pedido não pode estar em branco")
    @Size(max = 30, message = "Número do pedido deve ter no máximo 30 caracteres")
    @Column(name = "numero_pedido", unique = true, nullable = false, length = 30)
    private String numeroPedido; // Gerado unicamente no service

    @NotNull(message = "Data do pedido não pode ser nula")
    @Column(name= "data_pedido", nullable = false)
    @Builder.Default // Inicializa com a data/hora atual
    private LocalDateTime dataPedido = LocalDateTime.now(); // Inicializa com a data/hora atual

    @NotNull(message = "Valor total não pode ser nulo")
    @PositiveOrZero(message = "Valor total deve ser positivo ou zero")
    @Column(name = " valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO; // Inicializa com zero

    @NotNull(message = "Status do pedido não pode ser nulo")
    @Enumerated(EnumType.STRING) // Armazena o nome do enum (ex: "AGUARDANDO_PAGAMENTO")
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO; // Inicializa com "Aguardando Pagamento"

    @NotNull(message = "Cliente não pode ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @ToString.Exclude
    private Cliente cliente;

    @NotNull(message = "Endereço de entrega não pode ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_entrega_id", nullable = false)
    @ToString.Exclude
    private Endereco enderecoEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    @Builder.Default
    @ToString.Exclude
    private List<ItemPedido> itens = new ArrayList<>();

    // Coleção de Enums para formas de pagamento
    @ElementCollection(targetClass = FormaPagamento.class, fetch = FetchType.LAZY) // coleção de enums
    @CollectionTable(name = "pedido_formas_pagamento", joinColumns = @JoinColumn(name = "pedido_id", referencedColumnName = "id") )// define a tabela de junção
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 30) // Nome da coluna na tabela de junção
    @NotEmpty(message = "Pedido deve ter pelo menos uma forma de pagamento")
    @Builder.Default
    private Set<FormaPagamento> formasPagamento = new HashSet<>(); // evitar duplicidade

//    public Pedido(Long id, String numeroPedido, LocalDateTime dataPedido, BigDecimal valorTotal, StatusPedido status, Cliente cliente, Endereco enderecoEntrega, List<ItemPedido> itens, Set<FormaPagamento> formasPagamento) {
//        this.id = id;
//        this.numeroPedido = numeroPedido;
//        this.dataPedido = dataPedido == null ? LocalDateTime.now() : dataPedido;
//        this.valorTotal = valorTotal == null ? BigDecimal.ZERO : valorTotal;
//        this.status = status == null ? StatusPedido.AGUARDANDO_PAGAMENTO : status;
//        this.cliente = cliente;
//        this.enderecoEntrega = enderecoEntrega;
//        this.setItens(itens); // Garantir o valor total correto
//        this.formasPagamento = formasPagamento == null ? new HashSet<>() : formasPagamento;
//    }

    // --- Métodos auxiliares de gerenciamento de coleções e estado ---

    /**
     * Adiciona um item ao pedido, atualizando o relacionamento bidirecional
     * e recalculando o valor total.
     */
    public void adicionarItem(ItemPedido item) {
        if (item != null) {
            this.itens.add(item);
            item.setPedido(this); // Mantém a consistência bidirecional
            this.recalcularValorTotal();
        }
    }

    /**
     * Remove um item do pedido e recalcula o valor total.
     */
    public void removerItem(ItemPedido item) {
        if (item != null && this.itens.remove(item)) {
            item.setPedido(null); // Quebra o lado do item (orphanRemoval cuidará da deleção do DB se configurado)
            this.recalcularValorTotal();
        }
    }

    /**
     * Recalcula o valor total do pedido com base nos seus itens.
     */
    public void recalcularValorTotal() {

    }

    /**
     * 'Setters' customizado para itens para garantir a bidirecionalidade e recálculo do total
     * ao definir a lista inteira de uma vez.
     */
    public void setItens(List<ItemPedido> novosItens) {
        // Remover itens antigos que não estão na nova lista
        if (this.itens != null) {
            List<ItemPedido> itensARemover = new ArrayList<>(this.itens);
            if (novosItens != null) {
                itensARemover.removeAll(novosItens); // Remove da lista de "a remover" os que permanecem
            }
            for (ItemPedido itemParaRemover : itensARemover) {
                this.removerItem(itemParaRemover); // Usa o método que já atualiza o lado do item
            }
        } else {
            this.itens = new ArrayList<>();
        }

        // Adicionar novos itens ou atualizar os existentes (se a lógica de "adicionarItem" for idempotente)
        if (novosItens != null) {
            for (ItemPedido novoItem : novosItens) {
                if (!this.itens.contains(novoItem)) { // Evita adicionar duplicatas se já existem na lista
                    this.adicionarItem(novoItem); // Usa o método que já seta o pedido no item
                } else {
                    // Se o item já existe, você pode querer atualizar sua quantidade ou outros atributos
                    // Isso dependeria da lógica de como 'novosItens' é construído.
                    // Por simplicidade, aqui apenas garantimos que o 'pedido' está setado.
                    novoItem.setPedido(this);
                }
            }
        }
        // A chamada ao this.recalcularValorTotal() já está em adicionarItem/removerItem.
        // Mas se a lista foi completamente substituída, uma chamada final pode ser segura.
        this.recalcularValorTotal();
    }

    public void adicionarFormaPagamento(FormaPagamento forma) {
        if (forma != null) {
            if (this.formasPagamento == null) {
                this.formasPagamento = new HashSet<>();
            }
            this.formasPagamento.add(forma);
        }
    }

    public void removerFormaPagamento(FormaPagamento forma) {
        if (this.formasPagamento != null) {
            this.formasPagamento.remove(forma);
        }
    }
}

