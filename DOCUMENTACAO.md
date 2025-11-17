# Documentação do Projeto: Phantom

## 1. Visão Geral do Projeto

**Phantom** é um jogo 2D de tiro vertical do gênero *shoot 'em up* (ou *shmup*), com elementos de *bullet hell*. O jogador controla um herói que deve navegar por fases repletas de inimigos, desviar de uma grande quantidade de projéteis e derrotar chefes para progredir.

O jogo é construído em Java e utiliza uma estrutura de pacotes que separa a lógica de controle, o modelo de dados e as classes auxiliares, seguindo um padrão semelhante ao MVC (Model-View-Controller).

## 2. Estrutura do Código

O código-fonte está organizado principalmente nos seguintes pacotes dentro de `src/`:

-   `Controler`: Contém as classes que gerenciam a lógica principal do jogo, o estado e o fluxo de execução.
-   `Modelo`: Define as estruturas de dados para todos os objetos do jogo, como o herói, inimigos, projéteis e itens.
-   `Auxiliar`: Abriga classes de utilidade para configurações, gerenciamento de som, e outras funcionalidades de suporte.
-   `Assets`: Armazena todos os recursos visuais (sprites, planos de fundo) e de áudio.

### 2.1. Pacote `Controler` (O Cérebro do Jogo)

Este pacote é responsável pela execução do jogo.

-   **`Engine.java`**: É o coração do projeto, contendo o *game loop* principal. Esta classe inicializa a janela do jogo e gerencia os ciclos de atualização (`update`) e renderização (`draw`), garantindo que o jogo rode a uma taxa de quadros constante (definida em `Auxiliar/ConfigJogo.java`).
-   **`ControleDeJogo.java`**: Atua como o maestro do jogo. Ele gerencia o estado atual (menu, jogando, pausa, game over), processa os inputs do jogador através do `ControladorDoHeroi` e coordena as interações entre o herói, inimigos e o cenário.
-   **`GerenciadorDeFases.java`**: Responsável por carregar e gerenciar a progressão das fases do jogo. Ele utiliza "scripts" de fase para determinar quando e onde os inimigos devem aparecer.
-   **`ControladorDoHeroi.java`**: Traduz os inputs do teclado (configurados em `Auxiliar/ConfigTeclado.java`) em ações para o personagem do jogador, como movimento e disparo.

### 2.2. Pacote `Modelo` (As Entidades do Jogo)

Este pacote define a "aparência" e o "comportamento" de tudo que existe no jogo.

-   **`Personagem.java`**: Classe base abstrata para todas as entidades vivas do jogo (herói e inimigos). Define atributos comuns como posição (`x`, `y`), vida (`HP`), e métodos para movimentação e lógica de atualização.
-   **`Hero/Hero.java`**: Especialização de `Personagem` que representa o jogador. Contém lógicas específicas como o gerenciamento de armas e a resposta aos comandos do usuário.
-   **`Inimigos/Inimigo.java`**: Classe base para os adversários. As classes concretas (ex: `FadaComum`, `Boss`) herdam dela e implementam comportamentos de ataque e movimento específicos.
-   **`Projeteis/Projetil.java`**: Classe base para todos os projéteis. As definições de comportamento, dano e aparência dos projéteis são carregadas de um arquivo externo (`recursos/definicoes_projeteis.json`), permitindo fácil customização sem alterar o código Java. Existem especializações como `ProjetilHoming` (teleguiado).
-   **`Items/Item.java`**: Representa os itens que os inimigos podem dropar ao serem derrotados (ex: power-ups, pontos). A chance de drop é gerenciada pela classe `Auxiliar/LootTable.java`.

### 2.3. Pacote `Auxiliar` (Ferramentas de Suporte)

Este pacote contém classes que fornecem funcionalidades essenciais para o jogo.

-   **`ConfigJogo.java`**: Armazena constantes globais, como a resolução da tela, a taxa de quadros por segundo (FPS) e outras configurações de jogabilidade.
-   **`ConfigTeclado.java`**: Mapeia as teclas do teclado para as ações do jogo, permitindo que a configuração dos controles seja centralizada.
-   **`SoundManager.java`**: Gerencia a reprodução de músicas de fundo (OST) e efeitos sonoros (SFX), utilizando a biblioteca **TinySound**.
-   **`LootTable.java`**: Implementa um sistema de "tabela de loot" para determinar quais itens um inimigo dropará com base em probabilidades.

## 3. Recursos e Configuração

-   **Gráficos**: Todos os sprites de personagens, projéteis, itens e cenários estão localizados em `src/Assets/`. A estrutura de subpastas organiza os recursos por tipo (herói, inimigos, etc.).
-   **Áudio**: As músicas e efeitos sonoros estão em `src/OST/`, separados em `Music` e `SFX`.
-   **Definições de Projéteis**: O arquivo `recursos/definicoes_projeteis.json` é um ponto central para a customização de projéteis. Ele permite que desenvolvedores ou designers ajustem atributos como velocidade, dano, padrão de movimento e sprite de cada tipo de projétil sem precisar recompilar o código.

## 4. Bibliotecas Externas

O projeto utiliza as seguintes bibliotecas de terceiros, localizadas na pasta `lib/`:

-   **`tinysound.jar`**: Uma biblioteca leve para reprodução de áudio em Java. É a principal responsável pelo som no jogo, gerenciada através do `SoundManager`.
-   **`json-20250517.jar`**: Biblioteca para processar o formato JSON. É utilizada para ler e interpretar o arquivo de definições de projéteis.
-   **`jlayer-1.0.1.jar`, `mp3spi-1.9.5-1.jar`, `tritonus_share-0.3.6.jar`**: Conjunto de bibliotecas que adicionam suporte à reprodução de arquivos de áudio no formato MP3.

## 5. Como Compilar e Executar

O projeto utiliza o **Apache Ant** como ferramenta de build, com o script de configuração em `build.xml`. Para compilar e executar o projeto, os seguintes passos seriam necessários (assumindo que o Ant está instalado):

1.  **Compilar o código**: Executar o comando `ant compile` no terminal, na raiz do projeto.
2.  **Criar o JAR executável**: Executar o comando `ant jar`.
3.  **Executar o jogo**: Executar o arquivo JAR gerado na pasta `dist/` com o comando `java -jar dist/Phantom.jar`.

Este resumo abrange a arquitetura central, as principais funcionalidades e os componentes técnicos do projeto Phantom, fornecendo uma base sólida para entender seu funcionamento.