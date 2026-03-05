# Pull Request — Endpoint de Login + Correções de Bugs

**Autor:** Felipe Akryghti  
**Data:** 05/03/2026  
**Branch de origem:** fork/felipe → main (repositório principal)

---

## Parte 1 — Correções de bugs identificados na main

Antes de iniciar a implementação da feature de autenticação, foi feita uma análise completa do código-fonte da branch `main` (já com o merge do Igor). Foram identificados e corrigidos os seguintes problemas:

### 1.1 Dependência `jackson-dataformat-xml` ausente

**Arquivo:** `pom.xml`  
**Severidade:** Crítica (projeto não compilava)

Todas as entidades utilizam as anotações `@JacksonXmlRootElement` e `@JacksonXmlProperty` do módulo `jackson-dataformat-xml`, porém essa dependência não estava declarada no `pom.xml`. O projeto falhava na compilação com dezenas de erros `cannot find symbol`.

**Correção:** Adicionada a dependência:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

---

### 1.2 Tipo do ID incompatível em `InstallmentPaymentRepository`

**Arquivo:** `repository/InstallmentPaymentRepository.java`  
**Severidade:** Crítica (erro de compilação implícito / falha em runtime)

O repositório estendia `JpaRepository<InstallmentPayment, Long>`, mas o campo `@Id` da entidade `InstallmentPayment` é do tipo `String` (UUID de 36 caracteres). Isso causava incompatibilidade de tipo em todas as operações do repositório.

**Correção:** Alterado de `Long` para `String`:

```java
// Antes
public interface InstallmentPaymentRepository extends JpaRepository<InstallmentPayment, Long> {}

// Depois
public interface InstallmentPaymentRepository extends JpaRepository<InstallmentPayment, String> {}
```

---

### 1.3 `@PathVariable` com tipo errado no `InstallmentPaymentController`

**Arquivo:** `controller/InstallmentPaymentController.java`  
**Severidade:** Crítica (falha em runtime)

Os métodos `getById`, `update` e `delete` usavam `@PathVariable Long id`, incompatível com o repositório que espera `String`. Além disso, o `update` fazia `id.toString()` desnecessariamente.

**Correção:** Todos os parâmetros alterados de `Long id` para `String id`.

---

### 1.4 Uso de `getReferenceById()` no `UserController`

**Arquivo:** `controller/UserController.java`  
**Severidade:** Alta (erro em runtime)

Os métodos `readById` e `update` usavam `getReferenceById()`, que retorna um **proxy Hibernate**. Ao tentar serializar esse proxy para XML fora de uma transação ativa, ocorre `LazyInitializationException`.

**Correção:** Substituído por `findById()` com tratamento de 404:

```java
// Antes
User entity = userRepository.getReferenceById(id);

// Depois
User entity = userRepository.findById(id).orElse(null);
if (entity == null) {
    return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
}
```

---

### 1.5 Campo `address` ignorado no update de `User`

**Arquivo:** `controller/UserController.java`  
**Severidade:** Média (bug lógico)

O método `update` copiava `name`, `email`, `phone` e `role` do body para a entidade, mas esquecia o campo `address`. Qualquer atualização de usuário zerava o endereço.

**Correção:** Adicionado `entity.setAddress(user.getAddress())`.

---

### 1.6 `FetchType.LAZY` em `OrderItem` causando erro de serialização

**Arquivo:** `domain/OrderItem.java`  
**Severidade:** Alta (erro em runtime)

Os relacionamentos `@ManyToOne` com `Orders` e `Product` usavam `FetchType.LAZY`. Como o `OrderItemController` retorna a entidade diretamente (sem DTO e fora de transação), a serialização XML tentava acessar os proxies e falhava com `LazyInitializationException`.

**Correção:** Alterado para `FetchType.EAGER` em ambos os relacionamentos.

---

## Parte 2 — Implementação do Endpoint de Login (`/auth/login`)

### Contexto

Conforme a issue de autenticação JWT, a parte do Igor (configuração de segurança, filtro JWT e utilitário de token) já foi implementada e mergeada na main. A minha tarefa é **implementar o endpoint de login** que recebe credenciais e retorna um token JWT válido e **Criar DTOs para requisição e resposta de autenticação**.

### O que foi implementado

#### 2.1 Campo `password` na entidade `User`

**Arquivo:** `domain/User.java`

A entidade `User` não possuía campo de senha. Foi adicionado o campo `password` com a anotação `@JsonProperty(access = WRITE_ONLY)` para garantir que a senha **nunca seja exposta** nas respostas XML/JSON da API.

```java
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
```

**Arquivo:** `resources/schema.sql`

Coluna correspondente adicionada à tabela `tb_user`:

```sql
password VARCHAR(255)
```

---

#### 2.2 DTO `AuthRequest`

**Arquivo:** `dto/AuthRequest.java`

DTO para receber os dados de login. Possui dois campos:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `email` | String | Email do usuário (usado como username) |
| `password` | String | Senha em texto plano (será comparada com o hash BCrypt) |

Anotado com `@JacksonXmlRootElement(localName = "authRequest")` para manter consistência com o padrão XML do projeto.

---

#### 2.3 DTO `AuthResponse`

**Arquivo:** `dto/AuthResponse.java`

DTO para retornar o token gerado. Possui um campo:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `token` | String | Token JWT assinado |

Anotado com `@JacksonXmlRootElement(localName = "authResponse")`.

---

#### 2.4 `AuthController`

**Arquivo:** `controller/AuthController.java`

Controller com o endpoint `POST /auth/login`. O fluxo é:

```
1. Recebe AuthRequest (email + password) via XML
2. Valida se email e password não são nulos → 400 Bad Request
3. Busca usuário por email no banco (UserRepository.findByEmail)
   → Se não encontra: 401 Unauthorized
4. Compara a senha enviada com o hash armazenado (PasswordEncoder.matches)
   → Se não confere: 401 Unauthorized
5. Gera token JWT usando JwtUtil.generateToken(email)
6. Retorna 200 OK com AuthResponse contendo o token
```

**Dependências utilizadas (já existentes no projeto):**
- `UserRepository` — busca por email
- `PasswordEncoder` — bean `BCryptPasswordEncoder` do `SecurityConfig` do Igor
- `JwtUtil` — geração de token do Igor

**Decisões de design:**
- Retorna `401 Unauthorized` tanto para email inexistente quanto para senha errada (sem diferenciar, por segurança)
- Consome e produz `application/xml` para manter consistência com todos os outros controllers do projeto
- A rota `/auth/**` já está configurada como pública no `SecurityConfig` do Igor

---

### Como testar

**Requisição:**
```
POST /auth/login
Content-Type: application/xml

<authRequest>
    <email>usuario@email.com</email>
    <password>senha123</password>
</authRequest>
```

**Resposta de sucesso (200):**
```xml
<authResponse>
    <token>eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c3Vhcm...</token>
</authResponse>
```

**Resposta de falha (401):** corpo vazio.

**Uso do token nas demais rotas:**
```
GET /api/product/all
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

> **Nota:** Para testar de fato, é necessário que exista um usuário no banco com senha em BCrypt. Essa parte (seed de dados e service de cadastro) ficará com o Fernando e o Samir nas próximas etapas.

---

### Arquivos modificados neste PR

| Arquivo | Tipo |
|---------|------|
| `pom.xml` | Editado (dependência jackson-dataformat-xml) |
| `domain/User.java` | Editado (campo password) |
| `resources/schema.sql` | Editado (coluna password) |
| `repository/InstallmentPaymentRepository.java` | Editado (tipo do ID) |
| `controller/InstallmentPaymentController.java` | Editado (tipo do PathVariable) |
| `controller/UserController.java` | Editado (getReferenceById → findById, address) |
| `domain/OrderItem.java` | Editado (LAZY → EAGER) |
| `dto/AuthRequest.java` | **Novo** |
| `dto/AuthResponse.java` | **Novo** |
| `controller/AuthController.java` | **Novo** |
