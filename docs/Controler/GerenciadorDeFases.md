# Classe `GerenciadorDeFases`

**Pacote:** `Controler`

## Descrição

Controla a progressão das fases do jogo, gerenciando o nível atual e carregando a fase correspondente quando solicitado.

## Métodos Principais

### `carregarFase(Engine engine)`
*   **@brief** Carrega e retorna a fase (`Fase`) correspondente ao `nivelAtual`.
*   **@param engine** Uma referência à `Engine` principal, necessária para ser passada ao construtor do `ScriptDeFase`.
*   **@return `Fase`** Um novo objeto `Fase`, inicializado com o `ScriptDeFase` correto para o nível atual.
*   **@details** Utiliza uma estrutura `switch` para determinar qual classe de `ScriptDeFase` (ex: `ScriptFase1`, `ScriptFase2`) deve ser instanciada com base no valor de `nivelAtual`.

### `proximaFase(Engine engine)`
*   **@brief** Avança para o próximo nível e carrega a nova fase.
*   **@param engine** A referência à `Engine`, passada para `carregarFase`.
*   **@return `Fase`** A nova fase carregada.
*   **@details** Incrementa o `nivelAtual`. Se o `nivelAtual` ultrapassar o `TOTAL_DE_FASES`, ele é resetado para 1, criando um loop. Em seguida, chama `carregarFase()` para obter a nova fase.

### `irParaFase(int numeroDaFase, Engine engine)`
*   **@brief** Pula para um número de fase específico.
*   **@param numeroDaFase** O nível para o qual pular.
*   **@param engine** A referência à `Engine`.
*   **@return `Fase`** A nova fase carregada.
*   **@details** Define o `nivelAtual` para o número especificado (se for válido) e então carrega a fase correspondente.

### `resetar()`
*   **@brief** Reseta o gerenciador para o estado inicial.
*   **@details** Define o `nivelAtual` de volta para 1.

