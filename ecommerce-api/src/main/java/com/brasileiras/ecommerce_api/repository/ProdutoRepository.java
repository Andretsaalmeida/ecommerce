package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.Fornecedor;
import com.brasileiras.ecommerce_api.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigoProduto(String codigoProduto);
    boolean existsByCodigoProduto(String codigoProduto);
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    boolean existsByCodigoBarras(String codigoBarras);
    Page<Produto> findByDescricaoContainingIgnoreCase(String descricaoTermo, Pageable pageable);
    Page<Produto> findByFornecedor(Fornecedor fornecedor, Pageable pageable);
    /**
     * Busca produtos de um fornecedor específico pelo ID do fornecedor.
     * @param fornecedorId O ID do Fornecedor.
     * @param pageable Informações de paginação e ordenação.
     * @return Uma página de produtos do fornecedor especificado.
     */
    Page<Produto> findByFornecedorId(Long fornecedorId, Pageable pageable);

}
