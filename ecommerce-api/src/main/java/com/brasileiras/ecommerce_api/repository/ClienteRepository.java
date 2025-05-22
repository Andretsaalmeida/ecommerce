package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/* @Repository: opcional, mas recomendada para clareza.
* Spring Data JPA detecta automaticamente as interfaces que estendem JpaRepository
* e as registra como beans de repositório.
*/
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
   /* Optional é a melhor prática para métodos que podem ou não encontrar uma entidade.
    * Isso evita NullPointerExceptions: Isso força o código que chama este método a lidar
    * com o caso de ausência de forma explícita (usando isPresent(), orElse(), orElseThrow(),
    * e torna o código mais legível.
    * Além disso, o uso de Optional é uma maneira de expressar a intenção de que o resultado pode não estar presente.
    */
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
 boolean existsByEnderecos_Id(Long enderecoId);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}
