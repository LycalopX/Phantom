# Classe `Item`

**Pacote:** `Modelo.Items`

## Descrição

Representa um item coletável no jogo, como power-ups e bombas. Herda de `Personagem` e possui sua própria lógica de movimento e física.

## Comportamento

Um item pode ter dois comportamentos principais:

1.  **Queda Livre**: Por padrão, o item cai com uma leve aceleração (gravidade).
2.  **Atração**: Se o jogador estiver no topo da tela ou se uma bomba estiver ativa, todos os itens na tela são atraídos em direção ao herói, facilitando a coleta.

## Métodos Principais

### `Item(...)`
*   **@brief** Construtor que define o tipo do item e recorta seu sprite específico a partir de um spritesheet compartilhado.

### `atualizar()`
*   **@brief** Atualiza a posição do item a cada frame, aplicando a lógica de gravidade ou de atração em direção ao herói.

### `lancarItem(...)`
*   **@brief** Dá ao item um impulso inicial em uma direção específica. Usado quando o herói morre e seus power-ups são espalhados.
