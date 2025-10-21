# Classe `ControladorDoHeroi`

**Pacote:** `Controler`

## Descrição

Classe responsável por traduzir os inputs do teclado, recebidos da `Engine`, em ações específicas para o personagem `Hero`.

## Métodos Principais

### `ControladorDoHeroi(Engine engine)`
*   **@brief** Construtor que armazena uma referência à `Engine` para acessar o estado do jogo.

### `processarInput(...)`
*   **@brief** Processa o conjunto de teclas pressionadas para controlar todas as ações do herói, incluindo:
    *   Movimento em 8 direções.
    *   Ativação do modo "Foco" (movimento lento).
    *   Disparo de projéteis.
    *   Uso de bombas.
    *   Atualização do estado da animação do herói.
