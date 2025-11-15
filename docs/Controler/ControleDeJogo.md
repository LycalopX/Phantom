# ControleDeJogo

A classe `ControleDeJogo` orquestra a lógica principal do jogo a cada frame. Sua responsabilidade central é gerenciar as interações entre todos os `Personagem` na fase, com um foco especial na detecção e tratamento de colisões.

## Otimização com Quadtree

Para evitar a verificação de colisão de cada objeto contra todos os outros (O(n²)), `ControleDeJogo` utiliza uma `Quadtree`. No início de cada chamada a `processaTudo()`, a Quadtree é limpa e todos os personagens ativos são inseridos nela.

Ao verificar as colisões, em vez de iterar sobre a lista inteira de personagens, a classe pede à Quadtree uma lista menor de "vizinhos potenciais" para cada personagem. Isso reduz drasticamente o número de verificações necessárias e é fundamental para o bom desempenho do jogo.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `processaTudo(List<Personagem> personagens, boolean removeProjectiles)` | `boolean` | O método principal da classe. Processa todas as colisões e interações para um frame. Retorna `true` se o herói foi atingido. |
| `desenhaTudo(List<Personagem> e, Graphics g)` | `void` | Um método auxiliar para desenhar uma lista de personagens (atualmente não utilizado pela `Engine`, que delega o desenho ao `Cenario`). |
| `ehPosicaoValida(List<Personagem> umaFase, Personagem p, double proximoX, double proximoY)` | `boolean` | Verifica se uma nova posição para um personagem é válida, checando por colisões com outros personagens não transponíveis. |
| `handleCollision(Personagem p1, Personagem p2, Hero hero)` | `boolean` | Gerencia a lógica de interação para um par de personagens que colidiram (ex: Herói vs Inimigo, Projétil vs Inimigo). |
| `aplicarEfeitoDoItem(Hero heroi, Item item)` | `void` | Aplica o efeito de um item coletado (power-up, bomba, etc.) ao herói. |