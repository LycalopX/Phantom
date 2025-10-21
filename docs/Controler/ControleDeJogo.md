# Classe `ControleDeJogo`

**Pacote:** `Controler`

## Descrição

Orquestra a lógica principal do jogo, incluindo detecção de colisão, interações entre personagens e gerenciamento de estado dos objetos da fase.

## Detalhes da Implementação

Para otimizar a detecção de colisão, esta classe utiliza uma **Quadtree**. A cada frame, os personagens são inseridos na Quadtree. Em vez de verificar cada personagem contra todos os outros, a classe `retrieve` da Quadtree é usada para obter apenas uma lista de vizinhos próximos para cada personagem, reduzindo significativamente o número de comparações necessárias.

## Métodos Principais

### `ControleDeJogo()`
*   **@brief** Construtor que inicializa a Quadtree e as listas de apoio para o processamento de objetos.

### `processaTudo(...)`
*   **@brief** O método central da classe. Processa todas as interações e colisões para um frame do jogo, populando a Quadtree, verificando colisões, lidando com a lógica da bomba e limpando os personagens inativos.

### `ehPosicaoValida(...)`
*   **@brief** Verifica se uma nova posição para um personagem é válida, checando por colisões com outros personagens não transponíveis.

### `handleCollision(...)`
*   **@brief** Gerencia a lógica de interação para um par de personagens que colidiram (e.g., Herói vs. Inimigo, Projétil vs. Inimigo, Herói vs. Item).
