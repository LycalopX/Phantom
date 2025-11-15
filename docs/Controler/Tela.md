# Tela

A classe `Tela` é um componente simples da interface gráfica que estende `JFrame`. Ela funciona como a janela principal do jogo.

## Responsabilidade

Sua única responsabilidade é criar e configurar a janela que conterá todos os outros elementos visuais, principalmente o `Cenario` (que é um `JPanel`).

No construtor, ela define:
- O título da janela.
- A operação de fechamento padrão (`EXIT_ON_CLOSE`), que encerra a aplicação quando a janela é fechada.
- A propriedade de não ser redimensionável (`setResizable(false)`), para manter as dimensões do jogo fixas.

A `Engine` cria uma instância da `Tela` e adiciona o `Cenario` a ela.