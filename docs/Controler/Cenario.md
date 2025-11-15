# Classe `Cenario`

**Pacote:** `Controler`

## Descrição

Principal componente da camada de Visualização (View). Herda de `JPanel` e é responsável por desenhar todos os elementos visuais do jogo na tela.

## Métodos Principais

### `Cenario()`
*   **@brief** Construtor do painel.
*   **@details** Configura as propriedades do `JPanel`, como dimensões e cor de fundo. Inicializa o `ContadorFPS` e chama `setupDropTarget()` para habilitar a funcionalidade de arrastar e soltar para debug.

### `paintComponent(Graphics g)`
*   **@brief** O método de desenho principal, sobreposto de `JPanel`.
*   **@param g** O contexto gráfico do Swing para desenhar.
*   **@details** Este método é o coração da renderização. A cada frame, ele:
    1.  Verifica o `estadoDoJogo`. Se for `GAME_OVER`, desenha a tela de fim de jogo e para.
    2.  Desenha os elementos de cenário da camada de fundo (`BACKGROUND`).
    3.  Aplica um gradiente de cor e um overlay de cor de fundo, definidos pelo script da fase.
    4.  Desenha os elementos de cenário da camada da frente (`FOREGROUND`).
    5.  Se o estado for `DEATHBOMB_WINDOW`, desenha um overlay vermelho semi-transparente.
    6.  Ordena todos os personagens com base em sua `RenderLayer` para garantir que os elementos sejam desenhados na ordem correta (ex: herói na frente de inimigos).
    7.  Desenha cada personagem ativo.
    8.  Se o `DebugManager` estiver ativo, chama `desenharHUD()` para mostrar informações de debug.

### `setupDropTarget()`
*   **@brief** Configura a funcionalidade de arrastar e soltar (drag-and-drop).
*   **@details** Cria um `DropTarget` que escuta por arquivos soltos na janela do jogo. Se o modo de debug estiver ativo, ele aceita o drop e chama `processarArquivoSolto()`.

### `processarArquivoSolto(File file, Point dropPoint)`
*   **@brief** Processa um arquivo `.zip` solto na tela.
*   **@details** Desserializa um objeto `Personagem` do arquivo `.zip`, define sua posição para a localização do mouse e o adiciona à fase atual. Usado para instanciar inimigos dinamicamente para testes.

### `desenharHUD(Graphics2D g2d)`
*   **@brief** Desenha o Heads-Up Display (HUD) com informações de debug.
*   **@details** Mostra na tela o contador de FPS e estatísticas do herói, como Bombas, HP, Power, Score e nível dos mísseis.

### `setFase(Fase fase)`
*   **@brief** Define a fase cujos elementos serão desenhados.
*   **@details** Armazena a referência da fase e extrai dela as informações de estilo visual, como a cor do overlay e o gradiente de fundo.

### `setEstadoDoJogo(Engine.GameState estado)`
*   **@brief** Recebe da `Engine` o estado atual do jogo para controlar o que é renderizado.

