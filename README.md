# VINCULE - Plataforma de Gestão de Impacto Social

O **Vincule** é uma plataforma Fullstack (Java/Spring Boot + React/Vite) desenhada para conectar organizações não-governamentais (ONGs) a voluntários e gerenciar recursos de forma eficiente e transparente.

Este projeto aplica conceitos rigorosos de **Clean Architecture**, APIs RESTful, Autenticação JWT e boas práticas de Interface de Usuário (Mobile First).

---

## 🚀 Como Executar o Projeto (Guia para Avaliadores)

Siga este passo a passo para testar a aplicação funcional de ponta a ponta na sua máquina local.

### 1. Subir o Banco de Dados (PostgreSQL)
Certifique-se de ter o Docker instalado e rodando. Na raiz do projeto, execute:
```bash
docker-compose up -d
```
> Isso iniciará o PostgreSQL na porta `5432`. As tabelas e migrações (Flyway) serão criadas automaticamente quando a API Spring Boot iniciar.

### 2. Gerar as Chaves RSA (JWT)
As chaves de segurança não são versionadas no repositório. Gere-as com os comandos abaixo:
```bash
mkdir -p src/main/resources/certs
openssl genrsa -out src/main/resources/certs/private.pem 2048
openssl rsa -in src/main/resources/certs/private.pem -pubout -out src/main/resources/certs/public.pem
```

### 3. Iniciar a API Backend (Spring Boot)
O backend necessita da versão **Java 21**. Na raiz do projeto, execute:
```bash
./mvnw spring-boot:run
```
> O servidor iniciará na porta `http://localhost:8080`. Ele irá expor os endpoints documentados e o Cron Job interno.

### 4. Iniciar o Frontend (React + Vite)
Abra um novo terminal (mantendo o backend rodando), navegue para a pasta `frontend/` e inicie o servidor:
```bash
cd frontend
npm install
npm run dev
```
> A interface subirá na porta `http://localhost:5173`. Clique no link exibido no terminal para abrir no navegador.

---

## 🧪 Roteiro de Testes Funcionais da Interface

Para avaliar o sistema operando em 100% da sua capacidade através do fluxo do Frontend, siga estes passos:

### Passo 1: O Mural Público
1. Ao acessar `http://localhost:5173/mural`, você verá a interface pública.
2. Esta página carrega anonimamente os eventos futuros e os itens que estão marcados como **Urgentes** (abaixo do estoque mínimo no banco de dados).

### Passo 2: Acesso Restrito (Login)
O sistema exige autenticação. Como a plataforma está limpa, você pode usar a conta de testes que foi configurada pela primeira migração/inserção ou cadastrar via cURL (se for o caso), mas assumindo o ambiente de teste, criamos uma rota padrão.
* Para facilitar o teste, o usuário administrador default criado nas rotas da API durante a nossa validação é:
  * **E-mail:** `maria.silva@example.com`
  * **Senha:** `password123`
* Acesse `http://localhost:5173/login`, insira as credenciais e clique em Entrar.

### Passo 3: Operando a Dashboard
Uma vez logado, você será redirecionado para a **Dashboard Administrativa** (`/dashboard`). Teste as funcionalidades completas:
1. **Adicionar ao Inventário:** Clique em `+ Adicionar` no cartão de Inventário. Preencha o modal com um nome (ex: *Leite*), Quantidade (ex: *5*) e Estoque Mínimo (ex: *10*). Ao salvar, você verá que o item aparece na tabela, o card de KPI "Itens em Estoque" atualiza automaticamente, e, como a quantidade é menor que o mínimo, na próxima hora o sistema automatizado de alertas marcará este item como Crítico (ele aparecerá no Mural Público!).
2. **Criar Evento:** Clique em `+ Criar Evento` no cartão de Eventos. Insira um nome, uma descrição e data para o futuro. Salve e veja as métricas do card de KPIs aumentarem na mesma hora, graças à reatividade do React Query!

### Passo 4: Perfil e Recuperação de Senha
1. **Perfil do Usuário:** Acesse a página de Perfil para visualizar o impacto do usuário, suas horas de voluntariado e gerenciar suas informações e foto de perfil.
2. **Recuperação de Senha:** A plataforma possui o fluxo completo de "Esqueci minha senha" integrado com tokens seguros armazenados no banco de dados e interface intuitiva para o usuário criar uma nova senha.

---

## 🛠️ Stack Tecnológica

### Backend
* **Java 21 + Spring Boot 3**
* **Spring Security + JWT (RS256)**: Proteção de rotas em alta segurança.
* **Spring Data JPA + Hibernate**: Camada de persistência (ORM).
* **Flyway**: Versionamento automático do esquema de banco de dados (`V1` a `V4`, incluindo sementes e tabelas de reset de senha).
* **PostgreSQL**: SGBD Relacional rodando via Docker.

### Frontend
* **React 18 + Vite + TypeScript**: SPA super rápida e tipada.
* **Tailwind CSS v4**: Design System baseado em utilitários focados em Mobile-First.
* **Zustand**: Gerenciamento leve do Estado Global (guardando a sessão do JWT em memória persistida).
* **React Query (@tanstack)**: Cache assíncrono e sincronização imbatível dos modais com o banco de dados.
* **React Router v6**: Rotas públicas e Rota Privada inteligente (`ProtectedRoute`).
* **Axios**: Injeção e interceptação automática do Bearer Token em todas as requests seguras.
