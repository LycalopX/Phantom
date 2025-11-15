# LootTable

A classe `LootTable` representa uma tabela de itens que podem ser "dropados" por um inimigo ao ser derrotado. Cada inimigo pode ter sua própria `LootTable`, permitindo uma grande customização de recompensas.

## Funcionamento

Uma `LootTable` contém uma lista de objetos `LootItem`. Cada `LootItem` define um tipo de item, a quantidade (mínima e máxima) que pode ser dropada e a probabilidade (de 0.0 a 1.0) de isso acontecer.

Quando o método `gerarDrops()` é chamado, a classe itera sobre todos os `LootItem` possíveis. Para cada um, um número aleatório é gerado e comparado com a probabilidade do item. Se o "roll" for bem-sucedido, uma quantidade aleatória do item (entre o mínimo e o máximo definidos) é adicionada à lista de drops a serem gerados na fase.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `addItem(LootItem item)` | `void` | Adiciona um novo item possível à tabela de loot. |
| `gerarDrops()` | `ArrayList<LootItem>` | Processa a tabela, rolando as probabilidades para cada item e retorna uma lista contendo os drops que foram efetivamente gerados. |