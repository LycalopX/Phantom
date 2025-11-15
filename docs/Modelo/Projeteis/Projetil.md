# Projetil

`Projetil` é a classe base para todos os projéteis do jogo, tanto do jogador quanto dos inimigos. Ela herda de `Personagem`.

## Sistema de Pooling

Assim como os `Item`s, os `Projetil`s são gerenciados por um sistema de "pooling" (`ProjetilPool`) para otimização de desempenho. Em vez de criar novos objetos a cada tiro, projéteis inativos são reciclados.

O método `reset()` é fundamental para esse sistema. Ele pega um projétil inativo e o reconfigura com uma nova posição, velocidade, ângulo, tipo (jogador ou inimigo) e aparência (`ProjetilTipo`), ativando-o para um novo disparo.

## Movimento e Aparência

- **Movimento**: O movimento padrão de um projétil é linear. O método `atualizar()` simplesmente move o projétil com base em sua `velocidade` e `anguloRad` (ângulo em radianos).
- **Desenho**: O método `autoDesenho()` desenha o sprite do projétil. Para projéteis inimigos, ele aplica uma rotação para que o sprite aponte na direção do movimento.

## Hitbox

A classe suporta dois tipos de hitbox, definidos pelo `HitboxType`:
- **`CIRCULAR`**: A colisão é verificada usando o `hitboxRaio` padrão.
- **`RECTANGULAR`**: A colisão é verificada usando um retângulo delimitador (`getBounds()`). Isso é útil para projéteis com formas não circulares, como lasers.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `reset(...)` | `void` | Reconfigura um projétil da pool para um novo disparo com novos atributos. |
| `atualizar()` | `void` | Atualiza a posição do projétil com base em sua velocidade e ângulo. |
| `estaForaDaTela()` | `boolean` | Verifica se o projétil saiu dos limites da tela, marcando-o para desativação. |
| `getTipoHitbox()` | `HitboxType` | Retorna o tipo de hitbox do projétil (CIRCULAR ou RECTANGULAR). |
| `getBounds()` | `Rectangle` | Retorna o retângulo delimitador para colisões retangulares. |