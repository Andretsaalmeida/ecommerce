package com.brasileiras.ecommerce_api.dto;

import com.brasileiras.ecommerce_api.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {
    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "ID do endereço de entrega é obrigatório")
    private Long enderecoEntregaId; // ID do endereço escolhido pelo cliente

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    @Valid
    @Size(min = 1, message = "O pedido deve conter pelo menos um item.")
    private List<ItemPedidoRequestDTO> itens;

    @NotEmpty(message = "Pedido deve ter pelo menos uma forma de pagamento")
    private Set<FormaPagamento> formasPagamento;

}
