package Controler;

import Modelo.Personagem;
import Modelo.Fases.Fase;
import Modelo.Fases.ScriptCreditos;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Auxiliar.ConfigMapa;
import Auxiliar.SoundManager;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import Auxiliar.Debug.ContadorFPS;
import Auxiliar.Debug.DebugManager;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @brief Painel principal do jogo, responsável por toda a renderização.
 * 
 *        Esta classe desenha os elementos visuais da fase, a interface (HUD),
 *        e as telas de estado, como Game Over e Pause. Também gerencia a
 *        funcionalidade de arrastar e soltar (drag-and-drop) para debug.
 */
public class Cenario extends JPanel {
    private Fase faseAtual;
    private ContadorFPS contadorFPS;
    private Engine engine;
    private BufferedImage imagemGameOver;
    private MenuPausa menuPausa;

    private Color corFundoOverlay;
    private final Color corDeathbombOverlay = new Color(255, 0, 0, 30);
    private final Font fonteHUD = new Font("Arial", Font.BOLD, 20);
    private LinearGradientPaint gradienteFundo;

    /**
     * @brief Construtor do Cenario.
     * 
     *        Configura as dimensões do painel, o contador de FPS e a funcionalidade
     *        de arrastar e soltar (drag-and-drop) para debug.
     */
    public Cenario(Engine engine) {
        this.engine = engine;
        this.setPreferredSize(new Dimension(ConfigMapa.LARGURA_TELA, ConfigMapa.ALTURA_TELA));
        this.setFocusable(false);
        this.setBackground(Color.BLACK);
        this.contadorFPS = new ContadorFPS();
        this.menuPausa = new MenuPausa();
        setupDropTarget();
    }

    /**
     * @brief Configura a área para a funcionalidade de arrastar e soltar.
     * 
     *        Permite adicionar inimigos ao jogo dinamicamente arrastando arquivos
     *        .zip
     *        para a janela, apenas quando o modo de debug está ativo.
     */
    private void setupDropTarget() {
        new DropTarget(this, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                if (!DebugManager.isActive()) {
                    dtde.rejectDrop();
                    return;
                }
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        for (File file : files) {
                            processarArquivoSolto(file, dtde.getLocation());
                        }
                    }
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (DebugManager.isActive() && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                if (DebugManager.isActive() && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
            }
        });
    }

    /**
     * @brief Processa um arquivo .zip solto na tela.
     * 
     *        Desserializa um objeto `Personagem` do arquivo e o adiciona na fase
     *        na posição do cursor do mouse.
     */
    private void processarArquivoSolto(File file, Point dropPoint) {
        if (file == null || !file.getName().toLowerCase().endsWith(".zip")) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
                ZipInputStream zis = new ZipInputStream(fis)) {

            if (zis.getNextEntry() != null) {
                try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                    Personagem p = (Personagem) ois.readObject();

                    double gridX = dropPoint.getX() / ConfigMapa.CELL_SIDE;
                    double gridY = dropPoint.getY() / ConfigMapa.CELL_SIDE;

                    p.setPosition(gridX, gridY);

                    if (faseAtual != null) {
                        if (p instanceof Inimigo) {
                            Inimigo inimigo = (Inimigo) p;
                            inimigo.initialize(faseAtual);
                            inimigo.setInitialX(gridX);
                        }
                        faseAtual.adicionarPersonagem(p);
                        System.out.println("Personagem " + p.getClass().getSimpleName() + " adicionado em (" + gridX
                                + ", " + gridY + ")");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Falha ao processar o arquivo solto: " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * @brief Define a fase atual a ser desenhada e extrai seus elementos visuais.
     */
    public void setFase(Fase fase) {
        this.faseAtual = fase;
        if (fase != null && fase.getScript() != null) {
            this.corFundoOverlay = fase.getScript().getBackgroundOverlayColor();
            this.gradienteFundo = fase.getScript().getBackgroundGradient();
        }
    }

    /**
     * @brief Método principal de desenho do Swing, chamado pelo RepaintManager.
     * 
     *        Orquestra a renderização do jogo com base no estado atual (jogando,
     *        pausado, game over, etc.).
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (faseAtual == null) {
            return;
        }

        // Lógica de renderização especial para a tela de créditos.
        if (faseAtual.getScript() instanceof ScriptCreditos) {
            ((ScriptCreditos) faseAtual.getScript()).render((Graphics2D) g);
            if (faseAtual.getHero() != null && faseAtual.getHero().isActive()) {
                faseAtual.getHero().autoDesenho((Graphics2D) g);
            }
            return;
        }

        // Renderiza a cena principal do jogo se não estiver em Game Over ou Pausado.
        if (engine.getEstadoAtual() == null || engine.getEstadoAtual() == Engine.GameState.JOGANDO
                || engine.getEstadoAtual() == Engine.GameState.RESPAWNANDO
                || engine.getEstadoAtual() == Engine.GameState.DEATHBOMB_WINDOW) {

            desenharCenaDoJogo((Graphics2D) g);

            // Adiciona um overlay vermelho durante a janela de "deathbomb".
            if (engine.getEstadoAtual() == Engine.GameState.DEATHBOMB_WINDOW) {
                g.setColor(corDeathbombOverlay);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            if (DebugManager.isActive()) {
                desenharHUD((Graphics2D) g);
            }
        } else if (engine.getEstadoAtual() == Engine.GameState.PAUSADO) {
            desenharCenaDoJogo((Graphics2D) g); // Desenha o fundo do jogo
            menuPausa.desenhar((Graphics2D) g, engine.getMenuSelection(), engine.isShowQuitConfirmation(), getWidth(),
                    getHeight());
        } else if (engine.getEstadoAtual() == Engine.GameState.GAME_OVER) {
            desenharTelaGameOver(g);
            SoundManager.getInstance().stopAllMusic();
        }
    }

    /**
     * @brief Desenha todos os elementos da cena do jogo.
     * 
     *        A ordem de renderização é crucial: fundo, overlays, primeiro plano e,
     *        por fim, os personagens ordenados por sua camada de renderização.
     */
    private void desenharCenaDoJogo(Graphics2D g2d) {

        for (var elemento : faseAtual.getElementosCenario()) {
            if (elemento.getDrawLayer() == Modelo.Cenario.DrawLayer.BACKGROUND) {
                elemento.desenhar(g2d, getWidth(), getHeight());
            }
        }

        g2d.setColor(corFundoOverlay);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setPaint(gradienteFundo);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (var elemento : faseAtual.getElementosCenario()) {
            if (elemento.getDrawLayer() == Modelo.Cenario.DrawLayer.FOREGROUND) {
                elemento.desenhar(g2d, getWidth(), getHeight());
            }
        }

        // Agrupa todos os personagens (herói, inimigos, projéteis, etc.) em uma
        // única lista para renderização.
        ArrayList<Personagem> personagensParaRenderizar = new ArrayList<>();
        personagensParaRenderizar.add(faseAtual.getHero());
        personagensParaRenderizar.addAll((List<Personagem>) (List<?>) faseAtual.getInimigos());
        personagensParaRenderizar.addAll((List<Personagem>) (List<?>) faseAtual.getProjeteis());
        personagensParaRenderizar.addAll((List<Personagem>) (List<?>) faseAtual.getItens());
        personagensParaRenderizar.addAll((List<Personagem>) (List<?>) faseAtual.getBombas());

        // Ordena a lista com base na camada de renderização de cada personagem,
        // garantindo que os elementos certos apareçam na frente dos outros.
        personagensParaRenderizar.sort(Comparator.comparing(p -> p.getRenderLayer().ordinal()));

        for (Personagem p : personagensParaRenderizar) {
            if (p != null && p.isActive()) {
                p.autoDesenho(g2d);
            }
        }
    }

    /**
     * @brief Desenha o Heads-Up Display (HUD) com informações de debug.
     */
    private void desenharHUD(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(fonteHUD);

        g2d.drawString(contadorFPS.getFPSString(), 10, 60);

        Personagem heroi = faseAtual.getHero();
        if (heroi instanceof Hero) {
            Hero h = (Hero) heroi;

            g2d.drawString("Bombas: " + h.getBombas(), 10, 80);
            g2d.drawString("HP: " + h.getHP(), 10, 100);
            g2d.drawString("Power: " + h.getPower(), 10, 120);
            g2d.drawString("Score: " + h.getScore(), 10, 140);
            g2d.drawString("Mísseis: " + h.getNivelDeMisseis(), 10, 160);
        }
    }

    /**
     * @brief Desenha a tela de Game Over.
     */
    private void desenharTelaGameOver(Graphics g) {
        if (imagemGameOver == null) {
            carregarImagensGameOver();
        }

        if (imagemGameOver != null) {
            g.drawImage(imagemGameOver, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * @brief Carrega a imagem da tela de Game Over do sistema de arquivos.
     */
    private void carregarImagensGameOver() {
        try {
            imagemGameOver = ImageIO.read(getClass().getClassLoader().getResource("Assets/gameover.png"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem de Game Over: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Atualiza o contador de FPS.
     */
    public void atualizarContadorFPS() {
        this.contadorFPS.atualizar();
    }
}