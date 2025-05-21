package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.*;
import com.brasileiras.ecommerce_api.model.Cliente;
import com.brasileiras.ecommerce_api.model.Endereco;
import com.brasileiras.ecommerce_api.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    //criar a classe ClienteService com base na classe ClienteController
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO) throws Exception {
        if (clienteRepository.existsByCpf(clienteRequestDTO.getCpf())) {
            throw new Exception("CPF já cadastrado.");
        }
        if (clienteRepository.existsByEmail(clienteRequestDTO.getEmail())) {
            throw new Exception("Email já cadastrado.");
        }

        // Converte o DTO para a entidade Cliente
        Cliente cliente = ClienteRequestDTO.toEntity(clienteRequestDTO);
        if (cliente.getEnderecos() != null) {
            for (Endereco endereco : cliente.getEnderecos()) {
                endereco.setCliente(cliente); // Garante a associação bidirecional (FK)
            }
        } else {
            cliente.setEnderecos(new ArrayList<>()); // Garante que não seja nulo
        }

        // Salva o cliente no banco de dados (JPA cascade deve salvar os endereços)
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Converte a entidade Cliente salva de volta para DTO de resposta
        return toClienteResponseDTO(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::toClienteResponseDTO)// Mapeia Cliente para ClienteResponseDTO
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }


    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::toClienteResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO adicionarEnderecoAoCliente(Long clienteId, EnderecoRequestDTO enderecoRequestDTO) throws Exception {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new Exception("Cliente não encontrado com ID: " + clienteId));
        // enderecoDTO.getId() DEVE ser null aqui para adicionar um novo endereço
        if (enderecoRequestDTO.getId() != null) {
            throw new Exception("Para adicionar um novo endereço, o ID do endereço deve ser nulo.");
        }
        // Converte o DTO de endereço para a entidade Endereco
        Endereco novoEndereco = EnderecoRequestDTO.toEntity(enderecoRequestDTO);

        // Verifica se o endereço já existe para o cliente
        if (cliente.getEnderecos() != null) {
            for (Endereco enderecoExistente : cliente.getEnderecos()) {
                if (saoEnderecosIguais(enderecoExistente, novoEndereco)) {
                    throw new Exception("Este endereço já está cadastrado para o cliente de ID: " + clienteId);
                }
            }
        } else {
            cliente.setEnderecos(new ArrayList<>()); // Inicializa a lista se estiver nula
        }

        // Adiciona o novo endereço à lista de endereços do cliente
        novoEndereco.setCliente(cliente);// Garante a associação bidirecional (FK)
        cliente.getEnderecos().add(novoEndereco);
        Cliente clienteAtualizado = clienteRepository.save(cliente); // Cascade irá salvar novoEndereco
        // Converte a entidade Cliente atualizada de volta para DTO de resposta
        return ClienteResponseDTO.toClienteResponseDTO(clienteAtualizado);

    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateRequestDTO clienteUpdateRequestDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        // Atualiza os campos primitivos do cliente
        if (clienteUpdateRequestDTO.getNome() != null) { // Permitir atualização parcial
            cliente.setNome(clienteUpdateRequestDTO.getNome());
        }
        if (clienteUpdateRequestDTO.getEmail() != null) {
            cliente.setEmail(clienteUpdateRequestDTO.getEmail());
        }
        if (clienteUpdateRequestDTO.getTelefone() != null) {
            cliente.setTelefone(clienteUpdateRequestDTO.getTelefone());
        }

        // Substituir todos os endereços:
        // Limpar os endereços existentes associados a este cliente.
        // Se orphanRemoval=true na relação Cliente->Enderecos, isso é suficiente para o JPA remover do DB.
        if (cliente.getEnderecos() != null) {
            cliente.getEnderecos().clear();
        } else {
            cliente.setEnderecos(new ArrayList<>()); // Inicializa se for nulo
        }

        // Adicionar os novos endereços do DTO
        if (clienteUpdateRequestDTO.getEnderecos() != null && !clienteUpdateRequestDTO.getEnderecos().isEmpty()) {
            for (EnderecoRequestDTO enderecoDTO : clienteUpdateRequestDTO.getEnderecos()) {
                Endereco novoEndereco = EnderecoRequestDTO.toEntity(enderecoDTO);
                novoEndereco.setCliente(cliente); // associação bidirecional
                cliente.getEnderecos().add(novoEndereco);
            }
        }

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        // Converte a entidade atualizada de volta para DTO de resposta
        return toClienteResponseDTO(clienteAtualizado); // Garanta que este método mapeie os endereços também
    }

    @Transactional
    public boolean deletarCliente(Long id) {
        if(clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Método auxiliar para converter Cliente para ClienteResponseDTO
    private ClienteResponseDTO toClienteResponseDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        List<EnderecoResponseDTO> enderecosResponse;
        if(cliente.getEnderecos() != null){
            enderecosResponse = cliente.getEnderecos()
                    .stream()
                    .map(EnderecoResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        } else {
            enderecosResponse = Collections.emptyList();
        }

        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .enderecos(enderecosResponse)
                .build();
    }

    /**
     * Método auxiliar para verificar se dois endereços são considerados iguais
     * para fins de duplicidade.
     */
    private boolean saoEnderecosIguais(Endereco end1, Endereco end2) {
        if (end1 == null || end2 == null) {
            return false;
        }
        // Compara campos relevantes, usando Objects.equals para tratar nulos corretamente.
        return Objects.equals(end1.getCep(), end2.getCep()) &&
                Objects.equals(end1.getLogradouro(), end2.getLogradouro()) &&
                Objects.equals(end1.getNumero(), end2.getNumero()) &&
                Objects.equals(end1.getBairro(), end2.getBairro()) &&
                Objects.equals(end1.getCidade(), end2.getCidade()) &&
                Objects.equals(end1.getEstado(), end2.getEstado()) &&
                Objects.equals(end1.getComplemento(), end2.getComplemento()); // Complemento é opcional
    }

}
