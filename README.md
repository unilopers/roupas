# Grupo 3 - Louis Vittao

O Louis Vittao é um sistema de gestão para uma loja de roupas, desenvolvido com Spring Boot, React e PostgreSQL, seguindo o padrão MVC. O sistema visa controlar produtos, clientes, pedidos, itens e parcelas de pagamento.

- Victor de Andrade Miranda
- Felipe Caldeira Akryghti
- Fernando Barros Greca
- Samir Kaled Ali Chehade
- Igor Domingues Pereira

## Autenticação

A API usa JWT. Toda rota fora de `/auth/**` exige o token no header.

### Usuário padrão

O `data.sql` já cria um admin na primeira inicialização:

```
Email: admin@louisvittao.com
Senha: admin123
```

### Login

`POST /auth/login` — Content-Type e Accept: `application/xml`

```xml
<authRequest>
    <email>admin@louisvittao.com</email>
    <password>admin123</password>
</authRequest>
```

Resposta:

```xml
<authResponse>
    <token>eyJhbGciOiJIUzUxMiJ9...</token>
</authResponse>
```

### Usando o token

Adicione o token no header de cada requisição:

```
Authorization: Bearer <token>
```

### Exemplo (PowerShell)

```powershell
$response = Invoke-RestMethod -Method POST -Uri "http://localhost:8080/auth/login" `
  -ContentType "application/xml" `
  -Headers @{ Accept = "application/xml" } `
  -Body "<authRequest><email>admin@louisvittao.com</email><password>admin123</password></authRequest>"

$token = $response.authResponse.token

Invoke-RestMethod -Uri "http://localhost:8080/users" `
  -Headers @{ Authorization = "Bearer $token" }
```

### H2 Console

Acesse `http://localhost:8080/h2-console`:

```
JDBC URL: jdbc:h2:file:./schema
Username: unifil
Password: labinfo123
```
