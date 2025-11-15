# Fase

A classe `Fase` funciona como um contêiner principal para o estado de uma fase do jogo. Ela não contém a lógica de *eventos* da fase, mas armazena todos os elementos que a compõem.

## Responsabilidades

- **Armazenamento de Elementos**: Mantém listas de todos os `Personagem` (herói, inimigos, projéteis, itens) e `ElementoCenario` (fundos, parallax) ativos.
- **Pools de Objetos**: Contém instâncias de `ProjetilPool` e `ItemPool`, que são usadas para reciclar projéteis e itens, uma otimização de desempenho crucial.
- **Delegação de Lógica**: Possui uma referência a um `ScriptDeFase`. A `Fase` delega ao `ScriptDeFase` a responsabilidade de decidir *quando* e *quais* inimigos e elementos de cenário devem ser adicionados.
- **Atualização**: O método `atualizar()` é chamado a cada frame pela `Engine`. Ele, por sua vez, chama o método `atualizar()` do script da fase e de todos os personagens e elementos de cenário contidos nela.

## Serialização

Assim como `Personagem`, a `Fase` é `Serializable`. Isso permite que todo o estado do jogo (a fase atual com todos os seus personagens e seus estados) seja salvo em um arquivo. O método `readObject` customizado garante que os recursos transientes (como imagens) sejam recarregados e que as referências de objetos sejam restauradas corretamente.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `atualizar(double velocidadeScroll)` | `void` | Atualiza o estado da fase, chamando o script e atualizando todos os personagens e elementos de cenário. |
| `adicionarPersonagem(Personagem p)` | `void` | Adiciona um novo personagem à lista da fase. |
| `adicionarElementoCenario(ElementoCenario elemento)` | `void` | Adiciona um novo elemento de cenário à lista da fase. |
| `getPersonagens()` | `List<Personagem>` | Retorna a lista de todos os personagens na fase. |
| `getProjetilPool()` | `ProjetilPool` | Retorna a piscina de projéteis da fase. |
| `getItemPool()` | `ItemPool` | Retorna a piscina de itens da fase. |
| `getHero()` | `Personagem` | Retorna uma referência ao objeto do herói na fase. |