# Classe `Engine`

**Pacote:** `Controler`

## Descrição

Classe principal do motor do jogo, responsável pelo loop principal, gerenciamento de estado e coordenação entre os componentes do modelo, visão e controle.

## Métodos Principais

### `Engine()`
*   **@brief** Construtor da Engine.
*   **@details** Inicializa todos os componentes essenciais do jogo. Cria o `GerenciadorDeFases`, carrega a primeira fase, instancia o `Hero`, os `Pools` de itens e projéteis, os controladores (`ControleDeJogo`, `ControladorDoHeroi`) e configura a interface gráfica (`Cenario`, `Tela`).

### `run()`
*   **@brief** Implementa a interface `Runnable` e contém o loop principal do jogo.
*   **@details** Controla a taxa de atualização e renderização (FPS) usando um delta time para garantir uma execução consistente. A cada ciclo, chama `atualizar()` para a lógica e `cenario.repaint()` para o desenho.

### `atualizar()`
*   **@brief** O coração da lógica do jogo, chamado a cada frame pelo `run()`.
*   **@details** Gerencia uma máquina de estados (`GameState`) que dita o comportamento do jogo:
    *   `JOGANDO`: Processa input, atualiza a fase e verifica colisões.
    *   `DEATHBOMB_WINDOW`: Um breve período em câmera lenta após o herói ser atingido, dando a chance de usar uma bomba (deathbomb).
    *   `RESPAWNANDO`: Período de invencibilidade após a morte, onde o herói retorna à tela.
    *   `GAME_OVER`: Fim de jogo.

### `dropItensAoMorrer(int powerAtual)`
*   **@brief** Controla os itens que o herói "dropa" ao morrer.
*   **@param powerAtual** A quantidade de poder que o herói tinha no momento da morte.
*   **@details** Converte metade do poder do herói em itens de `MINI_POWER_UP` que são espalhados pela tela.

### `configurarTeclado()`
*   **@brief** Configura os `KeyListeners` para capturar os inputs do jogador.
*   **@details** Adiciona listeners para as teclas de movimento, tiro, bomba, foco, e também para as teclas de atalho de debug (salvar/carregar, reiniciar, mostrar informações de pools, etc.).

### `startGameThread()`
*   **@brief** Inicia a thread principal do jogo, que por sua vez chama o método `run()`.

### `reiniciarJogo()`
*   **@brief** Reseta o jogo para seu estado inicial, recarregando a primeira fase e reiniciando o herói.

### `carregarProximaFase()`
*   **@brief** Utiliza o `GerenciadorDeFases` para carregar a próxima fase do jogo, adicionando o herói a ela.

### `salvarJogo()` / `carregarJogo()`
*   **@brief** Métodos para serializar (salvar) e desserializar (carregar) o estado da `Fase` atual.
*   **@details** Usam `ObjectOutputStream` e `ObjectInputStream` para escrever/ler o objeto `Fase` em um arquivo `POO.dat`.

### `salvarInimigosParaTeste()`
*   **@brief** Uma função de debug para criar arquivos `.zip` de vários inimigos.
*   **@details** Esses arquivos podem ser arrastados para a janela do jogo (quando em modo debug) para instanciar inimigos dinamicamente.

