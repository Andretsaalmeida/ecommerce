package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.dto.PedidoRequestDTO;
import com.brasileiras.ecommerce_api.dto.PedidoResponseDTO;
import com.brasileiras.ecommerce_api.dto.PedidoStatusUpdateRequestDTO; // Certifique-se de ter este DTO
import com.brasileiras.ecommerce_api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @Valid @RequestBody PedidoRequestDTO pedidoRequestDTO,
            UriComponentsBuilder uriBuilder) {
        PedidoResponseDTO pedidoCriado = pedidoService.criarPedido(pedidoRequestDTO);
        URI uri = uriBuilder.path("/api/pedidos/{id}").buildAndExpand(pedidoCriado.id()).toUri(); // Acessando 'id()' do record
        return ResponseEntity.created(uri).body(pedidoCriado);
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResponseDTO>> listarPedidos(
            @PageableDefault(sort = "dataPedido,desc") Pageable pageable) {
        Page<PedidoResponseDTO> pedidos = pedidoService.listarPedidos(pageable);
        return ResponseEntity.ok(pedidos);
    }


    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<PedidoResponseDTO>> listarPedidosPorCliente(
            @PathVariable Long clienteId,
            @PageableDefault(sort = "dataPedido,desc") Pageable pageable) { // Adicionado Pageable
        Page<PedidoResponseDTO> pedidosPage = pedidoService.listarPedidosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(pedidosPage);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorNumero(@PathVariable String numeroPedido) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorNumero(numeroPedido);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @Valid @RequestBody PedidoStatusUpdateRequestDTO statusUpdateRequestDTO) {
        PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarStatusPedido(id, statusUpdateRequestDTO.novoStatus());
        return ResponseEntity.ok(pedidoAtualizado);
    }
}

