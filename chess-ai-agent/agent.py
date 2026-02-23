from fastapi import FastAPI, HTTPException
from langchain_openai import ChatOpenAI
from langchain_anthropic import ChatAnthropic
from langchain_core.messages import HumanMessage, SystemMessage
from pydantic import BaseModel
import re
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Chess AI Agent")

gpt = ChatOpenAI(model="gpt-4o-mini", temperature=0.1, max_tokens=300)
claude = ChatAnthropic(model="claude-haiku-4-5", temperature=0.1, max_tokens=300)

class BoardRequest(BaseModel):
    board: str
    color: str
    model: str
    history: list = []
    valid_moves: list = []
    in_check: bool = False

SYSTEM_PROMPT = """Você é um Grande Mestre de xadrez competitivo. Seu único objetivo é VENCER a partida.

Representação do tabuleiro:
- K=Rei branco, Q=Rainha branca, R=Torre branca, B=Bispo branco, N=Cavalo branco, P=Peão branco
- k=Rei preto,  q=Rainha preta,  r=Torre preta,  b=Bispo preto,  n=Cavalo preto,  p=Peão preto
- . = casa vazia

Layout: topo=linha 8, base=linha 1, esquerda=coluna a, direita=coluna h.

PRINCÍPIOS ESTRATÉGICOS (prioridade decrescente):
0. Se você está em xeque, OBRIGATORIAMENTE escolha um movimento válido e que tire o rei do xeque
1. Se há xeque-mate disponível, faça-o imediatamente
2. Se há captura de peça valiosa sem perda equivalente, faça-a (Q=9, R=5, B=3, N=3, P=1)
3. Se o adversário ameaça sua peça valiosa, defenda ou mova-a
4. Controle o centro (casas e4, e5, d4, d5)
5. Desenvolva peças menores (bispos e cavalos) antes de mover a rainha
6. Mantenha o rei seguro (roque cedo quando possível)
7. Conecte as torres e crie ameaças coordenadas
8. Previna possíveis cheques e ataques diretos ao rei (antecipe ameaças antes que ocorram)

REGRA ABSOLUTA: Você receberá uma lista de movimentos válidos.
Escolha EXATAMENTE um da lista. NUNCA invente um movimento fora dela.

Responda EXATAMENTE assim (duas linhas, nada mais):
RACIOCINIO: 3-5 palavras
MOVIMENTO: origem destino

Exemplo:
RACIOCINIO: captura peão central
MOVIMENTO: c5 d4"""

def build_prompt(board: str, color: str, history: list = [], valid_moves: list = [], in_check: bool = False) -> str:
    recent_history = history[-10:] if len(history) > 10 else history
    history_text = "\n".join(recent_history) if recent_history else "Nenhum movimento ainda"
    moves_text = ", ".join(valid_moves) if valid_moves else "Nenhum"
    check_alert = "\n⚠️ SEU REI ESTÁ EM XEQUE! Escolha OBRIGATORIAMENTE um movimento que tire o rei do xeque.\n" if in_check else ""

    return f"""Cor: {color}
{check_alert}
Tabuleiro ATUAL — leia antes de jogar (peças mudaram desde o início):
{board}

Histórico: {history_text}

MOVIMENTOS VÁLIDOS: {moves_text}

REGRA FINAL: Escolha APENAS um movimento da lista acima.
A lista já considera o estado atual do tabuleiro. Não invente movimentos."""

def extract_move(response: str) -> str:
    movimento_match = re.search(r'MOVIMENTO:\s*([a-h][1-8])\s+([a-h][1-8])', response, re.IGNORECASE)
    if movimento_match:
        move = f"{movimento_match.group(1).lower()} {movimento_match.group(2).lower()}"
        logger.info(f"Movimento extraído do formato MOVIMENTO:")
        return move

    response_lower = response.strip().lower()
    pattern = r'\b([a-h][1-8])\s+([a-h][1-8])\b'
    match = re.search(pattern, response_lower)
    if match:
        return f"{match.group(1)} {match.group(2)}"

    raise ValueError(f"Não foi possível extrair movimento válido de: {response}")

@app.post("/move")
async def get_move(req: BoardRequest):
    try:
        logger.info(f"Modelo: {req.model} | Cor: {req.color}")
        # logger.info(f"Movimentos válidos ({len(req.valid_moves)}): {req.valid_moves}")
        # logger.info(f"Histórico ({len(req.history)} movimentos): {req.history[-10:]}")

        messages = [
            SystemMessage(content=SYSTEM_PROMPT),
            HumanMessage(content=build_prompt(req.board, req.color, req.history, req.valid_moves, req.in_check))
        ]

        if req.model == "GPT":
            response = await gpt.ainvoke(messages)
        elif req.model == "CLAUDE":
            response = await claude.ainvoke(messages)
        else:
            raise HTTPException(status_code=400, detail=f"Modelo inválido: {req.model}")

        raw_move = response.content
        logger.info(f"Resposta bruta do {req.model}:\n{raw_move}")

        move = extract_move(raw_move)

        if req.valid_moves and move not in req.valid_moves:
            raise ValueError(f"Movimento '{move}' não está na lista de válidos.")

        logger.info(f"Movimento final: {move}")
        return {"move": move}

    except ValueError as e:
        logger.error(f"Erro: {e}")
        raise HTTPException(status_code=422, detail=str(e))
    except Exception as e:
        logger.error(f"Erro inesperado: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
def health():
    return {"status": "ok", "models": ["GPT", "CLAUDE"]}