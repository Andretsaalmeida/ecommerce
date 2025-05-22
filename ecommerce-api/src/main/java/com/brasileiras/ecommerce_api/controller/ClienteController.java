package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.dto.ClienteRequestDTO;
import com.brasileiras.ecommerce_api.dto.ClienteResponseDTO;
import com.brasileiras.ecommerce_api.dto.ClienteUpdateRequestDTO;
import com.brasileiras.ecommerce_api.dto.EnderecoRequestDTO;
import com.brasileiras.ecommerce_api.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Endpoint para registrar um novo cliente.
     * Recebe os dados do cliente via ClienteRequestDTO.
     * Retorna o ClienteResponseDTO do cliente criado e o status HTTP 201 (Created)
     * com a URI do novo recurso no cabeçalho Location.
     */
    @PostMapping("/registrar")
    public ResponseEntity<ClienteResponseDTO> registrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO clienteCriado = clienteService.criarCliente(clienteRequestDTO);

        // Cria a URI para o recurso recém-criado
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath() // ex: http://localhost:8080
                .path("/api/clientes/{id}")        // /{id}
                .buildAndExpand(clienteCriado.id()) // Substitui {id} pelo ID do cliente criado
                .toUri();

        return ResponseEntity.created(location).body(clienteCriado);
    }

    /**
     * Endpoint para buscar um cliente pelo ID.
     * Retorna o ClienteResponseDTO do cliente encontrado ou um erro 404 se não encontrado.
     */

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Long id) {
        ClienteResponseDTO clienteResponseDTO = clienteService.buscarPorId(id);
        return ResponseEntity.ok(clienteResponseDTO);
    }


    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodosClientes() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodos();
        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);
    }


    @PostMapping("/{clienteId}/enderecos")
    public ResponseEntity<ClienteResponseDTO> adicionarEnderecoAoCliente(@PathVariable Long clienteId, @Valid @RequestBody EnderecoRequestDTO enderecoRequestDTO) {

        ClienteResponseDTO clienteAtualizado = clienteService.adicionarEnderecoAoCliente(clienteId, enderecoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteAtualizado);
    }


    @DeleteMapping("/{clienteId}/enderecos/{enderecoId}")
    public ResponseEntity<String> deletarEnderecoDoCliente(@PathVariable Long clienteId, @PathVariable Long enderecoId){
        clienteService.deletarEnderecoDoCliente(clienteId, enderecoId);
        return ResponseEntity.ok("Endereço deletado com sucesso");
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente (@PathVariable Long
    id, @Valid @RequestBody ClienteUpdateRequestDTO clienteUpdateRequestDTO){
        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteUpdateRequestDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCliente (@PathVariable Long id){
        clienteService.deletarCliente(id);
        return ResponseEntity.ok("Cliente deletado com sucesso");
    }

}