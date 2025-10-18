package Controler;

import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Items.Item;
import Modelo.Items.ItemType;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import Auxiliar.Consts;
import Auxiliar.DebugManager;

public class Engine implements Runnable {

    private static final int FPS = 60;
    private Tela tela;
    private Cenario cenario;
    private Thread gameThread;

    // Componentes principais do padrão MVC
    private Fase faseAtual; // Modelo
    private Hero hero; // Modelo (referência direta para facilitar)
    private ControleDeJogo controleDeJogo; // Controlador de Regras
    private ControladorDoHeroi controladorHeroi; // Controlador de Input
    private GerenciadorDeFases gerenciadorDeFases;

    private GameState estadoAtual = GameState.JOGANDO;
    private int respawnTimer = 0;
    private final int TEMPO_DE_RESPAWN = 60; // 1 segundo
    private final Set<Integer> teclasPressionadas = new HashSet<>();
    private final double velocidadeScroll = 2.0;

    public enum GameState {
        JOGANDO,
        RESPAWNANDO,
        GAME_OVER
    }

    public Engine() {
        // 1. Inicializa os Controladores
        controleDeJogo = new ControleDeJogo();
        controladorHeroi = new ControladorDoHeroi();

        // 2. Inicializa o Gerenciador de Fases
        this.gerenciadorDeFases = new GerenciadorDeFases();

        // 3. Pede a primeira fase para o gerenciador
        this.faseAtual = gerenciadorDeFases.carregarFase();

        this.hero = new Hero("hero/hero_s0.png", Consts.respawnX, Consts.respawnY);
        this.faseAtual.adicionarPersonagem(hero);

        // 3. Inicializa a Visão (View)
        cenario = new Cenario();
        cenario.setFase(faseAtual);
        cenario.setEstadoDoJogo(estadoAtual);

        // 4. Configura a Janela Principal
        tela = new Tela();
        tela.add(cenario);
        configurarTeclado(); // Configura o listener de teclado
        tela.pack();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);
    }

    // O loop de jogo (run) permanece o mesmo, ele é o "coração" que pulsa a 60 FPS
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                atualizar(); // Atualiza a lógica
                cenario.repaint(); // Redesenha a tela
                cenario.atualizarContadorFPS();
                delta--;
            }
        }
    }

    private synchronized void atualizar() {
        switch (estadoAtual) {
            case JOGANDO:
                // 1. INPUT: O controlador lê as teclas e comanda o herói (movimento, tiro,
                // bomba).
                controladorHeroi.processarInput(teclasPressionadas, hero, faseAtual, controleDeJogo);

                // 2. MODELO: A fase atualiza seus componentes (scroll, spawn de inimigos,
                // movimento dos inimigos).
                faseAtual.atualizar(velocidadeScroll);

                // 3. REGRAS: O controle de jogo processa colisões e interações.
                controleDeJogo.processaTudo(faseAtual.getPersonagens());

                // 4. ESTADO GLOBAL: O Engine verifica se o herói morreu.
                if (hero.getHP() <= 0) {
                    dropItensAoMorrer();
                    hero.deactivate();
                    estadoAtual = GameState.GAME_OVER; // MUDANÇA: Ir direto para GAME_OVER

                } else if (!hero.isActive()) { // Se o herói tomou dano mas não morreu
                    dropItensAoMorrer();
                    estadoAtual = GameState.RESPAWNANDO;
                    respawnTimer = TEMPO_DE_RESPAWN;
                }
                break;

            case RESPAWNANDO:
                // Durante o respawn, o jogo continua rodando no fundo
                faseAtual.atualizar(velocidadeScroll);
                controleDeJogo.processaTudo(faseAtual.getPersonagens());

                respawnTimer--;
                if (respawnTimer <= 0) {
                    hero.respawn();
                    estadoAtual = GameState.JOGANDO;
                }
                break;

            case GAME_OVER:
                // O jogo para. Nenhuma lógica de atualização é executada.
                break;
        }
        // Informa a view qual é o estado atual do jogo para que ela possa desenhar a
        // tela correta
        cenario.setEstadoDoJogo(estadoAtual);
    }

    private void dropItensAoMorrer() {
        int powerAtual = hero.getPower();
        int itensADropar = powerAtual / 10; // Ex: 1 item a cada 10 de power

        for (int i = 0; i < itensADropar; i++) {
            Item itemDropado = new Item(ItemType.MINI_POWER_UP, hero.x, hero.y, hero);
            double angulo = -30 - (Math.random() * 120); // Leque para cima
            double forca = 0.15;
            itemDropado.lancarItem(angulo, forca);
            faseAtual.adicionarPersonagem(itemDropado);
        }
    }

    private synchronized void carregarJogo() {
        try {
            FileInputStream fis = new FileInputStream("POO.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.faseAtual = (Fase) ois.readObject();
            ois.close();
            fis.close();

            // ATUALIZA AS REFERÊNCIAS CRUCIAIS APÓS CARREGAR
            cenario.setFase(this.faseAtual);
            this.hero = (Hero) this.faseAtual.getHero(); // Garante que o Engine está controlando o herói correto

            System.out.println(">>> JOGO CARREGADO!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ... O resto da sua classe Engine (startGameThread, reiniciarJogo,
    // configurarTeclado, salvarJogo) ...
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void reiniciarJogo() {
        estadoAtual = GameState.JOGANDO;
        respawnTimer = 0;

        gerenciadorDeFases.resetar();
        faseAtual = gerenciadorDeFases.carregarFase(); // Carrega a Fase 1
        hero = new Hero("hero/hero_s0.png", Consts.respawnX, Consts.respawnY);
        faseAtual.adicionarPersonagem(hero);

        cenario.setFase(faseAtual);
        cenario.setEstadoDoJogo(estadoAtual);
    }

    private void configurarTeclado() {
        tela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("DEBUG 1: Tecla Pressionada - Código: " + e.getKeyCode());

                if (estadoAtual == GameState.GAME_OVER && e.getKeyCode() == Consts.KEY_RESTART) {
                    reiniciarJogo();
                    return;
                }
                if (e.getKeyCode() == Consts.KEY_SAVE) {
                    salvarJogo();
                    return;
                }
                if (e.getKeyCode() == Consts.KEY_LOAD) {
                    carregarJogo();
                    return;
                } // Mudei para 'O' para não conflitar com o 'R' de reiniciar

                teclasPressionadas.add(e.getKeyCode());

                if (teclasPressionadas.contains(KeyEvent.VK_F) && teclasPressionadas.contains(KeyEvent.VK_3)) {
                    DebugManager.toggle();
                    teclasPressionadas.remove(KeyEvent.VK_F);
                    teclasPressionadas.remove(KeyEvent.VK_3);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclasPressionadas.remove(e.getKeyCode());
            }
        });
    }

    private synchronized void salvarJogo() {
        try {
            FileOutputStream fos = new FileOutputStream("POO.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.faseAtual);
            oos.close();
            fos.close();
            System.out.println(">>> JOGO SALVO!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void carregarProximaFase() {
        // (Você precisará de uma lógica para detectar o fim da fase,
        // por exemplo, distanciaTotalRolada > 10000 ou um boss morrer)

        this.faseAtual = gerenciadorDeFases.proximaFase();

        // Adiciona o herói (e outros elementos persistentes) à nova fase
        this.faseAtual.adicionarPersonagem(hero);

        // Atualiza a visão
        cenario.setFase(this.faseAtual);
    }
}