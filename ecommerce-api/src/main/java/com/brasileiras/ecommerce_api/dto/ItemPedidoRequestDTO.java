package com.brasileiras.ecommerce_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Útil se você for usar Jackson para desserializar JSON
@AllArgsConstructor // Útil para testes ou construção manual
@Builder
public class ItemPedidoRequestDTO {
    @NotNull (message = "O ID do produto não pode ser nulo")
    private Long produtoId;

    @NotNull (message = "A quantidade não pode ser nula")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantidade;


}
