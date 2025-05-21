package com.brasileiras.ecommerce_api.model;

import com.brasileiras.ecommerce_api.enums.FormaPagamento;
import com.brasileiras.ecommerce_api.enums.StatusPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Número do pedido não pode estar em branco")
    @Size(max = 30, message = "Número do pedido deve ter no máximo 30 caracteres")
    @Column(unique = true, nullable = false, length = 30)
    private String numeroPedido; // Gerado unicamente

    @NotNull(message = "Data do pedido não pode ser nula")
    @Column(nullable = false)
    private LocalDateTime dataPedido = LocalDateTime.now(); // Inicializa com a data/hora atual

    @NotNull(message = "Valor total não pode ser nulo")
    @PositiveOrZero(message = "Valor total deve ser positivo ou zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO; // Inicializa com zero

    @NotNull(message = "Status do pedido não pode ser nulo")
    @Enumerated(EnumType.STRING) // Armazena o nome do enum (ex: "AGUARDANDO_PAGAMENTO")
    @Column(nullable = false, length = 20)
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
    //@JoinColumn(name = "pedido_id", nullable = false) // Garante que ItemPedido tenha pedido_id
    private List<ItemPedido> itens = new ArrayList<>();

    // Coleção de Enums para formas de pagamento
    @ElementCollection(targetClass = FormaPagamento.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "pedido_formas_pagamento", joinColumns = @JoinColumn(name = "pedido_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 30) // Nome da coluna na tabela de junção
    @NotEmpty(message = "Pedido deve ter pelo menos uma forma de pagamento")
    private Set<FormaPagamento> formasPagamento = new HashSet<>(); // evitar duplicidade

    public Pedido(Long id, String numeroPedido, LocalDateTime dataPedido, BigDecimal valorTotal, StatusPedido status, Cliente cliente, Endereco enderecoEntrega, List<ItemPedido> itens, Set<FormaPagamento> formasPagamento) {
        this.id = id;
        this.numeroPedido = numeroPedido;
        this.dataPedido = dataPedido == null ? LocalDateTime.now() : dataPedido;
        this.valorTotal = valorTotal == null ? BigDecimal.ZERO : valorTotal;
        this.status = status == null ? StatusPedido.AGUARDANDO_PAGAMENTO : status;
        this.cliente = cliente;
        this.enderecoEntrega = enderecoEntrega;
        this.setItens(itens); // Garantir o valor total correto
        this.formasPagamento = formasPagamento == null ? new HashSet<>() : formasPagamento;
    }

    // --- Métodos auxiliares ---

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
        this.valorTotal = this.itens.stream()
                .map(ItemPedido::getSubtotal) // Assumindo que ItemPedido tem getSubtotal()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 'Setters' customizado para itens para garantir a bidirecionalidade e recálculo do total
     * ao definir a lista inteira de uma vez.
     */
    public void setItens(List<ItemPedido> itens) {
        // Limpa itens antigos e remove a referência deles a este pedido
        if (this.itens != null) {
            for (ItemPedido oldItem : new ArrayList<>(this.itens)) { // Itera sobre uma cópia para evitar ConcurrentModificationException
                this.removerItem(oldItem);
            }
        }
        // Adiciona novos itens
        this.itens = new ArrayList<>();
        if (itens != null) {
            for (ItemPedido newItem : itens) {
                this.adicionarItem(newItem);
            }
        }
        this.recalcularValorTotal(); // Garante que o total seja recalculado
    }

    public void adicionarFormaPagamento(FormaPagamento forma) {
        if (forma != null) {
            this.formasPagamento.add(forma);
        }
    }

    public void removerFormaPagamento(FormaPagamento forma) {
        this.formasPagamento.remove(forma);
    }

    public void setFormasPagamento(Set<FormaPagamento> formasPagamento) {
        this.formasPagamento = formasPagamento == null ? new HashSet<>() : formasPagamento;
    }
}

