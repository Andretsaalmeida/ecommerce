package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    Optional<Fornecedor> findByEmail(String email);
    /**
     * Verifica se já existe um fornecedor com o CNPJ informado.
     * Mais eficiente do que carregar a entidade inteira para checar existência.
     * @param cnpj O CNPJ a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);
    boolean existsByEndereco_Id(Long enderecoId);
    /**
     * Exemplo de método de consulta personalizado.
     * O Spring Data JPA irá gerar a implementação automaticamente
     * com base no nome do método.
     *
     * @param razaoSocial A razão social do fornecedor.
     * @return Uma lista de fornecedores com a razão social especificada.
     */
    List<Fornecedor> findByRazaoSocial(String razaoSocial);
}
