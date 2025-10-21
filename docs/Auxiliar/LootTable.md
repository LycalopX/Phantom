# Classe `LootTable`

**Pacote:** `Auxiliar`

## Descrição

Representa uma tabela de loot que pode ser associada a um inimigo. Ela contém uma lista de possíveis `LootItem` que podem ser "dropados" quando o inimigo é derrotado.

## Métodos Principais

### `LootTable()`
*   **@brief** Construtor que inicializa a lista de itens da tabela.

### `addItem(LootItem item)`
*   **@brief** Adiciona um novo item possível (`LootItem`) à tabela de loot.

### `gerarDrops()`
*   **@brief** Processa a tabela de loot. Para cada item, ele "rola um dado" com base na probabilidade do item. Se o item for sorteado, ele gera uma quantidade aleatória (entre o mínimo e o máximo definidos no `LootItem`) e retorna uma lista com os itens que foram efetivamente dropados.
