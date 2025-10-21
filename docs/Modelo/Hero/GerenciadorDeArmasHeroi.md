# Classe `GerenciadorDeArmasHeroi`

**Pacote:** `Modelo.Hero`

## Descrição

Gerencia os sistemas de armas do herói, controlando a lógica de tiro, os tipos de projéteis e os cooldowns com base no nível de poder do jogador.

## Métodos Principais

### `atualizarTimers()`
*   **@brief** Decrementa os contadores de cooldown para o tiro principal e os mísseis a cada frame.

### `disparar(...)`
*   **@brief** Executa a lógica de disparo do herói. Com base no nível de poder, ele determina o número de projéteis, a cadência de tiro e se os mísseis teleguiados devem ser disparados.

### `getNivelTiro(int power)`
*   **@brief** Calcula o nível de tiro atual do herói com base em uma fórmula que leva em conta o `power` acumulado.
