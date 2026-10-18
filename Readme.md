## 📄 Documentação da API (Swagger)

A API utiliza o **SpringDoc OpenAPI** para gerar a documentação interativa dos endpoints em tempo de execução. Com ela, você pode visualizar os contratos, schemas de dados e testar as requisições diretamente pelo navegador.

### Como acessar:

Com a aplicação rodando localmente, utilize os links abaixo:

*   **Interface Gráfica (Swagger UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **Especificação OpenAPI (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### 🔒 Autenticação no Swagger:
Como a API é protegida por segurança, siga o fluxo abaixo para testar os endpoints restritos na interface do Swagger:
1. Acesse o endpoint de login/autenticação da API e gere seu token.
2. No topo da página do Swagger UI, clique no botão **Authorize** (ícone de cadeado).
3. Insira o seu token e clique em **Authorize**. Os endpoints protegidos serão liberados para testes.

---

## ⚙️ Configuração do Ambiente

Abaixo mostro como o arquivo `src/main/resources/application.yml` deve ser configurado.
As configurações mais importantes são a especificação das datas sempre estarem em formato UTC e a secret do JWT.
O valor do usuário e senha do admin serão armazenados no banco pelo DataLoader quando executar a API.

```yaml
spring:
  application:
    name: deliveryApi

  datasource:
    url: jdbc:sqlite:delivery.db
    driver-class-name: org.sqlite.JDBC
    hikari:
      maximum-pool-size: 1
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: update
    open-in-view: false
    # Datas sempre em UTC
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
        type:
          preferred_instant_jdbc_type: TIMESTAMP

app:
  jwt:
    # JWT (HS256) (mínimo 32 caracteres)
    secret: ${JWT_SECRET:d03a0d49326d2daea2f8e4cd81a4f2ddb006244896e4176eba8d562f286c43fd}
    expiration-minutes: 60
    cors:
    allowed-origins: http://localhost:5173,http://127.0.0.1:5173
  admin:
    email: admin@exemplo.com
    password: "${ADMIN_PASSWORD:admin123}"

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: alpha
    tags-sorter: alpha
  show-actuator: false

```