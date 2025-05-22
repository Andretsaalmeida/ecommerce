package com.brasileiras.ecommerce_api.service;

import com.brasileiras.ecommerce_api.dto.EnderecoRequestDTO;
import com.brasileiras.ecommerce_api.dto.EnderecoResponseDTO;
import com.brasileiras.ecommerce_api.exception.DataConflictException;
import com.brasileiras.ecommerce_api.exception.ResourceNotFoundException;
import com.brasileiras.ecommerce_api.model.Endereco;
import com.brasileiras.ecommerce_api.repository.ClienteRepository;
import com.brasileiras.ecommerce_api.repository.EnderecoRepository;
import com.brasileiras.ecommerce_api.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    public final ClienteRepository clienteRepository;
    public final FornecedorRepository fornecedorRepository;

    public EnderecoService(EnderecoRepository enderecoRepository, ClienteRepository clienteRepository, FornecedorRepository fornecedorRepository) {
        this.enderecoRepository = enderecoRepository;
        this.clienteRepository = clienteRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    /**
     * Busca um endereço existente ou cria um novo se não for encontrado.
     * Garante que endereços com os mesmos dados sejam compartilhados.
     */
    @Transactional
    public Endereco findOrCreateEndereco(EnderecoRequestDTO dto) {
        // Normaliza o complemento: se for string vazia ou só espaços, trata como null.
        String complementoParaBuscaOuCriacao = (dto.getComplemento() == null || dto.getComplemento().trim().isEmpty())
                ? null
                : dto.getComplemento().trim(); // .trim() para remover espaços extras

        Optional<Endereco> enderecoExistente = enderecoRepository.findByCepAndLogradouroAndNumeroAndBairroAndCidadeAndEstadoAndComplemento(
                dto.getCep(),
                dto.getLogradouro(),
                dto.getNumero(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getEstado(),
                complementoParaBuscaOuCriacao
        );

        if (enderecoExistente.isPresent()) {
            return enderecoExistente.get();
        } else {
            Endereco novoEndereco = Endereco.builder()
                    .cep(dto.getCep())
                    .logradouro(dto.getLogradouro())
                    .numero(dto.getNumero())
                    .complemento(complementoParaBuscaOuCriacao)
                    .bairro(dto.getBairro())
                    .cidade(dto.getCidade())
                    .estado(dto.getEstado())
                    .build();
            // A entidade Endereco tem um método setCep que remove a formatação.
            // O builder chama o setter, então o dto.getCep() será normalizado se necessário.
            return enderecoRepository.save(novoEndereco);
        }
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO getEnderecoById(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + id));
        return EnderecoResponseDTO.fromEntity(endereco);
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> getAllEnderecos() {
        return enderecoRepository.findAll().stream()
                .map(EnderecoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEndereco(Long id) {
        //  Verifica se o endereço existe
        if (!enderecoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Endereço não encontrado com ID: " + id);
        }

        // Verifica se está associado a algum cliente
        boolean associadoACliente = clienteRepository.existsByEnderecos_Id(id);
        if (associadoACliente) {
            throw new DataConflictException("Não é possível deletar o endereço ID " + id + " pois está associado a um ou mais clientes.");
        }

        // Verifica se está associado a algum fornecedor
        boolean associadoAFornecedor = fornecedorRepository.existsByEndereco_Id(id);
        if (associadoAFornecedor) {
            throw new DataConflictException("Não é possível deletar o endereço ID " + id + " pois está associado a um fornecedor.");
        }

        enderecoRepository.deleteById(id);
    }

}
