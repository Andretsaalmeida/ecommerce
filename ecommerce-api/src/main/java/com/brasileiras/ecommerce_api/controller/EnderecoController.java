package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.dto.EnderecoRequestDTO;
import com.brasileiras.ecommerce_api.dto.EnderecoResponseDTO;
import com.brasileiras.ecommerce_api.model.Endereco; // Importe a entidade Endereco
import com.brasileiras.ecommerce_api.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "API para gerenciamento de endereços")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @Operation(summary = "Busca ou cria um endereço",
            description = "Verifica se um endereço com os dados fornecidos já existe. Se existir, retorna o endereço existente. Caso contrário, cria um novo endereço e o retorna. Este endpoint é útil para garantir a unicidade dos endereços baseados em seu conteúdo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado e retornado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnderecoResponseDTO.class))),
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnderecoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos")
    })
    @PostMapping
    public ResponseEntity<EnderecoResponseDTO> findOrCreateEndereco(@Valid @RequestBody EnderecoRequestDTO enderecoRequestDTO) {

        Endereco endereco = enderecoService.findOrCreateEndereco(enderecoRequestDTO);
        EnderecoResponseDTO responseDTO = EnderecoResponseDTO.fromEntity(endereco);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // /api/v1/enderecos
                .path("/{id}")       // Adiciona o ID do recurso
                .buildAndExpand(endereco.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }


    @Operation(summary = "Busca um endereço pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnderecoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> getEnderecoById(@PathVariable Long id) {
        EnderecoResponseDTO enderecoDTO = enderecoService.getEnderecoById(id);
        return ResponseEntity.ok(enderecoDTO);
    }

    @Operation(summary = "Lista todos os endereços cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de endereços",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping
    public ResponseEntity<List<EnderecoResponseDTO>> getAllEnderecos() {
        List<EnderecoResponseDTO> enderecos = enderecoService.getAllEnderecos();
        return ResponseEntity.ok(enderecos);
    }

    @Operation(summary = "Deleta um endereço pelo ID",
            description = "Permite deletar um endereço apenas se ele não estiver associado a nenhum cliente ou fornecedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito: Endereço está em uso e não pode ser deletado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEndereco(@PathVariable Long id) {
        enderecoService.deleteEndereco(id);
        return ResponseEntity.ok("Endereço deletado com sucesso");
    }
}

