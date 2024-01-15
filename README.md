# Short URL

Serviço de encurtamento de URL em Java 17 com Spring Boot

Não é necessario autentificação para utilizar o redirecionamento de URL, basta acessar o endereço com o código gerado.

Mas é necessário autenticação para criar URLs.

### Features

-  Encurtamento de URL
-  CRUD de Encurtamento de URL
-  CRUD de Key de Autentificação
-  Segurança com Spring Security

## Autentificação (Chave de Acesso para APIs)

Para utilizar as APIs que requerem autentificação é necessário passar o header `API-Key` com o valor da chave de autentificação.

Existe uma chave de autentificação padrão criada ao iniciar a aplicação (somente se não existir nenhuma já cadastrada), mas é recomendado criar uma nova chave para utilizar.

##  Rest APIs

### Redirecionamento de URL

| Método | URL    | Descrição                                    |
|--------|--------|----------------------------------------------|
| GET    | {code} | Redirecionar o usuario para a url cadastrada |

### Encurtamento de URL (requer chave de acesso)

| Método | URL                          | Descrição                                  |
|--------|------------------------------|--------------------------------------------|
| GET    | /api/v1/shorturl             | Recupera todos os Encurtamento de URL      | 
| GET    | /api/v1/shorturl/{id}        | Recupera o Encurtamento de URL             | 
| GET    | /api/v1/shorturl/code/{code} | Recupera o Encurtamento de URL pelo código | 
| POST   | /api/v1/shorturl/            | Adiciona novo redirecionamento de URL      | 
| PUT    | /api/v1/shorturl/{id}        | Atualiza o Encurtamento de URL             | 
| DELETE | /api/v1/shorturl/{id}        | Deleta o Encurtamento de URL               |

### Token de Autenficação (requer chave de acesso)

| Método | URL               | Descrição                                   | 
|--------|-------------------|---------------------------------------------|
| GET    | /api/v1/auth      | Recupera todas as chaves de autentificação  |
| GET    | /api/v1/auth/{id} | Recupera a chave de autentificação          |
| POST   | /api/v1/auth/     | Adiciona uma nova chave para autentificação |     
| PUT    | /api/v1/auth/{id} | Atualiza a chave de autentificação          | 
| DELETE | /api/v1/auth/{id} | Deleta a chave de autentificação            |    

## JSON Request Bodys

##### Encurtamento de URL

```json
{
  "redirectUrl": "https://www.google.com.br",
  "code": "google",
  "note": "Redirecionamento para o Google"
}
```

Campos do JSON:

- `redirectUrl` (obrigatório): URL para onde o usuário será redirecionado.
- `code` (opcional): código que será utilizado para acessar a URL encurtada. Caso não seja informado, será gerado um código aleatório.
- `note` (opcional): descrição ou nota sobre o redirecionamento.

##### Chave de Autentificação

```json
{
  "key": "123456789",
  "name": "Admin Key"
}
```

Campos do JSON:

- `key` (obrigatório): chave de autentificação.
- `name` (obrigatório): nome da chave de autentificação.
