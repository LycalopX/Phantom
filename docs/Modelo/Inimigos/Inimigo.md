# Classe `Inimigo`

**Pacote:** `Modelo.Inimigos`

## Descrição

Classe abstrata que serve como base para todos os inimigos do jogo. Herda de `Personagem` e adiciona propriedades específicas de inimigos, como vida e uma tabela de loot.

## Métodos Principais

### `Inimigo(...)`
*   **@brief** Construtores que inicializam as propriedades do inimigo, como sua vida e `LootTable`.

### `atualizar()`
*   **@brief** Define o comportamento padrão de movimento do inimigo. Este método é frequentemente sobreposto por subclasses para criar padrões de movimento mais complexos.

### `takeDamage(double damage)`
*   **@brief** Aplica dano ao inimigo. Se a vida do inimigo chegar a zero, ele é desativado (marcado para remoção) e um som de destruição é tocado.
