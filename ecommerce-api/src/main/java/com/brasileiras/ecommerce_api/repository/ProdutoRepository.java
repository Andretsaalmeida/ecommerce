package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigoBarrasProduto(String codigoBarrasProduto);
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    boolean existsByCodigoBarrasProduto(String codigoBarrasProduto);
    boolean existsByCodigoBarras(String codigoBarras);

    /**
     * Encontra produtos cuja descrição contenha a string fornecida (case-insensitive), com paginação e ordenação.
     * @param descricao Termo de busca para a descrição.
     * @param pageable Objeto contendo informações de paginação e ordenação.
     * @return Uma página de produtos correspondentes.
     */
    Page<Produto> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);

    /**
     * Encontra produtos com estoque abaixo de um certo limite, com paginação e ordenação.
     * @param quantidadeMaxima Limite máximo de estoque.
     * @param pageable Objeto contendo informações de paginação e ordenação.
     * @return Uma página de produtos com estoque baixo.
     */
    Page<Produto> findByEstoqueLessThanEqual(int quantidadeMaxima, Pageable pageable);

    /**
     * Encontra produtos de um fornecedor específico, com paginação e ordenação.
     * @param fornecedorId ID do fornecedor.
     * @param pageable Objeto contendo informações de paginação e ordenação.
     * @return Uma página de produtos do fornecedor.
     */
    Page<Produto> findByFornecedorId(Long fornecedorId, Pageable pageable);
}
