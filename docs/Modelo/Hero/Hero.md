# Classe `Hero`

**Pacote:** `Modelo.Hero`

## Descrição

Representa o personagem principal do jogo, controlado pelo jogador. Herda da classe `Personagem` e gerencia seus próprios status, como HP, bombas, poder e pontuação.

## Métodos Principais

### `Hero(...)`
*   **@brief** Construtor que inicializa o herói, definindo sua hitbox, e instanciando seus gerenciadores de animação e armas.

### `atualizar()`
*   **@brief** Atualiza o estado interno do herói que não depende de input, como os timers de invencibilidade e de efeito da bomba.

### `atualizarAnimacao(...)`
*   **@brief** Atualiza a máquina de estados da animação do herói com base no input de movimento recebido do `ControladorDoHeroi`.

### `usarBomba(Fase fase)`
*   **@brief** Utiliza uma bomba (se disponível), criando o `BombaProjetil` e ativando os timers de invencibilidade e efeito da bomba.

### `takeDamage()` / `processarMorte()`
*   **@brief** Gerenciam a lógica de quando o herói recebe dano e quando sua vida chega a zero.

### `respawn()`
*   **@brief** Reinicia o estado do herói após a morte, restaurando suas bombas, zerando seu poder e concedendo invencibilidade temporária.
