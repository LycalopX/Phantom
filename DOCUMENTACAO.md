# Documentação do Projeto Phantom

## Visão Geral

O projeto Phantom é um jogo 2D de tiro vertical (shoot'em up) desenvolvido em Java com Swing. O jogador controla um herói que deve atirar em inimigos, desviar de projéteis e coletar itens para aumentar seu poder, bombas e pontuação. O jogo é dividido em fases, cada uma com seus próprios padrões de inimigos.

A arquitetura do projeto segue um padrão semelhante ao MVC (Model-View-Controller):

*   **Modelo (`Modelo`):** Contém as classes que representam os dados e a lógica de negócios do jogo, como `Hero`, `Inimigo`, `Projetil` e `Item`.
*   **Visão (`Cenario`):** Responsável por desenhar todos os elementos do jogo na tela.
*   **Controlador (`Controler`):** Gerencia o fluxo do jogo, o input do jogador e as regras de colisão.

## Estrutura de Pacotes

O código-fonte está organizado nos seguintes pacotes dentro de `src/`:

*   `Main.java`: Ponto de entrada da aplicação.
*   `Auxiliar`: Classes utilitárias usadas em todo o projeto.
*   `Controler`: Classes que controlam a lógica e o fluxo do jogo.
*   `Modelo`: Classes que representam os objetos e as estruturas de dados do jogo.
*   `imgs`: Contém as imagens e sprites usados no jogo.

## Pacote `Controler`

Este pacote é o cérebro do jogo, orquestrando a interação entre o jogador, os inimigos e o ambiente.

### `Engine.java`

A classe principal do jogo.

*   Implementa `Runnable` para criar o loop principal do jogo (`game loop`).
*   Gerencia o estado do jogo (`GameState`: `JOGANDO`, `DEATHBOMB_WINDOW`, `RESPAWNANDO`, `GAME_OVER`).
*   Contém as instâncias dos principais componentes: `Fase`, `Hero`, `ControleDeJogo`, `ControladorDoHeroi` e `GerenciadorDeFases`.
*   Lida com a lógica de salvar, carregar e reiniciar o jogo.

### `ControleDeJogo.java`

Responsável pelas regras do jogo e pela detecção de colisões.

*   Usa uma `Quadtree` para otimizar a detecção de colisões entre os personagens.
*   Processa as interações entre os objetos do jogo (herói vs. inimigo, projétil vs. inimigo, etc.).
*   Lida com a lógica de drop de itens quando um inimigo é destruído.

### `ControladorDoHeroi.java`

Interpreta o input do teclado do jogador e o traduz em ações para o `Hero`.

*   Controla o movimento do herói, incluindo o modo de foco (movimento lento).
*   Aciona o disparo de projéteis e o uso de bombas.

### `Cenario.java`

A "tela" do jogo, responsável por desenhar todos os elementos visuais.

*   Estende `JPanel` e sobrescreve `paintComponent` para renderizar o jogo.
*   Desenha o fundo, os personagens, os projéteis e a interface (HUD).

### `GerenciadorDeFases.java`

Gerencia a progressão das fases do jogo.

*   Carrega a fase atual com base no nível.
*   Fornece a próxima fase quando a atual é concluída.

### `Tela.java`

A janela principal da aplicação.

*   Estende `JFrame` e contém o `Cenario`.

## Pacote `Modelo`

Este pacote contém as classes que representam os dados e a lógica de negócios do jogo.

### `Personagem.java`

Classe abstrata base para todos os objetos do jogo.

*   Define propriedades comuns como posição (`x`, `y`), `hitboxRaio`, imagem (`iImage`) e estado (`isActive`).
*   Contém o método abstrato `atualizar()`, que é implementado por cada tipo de personagem.

### `Hero.java`

Representa o personagem do jogador.

*   Gerencia os atributos do jogador: `HP`, `bombas`, `power` e `score`.
*   Lida com a lógica de dano, invencibilidade e morte.
*   Contém um `GerenciadorDeAnimacao` para as animações do herói e um `GerenciadorDeArmas` para os padrões de tiro.

### `Inimigo.java`

Representa um inimigo genérico.

*   Possui `vida` e uma `LootTable` que define quais itens podem ser dropados quando o inimigo é derrotado.

### `Projetil.java`

Representa um projétil.

*   Pode ser do tipo `JOGADOR` ou `INIMIGO`.
*   Possui `velocidade` e `anguloRad` para determinar sua trajetória.

### `Fase.java`

Representa o estágio atual do jogo.

*   Contém uma lista de todos os `Personagem` na fase.
*   Gerencia o `ProjetilPool` para reutilizar objetos de projéteis.
*   Delega a lógica de spawn de inimigos para um `ScriptDeFase`.

## Pacote `Auxiliar`

Contém classes e constantes de suporte.

### `Consts.java`

Define constantes globais usadas em todo o projeto, como dimensões da tela, velocidade dos personagens e teclas de controle.

### `Quadtree.java`

Implementa uma estrutura de dados de quadtree para otimizar a detecção de colisões, dividindo o espaço do jogo em quadrantes.

### `Desenho.java`

Classe utilitária com métodos para desenhar elementos na tela.

### `Posicao.java`

Representa uma posição (coordenadas x, y) no grid do jogo.

## Como Executar o Jogo

1.  Compile todos os arquivos `.java`.
2.  Execute a classe `Main`.

Controles:

*   **Setas Direcionais:** Mover o herói.
*   **Z:** Atirar.
*   **X:** Usar bomba.
*   **Shift:** Modo de foco (movimento lento).
*   **F3:** Ativar/Desativar modo de depuração (exibe hitboxes e informações).
*   **S:** Salvar o jogo.
*   **L:** Carregar o jogo.
*   **R:** Reiniciar o jogo (na tela de Game Over).
