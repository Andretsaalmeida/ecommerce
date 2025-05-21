package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.dto.PedidoRequestDTO;
import com.brasileiras.ecommerce_api.model.Pedido;
import com.brasileiras.ecommerce_api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        // Em um sistema real, o 'ID' do cliente logado viria do contexto de segurança (Spring Security)
        // e não do corpo da requisição para maior segurança.
        try {
            Pedido novoPedido = pedidoService.criarPedido(pedidoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
        } catch (RuntimeException e) {
            // Logar o erro e retornar uma mensagem apropriada
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPedidosPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(clienteId);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pedidos);
    }
}

