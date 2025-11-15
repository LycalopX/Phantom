# GerenciadorDeFases

Esta classe simples gerencia a progressão das fases do jogo. Ela mantém o controle do nível atual e é responsável por instanciar o `ScriptDeFase` correto para cada nível.

## Funcionamento

O `GerenciadorDeFases` usa uma instrução `switch` para associar um número de nível (ex: 1, 2, 3) a uma classe de script concreta (ex: `ScriptFase1`, `ScriptFase2`). Quando a `Engine` solicita uma nova fase, este gerenciador cria o script apropriado, que por sua vez é usado para construir um novo objeto `Fase`.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `carregarFase(Engine engine)` | `Fase` | Carrega e retorna a fase correspondente ao nível atual. |
| `proximaFase(Engine engine)` | `Fase` | Avança para a próxima fase e a carrega. Se chegar ao final, reinicia do começo. |
| `irParaFase(int numeroDaFase, Engine engine)` | `Fase` | Pula para um número de fase específico. |
| `resetar()` | `void` | Reseta o gerenciador para o nível inicial (nível 1). |