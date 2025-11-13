# Classe `ContadorFPS`

**Pacote:** `Auxiliar.Debug`

## Descrição

Uma classe utilitária para calcular e formatar a taxa de quadros por segundo (FPS) do jogo.

## Métodos Principais

### `ContadorFPS()`
*   **@brief** Construtor que inicializa as variáveis de controle de tempo e contagem de frames.

### `atualizar()`
*   **@brief** Incrementa o contador de frames a cada chamada. Se um segundo tiver passado desde a última atualização, ele atualiza o valor de `fpsExibido` e reseta o contador.

### `getFPSString()`
*   **@brief** Retorna o último valor de FPS calculado, formatado como uma `String` (e.g., "FPS: 60").