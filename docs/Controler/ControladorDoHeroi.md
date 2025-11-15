# Classe `ControladorDoHeroi`

**Pacote:** `Controler`

## Descrição

Classe responsável por traduzir os inputs do teclado, recebidos da `Engine`, em ações específicas para o personagem `Hero`.

## Métodos Principais

### `ControladorDoHeroi(Engine engine)`
*   **@brief** Construtor do controlador.
*   **@param engine** Uma referência à instância principal da `Engine`.
*   **@details** Armazena a referência da `Engine` para poder interagir com o estado do jogo, como ao usar o cheat de pular de fase.

### `processarInput(Set<Integer> teclas, Hero heroi, Fase fase, ControleDeJogo cj)`
*   **@brief** O método central que traduz o input do jogador em ações do herói.
*   **@param teclas** O conjunto de `KeyCodes` das teclas atualmente pressionadas.
*   **@param heroi** A instância do `Hero` a ser controlada.
*   **@param fase** A instância da `Fase` atual, para interações como disparar projéteis.
*   **@param cj** A instância do `ControleDeJogo`, usada para validar a movimentação.
*   **@details** Este método é chamado a cada frame pela `Engine` e realiza as seguintes ações:
    1.  Calcula o vetor de movimento (`dx`, `dy`) com base nas teclas de direção.
    2.  Ajusta a velocidade se o modo "Foco" (Shift) estiver ativo.
    3.  Normaliza o movimento diagonal para manter a velocidade consistente.
    4.  Verifica os limites da tela para impedir que o herói saia da área de jogo.
    5.  Chama `cj.ehPosicaoValida()` para garantir que o movimento não colida com obstáculos.
    6.  Atualiza a animação do herói (esquerda/direita/parado).
    7.  Se a tecla de tiro estiver pressionada, chama o `GerenciadorDeArmasHeroi` para disparar.
    8.  Se a tecla de bomba for pressionada, e as condições forem válidas, utiliza uma bomba.
    9.  Contém a lógica para o cheat de pular de fase (Shift + F2).

