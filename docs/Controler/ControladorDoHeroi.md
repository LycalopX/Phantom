# ControladorDoHeroi

Esta classe atua como um tradutor entre o input bruto do teclado e as ações específicas do `Hero`. Sua única responsabilidade é processar o conjunto de teclas pressionadas a cada frame e chamar os métodos apropriados no objeto `Hero`.

## Lógica de Controle

- **Movimento**: Calcula o vetor de movimento (`dx`, `dy`) com base nas teclas de direção. Normaliza o vetor para movimento diagonal e aplica uma redução de velocidade se a tecla de foco (Shift) estiver pressionada.
- **Limites da Tela**: Garante que o herói não possa se mover para fora dos limites da área de jogo.
- **Animação**: Informa ao `Hero` se ele está se movendo para a esquerda ou direita para que ele possa atualizar sua animação de "strafing".
- **Ações**: Verifica as teclas de tiro e bomba e, se pressionadas, aciona os sistemas de armas e bombas do herói.
- **Cheats**: Inclui a lógica para cheats, como pular de fase.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `processarInput(Set<Integer> teclas, Hero heroi, Fase fase, ControleDeJogo cj)` | `void` | Processa o input do teclado para controlar o movimento, animação, tiro e bombas do herói. |