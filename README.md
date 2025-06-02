# BRASILEIRAS E-commerce API

API RESTful para o sistema de E-commerce da empresa "BRASILEIRAS", desenvolvida como parte de um projeto para modernizar suas operações de venda, migrando do modelo físico para o online.

## Sumário

*   [Visão Geral do Projeto](#visão-geral-do-projeto)
*   [Funcionalidades Principais](#funcionalidades-principais-conforme-pdf)
*   [Tecnologias Utilizadas](#tecnologias-utilizadas)
*   [Pré-requisitos](#pré-requisitos)
*   [Como Executar o Projeto](#como-executar-o-projeto)
    *   [Clonando o Repositório](#clonando-o-repositório)
    *   [Executando a Aplicação](#executando-a-aplicação)
*   [Documentação e Teste da API (Swagger UI)](#documentação-e-teste-da-api-swagger-ui)
*   [Acessando o Console do Banco H2](#acessando-o-console-do-banco-h2)
*   [Estrutura do Projeto](#estrutura-do-projeto)
*   [Considerações Adicionais do PDF](#considerações-adicionais-do-pdf)
*   [Próximos Passos e Melhorias Futuras](#próximos-passos-e-melhorias-futuras)
*   [Contribuição](#contribuição)
*   [Licença](#licença)

## Visão Geral do Projeto

A empresa BRASILEIRAS, tradicionalmente focada em vendas físicas, busca expandir sua atuação para o mercado online. Este projeto consiste no desenvolvimento do backend para seu sistema de E-commerce, permitindo o cadastro de produtos, fornecedores, clientes, o gerenciamento de estoque, e o processamento de pedidos online.

## Funcionalidades Principais

*   **Gerenciamento de Produtos:** Cadastro, estoque, preços.
*   **Gerenciamento de Fornecedores:** Cadastro básico.
*   **Notas Fiscais de Entrada:** Registro e geração de contas a pagar.
*   **Visualização de Produtos:** Acesso público.
*   **Gerenciamento de Clientes:** Cadastro e múltiplos endereços.
*   **Processo de Venda (Pedido):** Criação, seleção de endereço, múltiplas formas de pagamento, atualização de estoque pós-venda, geração de contas a receber.
*   **Entrega:** Acompanhamento e lógica de tentativas.


## Tecnologias Utilizadas

*   **Linguagem:** Java 17+
*   **Framework:** Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Validation)
*   **Banco de Dados:** H2 Database (Em memória)
*   **ORM:** Hibernate
*   **Documentação da API:** Springdoc OpenAPI (Swagger UI)
*   **Build Tool:** Maven
*   **Outros:** Lombok

## Pré-requisitos

*   JDK 17 ou superior.
*   Apache Maven 3.6 ou superior.
*   Git.

## Como Executar o Projeto

### Clonando o Repositório
```bash
git clone https://github.com/<SEU_USUARIO_GITHUB>/<NOME_DO_REPOSITORIO>.git
cd <NOME_DO_REPOSITORIO>

Executando a Aplicação

Via Maven Wrapper:

# Linux/macOS
./mvnw spring-boot:run
# Windows
./mvnw.cmd spring-boot:run


Via IDE: Importe como projeto Maven e execute a classe principal EcommerceApiApplication.java.

A aplicação estará disponível em: http://localhost:8080.

Documentação e Teste da API (Swagger UI)

Toda a documentação detalhada dos endpoints da API, incluindo parâmetros, corpos de requisição/resposta e a capacidade de testar os endpoints diretamente, está disponível através da interface do Swagger UI.

Com a aplicação em execução, acesse:

Swagger UI: [Link](http://localhost:8080/swagger-ui.html)

Consulte esta interface para explorar e interagir com todos os recursos da API (Produtos, Fornecedores, Clientes, Pedidos, etc.).

Acessando o Console do Banco H2

Para inspecionar o banco de dados H2 em memória enquanto a aplicação está rodando:

Certifique-se de que o console H2 está habilitado em application.properties:

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:brasileirasdb 


Acesse no navegador: http://localhost:8080/h2-console
Use as seguintes credenciais:
*   **Driver Class:** org.h2.Driver
*   **JDBC URL:** jdbc:h2:mem:brasileirasdb
*   **User Name:** sa
*   **Password:** password


Estrutura do Projeto

O projeto segue uma estrutura padrão para aplicações Spring Boot, com separação clara de responsabilidades entre controladores, serviços, repositórios, entidades e DTOs.

ecommerce-api/
├── src/main/java/com/brasileiras/ecommerce_api/
│   ├── controller/
│   ├── dto/
│   ├── enums/
│   ├── exception/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── EcommerceApiApplication.java
├── src/main/resources/application.properties
├── pom.xml
└── README.md

Considerações Adicionais:

Contas a Pagar/Receber: Lógicas específicas para esses módulos serão acionadas por eventos como 
lançamento de nota fiscal de compra e confirmação de pagamento.

Segurança: A implementação de autenticação e autorização é um passo futuro essencial.

Entrega: Detalhes de fluxo de entrega podem ser expandidos em módulos dedicados.

Próximos Passos e Melhorias Futuras

Implementação de Segurança (Spring Security + JWT).

Desenvolvimento dos módulos financeiros (Contas a Pagar/Receber).

Integração com Gateways de Pagamento.

Sistema de Notificações.

Testes unitários e de integração abrangentes.

Configuração para ambiente de produção.

Contribuição

Contribuições são bem-vindas!

Fork o projeto.

Crie uma branch para sua feature (git checkout -b feature/MinhaNovaFeature).

Commit suas mudanças (git commit -m 'feat: Adiciona MinhaNovaFeature').

Push para a branch (git push origin feature/MinhaNovaFeature).

Abra um Pull Request.

Licença

Este projeto está licenciado sob a Licença MIT.