# Classe `Projetil`

**Pacote:** `Modelo.Projeteis`

## Descrição

Classe base para todos os projéteis do jogo. Herda de `Personagem` e define o comportamento fundamental de um projétil, como movimento em linha reta e desenho com rotação.

## Métodos Principais

### `Projetil(...)`
*   **@brief** Construtor que inicializa um projétil como um objeto mortal e transponível.

### `reset(...)`
*   **@brief** Método crucial para o sistema de Object Pooling. Em vez de criar um novo projétil, este método é chamado para reconfigurar um projétil inativo da pool com uma nova posição, velocidade, ângulo e tipo.

### `atualizar()`
*   **@brief** Atualiza a posição do projétil a cada frame, movendo-o em linha reta com base em sua velocidade e ângulo.

### `autoDesenho(Graphics g)`
*   **@brief** Desenha o sprite do projétil na tela, aplicando uma transformação para rotacioná-lo de acordo com seu ângulo de movimento.
