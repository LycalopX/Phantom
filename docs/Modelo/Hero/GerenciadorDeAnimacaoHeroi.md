# Classe `GerenciadorDeAnimacaoHeroi`

**Pacote:** `Modelo.Hero`

## Descrição

Gerencia as animações do herói, incluindo a transição entre diferentes estados de movimento e a animação da hitbox de foco.

## Métodos Principais

### `GerenciadorDeAnimacaoHeroi(...)`
*   **@brief** Construtor que carrega todos os sprites e spritesheets necessários para as animações do herói.

### `atualizar(HeroState estado)`
*   **@brief** Atualiza o frame de animação atual com base no estado do herói (parado, movendo, etc.). Também gerencia a animação de rotação e o efeito de fade da hitbox de foco.

### `getImagemAtual(HeroState estado)`
*   **@brief** Retorna o `ImageIcon` do frame de animação correto para o estado atual do herói.

### `iniciarFadeInHitbox()` / `iniciarFadeOutHitbox()`
*   **@brief** Controlam o início do efeito de fade-in e fade-out para a visualização da hitbox quando o modo de foco é ativado ou desativado.
