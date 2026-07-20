# Sistema de Gerenciamento de Encomendas

Projeto desenvolvido para Atividade Substitutiva da Fase 4 da Pós-Graduação em Arquitetura e Desenvolvimento Java da FIAP.

O sistema permite que porteiros registrem encomendas destinadas aos moradores de um condomínio residencial. Após o recebimento da encomenda, uma mensagem é enviada de forma assíncrona para um microsserviço responsável pela notificação do morador. O morador pode confirmar a ciência da chegada da encomenda através de um link recebido por e-mail, enquanto a retirada da encomenda é posteriormente registrada pela portaria.

Projeto desenvolvido e testado no Debian Linux. A execução em Windows é recomendada através do Windows Subsystem for Linux (WSL).


---

# Objetivo

Demonstrar a aplicação dos conceitos estudados durante a fase, utilizando:

- Spring Boot
- Quarkus
- Apache Kafka
- PostgreSQL
- Docker
- Arquitetura Limpa (Clean Architecture)
- Mensageria assíncrona
- Outbox Pattern

---

# Arquitetura

O projeto é dividido em dois serviços independentes.

## Sistema principal (Spring Boot)

Responsável por:

- autenticação dos usuários;
- cadastro de moradores;
- registro de encomendas;
- baixa de encomendas;
- gravação da Outbox;
- publicação de eventos no Kafka.

## Serviço de Notificações (Quarkus)

Responsável por:

- consumir mensagens do Kafka;
- enviar e-mails aos moradores;
- registrar notificações processadas;
- realizar tentativas automáticas em caso de falha.

---

# Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- Quarkus
- Apache Kafka
- PostgreSQL
- Docker
- Maven
- Bootstrap
- JUnit 5
- Mockito


# Estrutura do projeto

Atividade-Substitutiva-Modulo-4-A
│
├── README.md
│
├── sistemaencomendas/
│   ├── src/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── pom.xml
│   └── ...
│
└── notificacao-encomendas/
    ├── src/
    ├── pom.xml
    └── ...


# Fluxo da aplicação

O funcionamento do sistema ocorre da seguinte maneira:

. O porteiro realiza o login.
. Os moradores são cadastrados.
. Uma encomenda é registrada para um morador.
. A encomenda é gravada no banco de dados.
. Um evento é registrado na tabela Outbox.
. O evento é publicado no Apache Kafka.
. O microsserviço Quarkus consome esse evento.
. O microsserviço envia um e-mail ao morador.
. O morador confirma a ciência através do link recebido.
. Quando a encomenda é retirada, o porteiro registra a entrega.




# Como executar o projeto

O projeto é dividido em dois módulos independentes:

- **sistemaencomendas/** → aplicação principal desenvolvida em Spring Boot;
- **notificacao-encomendas/** → microsserviço de notificações desenvolvido em Quarkus.

Antes de iniciar as aplicações, é necessário iniciar a infraestrutura do projeto.

---

## 1. Clonar o repositório

```bash
git clone https://github.com/GustavoOtsuka/Atividade-Substitutiva-Modulo-4-A.git

cd Atividade-Substitutiva-Modulo-4-A
```

Todos os comandos abaixo devem ser executados a partir desta pasta, salvo quando indicado o contrário.

---

## 2. Iniciar a infraestrutura

O arquivo `docker-compose.yml` está localizado na pasta **sistemaencomendas**.

Partindo da pasta raiz do projeto, execute:

```bash
cd sistemaencomendas

docker compose up -d
```

Esse comando iniciará os containers necessários para a execução da aplicação, incluindo o banco de dados PostgreSQL.

Para verificar se os containers foram iniciados corretamente:

```bash
docker ps
```

Após iniciar os containers, mantenha este terminal aberto ou abra um novo terminal para executar a aplicação Spring Boot.

---

## 3. Executar a aplicação Spring Boot

Abra um terminal.

Entre na pasta:

```bash
cd sistemaencomendas
```

Execute:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em:

```
http://localhost:8080
```


### Acesso do morador

Ao cadastrar um novo morador, o sistema define automaticamente a senha inicial como:

123456

Essa senha é armazenada de forma criptografada utilizando o Spring Security (BCrypt).

O login utilizado é o mesmo informado durante o cadastro do morador.



### Acesso do porteiro

login: porteiro
senha: 123456

Essa senha é armazenada de forma criptografada utilizando o Spring Security (BCrypt).



---

## 4. Executar o microsserviço Quarkus

Abra um **novo terminal**.

Volte para a raiz do projeto:

```bash
cd Atividade-Substitutiva-Modulo-4-A
```

Entre na pasta do microsserviço:

```bash
cd notificacao-encomendas
```

Execute:

```bash
./mvnw quarkus:dev
```

O microsserviço ficará aguardando mensagens enviadas pelo Apache Kafka.

---

## 5. Apache Kafka

O sistema utiliza o Apache Kafka para realizar a comunicação assíncrona entre a aplicação principal e o microsserviço de notificações.

Sempre que uma nova encomenda é cadastrada:

1. o Spring Boot grava um evento na Outbox;
2. o evento é publicado no Kafka;
3. o Quarkus consome esse evento;
4. o e-mail é enviado ao morador.

---

# Envio de e-mails

O microsserviço pode funcionar de duas maneiras.

## Modo de desenvolvimento (Mock)

Por padrão, o projeto utiliza:

```
MAILER_MOCK=true
```

Nesse modo nenhum e-mail real é enviado. O conteúdo é apenas registrado nos logs da aplicação, permitindo testar todo o fluxo sem a necessidade de configurar uma conta de e-mail.

---

## Enviando e-mails reais

Caso deseje testar o envio real de e-mails, é possível utilizar uma conta Gmail.

### Passo 1

Ative a verificação em duas etapas na conta Google.

### Passo 2

No painel da conta Google, acesse:

**Segurança → Senhas de app**

Crie uma nova senha de aplicativo para o projeto.

O Google fornecerá uma senha semelhante a:

```
abcd efgh ijkl mnop
```

Essa senha é diferente da senha utilizada para acessar sua conta.

### Passo 3

Antes de iniciar o microsserviço Quarkus, exporte as variáveis de ambiente:

```bash
export MAILER_MOCK=false

export GMAIL_USUARIO=seuemail@gmail.com

export GMAIL_SENHA_APP="sua senha de app"
```

Exemplo:

```bash
export MAILER_MOCK=false

export GMAIL_USUARIO=exemplo@gmail.com

export GMAIL_SENHA_APP="abcdefghijklmnop"
```

Depois execute normalmente:

```bash
./mvnw quarkus:dev
```

A partir desse momento, todas as notificações serão enviadas para os moradores utilizando a conta Gmail configurada.

> **Importante:** nunca compartilhe sua Senha de App nem a envie para repositórios públicos.





