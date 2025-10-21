# Classe `GerenciadorDeFases`

**Pacote:** `Controler`

## Descrição

Controla a progressão das fases do jogo, gerenciando o nível atual e carregando a fase correspondente quando solicitado.

## Métodos Principais

### `carregarFase()`
*   **@brief** Carrega e retorna a fase (`Fase`) correspondente ao nível atual. Utiliza um `switch` para instanciar o `ScriptDeFase` correto para o nível.

### `proximaFase()`
*   **@brief** Avança para o próximo nível e carrega a nova fase. Se o jogo atingir a última fase, ele volta para a primeira.

### `resetar()`
*   **@brief** Reseta o progresso para o nível 1.
