package com.brasileiras.ecommerce_api.controller;

import com.brasileiras.ecommerce_api.model.Fornecedor;
import com.brasileiras.ecommerce_api.service.FornecedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {
    /* @Autowired
    * private FornecedorService fornecedorService;
    */
    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public List<Fornecedor> listarFornecedores() {
        return fornecedorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarFornecedorPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Fornecedor> criarFornecedor(@RequestBody Fornecedor fornecedor) {
        // Usar DTOs e @Valid
        Fornecedor novoFornecedor = fornecedorService.salvar(fornecedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoFornecedor);
    }
}
