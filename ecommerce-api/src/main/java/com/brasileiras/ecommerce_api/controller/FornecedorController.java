package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.dto.FornecedorRequestDTO;
import com.brasileiras.ecommerce_api.dto.FornecedorResponseDTO;
import com.brasileiras.ecommerce_api.dto.FornecedorUpdateRequestDTO;
import com.brasileiras.ecommerce_api.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    public final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<FornecedorResponseDTO> criarFornecedor(@Valid @RequestBody FornecedorRequestDTO requestDTO) {
        FornecedorResponseDTO fornecedorCriado = fornecedorService.criarFornecedor(requestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(fornecedorCriado.id())
                .toUri();
        return ResponseEntity.created(location).body(fornecedorCriado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> buscarFornecedorPorId(@PathVariable Long id) {
        FornecedorResponseDTO fornecedor = fornecedorService.buscarFornecedorPorId(id);
        return ResponseEntity.ok(fornecedor);
    }

    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> listarTodosFornecedores() {
        List<FornecedorResponseDTO> fornecedores = fornecedorService.listarTodosFornecedores();
        if (fornecedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fornecedores);
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<FornecedorResponseDTO> atualizarFornecedor(@PathVariable Long id, @Valid @RequestBody FornecedorUpdateRequestDTO requestDTO) {
        FornecedorResponseDTO fornecedorAtualizado = fornecedorService.atualizarFornecedor(id, requestDTO);
        return ResponseEntity.ok(fornecedorAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarFornecedor(@PathVariable Long id) {
        fornecedorService.deletarFornecedor(id);
        String mensagemSucesso = "Fornecedor com ID " + id + " deletado com sucesso.";
        return ResponseEntity.ok(mensagemSucesso);
    }
}