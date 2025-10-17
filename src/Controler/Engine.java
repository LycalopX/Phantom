package Controler;

import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Projeteis.BombaProjetil;

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

    private Fase faseAtual;
    private Hero hero;
    private ControleDeJogo controleDeJogo;
    private ControladorDoHeroi controladorHeroi;
    private GerenciadorDeFases gerenciadorDeFases;

    private GameState estadoAtual = GameState.JOGANDO;
    private int respawnTimer = 0;
    private final int TEMPO_DE_RESPAWN = 120; // 2 segundos

    private int deathbombTimer = 0;
    private final int JANELA_DEATHBOMB = 8;

    private final Set<Integer> teclasPressionadas = new HashSet<>();
    private final double velocidadeScroll = 2.0;

    // MUDANÇA: Novo estado para a janela de Deathbomb
    public enum GameState {
        JOGANDO,
        DEATHBOMB_WINDOW, // Estado em que o jogador foi atingido e pode usar a bomba
        RESPAWNANDO,
        GAME_OVER
    }

    public Engine() {
        controleDeJogo = new ControleDeJogo();
        controladorHeroi = new ControladorDoHeroi();
        this.gerenciadorDeFases = new GerenciadorDeFases();
        this.faseAtual = gerenciadorDeFases.carregarFase();
        this.hero = new Hero("hero/hero_s0.png", Consts.respawnX, Consts.respawnY);
        this.faseAtual.adicionarPersonagem(hero);
        cenario = new Cenario();
        cenario.setFase(faseAtual);
        cenario.setEstadoDoJogo(estadoAtual);
        tela = new Tela();
        tela.add(cenario);
        configurarTeclado();
        tela.pack();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);
    }

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
                atualizar();
                cenario.repaint();
                cenario.atualizarContadorFPS();
                delta--;
            }
        }
    }

    private void atualizar() {
        switch (estadoAtual) {
            case JOGANDO:
                controladorHeroi.processarInput(teclasPressionadas, hero, faseAtual, controleDeJogo, estadoAtual);
                faseAtual.atualizar(velocidadeScroll);
                boolean foiAtingido = !controleDeJogo.processaTudo(faseAtual.getPersonagens());

                // Se o herói foi atingido, entra na janela de deathbomb
                if (foiAtingido && hero.getHP() > 0) { // Apenas entra se o HP ainda não zerou
                    estadoAtual = GameState.DEATHBOMB_WINDOW;
                    deathbombTimer = JANELA_DEATHBOMB;
                    // Opcional: Tocar um som de "hit" aqui
                } else if (hero.getHP() <= 0) {
                    estadoAtual = GameState.GAME_OVER;
                }
                break;

            case DEATHBOMB_WINDOW:
                faseAtual.atualizar(velocidadeScroll); // O jogo não para
                deathbombTimer--;

                // O jogador pressionou a bomba a tempo?
                if (teclasPressionadas.contains(Consts.KEY_BOMB) && hero.getBombas() > 0) {
                    BombaProjetil bomba = hero.usarBomba(); // Usa a bomba
                    if (bomba != null) {
                        faseAtual.adicionarPersonagem(bomba);
                    }
                    hero.cancelarDano(); // Nega a morte que iria acontecer
                    estadoAtual = GameState.JOGANDO; // Salvo! Volta ao jogo normal.
                }
                // O tempo acabou e o jogador não usou a bomba
                else if (deathbombTimer <= 0) {
                    hero.processarMorte(); // AGORA sim, o herói perde vida/power
                    dropItensAoMorrer();

                    if (hero.getHP() <= 0) {
                        estadoAtual = GameState.GAME_OVER;
                    } else {
                        estadoAtual = GameState.RESPAWNANDO;
                        respawnTimer = TEMPO_DE_RESPAWN;
                    }
                }
                break;

            case RESPAWNANDO:
                faseAtual.atualizar(velocidadeScroll);
                controleDeJogo.processaTudo(faseAtual.getPersonagens());
                respawnTimer--;
                if (respawnTimer <= 0) {
                    hero.respawn();
                    estadoAtual = GameState.JOGANDO;
                }
                break;

            case GAME_OVER:
                // O jogo para. Nenhuma lógica de atualização aqui.
                break;
        }
        cenario.setEstadoDoJogo(estadoAtual);
    }

    private void dropItensAoMorrer() {
        int powerAtual = hero.getPower();
        int itensADropar = powerAtual / 10;
        for (int i = 0; i < itensADropar; i++) {
            Item itemDropado = new Item(ItemType.MINI_POWER_UP, hero.x, hero.y);
            double angulo = -30 - (Math.random() * 120);
            double forca = 0.15;
            itemDropado.lancarItem(angulo, forca);
            faseAtual.adicionarPersonagem(itemDropado);
        }
    }

    // Métodos de salvar, carregar, reiniciar e configurar teclado permanecem
    // praticamente os mesmos.
    // Apenas uma correção crucial no carregarJogo:
    private void carregarJogo() {
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

    private void salvarJogo() {
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