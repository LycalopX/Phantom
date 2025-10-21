# Classe `ProjetilBombaHoming`

**Pacote:** `Modelo.Projeteis`

## Descrição

Subclasse de `ProjetilHoming` que representa os mísseis especiais lançados pela bomba do herói. Este projétil possui um comportamento de três fases.

## Máquina de Estados

1.  **`EXPANDINDO`**: O projétil é lançado para fora a partir do herói em um padrão circular e se move em linha reta por um curto período.
2.  **`PERSEGUINDO`**: Após a fase de expansão, o projétil ativa a lógica de perseguição herdada de `ProjetilHoming` e começa a caçar o inimigo mais próximo.
3.  **Fim de Vida**: O projétil tem um tempo de vida limitado e se desativa após um certo período, independentemente de ter atingido um alvo.

## Métodos Principais

### `resetBombaHoming(...)`
*   **@brief** Reseta o projétil para um novo disparo, definindo seu estado inicial como `EXPANDINDO` e reiniciando seus timers.

### `atualizar()`
*   **@brief** Implementa a lógica da máquina de estados e o timer de vida do projétil.

### `autoDesenho(Graphics g)`
*   **@brief** Desenha o projétil com um efeito visual customizado, incluindo um rastro e uma animação de pulsação.
