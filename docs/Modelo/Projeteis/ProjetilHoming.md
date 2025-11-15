# ProjetilHoming

`ProjetilHoming` é uma subclasse de `Projetil` que implementa a lógica de um projétil teleguiado.

## Máquina de Estados

O comportamento do míssil é controlado por uma máquina de estados simples com dois estados:

1.  **`INERCIA`**: No início de sua vida, o projétil viaja em linha reta em sua direção inicial por um curto período (`TEMPO_INERCIA`). Isso evita que ele se vire bruscamente para um alvo assim que é disparado.
2.  **`PERSEGUINDO`**: Após o período de inércia, o projétil começa a perseguir um alvo.

## Lógica de Perseguição

- **Encontrar Alvo**: Se o projétil não tem um alvo (ou se seu alvo foi destruído), o método `encontrarAlvoMaisProximo()` varre a lista de personagens da fase para encontrar o `Inimigo` mais próximo.
- **Ajustar Ângulo**: Uma vez que um alvo é definido, o método `ajustarAnguloParaOAlvo()` calcula o ângulo ideal em direção ao alvo. Em vez de se virar instantaneamente, o ângulo do projétil é ajustado gradualmente a cada frame, controlado pela `taxaDeCurva`. Isso cria um movimento de perseguição suave e curvo, em vez de uma linha reta.

## Métodos Principais

| Método | Retorno | Descrição |
|---|---|---|
| `resetHoming(...)` | `void` | Reseta o estado do projétil, incluindo o temporizador de inércia e o alvo, para um novo disparo. |
| `atualizar()` | `void` | Gerencia a máquina de estados (Inércia -> Perseguindo) e chama a lógica de perseguição quando apropriado. |
| `ajustarAnguloParaOAlvo()` | `void` | Ajusta suavemente o ângulo do projétil em direção ao alvo atual. |
| `encontrarAlvoMaisProximo(List<Personagem> personagens)` | `void` | Procura e define o inimigo mais próximo como o novo alvo. |