package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.enums.EstadoBrasileiro;
import com.brasileiras.ecommerce_api.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
