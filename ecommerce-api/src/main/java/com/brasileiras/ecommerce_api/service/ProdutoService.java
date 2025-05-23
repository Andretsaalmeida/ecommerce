package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.ProdutoRequestDTO;
import com.brasileiras.ecommerce_api.dto.ProdutoResponseDTO;
import com.brasileiras.ecommerce_api.exception.DataConflictException;
import com.brasileiras.ecommerce_api.exception.ResourceNotFoundException;
import com.brasileiras.ecommerce_api.model.Fornecedor;
import com.brasileiras.ecommerce_api.model.Produto;
import com.brasileiras.ecommerce_api.repository.FornecedorRepository;
import com.brasileiras.ecommerce_api.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository, FornecedorRepository fornecedorRepository) {
        this.produtoRepository = produtoRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO) {
        if (produtoRepository.existsByCodigoBarrasProduto(produtoRequestDTO.getCodigoBarrasProduto())) {
            throw new DataConflictException("Já existe um produto com o código de barras interno (SKU): " + produtoRequestDTO.getCodigoBarrasProduto());
        }
        if (produtoRepository.existsByCodigoBarras(produtoRequestDTO.getCodigoBarras())) {
            throw new DataConflictException("Já existe um produto com o código de barras da NF: " + produtoRequestDTO.getCodigoBarras());
        }

        Fornecedor fornecedor = fornecedorRepository.findById(produtoRequestDTO.getFornecedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com ID: " + produtoRequestDTO.getFornecedorId()));

        Produto produto = produtoRequestDTO.toEntity(fornecedor);
        Produto produtoSalvo = produtoRepository.save(produto);
        return ProdutoResponseDTO.fromEntity(produtoSalvo);
    }

    /**
     * Lista produtos com filtros opcionais.
     *
     * @param descricaoFiltro      Filtro por descrição (opcional).
     * @param fornecedorIdFiltro   Filtro por ID do fornecedor (opcional).
     * @param estoqueMaximoFiltro  Filtro por estoque máximo (opcional).
     * @param pageable             Objeto Pageable para paginação.
     * @return Page de ProdutoResponseDTO com os produtos filtrados.
     */

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarProdutosComFiltros(
            String descricaoFiltro,
            Long fornecedorIdFiltro,
            Integer estoqueMaximoFiltro,
            Pageable pageable) {

        Page<Produto> produtosPage;
        if (StringUtils.hasText(descricaoFiltro)) {
            produtosPage = produtoRepository.findByDescricaoContainingIgnoreCase(descricaoFiltro, pageable);
        } else if (fornecedorIdFiltro != null) {
            produtosPage = produtoRepository.findByFornecedorId(fornecedorIdFiltro, pageable);
        } else if (estoqueMaximoFiltro != null) {
            produtosPage = produtoRepository.findByEstoqueLessThanEqual(estoqueMaximoFiltro, pageable);
        } else {
            produtosPage = produtoRepository.findAll(pageable);
        }
        return produtosPage.map(ProdutoResponseDTO::fromEntity); // Method reference para conversão
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        return ProdutoResponseDTO.fromEntity(produto);
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        if (produtoRequestDTO.getCodigoBarrasProduto() != null &&
                !produtoRequestDTO.getCodigoBarrasProduto().equals(produtoExistente.getCodigoBarrasProduto()) &&
                produtoRepository.existsByCodigoBarrasProduto(produtoRequestDTO.getCodigoBarrasProduto())) {
            throw new DataConflictException("Já existe outro produto com o código de barras interno (SKU): " + produtoRequestDTO.getCodigoBarrasProduto());
        }
        if (produtoRequestDTO.getCodigoBarras() != null &&
                !produtoRequestDTO.getCodigoBarras().equals(produtoExistente.getCodigoBarras()) &&
                produtoRepository.existsByCodigoBarras(produtoRequestDTO.getCodigoBarras())) {
            throw new DataConflictException("Já existe outro produto com o código de barras da NF: " + produtoRequestDTO.getCodigoBarras());
        }

        Fornecedor fornecedorParaAtualizacao = produtoExistente.getFornecedor();
        if (produtoRequestDTO.getFornecedorId() != null &&
                !produtoRequestDTO.getFornecedorId().equals(produtoExistente.getFornecedor().getId())) {
            fornecedorParaAtualizacao = fornecedorRepository.findById(produtoRequestDTO.getFornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Novo fornecedor não encontrado com ID: " + produtoRequestDTO.getFornecedorId()));
        }

        produtoRequestDTO.updateEntity(produtoExistente, fornecedorParaAtualizacao);
        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return ProdutoResponseDTO.fromEntity(produtoAtualizado);
    }

    @Transactional
    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

    @Transactional
    public ProdutoResponseDTO adicionarEstoqueAoProduto(Long produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para adicionar ao estoque deve ser positiva.");
        }
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));
        produto.adicionarEstoque(quantidade);
        Produto produtoSalvo = produtoRepository.save(produto);
        return ProdutoResponseDTO.fromEntity(produtoSalvo);
    }

    @Transactional
    public ProdutoResponseDTO removerEstoqueDoProduto(Long produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para remover do estoque deve ser positiva.");
        }
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + produtoId));
        produto.removerEstoque(quantidade);
        Produto produtoSalvo = produtoRepository.save(produto);
        return ProdutoResponseDTO.fromEntity(produtoSalvo);
    }

    public void atualizarEstoque(Long id, int quantidade) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        produto.adicionarEstoque(quantidade);
        produtoRepository.save(produto);
    }
}