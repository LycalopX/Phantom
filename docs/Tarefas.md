# Backlog de Funcionalidades e Melhorias

Este arquivo lista as próximas tarefas e ideias para a evolução do projeto "Phantom".

---

### 2. Transição Automática de Fases
*   **Objetivo:** Fazer o jogo avançar para a próxima fase assim que o jogador completar a atual.
*   **Plano de Ação:**
    1.  Na `Engine`, dentro do loop principal (no estado `JOGANDO`), verificar continuamente se a fase atual terminou: `fase.getScript().isFinalizada()`.
    2.  Quando a condição for verdadeira, chamar o método `GerenciadorDeFases.proximaFase()` para obter o novo objeto `Fase`.
    3.  Substituir a fase antiga pela nova na `Engine` e no `Cenario`, talvez com um pequeno efeito de transição (como um fade-out/fade-in).