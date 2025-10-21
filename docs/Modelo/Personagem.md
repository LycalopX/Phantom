# Classe `Personagem`

**Pacote:** `Modelo`

## Descrição

Classe abstrata que serve como base para todas as entidades do jogo (Herói, Inimigos, Itens, Projéteis). Define propriedades e comportamentos comuns, como posição, imagem, hitbox e estado (ativo/inativo).

## Métodos Principais

### `Personagem(...)`
*   **@brief** Construtores que inicializam as propriedades do personagem. A versão automática calcula o tamanho e a hitbox com base nas dimensões da imagem e em uma proporção global.

### `atualizar()`
*   **@brief** Método abstrato que deve ser implementado por todas as subclasses para definir seu comportamento a cada frame.

### `autoDesenho(Graphics g)`
*   **@brief** Desenha a hitbox de debug do personagem se o modo de debug estiver ativo.

### `activate()` / `deactivate()`
*   **@brief** Métodos para ativar ou desativar o personagem, controlando se ele deve ser processado e renderizado no jogo.
