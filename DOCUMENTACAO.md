# Documentação do Projeto Phantom

## Visão Geral

O Projeto Phantom é um jogo de tiro 2D vertical no estilo "bullet hell", inspirado em jogos como a série Touhou Project. O jogador controla um personagem que deve desviar de uma grande quantidade de projéteis inimigos enquanto tenta derrotá-los.

## Arquitetura

O projeto segue uma arquitetura baseada no padrão Model-View-Controller (MVC):

*   **Model**: Representa os dados e a lógica de negócio do jogo. Inclui as classes para o herói, inimigos, itens, projéteis e as fases do jogo. A lógica de como cada entidade se comporta está aqui.
*   **View**: É a representação visual do modelo. A classe `Cenario` é a principal responsável por desenhar o estado atual do jogo na tela.
*   **Controller**: Atua como intermediário entre o Model e a View. Ele processa a entrada do usuário (teclado), atualiza o estado do jogo e comanda a View para se redesenhar. As classes `Engine`, `ControleDeJogo` e `ControladorDoHeroi` compõem esta camada.

## Estrutura de Pacotes

O código-fonte está organizado nos seguintes pacotes dentro de `src/`:

*   `Main.java`: Ponto de entrada da aplicação.
*   `Auxiliar`: Classes utilitárias e de suporte.
*   `Controler`: Classes que controlam o fluxo do jogo.
*   `Modelo`: Classes que representam os dados e a lógica do jogo.

### Pacote `Auxiliar`

Contém classes que fornecem funcionalidades de suporte para o resto do projeto.

*   `Consts`: Armazena constantes globais do jogo, como dimensões da tela, velocidade dos personagens e códigos de teclas.
*   `Posicao`: Uma classe simples para representar coordenadas (linha, coluna) no grid do jogo.
*   `SoundManager`: Gerencia o carregamento e a reprodução de efeitos sonoros (WAV) e música (MP3).
*   `Quadtree`: Uma estrutura de dados para otimizar a detecção de colisões, dividindo o espaço do jogo em quadrantes.
*   `ContadorFPS`: Calcula e exibe a taxa de quadros por segundo (FPS).
*   `DebugManager`: Controla a exibição de informações de debug, como hitboxes.
*   `LootTable` e `LootItem`: Gerenciam o sistema de drops de itens quando um inimigo é derrotado.
*   `TipoProjetil`: Enum que diferencia projéteis do jogador e de inimigos.
*   `ArvoreParallax` e `BlocoDeFolha`: Controlam o efeito de parallax do cenário de fundo.

### Pacote `Controler`

Responsável pela lógica principal e pelo fluxo do jogo.

*   `Engine`: O coração do jogo. Contém o loop principal (`run`), gerencia o estado do jogo (jogando, game over, etc.) e coordena as atualizações do modelo e da visão.
*   `ControleDeJogo`: Processa a lógica de colisões entre os personagens usando a `Quadtree` e aplica as regras do jogo.
*   `ControladorDoHeroi`: Interpreta o input do teclado do jogador e comanda o objeto `Hero`.
*   `Tela`: A janela principal do jogo (`JFrame`).
*   `Cenario`: O painel (`JPanel`) onde o jogo é desenhado. É a principal classe da camada View.
*   `GerenciadorDeFases`: Controla a transição entre as fases do jogo.

### Pacote `Modelo`

Contém as classes que representam as entidades do jogo.

*   `Personagem`: Classe abstrata base para todas as entidades que se movem e interagem no jogo (herói, inimigos, itens, projéteis).

#### Subpacote `Modelo.Hero`

*   `Hero`: Representa o personagem do jogador. Gerencia seus status (HP, bombas, poder), movimento e animação.
*   `GerenciadorDeAnimacao`: Controla as animações do herói com base em seu estado (parado, movendo para os lados).
*   `GerenciadorDeArmas`: Gerencia a lógica de tiro do herói, incluindo diferentes tipos de armas e níveis de poder.
*   `HeroState`: Enum que define os possíveis estados de animação do herói.

#### Subpacote `Modelo.Inimigos`

*   `Inimigo`: Representa um personagem inimigo. Possui lógica para movimento, vida e drop de itens.

#### Subpacote `Modelo.Items`

*   `Item`: Representa um item coletável no jogo (power-up, bomba, etc.).
*   `ItemType`: Enum que define os diferentes tipos de itens e suas propriedades.

#### Subpacote `Modelo.Projeteis`

*   `Projetil`: Classe base para todos os projéteis. Define movimento e comportamento básico.
*   `ProjetilHoming`: Um projétil que persegue um alvo.
*   `ProjetilBombaHoming`: Um tipo especial de projétil teleguiado criado pela bomba do herói.
*   `BombaProjetil`: Representa a bomba do herói, que causa dano em área.
*   `ProjetilPool`: Otimiza o uso de memória reutilizando instâncias de projéteis em vez de criar e destruir objetos constantemente.

#### Subpacote `Modelo.Fases`

*   `Fase`: Representa o estado de uma fase do jogo, contendo a lista de personagens, o cenário e o script da fase.
*   `ScriptDeFase`: Classe abstrata que define a interface para os scripts de fase. Cada fase tem seu próprio script que dita o comportamento dos inimigos e do cenário.
*   `ScriptFase1`, `ScriptFase2`, etc.: Implementações concretas de `ScriptDeFase` para cada fase do jogo.