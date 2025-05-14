Controle de Vendas
Descrição
Este é um projeto de API RESTful desenvolvido para gerenciar vendas, utilizando Spring Boot com Kotlin. A aplicação permite criar, consultar e analisar estatísticas de vendas, integrando-se a um banco de dados MongoDB para armazenar os dados.
Tecnologias Utilizadas

Linguagem: Kotlin
Framework: Spring Boot
Banco de Dados: MongoDB
Testes: JUnit, MockK, Spring Test
Build: Gradle
Dependências: Spring Data MongoDB, Jackson, Validation

Pré-requisitos

Java 17 ou superior
MongoDB instalado e rodando localmente
Gradle 8.x ou superior
IDE (recomendado: IntelliJ IDEA)

Instalação

Clone o repositório:git clone https://github.com/seu-usuario/controle-vendas.git
cd controle-vendas


Configure as variáveis de ambiente no arquivo application.properties ou application.yml:
URL do MongoDB (ex.: spring.data.mongodb.uri=mongodb://localhost:27017/vendas)


Instale as dependências:./gradlew build


Execute a aplicação:./gradlew bootRun



Endpoints Principais

GET /v1/sales: Retorna todas as vendas registradas.
GET /v1/sales/{id}: Retorna uma venda específica pelo ID.
POST /v1/sales: Cria uma nova venda (requer corpo JSON com dados da venda).
GET /v1/statistics: Retorna estatísticas de vendas (ex.: média de peso, lucro).

Estrutura do Projeto

com.example.controle_vendas.controller: Contém os controllers REST (ex.: GetSales, PostCreateSales).
com.example.controle_vendas.dto.response: Define os DTOs de resposta (ex.: SalesResponse, ProductResponse).
com.example.controle_vendas.service: Implementa a lógica de negócio (ex.: GetStatisticsService).
com.example.controle_vendas.model: Inclui entidades e enums (ex.: ProductType).
test: Contém os testes unitários e de integração.

Executando Testes

Execute os testes com:./gradlew test jacocoTestReport


Veja os relatórios em build/reports/tests/test/index.html e build/reports/jacoco/test/html/index.html.

Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
Contato
Para dúvidas ou sugestões, entre em contato pelo email: lucas@exemplo.com.
