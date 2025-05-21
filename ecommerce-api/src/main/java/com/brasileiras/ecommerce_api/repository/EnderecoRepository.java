package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByClienteId(Long clienteId);

}
