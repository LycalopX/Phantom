# Classe `Engine`

**Pacote:** `Controler`

## Descrição

Classe principal do motor do jogo, responsável pelo loop principal, gerenciamento de estado e coordenação entre os componentes do modelo, visão e controle.

## Métodos Principais

### `Engine()`
*   **@brief** Construtor que inicializa todos os componentes do jogo (controladores, fases, herói, UI).

### `run()`
*   **@brief** Implementa o loop principal do jogo, controlando a taxa de atualização (FPS) e renderização.

### `atualizar()`
*   **@brief** Gerencia a máquina de estados do jogo (`JOGANDO`, `DEATHBOMB_WINDOW`, `RESPAWNANDO`, `GAME_OVER`) e atualiza a lógica a cada frame.

### `configurarTeclado()`
*   **@brief** Configura os listeners de teclado para capturar os inputs do jogador e funções de debug.

### `salvarJogo()` / `carregarJogo()`
*   **@brief** Métodos para serializar (salvar) e desserializar (carregar) o estado da fase atual.
