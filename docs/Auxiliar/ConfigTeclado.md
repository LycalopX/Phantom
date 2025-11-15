# ConfigTeclado

A classe `ConfigTeclado` centraliza todas as configurações de mapeamento de teclas do jogo. Como uma classe final com um construtor privado, ela não pode ser instanciada, servindo apenas como um contêiner estático para constantes.

Isso facilita a manutenção e a alteração dos controles do jogo, pois todas as teclas estão definidas em um único local.

## Constantes de Teclado

| Constante | Valor (KeyEvent) | Descrição |
|---|---|---|
| `KEY_UP` | `KeyEvent.VK_W` | Tecla para movimentar o herói para cima. |
| `KEY_DOWN` | `KeyEvent.VK_S` | Tecla para movimentar o herói para baixo. |
| `KEY_LEFT` | `KeyEvent.VK_A` | Tecla para movimentar o herói para a esquerda. |
| `KEY_RIGHT`| `KeyEvent.VK_D` | Tecla para movimentar o herói para a direita. |
| `KEY_SHOOT`| `KeyEvent.VK_K` | Tecla para o herói disparar. |
| `KEY_BOMB` | `KeyEvent.VK_L` | Tecla para o herói usar uma bomba. |
| `KEY_SAVE` | `KeyEvent.VK_P` | Tecla para salvar o estado do jogo. |
| `KEY_LOAD` | `KeyEvent.VK_R` | Tecla para carregar o estado do jogo. |
| `KEY_RESTART`| `KeyEvent.VK_R` | Tecla para reiniciar o jogo (na tela de Game Over). |