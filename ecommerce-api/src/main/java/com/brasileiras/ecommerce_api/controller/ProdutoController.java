package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.model.Produto;
import com.brasileiras.ecommerce_api.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    // O controller é responsável por receber as requisições HTTP e delegar para o serviço
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> listarProdutos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Produto> criarProduto(@RequestBody Produto produto) {
        // Em um cenário real, usar DTOs e validações (@Valid)
        Produto novoProduto = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoService.buscarPorId(id)
                .map(produtoExistente -> {
                    produto.setId(id); // Garante que o ID correto seja usado
                    // Copiar apenas os campos permitidos para atualização
                    produtoExistente.setDescricao(produto.getDescricao());
                    produtoExistente.setValorVenda(produto.getValorVenda());
                    produtoExistente.setEstoque(produto.getEstoque());
                    // etc.
                    return ResponseEntity.ok(produtoService.salvar(produtoExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        if (produtoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
