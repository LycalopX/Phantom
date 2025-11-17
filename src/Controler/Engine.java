package Controler;

import static Auxiliar.ConfigMapa.*;
import static Auxiliar.ConfigJogo.*;
import Auxiliar.ConfigTeclado;
import Auxiliar.Debug.DebugManager;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Inimigos.*;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Personagem;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @brief A classe principal do motor do jogo, responsável pelo loop principal,
 *        gerenciamento de estado e coordenação entre os componentes do modelo,
 *        visão e controle.
 */
public class Engine implements Runnable {
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
    private final Set<Integer> teclasPressionadas = new HashSet<>();
    private final double velocidadeScroll = 2.0 * FATOR_ESCALA_ALTURA;
    private int deathbombTimer = 0;
    private boolean removeProjectiles = false;

    private int menuSelection = 0;
    private boolean showQuitConfirmation = false;

    public enum GameState {
        JOGANDO,
        DEATHBOMB_WINDOW,
        RESPAWNANDO,
        GAME_OVER,
        PAUSADO
    }

    /**
     * @brief Construtor da Engine. Inicializa todos os componentes do jogo,
     *        incluindo controladores, fases, herói e a interface gráfica.
     */
    public Engine() {
        this.gerenciadorDeFases = new GerenciadorDeFases();
        this.faseAtual = gerenciadorDeFases.carregarFase(this);
        this.hero = new Hero("hero/hero_s0.png", HERO_RESPAWN_X, HERO_RESPAWN_Y);
        this.faseAtual.adicionarPersonagem(hero);

        for (Item item : faseAtual.getItemPool().getTodosOsItens()) {
            item.setHero(hero);
        }

        controleDeJogo = new ControleDeJogo(faseAtual.getItemPool());
        controladorHeroi = new ControladorDoHeroi(this);

        cenario = new Cenario(this);
        cenario.setFase(faseAtual);

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
        double drawInterval = 1000000000.0 / GAME_FPS;
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
        if (estadoAtual != GameState.PAUSADO) {
            faseAtual.getItemPool().updateHighWatermark();
            faseAtual.getProjetilPool().updateHighWatermark();
        }

        switch (estadoAtual) {
            case JOGANDO:
                controladorHeroi.processarInput(teclasPressionadas, hero, faseAtual, controleDeJogo);
                faseAtual.atualizar(velocidadeScroll);

                boolean foiAtingido = controleDeJogo.processaTudo(faseAtual.getPersonagens(), false);

                if (foiAtingido) {
                    estadoAtual = GameState.DEATHBOMB_WINDOW;
                    deathbombTimer = DEATHBOMB_WINDOW_FRAMES;
                    SoundManager.getInstance().playSfx("se_pldead00", 1.5f);
                }
                break;
            case PAUSADO:
                // A lógica do jogo está parada. A entrada do menu é tratada em
                // configurarTeclado().
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
                        respawnTimer = RESPAWN_TIME_FRAMES;
                    }
                }
                break;
            case RESPAWNANDO:
                respawnTimer--;

                if (respawnTimer == RESPAWN_TIME_FRAMES / 2) {
                    removeProjectiles = true;
                }

                faseAtual.atualizar(velocidadeScroll);
                controleDeJogo.processaTudo(faseAtual.getPersonagens(), removeProjectiles);
                removeProjectiles = false;

                if (respawnTimer <= 0) {
                    hero.respawn();
                    hero.setPosition(HERO_RESPAWN_X, HERO_RESPAWN_Y);
                    estadoAtual = GameState.JOGANDO;
                }
                break;
            case GAME_OVER:
                break;
        }
    }

    /**
     * @brief Gera os itens que são dropados pelo herói ao morrer.
     */
    private void dropItensAoMorrer(int powerAtual) {
        int nMiniPowerUp = (int) (powerAtual * 0.9) % 5;
        int nPowerUp = (int) (powerAtual * 0.9) / 5;

        for (int i = 0; i < nMiniPowerUp; i++) {
            Item itemDropado = faseAtual.getItemPool().getItem(ItemType.MINI_POWER_UP);
            if (itemDropado != null) {
                itemDropado.init(hero.getX(), hero.getY());
                double angulo = -30 - Math.random() * 120;
                double forca = 0.15;
                itemDropado.lancarItem(angulo, forca);
            }
        }

        for (int i = 0; i < nPowerUp; i++) {
            Item itemDropado = faseAtual.getItemPool().getItem(ItemType.POWER_UP);
            if (itemDropado != null) {
                itemDropado.init(hero.getX(), hero.getY());
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
        try (FileInputStream fis = new FileInputStream(SAVE_FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis)) {

            this.faseAtual = (Fase) ois.readObject();
            if (this.faseAtual.getScript() != null) {
                this.faseAtual.getScript().setEngine(this);
            }
            
            cenario.setFase(this.faseAtual);
            this.controleDeJogo.setItemPool(this.faseAtual.getItemPool());

            this.hero = (Hero) this.faseAtual.getHero();
            for (Item item : this.faseAtual.getItemPool().getTodosOsItens()) {
                item.setHero(this.hero);
            }
            for (Personagem p : this.faseAtual.getPersonagens()) {
                if (p instanceof Inimigo) {
                    ((Inimigo) p).initialize(this.faseAtual);
                }
            }
            System.out.println(">>> JOGO CARREGADO!");
        } catch (Exception e) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, "Erro ao carregar o jogo.", e);
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
        faseAtual = gerenciadorDeFases.carregarFase(this);
        hero = new Hero("hero/hero_s0.png", HERO_RESPAWN_X, HERO_RESPAWN_Y);

        faseAtual.adicionarPersonagem(hero);
        for (Personagem p : this.faseAtual.getPersonagens()) {
            if (p instanceof Inimigo) {
                ((Inimigo) p).initialize(this.faseAtual);
            }
        }
        cenario.setFase(faseAtual);
        this.controleDeJogo.setItemPool(this.faseAtual.getItemPool());

        SoundManager.getInstance().playMusic("Illusionary Night ~ Ghostly Eyes", true);
    }

    /**
     * @brief Configura os listeners de teclado para capturar os inputs do jogador.
     */
    private void configurarTeclado() {
        tela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (estadoAtual == GameState.PAUSADO) {
                    if (showQuitConfirmation) {
                        if (e.getKeyCode() == ConfigTeclado.KEY_SELECT) {
                            // Lógica para sair do jogo (ainda não implementada)
                            System.exit(0);

                        } else if (e.getKeyCode() == ConfigTeclado.KEY_CANCEL) {
                            showQuitConfirmation = false;
                            SoundManager.getInstance().playSfx("se_ok00", 3f);
                        }
                    } else {
                        if (e.getKeyCode() == ConfigTeclado.ARROW_UP || e.getKeyCode() == ConfigTeclado.KEY_UP) {
                            menuSelection = (menuSelection - 1 + 2) % 2;
                            SoundManager.getInstance().playSfx("se_select00", 3f);

                        } else if (e.getKeyCode() == ConfigTeclado.ARROW_DOWN
                                || e.getKeyCode() == ConfigTeclado.KEY_DOWN) {
                            menuSelection = (menuSelection + 1) % 2;
                            SoundManager.getInstance().playSfx("se_select00", 3f);

                        } else if (e.getKeyCode() == ConfigTeclado.KEY_SELECT) {
                            if (menuSelection == 0) { // Return to Game
                                estadoAtual = GameState.JOGANDO;
                                SoundManager.getInstance().resumeMusic();
                                SoundManager.getInstance().playSfx("se_ok00", 3f);

                            } else { // Quit
                                showQuitConfirmation = true;
                                SoundManager.getInstance().playSfx("se_ok00", 3f);
                            }
                        } else if (e.getKeyCode() == ConfigTeclado.KEY_CANCEL) {
                            estadoAtual = GameState.JOGANDO;
                            SoundManager.getInstance().resumeMusic();
                            SoundManager.getInstance().playSfx("se_ok00", 3f);
                        }
                    }
                    return;
                }

                if (e.getKeyCode() == ConfigTeclado.KEY_PAUSE) {
                    if (estadoAtual == GameState.JOGANDO) {
                        estadoAtual = GameState.PAUSADO;
                        menuSelection = 0;
                        showQuitConfirmation = false;
                        
                        SoundManager.getInstance().pauseMusic();
                        SoundManager.getInstance().playSfx("se_pause", 6f);
                    }
                    return;
                }

                if (estadoAtual == GameState.GAME_OVER && e.getKeyCode() == ConfigTeclado.KEY_RESTART) {
                    reiniciarJogo();
                    SoundManager.getInstance().playSfx("se_ok00", 3f);
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
                if (teclasPressionadas.contains(KeyEvent.VK_G) && teclasPressionadas.contains(KeyEvent.VK_3)) {
                    DebugManager.toggle();
                }

                if (teclasPressionadas.contains(KeyEvent.VK_G) && teclasPressionadas.contains(KeyEvent.VK_4)) {
                    salvarInimigosParaTeste();
                }

                if (teclasPressionadas.contains(KeyEvent.VK_G) && teclasPressionadas.contains(KeyEvent.VK_5)) {
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
        try (FileOutputStream fos = new FileOutputStream(SAVE_FILE_NAME);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.faseAtual);
            System.out.println(">>> JOGO SALVO!");
        } catch (Exception e) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, "Erro ao salvar o jogo.", e);
        }
    }

    /**
     * @brief Salva um inimigo padrão em um arquivo .zip para testes de
     *        drag-and-drop.
     */
    private void salvarInimigosParaTeste() {
        System.out.println("Salvando inimigos para teste...");
        FadaComum1 fada1 = new FadaComum1(0, 0, new LootTable(), 100, null, "", 1);
        FadaComum2 fada2 = new FadaComum2(0, 0, new LootTable(), 100, null, "", 1);
        FadaComum3 fada3 = new FadaComum3(0, 0, 0, new LootTable(), 100, null, "", 1);
        FadaComum4 fada4 = new FadaComum4(0, 0, new LootTable(), 100, null, "", 1);
        Nightbug nightbug = new Nightbug(0, 0, new LootTable(), 1000, null);
        Lorelei lorelei = new Lorelei(0, 0, new LootTable(), 1200, null);
        Reimu reimu = new Reimu(0, 0, new LootTable(), 5000, null);
        Keine keine = new Keine(0, 0, new LootTable(), 8000, null);
        Reisen reisen = new Reisen(0, 0, new LootTable(), 6000, null);

        salvarPersonagemParaTeste(fada1, "enemies/fada_comum1.zip");
        salvarPersonagemParaTeste(fada2, "enemies/fada_comum2.zip");
        salvarPersonagemParaTeste(fada3, "enemies/fada_comum3.zip");
        salvarPersonagemParaTeste(fada4, "enemies/fada_comum4.zip");
        salvarPersonagemParaTeste(nightbug, "enemies/nightbug.zip");
        salvarPersonagemParaTeste(lorelei, "enemies/lorelei.zip");
        salvarPersonagemParaTeste(reimu, "enemies/reimu.zip");
        salvarPersonagemParaTeste(keine, "enemies/keine.zip");
        salvarPersonagemParaTeste(reisen, "enemies/reisen.zip");
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
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE,
                    "Erro ao salvar personagem para teste: " + nomeArquivo, e);
        }
    }

    /**
     * @brief Carrega a próxima fase do jogo.
     */
    public void carregarProximaFase() {
        this.faseAtual = gerenciadorDeFases.proximaFase(this);
        this.faseAtual.adicionarPersonagem(hero);
        for (Item item : this.faseAtual.getItemPool().getTodosOsItens()) {
            item.setHero(this.hero);
        }
        cenario.setFase(this.faseAtual);
        this.controleDeJogo.setItemPool(this.faseAtual.getItemPool());
    }

    /**
     * @brief Retorna o estado atual do jogo.
     */
    public GameState getEstadoAtual() {
        return this.estadoAtual;
    }

    public int getMenuSelection() {
        return menuSelection;
    }

    public boolean isShowQuitConfirmation() {
        return showQuitConfirmation;
    }
}