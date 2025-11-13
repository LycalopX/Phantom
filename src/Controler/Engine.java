package Controler;

import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Inimigos.FadaComum1;
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

/**
 * @brief A classe principal do motor do jogo, responsável pelo loop principal,
 *        gerenciamento de estado e coordenação entre os componentes do modelo,
 *        visão e controle.
 */
public class Engine implements Runnable {

    private static final int FPS = 60;
    private static final int RESPAWN_X = (LARGURA_TELA / CELL_SIDE) / 2;
    private static final int RESPAWN_Y = (int) ((ALTURA_TELA / CELL_SIDE) * 0.9);

    private Tela tela;
    private Cenario cenario;
    private Thread gameThread;

    private int slowMotionCounter = 0;

    private Fase faseAtual;
    private Hero hero;
    private ControleDeJogo controleDeJogo;
    private ControladorDoHeroi controladorHeroi;
    private GerenciadorDeFases gerenciadorDeFases;

    private GameState estadoAtual = GameState.JOGANDO;
    private int respawnTimer = 0;
    private final int TEMPO_DE_RESPAWN = 60;
    private final Set<Integer> teclasPressionadas = new HashSet<>();
    private final double velocidadeScroll = 2.0 * FATOR_ESCALA_ALTURA;
    private int deathbombTimer = 0;
    private final int JANELA_DEATHBOMB = 8;
    private boolean removeProjectiles = false;

    public enum GameState {
        JOGANDO,
        DEATHBOMB_WINDOW,
        RESPAWNANDO,
        GAME_OVER
    }

    /**
     * @brief Construtor da Engine. Inicializa todos os componentes do jogo,
     *        incluindo controladores, fases, herói e a interface gráfica.
     */
    public Engine() {
        this.gerenciadorDeFases = new GerenciadorDeFases();
        this.faseAtual = gerenciadorDeFases.carregarFase();
        this.hero = new Hero("hero/hero_s0.png", RESPAWN_X, RESPAWN_Y);
        this.faseAtual.adicionarPersonagem(hero);

        for (Item item : faseAtual.getItemPool().getTodosOsItens()) {
            item.setHero(hero);
        }

        controleDeJogo = new ControleDeJogo(faseAtual.getItemPool());
        controladorHeroi = new ControladorDoHeroi(this);

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

    /**
     * @brief O loop principal do jogo, que controla a taxa de atualização e
     *        renderização (FPS).
     */
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

    /**
     * @brief Atualiza o estado do jogo com base na máquina de estados (jogando,
     *        morrendo, etc.).
     */
    private synchronized void atualizar() {
        faseAtual.getItemPool().updateHighWatermark();
        faseAtual.getProjetilPool().updateHighWatermark();

        switch (estadoAtual) {
            case JOGANDO:
                controladorHeroi.processarInput(teclasPressionadas, hero, faseAtual, controleDeJogo);
                faseAtual.atualizar(velocidadeScroll);

                boolean foiAtingido = controleDeJogo.processaTudo(faseAtual.getPersonagens(), false);

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

                } else if (deathbombTimer <= 0) {
                    int powerAntesDaMorte = hero.getPower();

                    hero.processarMorte();
                    dropItensAoMorrer(powerAntesDaMorte);

                    if (hero.getHP() <= 0) {
                        hero.deactivate();
                        estadoAtual = GameState.GAME_OVER;
                    } else {
                        estadoAtual = GameState.RESPAWNANDO;
                        respawnTimer = TEMPO_DE_RESPAWN;
                    }
                }
                break;
            case RESPAWNANDO:
                respawnTimer--;

                if (respawnTimer == TEMPO_DE_RESPAWN / 2) {
                    removeProjectiles = true;
                }

                faseAtual.atualizar(velocidadeScroll);
                controleDeJogo.processaTudo(faseAtual.getPersonagens(), removeProjectiles);
                removeProjectiles = false;

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

    /**
     * @brief Gera os itens que são dropados pelo herói ao morrer.
     */
    private void dropItensAoMorrer(int powerAtual) {
        int itensADropar = powerAtual / 2;
        for (int i = 0; i < itensADropar; i++) {
            Item itemDropado = faseAtual.getItemPool().getItem(ItemType.MINI_POWER_UP);
            if (itemDropado != null) {
                itemDropado.init(hero.x, hero.y);
                double angulo = -30 - Math.random() * 120;
                double forca = 0.15;
                itemDropado.lancarItem(angulo, forca);
            }
        }
    }

    /**
     * @brief Carrega o estado do jogo a partir de um arquivo 'POO.dat'.
     */
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

    /**
     * @brief Inicia a thread principal do jogo.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * @brief Reinicia o jogo para o estado inicial.
     */
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

    /**
     * @brief Configura os listeners de teclado para capturar os inputs do jogador.
     */
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

                if (teclasPressionadas.contains(KeyEvent.VK_F) && teclasPressionadas.contains(KeyEvent.VK_5)) {
                    System.out.println("--- Pool High Watermarks ---");
                    System.out.println("Items: " + faseAtual.getItemPool().getMaxActiveItems());
                    System.out.println("Projeteis Normais: " + faseAtual.getProjetilPool().getMaxActiveNormais());
                    System.out.println("Projeteis Homing: " + faseAtual.getProjetilPool().getMaxActiveHoming());
                    System.out.println(
                            "Projeteis Bomba Homing: " + faseAtual.getProjetilPool().getMaxActiveBombaHoming());
                    System.out.println("Projeteis Inimigos: " + faseAtual.getProjetilPool().getMaxActiveInimigos());
                    System.out.println("---------------------------");
                }

                // Kate Cheatcode
                if (teclasPressionadas.contains(KeyEvent.VK_K) && teclasPressionadas.contains(KeyEvent.VK_A)
                        && teclasPressionadas.contains(KeyEvent.VK_T) && teclasPressionadas.contains(KeyEvent.VK_E)) {
                    hero.toggleCheats();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclasPressionadas.remove(e.getKeyCode());
            }
        });
    }

    /**
     * @brief Salva o estado atual da fase em um arquivo 'POO.dat'.
     */
    private synchronized void salvarJogo() {
        try (FileOutputStream fos = new FileOutputStream("POO.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.faseAtual);
            System.out.println(">>> JOGO SALVO!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Salva um inimigo padrão em um arquivo .zip para testes de
     *        drag-and-drop.
     */
    private void salvarInimigosParaTeste() {
        System.out.println("Salvando inimigos para teste...");
        FadaComum1 fada = new FadaComum1(0, 0, new LootTable(), 100, null);

        salvarPersonagemParaTeste(fada, "fada_comum.zip");
        System.out.println("Inimigos de teste salvos na pasta do projeto.");
    }

    /**
     * @brief Serializa um personagem e o salva em um arquivo .zip.
     */
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

    /**
     * @brief Carrega a próxima fase do jogo.
     */
    public void carregarProximaFase() {
        this.faseAtual = gerenciadorDeFases.proximaFase();
        this.faseAtual.adicionarPersonagem(hero);
        cenario.setFase(this.faseAtual);
    }

    /**
     * @brief Pula para a próxima fase, como um cheat.
     */
    public void pularParaProximaFase() {
        this.faseAtual = gerenciadorDeFases.proximaFase();
        this.faseAtual.adicionarPersonagem(hero);
        cenario.setFase(this.faseAtual);
    }

    /**
     * @brief Retorna o estado atual do jogo.
     */
    public GameState getEstadoAtual() {
        return this.estadoAtual;
    }
}