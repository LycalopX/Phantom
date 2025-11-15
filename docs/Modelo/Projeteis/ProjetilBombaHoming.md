# ProjetilBombaHoming

Esta classe estende `ProjetilHoming` e representa os mísseis especiais que são disparados ao final do efeito de uma bomba do jogador.

## Comportamento

Ela herda a capacidade de perseguição de `ProjetilHoming`, mas adiciona uma fase inicial ao seu comportamento, controlada por uma nova máquina de estados:

1.  **`EXPANDINDO`**: Logo após ser criado, o míssil viaja em linha reta para fora a partir da posição do jogador por um curto período (`DURACAO_EXPANSAO`). Isso cria o efeito visual de uma "explosão" de mísseis se espalhando.
2.  **`PERSEGUINDO`**: Após a fase de expansão, o míssil entra no estado de perseguição normal, herdado de `ProjetilHoming`, e começa a procurar e perseguir o inimigo mais próximo.

## Efeito Visual

A classe sobrescreve o método `autoDesenho()` para adicionar um efeito visual customizado:
- **Rastro (Trail)**: Mantém um histórico das últimas posições (`positionHistory`) e as desenha com uma transparência decrescente, criando um efeito de rastro.
- **Pulsação**: O tamanho do sprite do míssil é animado usando uma função seno (`Math.sin`), fazendo-o pulsar e se destacar visualmente.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `resetBombaHoming(...)` | `void` | Reseta o estado do míssil, definindo seu estado inicial para `EXPANDINDO`. |
| `atualizar()` | `void` | Gerencia a transição do estado de `EXPANDINDO` para `PERSEGUINDO` e então delega para a lógica de atualização da superclasse. |
| `autoDesenho(Graphics g)` | `void` | Desenha o míssil com seus efeitos visuais customizados de rastro e pulsação. |