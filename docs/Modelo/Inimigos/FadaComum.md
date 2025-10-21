# Classe `FadaComum`

**Pacote:** `Modelo.Inimigos`

## Descrição

Implementação de um inimigo comum do tipo "fada". Esta classe possui uma máquina de estados interna simples para controlar seu comportamento.

## Máquina de Estados

O comportamento da fada é dividido em três estados:

1.  **`ENTERING`**: A fada entra na tela com um movimento senoidal.
2.  **`SHOOTING`**: A fada para em uma posição e dispara projéteis em direção ao herói por um determinado período.
3.  **`EXITING`**: Após o período de tiro, a fada se move para fora da tela e é desativada.

## Métodos Principais

### `FadaComum(...)`
*   **@brief** Construtor que inicializa a fada, definindo seu estado inicial como `ENTERING`.

### `atualizar()`
*   **@brief** Implementa a lógica da máquina de estados, atualizando o movimento e o comportamento de tiro da fada com base em seu estado atual.

### `atirar()`
*   **@brief** Cria e dispara um projétil em direção à posição atual do herói.
