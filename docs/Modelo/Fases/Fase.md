# Classe `Fase`

**Pacote:** `Modelo.Fases`

## Descrição

Representa um contêiner para uma fase (ou nível) do jogo. Ela armazena todos os personagens, elementos de cenário e o estado de rolagem da tela. A lógica de eventos e spawning de inimigos é delegada a um `ScriptDeFase`.

## Métodos Principais

### `Fase(ScriptDeFase script)`
*   **@brief** Construtor que inicializa a fase com um script específico. Ele também cria o `ProjetilPool` e pré-carrega os recursos visuais.

### `atualizar(double velocidadeScroll)`
*   **@brief** Atualiza o estado da fase a cada frame. Isso inclui a rolagem do cenário, a execução do `ScriptDeFase` para spawning, e a chamada do método `atualizar()` de cada personagem na fase.

### `adicionarPersonagem(Personagem p)`
*   **@brief** Adiciona um novo personagem à lista de personagens da fase.
