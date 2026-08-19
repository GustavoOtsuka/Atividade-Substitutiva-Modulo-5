# Sistema de Gerenciamento de Encomendas

Projeto desenvolvido para a **Atividade Substitutiva do Módulo 5 da Pós-Graduação em Arquitetura e Desenvolvimento Java da FIAP**.

O sistema gerencia o recebimento e a retirada de encomendas em um condomínio residencial, permitindo a interação entre funcionários da portaria e moradores.

A aplicação utiliza autenticação e autorização, comunicação assíncrona entre serviços, persistência em PostgreSQL, envio de notificações por e-mail, testes automatizados, análise estática e documentação OpenAPI.

O projeto foi desenvolvido e testado em **Debian Linux** utilizando **Java 21**.

---

# Funcionalidades

## Funcionário / Porteiro

O funcionário pode:

- realizar login no sistema;
- cadastrar novos funcionários;
- cadastrar moradores;
- consultar moradores;
- editar dados dos moradores;
- registrar novas encomendas;
- consultar encomendas recebidas;
- registrar a retirada de uma encomenda;
- acompanhar se o morador confirmou ciência da chegada da encomenda.

## Morador

O morador pode:

- realizar cadastro;
- realizar login;
- consultar suas encomendas;
- confirmar ciência da chegada de uma encomenda;
- consultar o status das encomendas;
- atualizar seus próprios dados.

A confirmação de ciência é realizada pelo **morador autenticado no sistema**, evitando confirmação por links públicos ou tokens enviados por e-mail.

---

# Tecnologias utilizadas

## Backend principal

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring MVC
- Thymeleaf
- Maven

## Microsserviço de notificações

- Java 21
- Quarkus
- SmallRye Reactive Messaging
- Kafka
- Quarkus Mailer

## Infraestrutura

- PostgreSQL 17
- Apache Kafka 4.1.2
- Docker
- Docker Compose

## Interface

- HTML
- Thymeleaf
- Bootstrap

## Segurança

- Spring Security
- JWT
- BCrypt
- Controle de acesso baseado em roles

## Qualidade e testes

- JUnit 5
- Mockito
- JaCoCo
- SpotBugs

## Documentação

- OpenAPI 3.1
- Swagger UI

---

# Arquitetura

O projeto é dividido em dois módulos principais:

```text
Atividade-Substitutiva-Modulo-5/
│
├── README.md
│
├── sistemaencomendas/
│   ├── src/
│   ├── docker-compose.yml
│   ├── pom.xml
│   └── ...
│
└── notificacao-encomendas/
    ├── src/
    ├── pom.xml
    └── ...
```

## `sistemaencomendas`

Aplicação principal desenvolvida com Spring Boot.

É responsável por:

- autenticação e autorização;
- geração e validação de JWT;
- cadastro de moradores;
- cadastro de funcionários;
- gerenciamento das encomendas;
- confirmação de ciência pelo morador;
- registro de retirada;
- persistência dos dados;
- geração de eventos;
- implementação do Outbox Pattern;
- publicação dos eventos no Kafka;
- interface web com Thymeleaf.

## `notificacao-encomendas`

Microsserviço desenvolvido com Quarkus.

É responsável por:

- consumir eventos do Apache Kafka;
- processar notificações;
- enviar e-mails aos moradores;
- registrar notificações processadas;
- controlar tentativas de envio;
- realizar novas tentativas em caso de falha.

---

# Fluxo de uma encomenda

O fluxo principal ocorre da seguinte forma:

1. O funcionário realiza login.
2. O funcionário registra uma nova encomenda para um morador.
3. A encomenda é persistida no PostgreSQL.
4. Na mesma operação é criado um evento na Outbox.
5. O evento pendente é posteriormente publicado no Apache Kafka.
6. O microsserviço Quarkus consome o evento.
7. O Quarkus envia um e-mail ao morador informando sobre a encomenda.
8. O morador entra no sistema utilizando seu usuário e senha.
9. Em **Minhas encomendas**, o morador confirma que tomou ciência da chegada.
10. A portaria passa a visualizar que a ciência foi confirmada.
11. Quando o morador retirar a encomenda, o funcionário registra a retirada.

---

# Mensageria e resiliência

A comunicação entre o sistema principal e o serviço de notificações é realizada através do **Apache Kafka**.

O tópico utilizado é:

```text
encomendas.recebidas
```

O projeto utiliza o **Outbox Pattern**.

Ao registrar uma encomenda, a aplicação não depende da disponibilidade imediata do serviço de e-mail. O evento é primeiro persistido na Outbox e posteriormente publicado no Kafka.

Isso reduz o acoplamento entre o cadastro da encomenda e o envio da notificação.

O microsserviço Quarkus também mantém o estado das notificações processadas e possui mecanismo de novas tentativas em caso de falha no envio.

---

# Segurança

O projeto utiliza **Spring Security**.

Existem dois perfis principais:

```text
ROLE_PORTEIRO
ROLE_MORADOR
```

As rotas protegidas são liberadas de acordo com o perfil autenticado.

As senhas são armazenadas utilizando **BCrypt**, evitando armazenamento de senha em texto puro.

---

# Autenticação JWT

Além da autenticação utilizada pela interface web, o projeto disponibiliza autenticação através de JWT.

Endpoint:

```http
POST /api/auth/login
```

O usuário envia suas credenciais e, após autenticação válida, recebe um token JWT contendo as informações necessárias para autorização.

Exemplo conceitual de requisição:

```json
{
  "login": "usuario",
  "senha": "senha"
}
```

O token pode ser utilizado através do cabeçalho:

```http
Authorization: Bearer TOKEN
```

O controle de acesso diferencia usuários com as roles `ROLE_PORTEIRO` e `ROLE_MORADOR`.

---

# Swagger / OpenAPI

A API possui documentação automática utilizando **Springdoc OpenAPI** e **Swagger UI**.

Com a aplicação Spring Boot em execução, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI também pode ser consultada em:

```text
http://localhost:8080/v3/api-docs
```

A documentação apresenta o endpoint REST de autenticação:

```text
POST /api/auth/login
```

e os respectivos modelos de requisição e resposta.

---

# Banco de dados

O projeto utiliza **PostgreSQL 17**.

O banco utilizado é:

```text
sistemaencomendas
```

Usuário padrão do ambiente de desenvolvimento:

```text
postgres
```

Porta exposta pelo Docker:

```text
5433
```

O Spring Boot e o Quarkus utilizam o mesmo banco durante a execução local.

---

# Apache Kafka

O Kafka é executado através do Docker Compose.

Container:

```text
sistemaencomendas-kafka
```

Porta:

```text
9092
```

Tópico utilizado pela aplicação:

```text
encomendas.recebidas
```

O grupo consumidor do microsserviço é:

```text
notificacao-encomendas
```

---

# Como executar o projeto

## Pré-requisitos

É necessário possuir:

- Java 21;
- Docker;
- Docker Compose;
- Git.

O projeto possui Maven Wrapper, portanto não é obrigatório instalar uma versão global do Maven.

---

## 1. Clonar o repositório

```bash
git clone https://github.com/GustavoOtsuka/Atividade-Substitutiva-Modulo-5.git

cd Atividade-Substitutiva-Modulo-5
```

---

## 2. Iniciar PostgreSQL e Kafka

O `docker-compose.yml` está localizado no módulo `sistemaencomendas`.

Execute:

```bash
cd sistemaencomendas
docker compose up -d
```

Verifique os containers:

```bash
docker ps
```

Devem estar disponíveis:

```text
sistemaencomendas-postgres
sistemaencomendas-kafka
```

O PostgreSQL é exposto na porta `5433` e o Kafka na porta `9092`.

---

## 3. Executar a aplicação Spring Boot

Dentro de:

```bash
cd sistemaencomendas
```

execute:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

---

# Executar o microsserviço Quarkus

Abra outro terminal e, a partir da raiz do projeto:

```bash
cd notificacao-encomendas
```

Execute:

```bash
./mvnw quarkus:dev
```

O Quarkus utiliza a porta:

```text
8081
```

e permanece consumindo os eventos enviados para o Kafka.

---

# Envio de e-mails

O microsserviço utiliza o Quarkus Mailer.

A configuração permite utilizar uma conta Gmail através de variáveis de ambiente, evitando armazenar credenciais diretamente no código-fonte.

## Envio real utilizando Gmail

É necessário utilizar uma **Senha de App do Google**.

Antes de iniciar o Quarkus:

```bash
export MAILER_MOCK=false
export GMAIL_USUARIO=seuemail@gmail.com
export GMAIL_SENHA_APP="sua-senha-de-app"
```

Depois:

```bash
./mvnw quarkus:dev
```

> Nunca envie `GMAIL_SENHA_APP` para o Git ou armazene a senha diretamente no repositório.

---

## Modo mock

Caso seja desejável testar o processamento sem enviar um e-mail real:

```bash
export MAILER_MOCK=true
```

Depois:

```bash
./mvnw quarkus:dev
```

Nesse modo o envio é simulado.

---

# Testes automatizados

O módulo Spring Boot possui testes unitários e testes de integração utilizando:

- JUnit 5;
- Mockito;
- Spring Boot Test.

Para executar:

```bash
cd sistemaencomendas
./mvnw clean test
```

Na última execução realizada durante o desenvolvimento:

```text
Tests run: 38
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Foram testados componentes relacionados a:

- moradores;
- funcionários;
- encomendas;
- autenticação;
- JWT;
- carregamento de usuários;
- controllers;
- autorização por perfil;
- integração de segurança.

---

# Cobertura de testes com JaCoCo

O projeto utiliza **JaCoCo** para geração do relatório de cobertura.

O relatório é gerado durante:

```bash
./mvnw clean test
```

O relatório HTML pode ser encontrado em:

```text
sistemaencomendas/target/site/jacoco/index.html
```

Na última medição realizada durante o desenvolvimento foram obtidos:

```text
Cobertura de instruções: 52,76%
Cobertura de branches:   60,71%
Cobertura de linhas:     48,15%
```

Esses valores representam a cobertura global do módulo Spring Boot, incluindo controllers, configurações, infraestrutura e demais classes analisadas pelo JaCoCo.

---

# Análise estática com SpotBugs

O projeto utiliza **SpotBugs** para análise estática do código Java.

Para executar:

```bash
cd sistemaencomendas
./mvnw spotbugs:check
```

A configuração utilizada prioriza problemas de severidade alta.

Na validação realizada durante o desenvolvimento:

```text
BugInstance size is 0
Error size is 0
No errors/warnings found

BUILD SUCCESS
```

---

# Relatório JaCoCo

Após executar os testes:

```bash
./mvnw clean test
```

o relatório pode ser aberto através de:

```text
target/site/jacoco/index.html
```

Em ambiente Linux com interface gráfica, por exemplo:

```bash
xdg-open target/site/jacoco/index.html
```

---

# Parando a infraestrutura

Para encerrar os containers:

```bash
cd sistemaencomendas
docker compose down
```

Para também remover os volumes e apagar os dados locais:

```bash
docker compose down -v
```

> O comando com `-v` remove os dados persistidos do PostgreSQL e do Kafka.

---

# Principais conceitos demonstrados

O projeto demonstra a utilização prática de:

- arquitetura em camadas;
- separação de responsabilidades;
- microsserviços;
- Spring Boot;
- Quarkus;
- autenticação e autorização;
- JWT;
- BCrypt;
- controle de acesso por roles;
- PostgreSQL;
- JPA/Hibernate;
- Apache Kafka;
- comunicação assíncrona;
- Outbox Pattern;
- processamento resiliente de notificações;
- Docker Compose;
- testes unitários;
- testes de integração;
- Mockito;
- JaCoCo;
- análise estática com SpotBugs;
- OpenAPI;
- Swagger.

---

# Autor

**Gustavo Otsuka**

Projeto acadêmico desenvolvido para a Pós-Graduação em Arquitetura e Desenvolvimento Java da FIAP.
