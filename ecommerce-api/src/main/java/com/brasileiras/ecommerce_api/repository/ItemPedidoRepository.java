package com.brasileiras.ecommerce_api.repository;

import com.brasileiras.ecommerce_api.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import com.brasileiras.ecommerce_api.model.Pedido;
import com.brasileiras.ecommerce_api.model.Produto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    /**
     * Encontra todos os itens de pedido associados a um pedido específico.
     * @param pedido O objeto Pedido.
     * @return Uma lista de ItemPedido.
     */
    List<ItemPedido> findByPedido(Pedido pedido);

    /**
     * Encontra todos os itens de pedido associados a um ID de pedido específico.
     * @param pedidoId O ID do Pedido.
     * @return Uma lista de ItemPedido.
     */
    List<ItemPedido> findByPedidoId(Long pedidoId);

    /**
     * Encontra todos os itens de pedido associados a um produto específico.
     * Útil para análises, como "quantas vezes este produto foi vendido?".
     * @param produto O objeto Produto.
     * @return Uma lista de ItemPedido.
     */
    List<ItemPedido> findByProduto(Produto produto);

    /**
     * Encontra todos os itens de pedido associados a um ID de produto específico.
     * @param produtoId O ID do Produto.
     * @return Uma lista de ItemPedido.
     */
    List<ItemPedido> findByProdutoId(Long produtoId);

    /**
     * Encontra um item de pedido específico dentro de um pedido, baseado no produto.
     * Útil para verificar se um produto já está no carrinho/pedido antes de adicionar um novo
     * ou para atualizar a quantidade de um item existente.
     * @param pedido O objeto Pedido.
     * @param produto O objeto Produto.
     * @return Um Optional contendo o ItemPedido se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<ItemPedido> findByPedidoAndProduto(Pedido pedido, Produto produto);

    /**
     * Encontra um item de pedido específico dentro de um pedido (por ID), baseado no ID do produto.
     * @param pedidoId O ID do Pedido.
     * @param produtoId O ID do Produto.
     * @return Um Optional contendo o ItemPedido se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<ItemPedido> findByPedidoIdAndProdutoId(Long pedidoId, Long produtoId);

    /**
     * Calcula a quantidade total de um produto específico vendida em todos os pedidos.
     * @param produtoId O ID do Produto.
     * @return A soma das quantidades, ou null se nenhum item for encontrado para o produto.
     */
    @Query("SELECT SUM(ip.quantidade) FROM ItemPedido ip WHERE ip.produto.id = :produtoId")
    Integer sumQuantidadeByProdutoId(@Param("produtoId") Long produtoId);
}