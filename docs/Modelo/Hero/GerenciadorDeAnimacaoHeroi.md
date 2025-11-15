# GerenciadorDeAnimacaoHeroi

Esta classe é um componente do `Hero` e tem a responsabilidade exclusiva de gerenciar suas animações visuais.

## Funcionalidades

- **Carregamento de Sprites**: No construtor, a classe carrega todas as imagens necessárias para as animações do herói a partir de spritesheets. Isso inclui animações para:
    - `Idle` (parado)
    - `Strafing` (movendo para os lados)
    - `Strafing Max` (movendo para os lados no frame final da animação de strafing)
- **Animação da Hitbox**: Também gerencia a animação da hitbox que aparece no modo "foco", incluindo sua rotação contínua e os efeitos de fade-in/fade-out.
- **Atualização de Frames**: O método `atualizar()` avança os contadores de frames das animações com base no estado atual do herói (`HeroState`), respeitando um `DELAY` para controlar a velocidade da animação.
- **Seleção de Imagem**: O método `getImagemAtual()` retorna o `ImageIcon` correto para ser desenhado pelo `Hero`, com base no estado e no frame atual da animação.

## Máquina de Estados

A lógica de qual animação tocar é controlada pela máquina de estados (`HeroState`) gerenciada pela classe `Hero`. O `GerenciadorDeAnimacaoHeroi` apenas responde a esse estado, atualizando seus contadores internos e fornecendo a imagem correta.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `atualizar(HeroState estado)` | `boolean` | Atualiza o frame da animação com base no estado do herói. Retorna `true` se uma animação de transição (como de-strafing) terminou. |
| `getImagemAtual(HeroState estado)` | `ImageIcon` | Retorna o `ImageIcon` do frame atual da animação com base no estado do herói. |
| `iniciarFadeInHitbox()` | `void` | Inicia o efeito de fade-in para a visualização da hitbox de foco. |
| `iniciarFadeOutHitbox()` | `void` | Inicia o efeito de fade-out para a visualização da hitbox de foco. |