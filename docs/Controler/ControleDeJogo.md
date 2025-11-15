# Classe `ControleDeJogo`

**Pacote:** `Controler`

## Descrição

Orquestra a lógica principal do jogo, incluindo detecção de colisão, interações entre personagens e gerenciamento de estado dos objetos da fase.

## Detalhes da Implementação

Para otimizar a detecção de colisão, esta classe utiliza uma **Quadtree**. A cada frame, os personagens são inseridos na Quadtree. Em vez de verificar cada personagem contra todos os outros, a classe `retrieve` da Quadtree é usada para obter apenas uma lista de vizinhos próximos para cada personagem, reduzindo significativamente o número de comparações necessárias.

## Métodos Principais

### `ControleDeJogo(ItemPool itemPool)`
*   **@brief** Construtor da classe.
*   **@param itemPool** Uma referência ao `ItemPool` da fase, usado para criar itens de loot.
*   **@details** Inicializa a `Quadtree` que será usada para otimizar a detecção de colisão e as listas de apoio para o processamento de objetos a cada frame.

### `processaTudo(List<Personagem> personagens, boolean removeProjectiles)`
*   **@brief** O método central da classe, orquestra toda a lógica de um frame.
*   **@param personagens** A lista de todos os personagens ativos na fase.
*   **@param removeProjectiles** Um flag que, se `true`, remove todos os projéteis da tela (usado durante o respawn do herói).
*   **@return `boolean`** Retorna `true` se o herói foi atingido durante este frame, `false` caso contrário.
*   **@details** Este método popula a `Quadtree` com os personagens ativos, verifica colisões usando a `Quadtree` para otimização, lida com a lógica da bomba do herói e, ao final, remove os personagens que se tornaram inativos.

### `handleCollision(Personagem p1, Personagem p2, Hero hero)`
*   **@brief** Gerencia a lógica de interação para um par de personagens que colidiram.
*   **@param p1, p2** O par de personagens que colidiram.
*   **@param hero** Uma referência ao herói.
*   **@return `boolean`** Retorna `true` se a colisão resultou em dano ao herói.
*   **@details** Determina os tipos dos personagens (Herói, Inimigo, Projétil, Item) e chama o método de tratamento de colisão apropriado (ex: `colisaoHeroiInimigo`, `colisaoProjetilHeroiInimigo`, `aplicarEfeitoDoItem`).

### `colisaoProjetilHeroiInimigo(Projetil p, Inimigo i, Hero hero)`
*   **@brief** Lida com a colisão entre um projétil do herói e um inimigo.
*   **@details** Aplica dano ao inimigo, desativa o projétil (a menos que seja uma bomba) e, se a vida do inimigo chegar a zero, utiliza a `LootTable` do inimigo para gerar os itens de loot correspondentes.

### `aplicarEfeitoDoItem(Hero heroi, Item item)`
*   **@brief** Aplica o efeito de um item coletado ao herói.
*   **@details** Com base no `ItemType` do item, aumenta o poder, bombas, vida ou pontuação do herói e toca o som correspondente.

### `ehPosicaoValida(...)`
*   **@brief** Verifica se uma nova posição para um personagem é válida, checando por colisões com outros personagens não transponíveis.
*   **@return `boolean`** Retorna `true` se a posição for válida, `false` caso contrário.

