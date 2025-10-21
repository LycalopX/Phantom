# Classe `Cenario`

**Pacote:** `Controler`

## Descrição

Principal componente da camada de Visualização (View). Herda de `JPanel` e é responsável por desenhar todos os elementos visuais do jogo na tela.

## Métodos Principais

### `Cenario()`
*   **@brief** Construtor que configura as propriedades do painel, como tamanho e cor de fundo, e inicializa a funcionalidade de drag-and-drop para debug.

### `paintComponent(Graphics g)`
*   **@brief** Método central de desenho do Swing, sobreposto para renderizar o estado atual do jogo. Ele desenha o fundo, os personagens, projéteis, HUD e a tela de Game Over, dependendo do estado do jogo. A ordem de desenho é gerenciada para criar uma sensação de profundidade (e.g., projéteis do jogador na frente dos inimigos).

### `setupDropTarget()`
*   **@brief** Configura a funcionalidade de arrastar e soltar (drag-and-drop) que permite adicionar inimigos (a partir de arquivos `.zip`) dinamicamente ao jogo quando o modo de debug está ativo.
