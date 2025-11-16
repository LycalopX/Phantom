# Backlog de Funcionalidades e Melhorias

Este arquivo lista as próximas tarefas e ideias para a evolução do projeto "Phantom".

---

### 1. Menu Principal e Sistema de Pause
*   **Objetivo:** Dar ao jogo uma estrutura mais completa, permitindo que o jogador inicie o jogo a partir de um menu e pause a ação quando necessário.
*   **Plano de Ação:**
    1.  **Menu Principal:**
        *   Criar um novo `JPanel` chamado `MenuPanel` com botões como "Iniciar Jogo" e "Sair".
        *   Alterar a classe `Main` para exibir o `MenuPanel` inicialmente, em vez de iniciar a `Engine` diretamente.
        *   Ao clicar em "Iniciar Jogo", o `MenuPanel` é substituído pelo `Cenario` e a thread da `Engine` é iniciada.
    2.  **Sistema de Pause:**
        *   Adicionar um novo estado `PAUSADO` ao `GameState` da `Engine`.
        *   No listener de teclado da `Engine`, mapear uma tecla (como 'P' ou 'Esc') para alternar entre os estados `JOGANDO` e `PAUSADO`.
        *   Quando em `PAUSADO`, o loop principal da `Engine` deve pular a lógica de `atualizar()` para todos os elementos do jogo.
        *   No `SoundManager`, adicionar métodos `pausarTudo()` e `retomarTudo()` para que a música e os sons parem junto com o jogo.
        *   No `Cenario`, desenhar uma sobreposição na tela (ex: um texto "PAUSADO") quando o jogo estiver neste estado.

### 2. Transição Automática de Fases
*   **Objetivo:** Fazer o jogo avançar para a próxima fase assim que o jogador completar a atual.
*   **Plano de Ação:**
    1.  Na `Engine`, dentro do loop principal (no estado `JOGANDO`), verificar continuamente se a fase atual terminou: `fase.getScript().isFinalizada()`.
    2.  Quando a condição for verdadeira, chamar o método `GerenciadorDeFases.proximaFase()` para obter o novo objeto `Fase`.
    3.  Substituir a fase antiga pela nova na `Engine` e no `Cenario`, talvez com um pequeno efeito de transição (como um fade-out/fade-in).

### 4. Barra de Vida para Chefes (Boss UI)
*   **Objetivo:** Fornecer ao jogador um feedback visual claro sobre o progresso da batalha contra um chefe.
*   **Plano de Ação:**
    1.  Na classe `Cenario`, no método `paintComponent`, verificar se existe um `Boss` ativo na fase.
    2.  Se existir, obter sua vida atual e máxima para calcular a porcentagem de vida.
    3.  Desenhar uma barra de vida no topo da tela que represente essa porcentagem.