# Classe `ArvoreParallax`

**Pacote:** `Auxiliar.Cenario1`

## Descrição

Cria e gerencia um grupo de `BlocoDeFolha` para simular uma árvore com efeito de parallax. Cada "bloco" da árvore se move a uma velocidade ligeiramente diferente para criar uma ilusão de profundidade no cenário.

## Métodos Principais

### `ArvoreParallax(...)`
*   **@brief** Construtor que cria uma árvore composta por três blocos de folhas com diferentes tamanhos, posições e velocidades.

### `mover(double velocidadeAtualDoFundo)`
*   **@brief** Move a árvore verticalmente, ajustando a velocidade de cada bloco com base na velocidade de rolagem do fundo para manter o efeito de parallax.

### `desenhar(Graphics2D g2d, int alturaDaTela)`
*   **@brief** Desenha cada bloco da árvore na tela.
