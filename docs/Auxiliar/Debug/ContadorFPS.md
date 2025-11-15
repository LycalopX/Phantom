# ContadorFPS

A classe `ContadorFPS` é um componente de depuração usado para medir e exibir a taxa de quadros por segundo (Frames Per Second) do jogo.

## Funcionamento

A cada frame do jogo, o método `atualizar()` é chamado. Ele incrementa um contador de frames. A classe também armazena o tempo da última atualização de FPS. Se a diferença entre o tempo atual e o último tempo armazenado for maior ou igual a um segundo, o valor do contador de frames é copiado para a variável `fpsExibido`, o contador é zerado e o tempo da última atualização é renovado.

O método `getFPSString()` pode então ser chamado a qualquer momento para obter uma string formatada, como "FPS: 60", para ser desenhada na tela.

## Métodos Públicos

| Método | Retorno | Descrição |
|---|---|---|
| `atualizar()` | `void` | Atualiza o contador de frames. Se um segundo passou, calcula o novo valor de FPS a ser exibido. |
| `getFPSString()` | `String` | Retorna o último valor de FPS calculado, formatado como uma string para exibição. |
