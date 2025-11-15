# Cenario

A classe `Cenario` estende `JPanel` e é o principal componente de renderização (a "view") do jogo. Sua responsabilidade é desenhar tudo o que aparece na tela, incluindo o cenário de fundo, os personagens, a HUD e as telas de estado como "Game Over".

## Processo de Renderização

O método `paintComponent(Graphics g)` é o coração da classe. Ele é chamado pelo sistema Swing sempre que a tela precisa ser redesenhada. A ordem de renderização é crucial para o efeito visual:

1.  **Camada de Fundo**: Desenha os `ElementoCenario` marcados como `DrawLayer.BACKGROUND`.
2.  **Overlays e Gradientes**: Aplica cores de sobreposição e gradientes de fundo, que são obtidos do `ScriptDeFase` atual.
3.  **Camada da Frente**: Desenha os `ElementoCenario` marcados como `DrawLayer.FOREGROUND`.
4.  **Personagens**: Desenha todos os `Personagem` (herói, inimigos, projéteis, itens). Antes de desenhar, a lista de personagens é ordenada por sua `RenderLayer` para garantir que o jogador apareça na frente dos inimigos, por exemplo.
5.  **HUD e Telas de Estado**: Se o modo de debug estiver ativo, desenha a HUD. Se o estado do jogo for `GAME_OVER`, desenha a tela correspondente.

## Funcionalidades Adicionais

- **Drag-and-Drop**: No modo de debug, permite arrastar arquivos `.zip` de inimigos para a janela do jogo para adicioná-los dinamicamente à fase.
- **Contador de FPS**: Mantém uma instância de `ContadorFPS` e o atualiza a cada frame.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `paintComponent(Graphics g)` | `void` | Método principal de desenho do Swing. Renderiza o estado atual do jogo. |
| `setFase(Fase fase)` | `void` | Define a fase atual a ser desenhada pelo cenário. |
| `setEstadoDoJogo(Engine.GameState estado)` | `void` | Define o estado atual do jogo, para controlar o que é desenhado (jogo, game over, etc.). |
| `atualizarContadorFPS()` | `void` | Atualiza o contador de FPS. |