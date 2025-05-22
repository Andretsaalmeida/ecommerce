package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.EnderecoRequestDTO;
import com.brasileiras.ecommerce_api.dto.FornecedorRequestDTO;
import com.brasileiras.ecommerce_api.dto.FornecedorResponseDTO;
import com.brasileiras.ecommerce_api.dto.FornecedorUpdateRequestDTO;
import com.brasileiras.ecommerce_api.exception.DataConflictException;
import com.brasileiras.ecommerce_api.exception.ResourceNotFoundException;
import com.brasileiras.ecommerce_api.model.Endereco;
import com.brasileiras.ecommerce_api.model.Fornecedor;
import com.brasileiras.ecommerce_api.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    public final EnderecoService enderecoService;

    public FornecedorService(FornecedorRepository fornecedorRepository, EnderecoService enderecoService) {
        this.fornecedorRepository = fornecedorRepository;
        this.enderecoService = enderecoService;
    }

    @Transactional
    public FornecedorResponseDTO criarFornecedor(FornecedorRequestDTO requestDTO) {
        String cnpjLimpo = requestDTO.getCnpj() != null ? requestDTO.getCnpj().replaceAll("[^0-9]", "") : null;
        if (cnpjLimpo == null || !cnpjLimpo.matches("\\d{14}")) {
            throw new DataConflictException("CNPJ inválido ou não fornecido.");
        }

        if (fornecedorRepository.existsByCnpj(cnpjLimpo)) {
            throw new DataConflictException("CNPJ " + cnpjLimpo + " já cadastrado.");
        }
        if (requestDTO.getEmail() != null && fornecedorRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DataConflictException("Email " + requestDTO.getEmail() + " já cadastrado.");
        }

        Fornecedor fornecedor = new Fornecedor();

        fornecedor.setRazaoSocial(requestDTO.getRazaoSocial());
        fornecedor.setCnpj(requestDTO.getCnpj());
        fornecedor.setEmail(requestDTO.getEmail());
        fornecedor.setTelefone(requestDTO.getTelefone());

        if (requestDTO.getEndereco() == null) {
            throw new DataConflictException("Endereço é obrigatório para o fornecedor.");
        }

        // CascadeType.ALL está ativo, o JPA salva o Endereço quando o Fornecedor é salvo.
        Endereco endereco = mapEnderecoRequestDTOToEntity(requestDTO.getEndereco(), new Endereco());
        fornecedor.setEndereco(endereco); // Com CascadeType.ALL, isso é suficiente

        Fornecedor fornecedorSalvo = fornecedorRepository.save(fornecedor);
        return FornecedorResponseDTO.fromEntity(fornecedorSalvo);
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarFornecedorPorId(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com o ID: " + id));
        return FornecedorResponseDTO.fromEntity(fornecedor);
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listarTodosFornecedores() {
        return fornecedorRepository.findAll().stream()
                .map(FornecedorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public FornecedorResponseDTO atualizarFornecedor(Long id, FornecedorUpdateRequestDTO requestDTO) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com o ID: " + id + " para atualização."));

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().equalsIgnoreCase(fornecedor.getEmail())) {
            if (fornecedorRepository.existsByEmail(requestDTO.getEmail())) {
                throw new DataConflictException("Email " + requestDTO.getEmail() + " já está em uso por outro fornecedor.");
            }
            fornecedor.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getTelefone() != null) {
            fornecedor.setTelefone(requestDTO.getTelefone());
        }

        if (requestDTO.getEndereco() == null) {
            throw new DataConflictException("Endereço é obrigatório para a atualização do fornecedor.");
        }

        // Busca o endereço existente associado ao fornecedor para garantir que está sendo atualizado.
        // O ID do endereço do fornecedor já deve existir.
        Endereco enderecoExistente = fornecedor.getEndereco();
        fornecedor.setEndereco(enderecoExistente); // Associa o novo endereço ao fornecedor


        // Mapeia os dados do DTO para a entidade Endereco existente
        mapEnderecoRequestDTOToEntity(requestDTO.getEndereco(), enderecoExistente);


        Fornecedor fornecedorAtualizado = fornecedorRepository.save(fornecedor); // Salva o fornecedor com o endereço atualizado/associado
        return FornecedorResponseDTO.fromEntity(fornecedorAtualizado);
    }

    @Transactional
    public void deletarFornecedor(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor com ID " + id + " não encontrado para deleção."));

        // deletado com sucesso


        // Lógica para tratar produtos associados (IMPORTANTE)
        if (fornecedor.getProdutos() != null && !fornecedor.getProdutos().isEmpty()) {
            throw new DataConflictException("Não é possível deletar o fornecedor ID " + id + " pois ele possui produtos associados. Remova ou desassocie os produtos primeiro.");
        }

        // Remove o endereço associado ao fornecedor, devido ao tipo de Cascade utilizado
        fornecedorRepository.deleteById(id);
    }

    // Método auxiliar para mapear DTO de Endereço para Entidade Endereço
    private Endereco mapEnderecoRequestDTOToEntity(EnderecoRequestDTO dto, Endereco entidade) {
        entidade.setCep(dto.getCep());
        entidade.setLogradouro(dto.getLogradouro());
        entidade.setNumero(dto.getNumero());
        entidade.setComplemento(dto.getComplemento());
        entidade.setBairro(dto.getBairro());
        entidade.setCidade(dto.getCidade());
        entidade.setEstado(dto.getEstado());
        return entidade;
    }
}