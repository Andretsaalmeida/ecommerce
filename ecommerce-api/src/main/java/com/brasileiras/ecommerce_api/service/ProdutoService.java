package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.model.Produto;
import com.brasileiras.ecommerce_api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto salvar(Produto produto) {
        // Adicionar validações e lógica de negócio
        // Ex: Verificar se o fornecedor existe, etc.
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    public void atualizarEstoque(Long produtoId, int quantidadeComprada) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado para atualização de estoque"));
        if (produto.getEstoque() < quantidadeComprada) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getDescricao());
        }
        produto.setEstoque(produto.getEstoque() - quantidadeComprada);
        produtoRepository.save(produto);
    }
}
