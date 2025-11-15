# ConfigMapa

A classe `ConfigMapa` é uma classe de utilidade final que centraliza todas as configurações dimensionais do jogo, como tamanho do mundo, da tela e do grid.

Uma característica importante desta classe é o seu bloco de inicialização estático (`static { ... }`). Este bloco calcula dinamicamente as dimensões da tela (`LARGURA_TELA`, `ALTURA_TELA`) e o tamanho da célula do grid (`CELL_SIDE`) com base na resolução do monitor do usuário. Ele tenta manter uma proporção de altura específica para garantir que o jogo se ajuste bem em diferentes telas. Caso a detecção da resolução falhe (por exemplo, em um ambiente sem interface gráfica), a classe recorre a valores padrão fixos.

## Constantes Principais

| Constante | Tipo | Descrição |
|---|---|---|
| `MUNDO_LARGURA` | `int` | A largura do mundo do jogo em unidades de grid. |
| `MUNDO_ALTURA` | `int` | A altura do mundo do jogo em unidades de grid. |
| `LARGURA_TELA` | `int` | A largura final da janela do jogo em pixels, calculada dinamicamente. |
| `ALTURA_TELA` | `int` | A altura final da janela do jogo em pixels, calculada dinamicamente. |
| `CELL_SIDE` | `int` | O tamanho de um lado de uma célula do grid em pixels, calculado dinamicamente. |
| `FATOR_ESCALA_ALTURA` | `double` | Um fator de escala baseado na altura da tela, usado para ajustar velocidades e tamanhos. |
| `HERO_RESPAWN_X` | `int` | A coordenada X (em grid) onde o herói renasce. |
| `HERO_RESPAWN_Y` | `int` | A coordenada Y (em grid) onde o herói renasce. |