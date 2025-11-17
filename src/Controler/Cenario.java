package Controler;

import Modelo.Personagem;
import Modelo.Fases.Fase;
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
 * @brief Painel principal do jogo, responsável por desenhar todos os elementos
 *        visuais
 *        da fase, HUD e telas de estado (como Game Over).
 */
public class Cenario extends JPanel {
    private Fase faseAtual;
    private ContadorFPS contadorFPS;
    private Engine.GameState estadoDoJogo;
    private BufferedImage imagemGameOver;

    // Otimização: Pré-alocar objetos de desenho para evitar criação em loop
    private Color corFundoOverlay;
    private final Color corDeathbombOverlay = new Color(255, 0, 0, 30);
    private final Font fonteHUD = new Font("Arial", Font.BOLD, 20);
    private LinearGradientPaint gradienteFundo;

    /**
     * @brief Construtor do Cenario. Configura as dimensões, foco, cor de fundo,
     *        contador de FPS e a funcionalidade de arrastar e soltar
     *        (drag-and-drop).
     */
    public Cenario() {
        this.setPreferredSize(new Dimension(ConfigMapa.LARGURA_TELA, ConfigMapa.ALTURA_TELA));
        this.setFocusable(false);
        this.setBackground(Color.BLACK);
        this.contadorFPS = new ContadorFPS();

        setupDropTarget();
    }

    /**
     * @brief Configura a área de drop para a funcionalidade de arrastar e soltar,
     *        permitindo adicionar inimigos ao jogo dinamicamente no modo de debug.
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
     * @brief Processa um arquivo .zip solto na tela, desserializando um personagem
     *        e o adicionando na fase na posição do mouse.
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
     * @brief Define a fase atual a ser desenhada pelo cenário.
     */
    public void setFase(Fase fase) {
        this.faseAtual = fase;
        if (fase != null && fase.getScript() != null) {
            this.corFundoOverlay = fase.getScript().getBackgroundOverlayColor();
            this.gradienteFundo = fase.getScript().getBackgroundGradient();
        }
    }

    /**
     * @brief Método principal de desenho do Swing. Renderiza o estado atual do
     *        jogo.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (faseAtual == null) {
            return;
        }

        if (estadoDoJogo == null || estadoDoJogo == Engine.GameState.JOGANDO
                || estadoDoJogo == Engine.GameState.RESPAWNANDO || estadoDoJogo == Engine.GameState.DEATHBOMB_WINDOW) {

            Graphics2D g2d = (Graphics2D) g;

            // 1. Desenha os elementos da camada de fundo
            for (var elemento : faseAtual.getElementosCenario()) {
                if (elemento.getDrawLayer() == Modelo.Cenario.DrawLayer.BACKGROUND) {
                    elemento.desenhar(g2d, getWidth(), getHeight());
                }
            }

            // 2. Aplica os gradientes de cor sobre o fundo
            g2d.setColor(corFundoOverlay);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setPaint(gradienteFundo);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // 3. Desenha os elementos da camada de frente
            for (var elemento : faseAtual.getElementosCenario()) {
                if (elemento.getDrawLayer() == Modelo.Cenario.DrawLayer.FOREGROUND) {
                    elemento.desenhar(g2d, getWidth(), getHeight());
                }
            }

            if (estadoDoJogo == Engine.GameState.DEATHBOMB_WINDOW) {
                g.setColor(corDeathbombOverlay);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // 4. Desenha os personagens com ordenação de camada
            ArrayList<Personagem> personagensParaRenderizar = new ArrayList<>(faseAtual.getPersonagens());
            personagensParaRenderizar.sort(Comparator.comparing(p -> p.getRenderLayer().ordinal()));

            for (Personagem p : personagensParaRenderizar) {
                if (p.isActive()) {
                    p.autoDesenho(g);
                }
            }

            if (DebugManager.isActive()) {
                desenharHUD(g2d);
            }
        } else if (estadoDoJogo == Engine.GameState.GAME_OVER) {
            desenharTelaGameOver(g);
            SoundManager.getInstance().stopAllMusic();
        }
    }

    /**
     * @brief Desenha o Heads-Up Display (HUD) com informações de debug, como FPS e
     *        status do herói.
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
     * @brief Define o estado atual do jogo, para controlar o que é desenhado.
     */
    public void setEstadoDoJogo(Engine.GameState estado) {
        this.estadoDoJogo = estado;
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
     * @brief Carrega a imagem da tela de Game Over.
     */
    private void carregarImagensGameOver() {
        try {
            imagemGameOver = ImageIO.read(getClass().getClassLoader().getResource("imgs/gameover.png"));
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