# Engine

A classe `Engine` é o coração do jogo, implementando a interface `Runnable` para criar o loop principal (`game loop`). Ela é responsável por coordenar todos os componentes principais: a `Tela` (visão), o `Cenario` (renderização), o `ControleDeJogo` (lógica de colisão) e o `ControladorDoHeroi` (input).

## Game Loop e Máquina de Estados

- **Game Loop**: O método `run()` contém o loop principal, que tenta manter uma taxa de atualização constante (definida por `GAME_FPS`) chamando os métodos `atualizar()` e `repaint()` em intervalos regulares.
- **Máquina de Estados**: O método `atualizar()` implementa uma máquina de estados finitos (`GameState`) que gerencia o fluxo do jogo. Os principais estados são:
    - `JOGANDO`: Estado normal de jogo.
    - `DEATHBOMB_WINDOW`: Uma pequena janela de tempo após o herói ser atingido, durante a qual o jogador pode usar uma bomba para se salvar. O jogo fica em câmera lenta.
    - `RESPAWNANDO`: O estado após a morte, onde a tela é limpa e o herói se prepara para reentrar no jogo.
    - `GAME_OVER`: O fim do jogo.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `run()` | `void` | O loop principal do jogo, que controla a taxa de atualização e renderização. |
| `atualizar()` | `void` | Atualiza o estado do jogo com base na máquina de estados (`GameState`). |
| `startGameThread()` | `void` | Inicia a thread principal do jogo, começando o game loop. |
| `configurarTeclado()` | `void` | Configura os listeners de teclado para capturar os inputs do jogador, incluindo teclas de jogo e de debug. |
| `carregarProximaFase()` | `void` | Carrega a próxima fase do jogo através do `GerenciadorDeFases`. |
| `getEstadoAtual()` | `GameState` | Retorna o estado atual da máquina de estados do jogo. |