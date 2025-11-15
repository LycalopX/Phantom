# Classe `Tela`

**Pacote:** `Controler`

## Descrição

Representa a janela principal do jogo (`JFrame`). Sua única responsabilidade é servir como um contêiner para o painel `Cenario`, onde o jogo é de fato desenhado.

## Métodos Principais

### `Tela()`
*   **@brief** Construtor da classe `Tela`.
*   **@details** Configura as propriedades fundamentais da janela principal do jogo (`JFrame`):
    *   Define o título da janela para "Phantom Project (POO)".
    *   Configura a operação padrão de fechamento para `JFrame.EXIT_ON_CLOSE`, garantindo que a aplicação termine quando a janela for fechada.
    *   Desativa o redimensionamento da janela (`setResizable(false)`) para manter um tamanho de tela fixo e consistente.

