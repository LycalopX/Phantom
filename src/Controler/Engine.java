package Controler;

import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Inimigos.FadaComum;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Personagem;
import Auxiliar.LootTable;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import Auxiliar.ConfigTeclado;
import Auxiliar.SoundManager;
import Auxiliar.Debug.DebugManager;
import static Auxiliar.ConfigMapa.*;

public class Engine implements Runnable {

    private static final int FPS = 60;
    private static final int RESPAWN_X = (LARGURA_TELA / CELL_SIDE) / 2;
    private static final int RESPAWN_Y = (int) ((ALTURA_TELA / CELL_SIDE) * 0.9);

    private Tela tela;
    private Cenario cenario;
    private Thread gameThread;

    // Para controle de slow motion (efeito de deathbombing)
    private int slowMotionCounter = 0;

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
    private int deathbombTimer = 0;
    private final int JANELA_DEATHBOMB = 8; // 8 frames para o jogador reagir

    public enum GameState {
        JOGANDO,
        DEATHBOMB_WINDOW,
        RESPAWNANDO,
        GAME_OVER
    }

    public Engine() {
        // 1. Inicializa os Controladores
        controleDeJogo = new ControleDeJogo();
        controladorHeroi = new ControladorDoHeroi(this);

        // 2. Inicializa o Gerenciador de Fases
        this.gerenciadorDeFases = new GerenciadorDeFases();

        // 3. Pede a primeira fase para o gerenciador
        this.faseAtual = gerenciadorDeFases.carregarFase();

        this.hero = new Hero("hero/hero_s0.png", RESPAWN_X, RESPAWN_Y);
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

    private synchronized void atualizar() {
        switch (estadoAtual) {
            case JOGANDO:
                controladorHeroi.processarInput(teclasPressionadas, hero, faseAtual, controleDeJogo);
                faseAtual.atualizar(velocidadeScroll);
                boolean foiAtingido = controleDeJogo.processaTudo(faseAtual.getPersonagens());
                if (foiAtingido) {
                    estadoAtual = GameState.DEATHBOMB_WINDOW;
                    deathbombTimer = JANELA_DEATHBOMB;
                }
                break;

            case DEATHBOMB_WINDOW:
                controladorHeroi.processarInput(teclasPressionadas, hero, faseAtual, controleDeJogo);
                deathbombTimer--;
                slowMotionCounter++;
                if (slowMotionCounter % Hero.SLOW_MOTION_FRAMES == 0) {
                    faseAtual.atualizar(velocidadeScroll);
                }
                if (hero.isBombing()) {
                    estadoAtual = GameState.JOGANDO;
                }
                else if (deathbombTimer <= 0) {
                    hero.processarMorte();
                    dropItensAoMorrer();
                    if (hero.getHP() <= 0) {
                        hero.deactivate();
                        estadoAtual = GameState.GAME_OVER;
                    }
                    else {
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
                    hero.x = RESPAWN_X;
                    hero.y = RESPAWN_Y;
                    estadoAtual = GameState.JOGANDO;
                }
                break;

            case GAME_OVER:
                break;
        }
        cenario.setEstadoDoJogo(estadoAtual);
    }

    private void dropItensAoMorrer() {
        int powerAtual = hero.getPower();
        int itensADropar = powerAtual / 10;
        for (int i = 0; i < itensADropar; i++) {
            Item itemDropado = new Item(ItemType.MINI_POWER_UP, hero.x, hero.y, hero);
            double angulo = -30 - (Math.random() * 120);
            double forca = 0.15;
            itemDropado.lancarItem(angulo, forca);
            faseAtual.adicionarPersonagem(itemDropado);
        }
    }

    private synchronized void carregarJogo() {
        try (FileInputStream fis = new FileInputStream("POO.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            this.faseAtual = (Fase) ois.readObject();
            cenario.setFase(this.faseAtual);
            this.hero = (Hero) this.faseAtual.getHero();
            System.out.println(">>> JOGO CARREGADO!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void reiniciarJogo() {
        estadoAtual = GameState.JOGANDO;
        respawnTimer = 0;
        SoundManager.getInstance().stopAllMusic();
        gerenciadorDeFases.resetar();
        faseAtual = gerenciadorDeFases.carregarFase();
        hero = new Hero("hero/hero_s0.png", RESPAWN_X, RESPAWN_Y);
        faseAtual.adicionarPersonagem(hero);
        cenario.setFase(faseAtual);
        cenario.setEstadoDoJogo(estadoAtual);
        SoundManager.getInstance().playMusic("Illusionary Night ~ Ghostly Eyes", true);
    }

    private void configurarTeclado() {
        tela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (estadoAtual == GameState.GAME_OVER && e.getKeyCode() == ConfigTeclado.KEY_RESTART) {
                    reiniciarJogo();
                    return;
                }
                
                if (e.getKeyCode() == ConfigTeclado.KEY_SAVE) {
                    salvarJogo();
                    return;
                }
                if (e.getKeyCode() == ConfigTeclado.KEY_LOAD) {
                    carregarJogo();
                    return;
                }

                teclasPressionadas.add(e.getKeyCode());

                if (teclasPressionadas.contains(KeyEvent.VK_F) && teclasPressionadas.contains(KeyEvent.VK_3)) {
                    DebugManager.toggle();
                }
                if (teclasPressionadas.contains(KeyEvent.VK_F) && teclasPressionadas.contains(KeyEvent.VK_4)) {
                    salvarInimigosParaTeste();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclasPressionadas.remove(e.getKeyCode());
            }
        });
    }

    private synchronized void salvarJogo() {
        try (FileOutputStream fos = new FileOutputStream("POO.dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.faseAtual);
            System.out.println(">>> JOGO SALVO!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarInimigosParaTeste() {
        System.out.println("Salvando inimigos para teste...");
        FadaComum fada = new FadaComum(0, 0, new LootTable(), 100, null);
        
        salvarPersonagemParaTeste(fada, "fada_comum.zip");
        System.out.println("Inimigos de teste salvos na pasta do projeto.");
    }

    private void salvarPersonagemParaTeste(Personagem p, String nomeArquivo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(p);
            }
            byte[] personagemBytes = baos.toByteArray();

            try (FileOutputStream fos = new FileOutputStream(nomeArquivo);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                
                ZipEntry entry = new ZipEntry(p.getClass().getSimpleName() + ".ser");
                zos.putNextEntry(entry);
                zos.write(personagemBytes);
                zos.closeEntry();
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar personagem para teste: " + nomeArquivo);
            e.printStackTrace();
        }
    }

    public void carregarProximaFase() {
        this.faseAtual = gerenciadorDeFases.proximaFase();
        this.faseAtual.adicionarPersonagem(hero);
        cenario.setFase(this.faseAtual);
    }

    public GameState getEstadoAtual() {
        return this.estadoAtual;
    }
}