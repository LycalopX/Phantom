# Classe `ProjetilHoming`

**Pacote:** `Modelo.Projeteis`

## Descrição

Subclasse de `Projetil` que implementa um comportamento de perseguição. Após um período inicial de inércia, o projétil começa a seguir o inimigo mais próximo.

## Máquina de Estados

O comportamento do projétil é dividido em dois estados:

1.  **`INERCIA`**: O projétil se move em linha reta por um curto período.
2.  **`PERSEGUINDO`**: O projétil identifica o inimigo mais próximo e ajusta continuamente seu ângulo para persegui-lo.

## Métodos Principais

### `ProjetilHoming(...)`
*   **@brief** Construtor que recebe a lista de personagens para poder identificar seus alvos.

### `resetHoming(...)`
*   **@brief** Reseta o projétil para um novo disparo, definindo seu estado inicial como `INERCIA`.

### `atualizar()`
*   **@brief** Implementa a lógica da máquina de estados, transitando da inércia para a perseguição e atualizando o movimento.

### `ajustarAnguloParaOAlvo()`
*   **@brief** Ajusta suavemente o ângulo do projétil a cada frame para que ele se vire em direção ao alvo atual.
