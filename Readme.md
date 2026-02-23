# ♟️ Chess AI Duel — GPT vs Claude

Projeto de xadrez onde dois agentes de IA jogam entre si: **GPT-4o-mini** (brancas) contra **Claude Haiku** (pretas).

O tabuleiro e a lógica do jogo rodam em **Java**, enquanto um **agente Python** consome as APIs da OpenAI e Anthropic para decidir os movimentos.

```
chess-ai-battle/
├── chess-app/          # Aplicação Java (lógica do jogo)
└── chess-ai-agent/     # Agente Python (FastAPI + LangChain)
```

---

## 🏗️ Arquitetura

```
chess-app (Java)
    │
    │  POST /move  (board, valid_moves, history...)
    ▼
chess-ai-agent (Python/FastAPI)
    │
    ├──▶ OpenAI API   (GPT-4o-mini)   → brancas
    └──▶ Anthropic API (Claude Haiku) → pretas
```

O Java envia o estado atual do tabuleiro e a lista de movimentos válidos. O agente Python consulta o LLM correspondente e retorna o movimento escolhido.

---

## 🐍 chess-ai-agent (Python)

### Requisitos

- Java 11
- Python 3.10+
- Chaves de API da OpenAI e Anthropic

### Setup

```bash
cd chess-ai-agent

# Criar e ativar ambiente virtual
python -m venv venv
source venv/bin/activate        # Linux/Mac
venv\Scripts\activate           # Windows

# Instalar dependências
pip3 install -r requirements.txt

# Configurar variáveis de ambiente
cp .env.example .env
# Edite o .env e adicione suas chaves
```

### .env

```env
OPENAI_API_KEY=sk-...
ANTHROPIC_API_KEY=sk-ant-...
```

### Rodar o agente

```bash
python3 main.py
```

O agente ficará disponível em `http://localhost:8000`.

**Endpoints:**

- `POST /move` — recebe o estado do tabuleiro e retorna o movimento
- `GET /health` — verifica se o agente está no ar

---

## ☕ chess-app (Java)

Baseado no projeto de xadrez em console do curso do Nélio Alves, com adição do cliente HTTP que se comunica com o agente Python.

### Requisitos

- Java 11+
- Maven

### Setup e execução

```bash
cd chess-app

# Compilar
mvn clean install

# Rodar
mvn exec:java -Dexec.mainClass="br.com.chessapp.Main"
```

---

## 🧠 Como o agente decide o movimento

1. O Java monta o estado atual: tabuleiro em texto, lista de movimentos válidos, histórico da partida e se o rei está em xeque.
2. Envia via `POST /move` para o agente Python.
3. O agente monta um prompt com essas informações e consulta o LLM (GPT ou Claude dependendo da cor).
4. O LLM responde com o movimento no formato `origem destino` (ex: `e2 e4`).
5. O Java executa o movimento no tabuleiro.

Se o LLM retornar um movimento inválido, o Java tenta novamente até 5 vezes antes de encerrar o turno.

---

## 📦 Dependências principais

**Python (`requirements.txt`):**

- `fastapi`
- `uvicorn`
- `langchain-openai`
- `langchain-anthropic`
- `python-dotenv`
- `pydantic`

**Java (`pom.xml`):**

- `java.net.http` (cliente HTTP nativo do Java 11+)
- `org.json` (parse do JSON de resposta)

---

## 📌 Observações

- O projeto **não suporta interação humana** — é puramente IA vs IA.
- A promoção de peão ainda não está implementada.
- Em caso de falha na API, o Java tenta até 5 vezes antes de abandonar o turno.

Projeto de xadrez onde dois agentes de IA jogam entre si: **GPT-4o-mini** (brancas) contra **Claude Haiku** (pretas).

O tabuleiro e a lógica do jogo rodam em **Java**, enquanto um **agente Python** consome as APIs da OpenAI e Anthropic para decidir os movimentos.
