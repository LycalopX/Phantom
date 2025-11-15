# Quadtree

A classe `Quadtree` implementa a estrutura de dados de mesmo nome, que é uma árvore onde cada nó interno tem exatamente quatro filhos. Ela é usada para particionar um espaço bidimensional (a tela do jogo) subdividindo-o recursivamente em quatro quadrantes.

## Propósito no Jogo

O principal objetivo da Quadtree neste projeto é otimizar a **detecção de colisão**. Em vez de verificar cada personagem contra todos os outros personagens na fase (um processo de complexidade O(n²)), a Quadtree permite verificar colisões apenas entre personagens que estão no mesmo quadrante ou em quadrantes adjacentes. Isso reduz drasticamente o número de comparações necessárias a cada frame, melhorando significativamente o desempenho, especialmente com muitos projéteis e inimigos na tela.

## Funcionamento

- **Inserção**: Quando um personagem é inserido (`insert(Personagem p)`), a Quadtree determina em qual quadrante ele se encaixa. Se um nó da árvore atinge sua capacidade máxima de objetos (`MAX_OBJECTS`) e ainda não atingiu a profundidade máxima (`MAX_LEVELS`), ele se subdivide (`split()`) em quatro nós filhos, e seus objetos são redistribuídos entre eles.
- **Recuperação**: O método `retrieve(returnObjects, p)` é o mais importante. Dado um personagem, ele retorna uma lista de todos os outros personagens que estão próximos o suficiente para potencialmente colidir com ele, buscando apenas nos quadrantes relevantes.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `clear()` | `void` | Limpa a Quadtree recursivamente, removendo todos os objetos e nós filhos. |
| `insert(Personagem p)` | `void` | Insere um personagem na Quadtree. |
| `retrieve(ArrayList<Personagem> returnObjects, Personagem p)` | `ArrayList<Personagem>` | Preenche a lista `returnObjects` com todos os personagens que podem colidir com o personagem `p`. |
| `retrieve(ArrayList<Personagem> returnObjects, Rectangle pBounds)` | `ArrayList<Personagem>` | Preenche a lista `returnObjects` com todos os personagens que podem colidir com uma determinada área retangular. |