# BombaProjetil

A classe `BombaProjetil` representa o efeito visual e funcional da bomba do jogador. Diferente de outros projéteis, ela herda diretamente de `Personagem`, pois seu comportamento não é um simples movimento linear.

## Funcionamento

Quando o jogador usa uma bomba, uma instância de `BombaProjetil` é criada na posição do herói.

1.  **Expansão**: O método `atualizar()` é chamado a cada frame, aumentando o `raioAtualGrid` da bomba de zero até um `raioMaximoGrid` ao longo de sua `duracao`. Essa área circular em expansão é usada pelo `ControleDeJogo` para detectar e danificar inimigos e projéteis que estão dentro dela.
2.  **Desenho**: O método `autoDesenho()` desenha um círculo translúcido na tela que corresponde ao raio atual da bomba, fornecendo o feedback visual da área de efeito.
3.  **Finalização**: Quando a duração da bomba termina, seu método `deactivate()` é chamado. Antes de se desativar, ele executa o método `lancarMisseis()`.
4.  **Lançar Mísseis**: O método `lancarMisseis()` cria uma barragem de `ProjetilBombaHoming`, que são os projéteis teleguiados que saem ao final da bomba, completando o efeito.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `atualizar()` | `void` | Aumenta o raio de efeito da bomba a cada frame. |
| `deactivate()` | `void` | Lança os mísseis teleguiados e então desativa o objeto da bomba. |
| `autoDesenho(Graphics g)` | `void` | Desenha a área de efeito circular e translúcida da bomba. |
| `getRaioAtualGrid()` | `double` | Retorna o raio atual da bomba em unidades de grid, usado para detecção de colisão. |