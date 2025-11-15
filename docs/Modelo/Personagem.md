# Personagem

`Personagem` é uma classe abstrata fundamental que serve como base para todas as entidades ativas no jogo, como o herói, inimigos, projéteis e itens.

## Propriedades Comuns

Ela define um conjunto de propriedades e comportamentos que são compartilhados por todos os "personagens" do jogo:

- **Posição e Tamanho**: Coordenadas `x`, `y` no grid do jogo, e `largura`, `altura` em pixels.
- **Sprite**: Uma `ImageIcon` para a representação visual. A classe lida com o carregamento do arquivo de imagem.
- **Hitbox**: Um `hitboxRaio` que define a área de colisão circular padrão.
- **Estado**: Um booleano `isActive` para controlar se o personagem deve ser processado e desenhado. Isso é crucial para o sistema de "pooling" de objetos.
- **Atributos de Jogo**: Propriedades como `vida`, `bTransponivel` (se pode ser atravessado) e `bMortal` (se pode ser destruído).

## Métodos Abstratos

A classe `Personagem` força suas subclasses a implementar dois métodos essenciais:

- **`atualizar()`**: Define a lógica que o personagem executa a cada frame (movimento, ataque, etc.).
- **`getRenderLayer()`**: Retorna um `RenderLayer`, que é usado para ordenar o desenho dos personagens na tela (Z-ordering), garantindo que o jogador seja desenhado sobre os inimigos, por exemplo.

## Serialização

A classe implementa `Serializable`, permitindo que o estado dos personagens (e, por consequência, o estado da fase) seja salvo e carregado. O `readObject` customizado garante que a `ImageIcon` (marcada como `transient`) seja recarregada após a desserialização.