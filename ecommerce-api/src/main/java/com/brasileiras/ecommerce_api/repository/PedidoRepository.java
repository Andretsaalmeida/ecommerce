package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.Cliente;
import com.brasileiras.ecommerce_api.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    /**
     * Busca todos os pedidos de um cliente específico, com paginação.
     * Útil para clientes com muitos pedidos.
     * @param clienteId O ID do cliente.
     * @param pageable Informações de paginação e ordenação.
     * @return Uma página de pedidos para o cliente especificado.
     */
    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);
    /**
     * Alternativa para buscar pedidos por objeto Cliente, se preferir passar a entidade.
     * @param cliente O objeto Cliente.
     * @return Uma lista de pedidos para o cliente especificado.
     */
    List<Pedido> findByCliente(Cliente cliente);
}