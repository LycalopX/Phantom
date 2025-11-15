# Item

A classe `Item` representa todos os objetos coletáveis no jogo, como power-ups, bombas e pontos. Ela herda de `Personagem`.

## Comportamento Físico

Um `Item` possui uma física simples:
- **Gravidade**: Após ser "dropado" por um inimigo, ele cai lentamente com uma aceleração constante (`GRAVIDADE`) até atingir uma velocidade máxima de queda (`MAX_FALL_SPEED`).
- **Lançamento**: Itens dropados pelo jogador ao morrer (`lancarItem`) recebem um impulso inicial e ignoram a gravidade por um curto período.
- **Atração**: Sob certas condições (o jogador está perto do topo da tela ou usando uma bomba), todos os itens na tela ignoram sua física normal e se movem diretamente em direção ao herói para serem coletados automaticamente.

## Pooling e Sprites

- **Pooling**: Itens são gerenciados pela `ItemPool`. Em vez de criar e destruir objetos `Item` constantemente, eles são reciclados. Um item "inativo" da pool é pego, inicializado com uma nova posição (`init()`) e "ativado". Quando coletado ou sai da tela, ele é simplesmente desativado, pronto para ser reutilizado.
- **Sprites**: Todos os sprites de itens estão em uma única imagem (`items.png`). O método `recortarSprite()` seleciona o sprite correto do spritesheet com base no `ItemType` do item.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `init(double x, double y)` | `void` | Inicializa (ou reseta) um item da pool, definindo sua posição inicial e ativando-o. |
| `setHero(Hero hero)` | `void` | Fornece ao item uma referência ao herói, necessária para o comportamento de atração. |
| `lancarItem(double anguloEmGraus, double forca)` | `void` | Dá ao item um impulso inicial, usado quando o herói morre e dropa seus power-ups. |
| `atualizar()` | `void` | Atualiza a posição do item a cada frame, aplicando a lógica de gravidade, lançamento ou atração. |
| `recortarSprite()` | `void` | Um método interno para carregar e recortar o sprite correto do spritesheet de itens. |