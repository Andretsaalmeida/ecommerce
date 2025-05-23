package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.dto.ProdutoRequestDTO;
import com.brasileiras.ecommerce_api.dto.ProdutoResponseDTO;
import com.brasileiras.ecommerce_api.service.ProdutoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/produtos")
@Validated // Adiciona a anotação @Validated para validação de parâmetros
public class ProdutoController {

    private final ProdutoService produtoService;

    @Autowired
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(
            @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO,
            UriComponentsBuilder uriBuilder) {
        ProdutoResponseDTO produtoCriado = produtoService.criarProduto(produtoRequestDTO);
        URI uri = uriBuilder.path("/api/v1/produtos/{id}").buildAndExpand(produtoCriado.id()).toUri();
        return ResponseEntity.created(uri).body(produtoCriado);
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutos(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Integer estoqueMaximo,
            @PageableDefault(sort = "descricao") Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.listarProdutosComFiltros(
                descricao,
                fornecedorId,
                estoqueMaximo,
                pageable
        );
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("buscar/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PutMapping("atualizar/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoRequestDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        String mensagemSucesso = "Produto com ID " + id + " deletado com sucesso.";
        return ResponseEntity.ok(mensagemSucesso);
    }

    @PatchMapping("/{id}/estoque/adicionar")
    public ResponseEntity<ProdutoResponseDTO> adicionarEstoque(
            @PathVariable Long id,
            @RequestParam @Min(value = 1, message = "A quantidade deve ser no mínimo 1") int quantidade) {
        ProdutoResponseDTO produtoAtualizado = produtoService.adicionarEstoqueAoProduto(id, quantidade);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @PatchMapping("/{id}/estoque/remover")
    public ResponseEntity<ProdutoResponseDTO> removerEstoque(
            @PathVariable Long id,
            @RequestParam @Min(value = 1, message = "A quantidade deve ser no mínimo 1") int quantidade) {
        ProdutoResponseDTO produtoAtualizado = produtoService.removerEstoqueDoProduto(id, quantidade);
        return ResponseEntity.ok(produtoAtualizado);
    }
}
