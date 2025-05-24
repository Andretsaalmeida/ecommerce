package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.enums.EstadoBrasileiro;
import com.brasileiras.ecommerce_api.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    Optional<Endereco> findByCepAndLogradouroAndNumeroAndBairroAndCidadeAndEstadoAndComplemento(
            String cep,
            String logradouro,
            String numero,
            String bairro,
            String cidade,
            EstadoBrasileiro estado,
            String complemento // Se este for null, a query será WHERE complemento IS NULL
    );

    /**
     * Encontra um endereço específico (pelo seu ID) que esteja associado a um cliente específico (pelo ID do cliente).
     * Esta query verifica se existe uma entrada na tabela de junção cliente_endereco
     * que conecta este endereço a este cliente.
     *
     * @param enderecoId O ID do Endereço.
     * @param clienteId O ID do Cliente.
     * @return Um Optional contendo o Endereço se encontrado e associado corretamente ao cliente.
     */
    @Query("SELECT e FROM Endereco e JOIN e.clientes c WHERE e.id = :enderecoId AND c.id = :clienteId")
    Optional<Endereco> findByIdAndClienteId(@Param("enderecoId") Long enderecoId, @Param("clienteId") Long clienteId);

    @Query("SELECT e FROM Endereco e JOIN e.clientes c WHERE c.id = :clienteId")
    List<Endereco> findAllByClienteId(@Param("clienteId") Long clienteId);
}
