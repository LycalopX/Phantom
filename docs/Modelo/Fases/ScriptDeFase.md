# Classe `ScriptDeFase`

**Pacote:** `Modelo.Fases`

## Descrição

Classe abstrata que define o contrato para os scripts de fase. Cada fase do jogo possui uma implementação concreta desta classe, que dita o comportamento dos inimigos e do cenário, permitindo um design de nível único para cada fase.

## Métodos Principais

### `atualizarInimigos(Fase fase)`
*   **@brief** Método abstrato que deve ser implementado por subclasses para controlar a lógica de spawning de inimigos.

### `atualizarCenario(Fase fase, double velocidadeScroll)`
*   **@brief** Método que pode ser sobreposto para controlar o spawning de elementos de cenário, como as árvores de parallax.

### `preencherCenarioInicial(Fase fase)`
*   **@brief** Método que pode ser sobreposto para preencher o cenário com elementos iniciais antes do início da fase.
