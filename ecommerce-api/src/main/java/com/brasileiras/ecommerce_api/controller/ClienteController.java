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
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        try{
            ClienteResponseDTO clienteCriado = clienteService.criarCliente(clienteRequestDTO);

            // Cria a URI para o recurso recém-criado
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath() // ex: http://localhost:8080
                    .path("/api/clientes/{id}")        // /{id}
                    .buildAndExpand(clienteCriado.id()) // Substitui {id} pelo ID do cliente criado
                    .toUri();

            return ResponseEntity.created(location).body(clienteCriado);
        } catch (Exception exception) {
            Map<String, Object> errorBody = new LinkedHashMap<>();
            HttpStatus status = HttpStatus.CONFLICT; // Default para erros de duplicidade conhecidos
            String errorType = "Conflito de Dados";    // Default

            // Tenta ser um pouco mais específico baseado na mensagem (opcional, mas melhora a resposta)
            if (exception.getMessage() != null) {
                if (exception.getMessage().toLowerCase().contains("cpf já cadastrado")) {
                    errorType = "Não é possível cadastrar o cliente com este CPF";
                } else if (exception.getMessage().toLowerCase().contains("email já cadastrado")) {
                    errorType = "Email Duplicado";
                } else {
                    status = HttpStatus.BAD_REQUEST; // Considerar BAD_REQUEST para outros erros de negócio
                    errorType = "Erro de Validação de Negócio";
                }
            }

            errorBody.put("timestamp", LocalDateTime.now().toString());
            errorBody.put("status", status.value());
            errorBody.put("error", errorType);
            errorBody.put("message", exception.getMessage());
            errorBody.put("path", "/api/clientes/registrar"); // Path da requisição que falhou

            return new ResponseEntity<>(errorBody, status);
        }
    }

    /**
     * Endpoint para buscar um cliente pelo ID.
     * Retorna o ClienteResponseDTO do cliente encontrado ou um erro 404 se não encontrado.
     */

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id) {
        try {
            ClienteResponseDTO clienteResponseDTO = clienteService.buscarPorId(id);
            return ResponseEntity.ok(clienteResponseDTO);
        } catch (RuntimeException exception) {
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", LocalDateTime.now().toString()); // Adiciona um timestamp
            errorBody.put("status", HttpStatus.NOT_FOUND.value());    // Código de status HTTP
            errorBody.put("error", "Not Found");                      // Breve descrição do erro
            errorBody.put("message", exception.getMessage());        // Mensagem da exceção (ex: "Cliente não encontrado... ")
            errorBody.put("path", "/api/clientes/" + id);             // O path da requisição que falhou

            return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarTodosClientes() {
        try{
            List<ClienteResponseDTO> clientes = clienteService.listarTodos();
            if (clientes.isEmpty()) {
                return ResponseEntity.noContent().build(); // Retorna 204 No Content se não houver clientes
            }
            return ResponseEntity.ok(clientes);
        } catch (Exception exception) {
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", LocalDateTime.now().toString());
            errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            errorBody.put("error", "Internal Server Error");
            errorBody.put("message", exception.getMessage());
            errorBody.put("path", "/api/clientes");

            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{clienteId}/enderecos")
    public ResponseEntity<?> adicionarEnderecoAoCliente(@PathVariable Long clienteId, @Valid @RequestBody EnderecoRequestDTO enderecoRequestDTO){
       try{
           ClienteResponseDTO clienteAtualizado = clienteService.adicionarEnderecoAoCliente(clienteId, enderecoRequestDTO);
           return ResponseEntity.status(HttpStatus.CREATED).body(clienteAtualizado);
       } catch (Exception exception) {
           Map<String, Object> errorBody = new LinkedHashMap<>();
           errorBody.put("timestamp", LocalDateTime.now().toString()); // Adiciona um timestamp
           errorBody.put("status", HttpStatus.CONFLICT.value());    // Código de status HTTP
           errorBody.put("error", "Endereço duplicado");                      // Breve descrição do erro
           errorBody.put("message", exception.getMessage());        // Mensagem da exceção
           errorBody.put("path", "api/clientes/id/enderecos");             // O path da requisição que falhou

           return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
       }
    }

    //deletar endereco do clinte
    @DeleteMapping("/{clienteId}/enderecos/{enderecoId}")
    public ResponseEntity<?> deletarEnderecoDoCliente(@PathVariable Long clienteId, @PathVariable Long enderecoId) {
        try {
            boolean enderecoDeletado = clienteService.deletarEnderecoDoCliente(clienteId, enderecoId);
            if (enderecoDeletado) {
                return ResponseEntity.ok("Endereço deletado com sucesso.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Endereço não encontrado.");

        } catch (Exception exception) {
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", LocalDateTime.now().toString());
            errorBody.put("status", HttpStatus.NOT_FOUND.value());
            errorBody.put("error", "Not Found");
            errorBody.put("message", "Endereço não encontrado");
            errorBody.put("path", "/api/clientes/" + clienteId + "/enderecos/" + enderecoId);

            return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
        }
    }


    // PUT /api/clientes/atualizar/{id} para ATUALIZAR um cliente existente (dados principais)
     @PutMapping("atualizar/{id}")
     public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteUpdateRequestDTO clienteUpdateRequestDTO) {
        try{
            ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteUpdateRequestDTO);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException exception) {
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("timestamp", LocalDateTime.now().toString());
            errorBody.put("status", HttpStatus.NOT_FOUND.value());
            errorBody.put("error", "Not Found");
            errorBody.put("message", exception.getMessage());
            errorBody.put("path", "/api/clientes/atualizar/" + id);

            return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
        }

     }

     //DELETE /api/clientes/{id} para DELETAR um cliente
     @DeleteMapping("/{id}")
     public ResponseEntity<String> deletarCliente(@PathVariable Long id) {
        boolean clienteDeletado = clienteService.deletarCliente(id);
        if (clienteDeletado) {
            String mensagem = "Cliente com ID " + id + " deletado com sucesso.";
            return ResponseEntity.ok(mensagem);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente com o ID " + id + " não encontrado.");
        }
     }
}