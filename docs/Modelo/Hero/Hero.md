# Hero

A classe `Hero` estende `Personagem` e representa o avatar controlado pelo jogador. Ela encapsula todos os atributos e comportamentos específicos do herói.

## Atributos Principais

- **Recursos**: `HP` (pontos de vida), `bombas`, `power` (poder que melhora as armas) e `score` (pontuação).
- **Timers**: `invencibilidadeTimer` (para invencibilidade após ser atingido ou usar bomba) e `efeitoBombaTimer`.
- **Hitboxes**: Além da hitbox de dano padrão, possui uma `grabHitboxRaio` maior para coletar itens.
- **Modo Foco**: Um booleano `isFocoAtivo` que é ativado ao segurar a tecla Shift, reduzindo a velocidade do herói e exibindo sua hitbox.

## Componentes Delegados

A lógica do `Hero` é dividida em componentes para melhor organização:

- **`GerenciadorDeAnimacaoHeroi`**: Controla qual sprite do herói é exibido com base em seu estado de movimento (parado, movendo para a esquerda/direita).
- **`GerenciadorDeArmasHeroi`**: Gerencia a lógica de tiro, incluindo tipo de projétil, cadência e mísseis teleguiados, com base no nível de `power` atual.

## Máquina de Estados de Animação

O `Hero` possui uma máquina de estados interna (`HeroState`) para gerenciar as transições de animação de forma suave. Os estados incluem `IDLE`, `STRAFING_LEFT`, `STRAFING_RIGHT`, e os estados de transição `DE_STRAFING_LEFT` e `DE_STRAFING_RIGHT`, que garantem que a animação de "parada" seja executada antes de o herói voltar ao estado `IDLE`.

## Métodos Notáveis

| Método | Retorno | Descrição |
|---|---|---|
| `atualizarAnimacao(boolean isMovingLeft, boolean isMovingRight)` | `void` | Atualiza a máquina de estados da animação com base no input de movimento. |
| `usarBomba(Fase fase)` | `BombaProjetil` | Usa uma bomba (se disponível), gasta o recurso, ativa a invencibilidade e cria o objeto `BombaProjetil`. |
| `takeDamage()` | `boolean` | Processa o dano recebido. Retorna `true` se o dano foi efetivo (herói não estava invencível). |
| `processarMorte()` | `void` | Reduz o HP, zera o poder e desativa o herói. |
| `respawn()` | `void` | Reinicia o estado do herói após a morte, restaurando bombas, zerando poder e ativando a invencibilidade temporária. |
| `setFocoAtivo(boolean isFocoAtivo)` | `void` | Ativa ou desativa o modo de foco, controlando a animação da hitbox. |