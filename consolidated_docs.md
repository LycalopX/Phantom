--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/Cenario1/ArvoreParallax.md ---

# Classe `ArvoreParallax`

**Pacote:** `Auxiliar.Cenario1`

## Descrição

Cria e gerencia um grupo de `BlocoDeFolha` para simular uma árvore com efeito de parallax. Cada "bloco" da árvore se move a uma velocidade ligeiramente diferente para criar uma ilusão de profundidade no cenário.

## Métodos Principais

### `ArvoreParallax(...)`
*   **@brief** Construtor que cria uma árvore composta por três blocos de folhas com diferentes tamanhos, posições e velocidades.

### `mover(double velocidadeAtualDoFundo)`
*   **@brief** Move a árvore verticalmente, ajustando a velocidade de cada bloco com base na velocidade de rolagem do fundo para manter o efeito de parallax.

### `desenhar(Graphics2D g2d, int alturaDaTela)`
*   **@brief** Desenha cada bloco da árvore na tela.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/ConfigMapa.md ---

# Classe `ConfigMapa`

**Pacote:** `Auxiliar`

## Descrição

Classe `final` que centraliza as constantes globais relacionadas às dimensões do jogo, como o tamanho da tela e do mundo do jogo, e o tamanho da célula do grid.

## Constantes Principais

*   **`LARGURA_TELA` / `ALTURA_TELA`**: Dimensões da janela do jogo em pixels.
*   **`MUNDO_LARGURA` / `MUNDO_ALTURA`**: Dimensões do mundo do jogo em unidades de grid.
*   **`CELL_SIDE`**: O tamanho de uma única célula do grid em pixels, usado para converter entre coordenadas de grid e coordenadas de pixel.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/ConfigTeclado.md ---

# Classe `ConfigTeclado`

**Pacote:** `Auxiliar`

## Descrição

Classe `final` que centraliza as constantes de configuração do teclado, mapeando as ações do jogo para códigos de tecla específicos (`KeyEvent`).

## Constantes Principais

*   **`KEY_UP`, `KEY_DOWN`, `KEY_LEFT`, `KEY_RIGHT`**: Teclas de movimentação do jogador.
*   **`KEY_SHOOT`**: Tecla de disparo.
*   **`KEY_BOMB`**: Tecla para usar a bomba.
*   **`KEY_SAVE` / `KEY_LOAD` / `KEY_RESTART`**: Teclas para ações do jogo, como salvar, carregar e reiniciar.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/Debug/ContadorFPS.md ---

# Classe `ContadorFPS`

**Pacote:** `Auxiliar.Debug`

## Descrição

Uma classe utilitária para calcular e formatar a taxa de quadros por segundo (FPS) do jogo.

## Métodos Principais

### `ContadorFPS()`
*   **@brief** Construtor que inicializa as variáveis de controle de tempo e contagem de frames.

### `atualizar()`
*   **@brief** Incrementa o contador de frames a cada chamada. Se um segundo tiver passado desde a última atualização, ele atualiza o valor de `fpsExibido` e reseta o contador.

### `getFPSString()`
*   **@brief** Retorna o último valor de FPS calculado, formatado como uma `String` (e.g., "FPS: 60").


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/Debug/DebugManager.md ---

# Classe `DebugManager`

**Pacote:** `Auxiliar.Debug`

## Descrição

Gerencia o estado global do modo de depuração (debug) do jogo através de métodos estáticos.

## Métodos Principais

### `toggle()`
*   **@brief** Alterna o estado do modo de depuração (ativo/inativo) e imprime o novo estado no console.

### `isActive()`
*   **@brief** Método estático que permite a qualquer classe do jogo verificar se o modo de depuração está atualmente ativo.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/LootTable.md ---

# Classe `LootTable`

**Pacote:** `Auxiliar`

## Descrição

Representa uma tabela de loot que pode ser associada a um inimigo. Ela contém uma lista de possíveis `LootItem` que podem ser "dropados" quando o inimigo é derrotado.

## Métodos Principais

### `LootTable()`
*   **@brief** Construtor que inicializa a lista de itens da tabela.

### `addItem(LootItem item)`
*   **@brief** Adiciona um novo item possível (`LootItem`) à tabela de loot.

### `gerarDrops()`
*   **@brief** Processa a tabela de loot. Para cada item, ele "rola um dado" com base na probabilidade do item. Se o item for sorteado, ele gera uma quantidade aleatória (entre o mínimo e o máximo definidos no `LootItem`) e retorna uma lista com os itens que foram efetivamente dropados.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/Personagem/Quadtree.md ---

# Classe `Quadtree`

**Pacote:** `Auxiliar.Personagem`

## Descrição

Implementa uma estrutura de dados Quadtree para otimizar a detecção de colisão espacial. A Quadtree divide o espaço de jogo em quadrantes recursivamente, permitindo que as verificações de colisão sejam feitas apenas entre objetos que estão próximos uns dos outros.

## Métodos Principais

### `Quadtree(int pLevel, Rectangle pBounds)`
*   **@brief** Construtor que cria um nó da Quadtree para uma determinada área (`pBounds`) e nível de profundidade (`pLevel`).

### `clear()`
*   **@brief** Limpa recursivamente a árvore, removendo todos os objetos e sub-nós.

### `insert(Personagem p)`
*   **@brief** Insere um personagem na árvore. Se um nó atinge sua capacidade máxima de objetos, ele se subdivide em quatro nós filhos e distribui seus objetos entre eles.

### `retrieve(ArrayList<Personagem> returnObjects, Personagem p)`
*   **@brief** Retorna uma lista de personagens que estão no mesmo quadrante (ou quadrantes sobrepostos) que o personagem `p`, fornecendo uma lista de candidatos potenciais para a verificação de colisão.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Auxiliar/SoundManager.md ---

# Classe `SoundManager`

**Pacote:** `Auxiliar`

## Descrição

Implementa um Singleton para gerenciar o carregamento e a reprodução de todos os recursos de áudio do jogo, utilizando a biblioteca `TinySound`.

## Métodos Principais

### `init()`
*   **@brief** Método estático que inicializa o `TinySound` e a instância do `SoundManager`, e pré-carrega todos os arquivos de som e música definidos.

### `getInstance()`
*   **@brief** Retorna a instância única (Singleton) do `SoundManager`.

### `playSfx(String name, double volume)`
*   **@brief** Reproduz um efeito sonoro (SFX) a partir do mapa de sons pré-carregados.

### `playMusic(String name, boolean loop)`
*   **@brief** Para todas as outras músicas e reproduz uma nova música a partir do mapa de músicas pré-carregadas.

### `setSfxVolume(float volume)` / `setMusicVolume(float volume)`
*   **@brief** Ajustam o volume global para efeitos sonoros e músicas, respectivamente.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Controler/Cenario.md ---

# Classe `Cenario`

**Pacote:** `Controler`

## Descrição

Principal componente da camada de Visualização (View). Herda de `JPanel` e é responsável por desenhar todos os elementos visuais do jogo na tela.

## Métodos Principais

### `Cenario()`
*   **@brief** Construtor que configura as propriedades do painel, como tamanho e cor de fundo, e inicializa a funcionalidade de drag-and-drop para debug.

### `paintComponent(Graphics g)`
*   **@brief** Método central de desenho do Swing, sobreposto para renderizar o estado atual do jogo. Ele desenha o fundo, os personagens, projéteis, HUD e a tela de Game Over, dependendo do estado do jogo. A ordem de desenho é gerenciada para criar uma sensação de profundidade (e.g., projéteis do jogador na frente dos inimigos).

### `setupDropTarget()`
*   **@brief** Configura a funcionalidade de arrastar e soltar (drag-and-drop) que permite adicionar inimigos (a partir de arquivos `.zip`) dinamicamente ao jogo quando o modo de debug está ativo.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Controler/ControladorDoHeroi.md ---

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


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Controler/ControleDeJogo.md ---

# Classe `ControleDeJogo`

**Pacote:** `Controler`

## Descrição

Orquestra a lógica principal do jogo, incluindo detecção de colisão, interações entre personagens e gerenciamento de estado dos objetos da fase.

## Detalhes da Implementação

Para otimizar a detecção de colisão, esta classe utiliza uma **Quadtree**. A cada frame, os personagens são inseridos na Quadtree. Em vez de verificar cada personagem contra todos os outros, a classe `retrieve` da Quadtree é usada para obter apenas uma lista de vizinhos próximos para cada personagem, reduzindo significativamente o número de comparações necessárias.

## Métodos Principais

### `ControleDeJogo()`
*   **@brief** Construtor que inicializa a Quadtree e as listas de apoio para o processamento de objetos.

### `processaTudo(...)`
*   **@brief** O método central da classe. Processa todas as interações e colisões para um frame do jogo, populando a Quadtree, verificando colisões, lidando com a lógica da bomba e limpando os personagens inativos.

### `ehPosicaoValida(...)`
*   **@brief** Verifica se uma nova posição para um personagem é válida, checando por colisões com outros personagens não transponíveis.

### `handleCollision(...)`
*   **@brief** Gerencia a lógica de interação para um par de personagens que colidiram (e.g., Herói vs. Inimigo, Projétil vs. Inimigo, Herói vs. Item).


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Controler/Engine.md ---

# Classe `Engine`

**Pacote:** `Controler`

## Descrição

Classe principal do motor do jogo, responsável pelo loop principal, gerenciamento de estado e coordenação entre os componentes do modelo, visão e controle.

## Métodos Principais

### `Engine()`
*   **@brief** Construtor que inicializa todos os componentes do jogo (controladores, fases, herói, UI).

### `run()`
*   **@brief** Implementa o loop principal do jogo, controlando a taxa de atualização (FPS) e renderização.

### `atualizar()`
*   **@brief** Gerencia a máquina de estados do jogo (`JOGANDO`, `DEATHBOMB_WINDOW`, `RESPAWNANDO`, `GAME_OVER`) e atualiza a lógica a cada frame.

### `configurarTeclado()`
*   **@brief** Configura os listeners de teclado para capturar os inputs do jogador e funções de debug.

### `salvarJogo()` / `carregarJogo()`
*   **@brief** Métodos para serializar (salvar) e desserializar (carregar) o estado da fase atual.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Controler/GerenciadorDeFases.md ---

# Classe `GerenciadorDeFases`

**Pacote:** `Controler`

## Descrição

Controla a progressão das fases do jogo, gerenciando o nível atual e carregando a fase correspondente quando solicitado.

## Métodos Principais

### `carregarFase()`
*   **@brief** Carrega e retorna a fase (`Fase`) correspondente ao nível atual. Utiliza um `switch` para instanciar o `ScriptDeFase` correto para o nível.

### `proximaFase()`
*   **@brief** Avança para o próximo nível e carrega a nova fase. Se o jogo atingir a última fase, ele volta para a primeira.

### `resetar()`
*   **@brief** Reseta o progresso para o nível 1.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Controler/Tela.md ---

# Classe `Tela`

**Pacote:** `Controler`

## Descrição

Representa a janela principal do jogo (`JFrame`). Sua única responsabilidade é servir como um contêiner para o painel `Cenario`, onde o jogo é de fato desenhado.

## Métodos Principais

### `Tela()`
*   **@brief** Construtor que configura as propriedades básicas da janela, como título, operação de fechamento e a desativação do redimensionamento.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Fases/Fase.md ---

# Classe `Fase`

**Pacote:** `Modelo.Fases`

## Descrição

Representa um contêiner para uma fase (ou nível) do jogo. Ela armazena todos os personagens, elementos de cenário e o estado de rolagem da tela. A lógica de eventos e spawning de inimigos é delegada a um `ScriptDeFase`.

## Métodos Principais

### `Fase(ScriptDeFase script)`
*   **@brief** Construtor que inicializa a fase com um script específico. Ele também cria o `ProjetilPool` e pré-carrega os recursos visuais.

### `atualizar(double velocidadeScroll)`
*   **@brief** Atualiza o estado da fase a cada frame. Isso inclui a rolagem do cenário, a execução do `ScriptDeFase` para spawning, e a chamada do método `atualizar()` de cada personagem na fase.

### `adicionarPersonagem(Personagem p)`
*   **@brief** Adiciona um novo personagem à lista de personagens da fase.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Fases/ScriptDeFase.md ---

# Classe `ScriptDeFase`

**Pacote:** `Modelo.Fases`

## Descrição

Classe abstrata que define o contrato para os scripts de fase. Cada fase do jogo possui uma implementação concreta desta classe, que dita o comportamento dos inimigos e do cenário, permitindo um design de nível único para cada fase.

## Métodos Principais

### `atualizarInimigos(Fase fase)`
*   **@brief** Método abstrato que deve ser implementado por subclasses para controlar a lógica de spawning de inimigos.

### `atualizarCenario(Fase fase, double velocidadeScroll)`
*   **@brief** Método que pode ser sobreposto para controlar o spawning de elementos de cenário, como as árvores de parallax.

### `preencherCenarioInicial(Fase fase)`
*   **@brief** Método que pode ser sobreposto para preencher o cenário com elementos iniciais antes do início da fase.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Hero/GerenciadorDeAnimacaoHeroi.md ---

# Classe `GerenciadorDeAnimacaoHeroi`

**Pacote:** `Modelo.Hero`

## Descrição

Gerencia as animações do herói, incluindo a transição entre diferentes estados de movimento e a animação da hitbox de foco.

## Métodos Principais

### `GerenciadorDeAnimacaoHeroi(...)`
*   **@brief** Construtor que carrega todos os sprites e spritesheets necessários para as animações do herói.

### `atualizar(HeroState estado)`
*   **@brief** Atualiza o frame de animação atual com base no estado do herói (parado, movendo, etc.). Também gerencia a animação de rotação e o efeito de fade da hitbox de foco.

### `getImagemAtual(HeroState estado)`
*   **@brief** Retorna o `ImageIcon` do frame de animação correto para o estado atual do herói.

### `iniciarFadeInHitbox()` / `iniciarFadeOutHitbox()`
*   **@brief** Controlam o início do efeito de fade-in e fade-out para a visualização da hitbox quando o modo de foco é ativado ou desativado.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Hero/GerenciadorDeArmasHeroi.md ---

# Classe `GerenciadorDeArmasHeroi`

**Pacote:** `Modelo.Hero`

## Descrição

Gerencia os sistemas de armas do herói, controlando a lógica de tiro, os tipos de projéteis e os cooldowns com base no nível de poder do jogador.

## Métodos Principais

### `atualizarTimers()`
*   **@brief** Decrementa os contadores de cooldown para o tiro principal e os mísseis a cada frame.

### `disparar(...)`
*   **@brief** Executa a lógica de disparo do herói. Com base no nível de poder, ele determina o número de projéteis, a cadência de tiro e se os mísseis teleguiados devem ser disparados.

### `getNivelTiro(int power)`
*   **@brief** Calcula o nível de tiro atual do herói com base em uma fórmula que leva em conta o `power` acumulado.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Hero/Hero.md ---

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


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Inimigos/FadaComum.md ---

# Classe `FadaComum`

**Pacote:** `Modelo.Inimigos`

## Descrição

Implementação de um inimigo comum do tipo "fada". Esta classe possui uma máquina de estados interna simples para controlar seu comportamento.

## Máquina de Estados

O comportamento da fada é dividido em três estados:

1.  **`ENTERING`**: A fada entra na tela com um movimento senoidal.
2.  **`SHOOTING`**: A fada para em uma posição e dispara projéteis em direção ao herói por um determinado período.
3.  **`EXITING`**: Após o período de tiro, a fada se move para fora da tela e é desativada.

## Métodos Principais

### `FadaComum(...)`
*   **@brief** Construtor que inicializa a fada, definindo seu estado inicial como `ENTERING`.

### `atualizar()`
*   **@brief** Implementa a lógica da máquina de estados, atualizando o movimento e o comportamento de tiro da fada com base em seu estado atual.

### `atirar()`
*   **@brief** Cria e dispara um projétil em direção à posição atual do herói.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Inimigos/Inimigo.md ---

# Classe `Inimigo`

**Pacote:** `Modelo.Inimigos`

## Descrição

Classe abstrata que serve como base para todos os inimigos do jogo. Herda de `Personagem` e adiciona propriedades específicas de inimigos, como vida e uma tabela de loot.

## Métodos Principais

### `Inimigo(...)`
*   **@brief** Construtores que inicializam as propriedades do inimigo, como sua vida e `LootTable`.

### `atualizar()`
*   **@brief** Define o comportamento padrão de movimento do inimigo. Este método é frequentemente sobreposto por subclasses para criar padrões de movimento mais complexos.

### `takeDamage(double damage)`
*   **@brief** Aplica dano ao inimigo. Se a vida do inimigo chegar a zero, ele é desativado (marcado para remoção) e um som de destruição é tocado.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Items/Item.md ---

# Classe `Item`

**Pacote:** `Modelo.Items`

## Descrição

Representa um item coletável no jogo, como power-ups e bombas. Herda de `Personagem` e possui sua própria lógica de movimento e física.

## Comportamento

Um item pode ter dois comportamentos principais:

1.  **Queda Livre**: Por padrão, o item cai com uma leve aceleração (gravidade).
2.  **Atração**: Se o jogador estiver no topo da tela ou se uma bomba estiver ativa, todos os itens na tela são atraídos em direção ao herói, facilitando a coleta.

## Métodos Principais

### `Item(...)`
*   **@brief** Construtor que define o tipo do item e recorta seu sprite específico a partir de um spritesheet compartilhado.

### `atualizar()`
*   **@brief** Atualiza a posição do item a cada frame, aplicando a lógica de gravidade ou de atração em direção ao herói.

### `lancarItem(...)`
*   **@brief** Dá ao item um impulso inicial em uma direção específica. Usado quando o herói morre e seus power-ups são espalhados.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Personagem.md ---

# Classe `Personagem`

**Pacote:** `Modelo`

## Descrição

Classe abstrata que serve como base para todas as entidades do jogo (Herói, Inimigos, Itens, Projéteis). Define propriedades e comportamentos comuns, como posição, imagem, hitbox e estado (ativo/inativo).

## Métodos Principais

### `Personagem(...)`
*   **@brief** Construtores que inicializam as propriedades do personagem. A versão automática calcula o tamanho e a hitbox com base nas dimensões da imagem e em uma proporção global.

### `atualizar()`
*   **@brief** Método abstrato que deve ser implementado por todas as subclasses para definir seu comportamento a cada frame.

### `autoDesenho(Graphics g)`
*   **@brief** Desenha a hitbox de debug do personagem se o modo de debug estiver ativo.

### `activate()` / `deactivate()`
*   **@brief** Métodos para ativar ou desativar o personagem, controlando se ele deve ser processado e renderizado no jogo.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Projeteis/BombaProjetil.md ---

# Classe `BombaProjetil`

**Pacote:** `Modelo.Projeteis`

## Descrição

Representa o efeito da bomba do herói. Esta classe não é um projétil no sentido tradicional, mas sim um efeito de área que limpa projéteis inimigos e causa dano.

## Comportamento

1.  **Expansão**: Ao ser ativada, a bomba cria uma área de dano circular que se expande rapidamente a partir da posição do herói.
2.  **Dano e Limpeza**: Qualquer inimigo ou projétil inimigo dentro deste raio é destruído.
3.  **Lançamento de Mísseis**: Ao final de sua curta duração, a bomba é desativada e lança uma barragem de `ProjetilBombaHoming` em um padrão circular.

## Métodos Principais

### `BombaProjetil(...)`
*   **@brief** Construtor que inicializa o efeito da bomba na posição do herói.

### `atualizar()`
*   **@brief** Atualiza o raio de expansão da bomba a cada frame.

### `deactivate()`
*   **@brief** Sobrescreve o método padrão para garantir que a barragem de mísseis seja lançada no momento em que a bomba é desativada.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Projeteis/Projetil.md ---

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


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Projeteis/ProjetilBombaHoming.md ---

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


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Projeteis/ProjetilHoming.md ---

# Classe `ProjetilHoming`

**Pacote:** `Modelo.Projeteis`

## Descrição

Subclasse de `Projetil` que implementa um comportamento de perseguição. Após um período inicial de inércia, o projétil começa a seguir o inimigo mais próximo.

## Máquina de Estados

O comportamento do projétil é dividido em dois estados:

1.  **`INERCIA`**: O projétil se move em linha reta por um curto período.
2.  **`PERSEGUINDO`**: O projétil identifica o inimigo mais próximo e ajusta continuamente seu ângulo para persegui-lo.

## Métodos Principais

### `ProjetilHoming(...)`
*   **@brief** Construtor que recebe a lista de personagens para poder identificar seus alvos.

### `resetHoming(...)`
*   **@brief** Reseta o projétil para um novo disparo, definindo seu estado inicial como `INERCIA`.

### `atualizar()`
*   **@brief** Implementa a lógica da máquina de estados, transitando da inércia para a perseguição e atualizando o movimento.

### `ajustarAnguloParaOAlvo()`
*   **@brief** Ajusta suavemente o ângulo do projétil a cada frame para que ele se vire em direção ao alvo atual.


--- /Users/alexweber/Desktop/Programs/Java/POO/Phantom/docs/Modelo/Projeteis/ProjetilPool.md ---

# Classe `ProjetilPool`

**Pacote:** `Modelo.Projeteis`

## Descrição

Implementa o padrão de projeto **Object Pool** para gerenciar projéteis de forma eficiente. Em vez de criar e destruir objetos de projéteis constantemente (o que é custoso e pode causar pausas do Garbage Collector), esta classe pré-instancia um grande número de projéteis no início do jogo e os reutiliza.

## Funcionamento

1.  **Inicialização**: No construtor, a classe cria várias listas (`ArrayList`) e as preenche com instâncias inativas de cada tipo de projétil (`Projetil`, `ProjetilHoming`, etc.).
2.  **Requisição**: Quando o jogo precisa de um projétil, ele chama um dos métodos `get...()` (e.g., `getProjetilNormal()`).
3.  **Reutilização**: O método percorre a lista correspondente e retorna o primeiro projétil que encontrar com o estado `isActive = false`.
4.  **Reset**: O projétil retornado tem seu estado reiniciado pelo método `reset()` com novas coordenadas, velocidade e ângulo, e é então ativado.

## Métodos Principais

### `ProjetilPool(...)`
*   **@brief** Construtor que inicializa e pré-aloca as piscinas de objetos para cada tipo de projétil.

### `getTodosOsProjeteis()`
*   **@brief** Retorna uma única lista contendo todos os projéteis de todas as piscinas, para que possam ser adicionados à lista principal de renderização e processamento da fase.

### `getProjetilNormal()` / `getProjetilHoming()` / etc.
*   **@brief** Métodos para requisitar um projétil inativo de uma piscina específica.



--- End of content ---