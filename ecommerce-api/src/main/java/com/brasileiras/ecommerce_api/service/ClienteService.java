package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.ClienteRequestDTO;
import com.brasileiras.ecommerce_api.dto.ClienteResponseDTO;
import com.brasileiras.ecommerce_api.dto.ClienteUpdateRequestDTO;
import com.brasileiras.ecommerce_api.dto.EnderecoRequestDTO; // Para o método de adicionar endereço
import com.brasileiras.ecommerce_api.exception.BusinessRuleException;
import com.brasileiras.ecommerce_api.exception.DataConflictException;
import com.brasileiras.ecommerce_api.exception.ResourceNotFoundException;
import com.brasileiras.ecommerce_api.model.Cliente;
import com.brasileiras.ecommerce_api.model.Endereco;
import com.brasileiras.ecommerce_api.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoService enderecoService; // INJETADO

    public ClienteService(ClienteRepository clienteRepository, EnderecoService enderecoService) {
        this.clienteRepository = clienteRepository;
        this.enderecoService = enderecoService;
    }

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO) {
        if (clienteRepository.existsByCpf(clienteRequestDTO.getCpf())) {
            throw new DataConflictException("CPF já cadastrado: " + clienteRequestDTO.getCpf());
        }
        if (clienteRepository.existsByEmail(clienteRequestDTO.getEmail())) {
            throw new DataConflictException("Email já cadastrado: " + clienteRequestDTO.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(clienteRequestDTO.getNome());
        cliente.setCpf(clienteRequestDTO.getCpf());
        cliente.setEmail(clienteRequestDTO.getEmail());
        cliente.setTelefone(clienteRequestDTO.getTelefone());
        cliente.setSenha(clienteRequestDTO.getSenha());

        // Se houver endereços no DTO de criação do cliente
        if (clienteRequestDTO.getEnderecos() != null && !clienteRequestDTO.getEnderecos().isEmpty()) {
            Set<Endereco> enderecosEntidade = new HashSet<>();
            for (EnderecoRequestDTO enderecoDTO : clienteRequestDTO.getEnderecos()) {
                Endereco enderecoEntidade = enderecoService.findOrCreateEndereco(enderecoDTO);
                cliente.addEndereco(enderecoEntidade); // Usa o método utilitário da entidade Cliente
            }
        }

        Cliente clienteSalvo = clienteRepository.save(cliente);
        return ClienteResponseDTO.fromEntity(clienteSalvo);
    }

    @Transactional
    public ClienteResponseDTO adicionarEnderecoAoCliente(Long clienteId, EnderecoRequestDTO enderecoRequestDTO) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        Endereco endereco = enderecoService.findOrCreateEndereco(enderecoRequestDTO);

        // Verifica se o cliente já possui este endereço específico (opcional, Set já lida com duplicatas por objeto)
        if (cliente.getEnderecos().contains(endereco)) {
            throw new DataConflictException("Cliente já possui este endereço associado.");
        }

        cliente.addEndereco(endereco);
        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return ClienteResponseDTO.fromEntity(clienteAtualizado);
    }

    @Transactional
    public void removerEnderecoDoCliente(Long clienteId, Long enderecoId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        Endereco enderecoParaRemover = cliente.getEnderecos().stream()
                .filter(e -> e.getId().equals(enderecoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Endereço ID " + enderecoId + " não encontrado para este cliente."));

        // REGRA DE NEGÓCIO: Não permitir remover o único endereço do cliente
        if (cliente.getEnderecos().size() == 1) {
            throw new BusinessRuleException("Não é possível remover o único endereço do cliente. O cliente deve ter pelo menos um endereço.");
        }

        cliente.removeEndereco(enderecoParaRemover); // Usa o método utilitário
        clienteRepository.save(cliente);

    }


    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteUpdateRequestDTO clienteUpdateRequestDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));

        if (clienteUpdateRequestDTO.getNome() != null) {
            cliente.setNome(clienteUpdateRequestDTO.getNome());
        }
        if (clienteUpdateRequestDTO.getEmail() != null && !cliente.getEmail().equals(clienteUpdateRequestDTO.getEmail())) {
            if (clienteRepository.existsByEmail(clienteUpdateRequestDTO.getEmail())) {
                throw new DataConflictException("Email " + clienteUpdateRequestDTO.getEmail() + " já está em uso.");
            }
            cliente.setEmail(clienteUpdateRequestDTO.getEmail());
        }
        if (clienteUpdateRequestDTO.getTelefone() != null) {
            cliente.setTelefone(clienteUpdateRequestDTO.getTelefone());
        }



        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return ClienteResponseDTO.fromEntity(clienteAtualizado);
    }

    @Transactional
    public void deletarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com ID " + id + " não encontrado para deleção."));
        clienteRepository.delete(cliente);
    }

    // Métodos buscarPorId e listarTodos...
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(ClienteResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}