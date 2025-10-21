# Classe `Quadtree`

**Pacote:** `Auxiliar.Personagem`

## Descrição

Implementa uma estrutura de dados Quadtree para otimizar a detecção de colisão espacial. A Quadtree divide o espaço de jogo em quadrantes recursivamente, permitindo que as verificações de colisão sejam feitas apenas entre objetos que estão próximos uns dos outros.

## Métodos Principais

### `Quadtree(int pLevel, Rectangle pBounds)`
*   **@brief** Construtor que cria um nó da Quadtree para uma determinada área (`pBounds`) e nível de profundidade (`pLevel`).

### `clear()`
*   **@brief** Limpa recursivamente a árvore, removendo todos os objetos e sub-nós.

### `insert(Personagem p)`
*   **@brief** Insere um personagem na árvore. Se um nó atinge sua capacidade máxima de objetos, ele se subdivide em quatro nós filhos e distribui seus objetos entre eles.

### `retrieve(ArrayList<Personagem> returnObjects, Personagem p)`
*   **@brief** Retorna uma lista de personagens que estão no mesmo quadrante (ou quadrantes sobrepostos) que o personagem `p`, fornecendo uma lista de candidatos potenciais para a verificação de colisão.
