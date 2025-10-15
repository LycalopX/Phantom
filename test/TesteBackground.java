import javax.swing.*;
import Auxiliar.ArvoreParallax;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TesteBackground extends JPanel implements Runnable {

    // --- CONFIGURAÇÕES GERAIS ---
    private static final String BG1_PATH = "imgs/stage1/stage_1_bg1.png";
    private static final String BG2_PATH = "imgs/stage1/stage_1_bg2.png";
    private static final int LARGURA_TELA_INICIAL = 780;
    private static final int ALTURA_TELA_INICIAL = 640;
    private static final int FPS = 60;

    // --- Configurações de velocidade com double e oscilação ---
    private static final double VELOCIDADE_BASE = 2.0;
    private static final double AMPLITUDE_VELOCIDADE = 10.0;
    private static final double PERIODO_ONDA = 600;

    // --- CONFIGURAÇÕES DA CAMADA 2 (ÁRVORES) ---
    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;
    private static final int OFFSET_DIAGONAL_X = 100;
    private static final int VARIACAO_ALEATORIA_PIXELS = 40;
    private static final int NUMERO_DE_DIAGONAIS = 3;
    private static final int ESPACO_ENTRE_DIAGONAIS_X = 500;

    // --- Variáveis de estado convertidas/adicionadas ---
    private BufferedImage imagemFundo1, imagemFundo2;
    private double scrollY = 0;
    private Thread gameThread;
    private final Random random = new Random();
    private final List<ArvoreParallax> arvoresNaTela = new ArrayList<>();
    private double distanciaTotalRolada = 0;
    private long proximoSpawnY = 0;
    private final int[] posicoesXDasDiagonais;
    private int direcaoDoGrupo = 1;
    private boolean isInitialized = false;

    private double velocidadeAtual = VELOCIDADE_BASE;
    private double cicloOnda = 0;
    private boolean oscilacaoAtiva = false;

    public TesteBackground() {
        this.setPreferredSize(new Dimension(LARGURA_TELA_INICIAL, ALTURA_TELA_INICIAL));
        this.setBackground(Color.BLACK);
        
        // --- MUDANÇA 1: Torna o painel "focável" para que ele possa receber eventos do teclado ---
        this.setFocusable(true); 

        posicoesXDasDiagonais = new int[NUMERO_DE_DIAGONAIS];
        for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
            posicoesXDasDiagonais[i] = 50 + (i * ESPACO_ENTRE_DIAGONAIS_X);
        }
        try {
            imagemFundo1 = ImageIO.read(new File(BG1_PATH));
            imagemFundo2 = ImageIO.read(new File(BG2_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // --- MUDANÇA 2: Adiciona o KeyListener diretamente ao PAINEL ---
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    toggleOscilacao();
                }
            }
        });
    }

    private void preencherCenarioInicial() {
        int alturaTela = getHeight();
        int larguraTela = getWidth();
        int tamanhoBase = (int) Math.round(alturaTela * 0.8);
        while (proximoSpawnY < alturaTela + tamanhoBase) {
            boolean vaiBaterNaDireita = direcaoDoGrupo == 1 && (posicoesXDasDiagonais[NUMERO_DE_DIAGONAIS - 1] + tamanhoBase) > larguraTela;
            boolean vaiBaterNaEsquerda = direcaoDoGrupo == -1 && posicoesXDasDiagonais[0] < 0;
            if (vaiBaterNaDireita || vaiBaterNaEsquerda) {
                direcaoDoGrupo *= -1;
            }
            for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
                int xBase = posicoesXDasDiagonais[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = random.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;
                int yInicialCorreto = -tamanhoBase + (int)proximoSpawnY;
                arvoresNaTela.add(new ArvoreParallax(novoX + randomOffsetX, yInicialCorreto, tamanhoBase, VELOCIDADE_BASE, imagemFundo2));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    public void atualizar() {
        if (oscilacaoAtiva) {
            double onda = Math.abs(Math.sin(cicloOnda));
            velocidadeAtual = VELOCIDADE_BASE + (onda * AMPLITUDE_VELOCIDADE);
            cicloOnda += (2 * Math.PI) / PERIODO_ONDA;
        } else {
            velocidadeAtual = VELOCIDADE_BASE;
            cicloOnda = 0;
        }

        scrollY += velocidadeAtual;
        distanciaTotalRolada += velocidadeAtual;
        if (scrollY >= getHeight()) {
            scrollY -= getHeight();
        }

        if (imagemFundo2 != null && distanciaTotalRolada >= proximoSpawnY) {
            int tamanhoBase = (int) Math.round(getHeight() * 0.8);
            boolean vaiBaterNaDireita = direcaoDoGrupo == 1 && (posicoesXDasDiagonais[NUMERO_DE_DIAGONAIS - 1] + tamanhoBase) > getWidth();
            boolean vaiBaterNaEsquerda = direcaoDoGrupo == -1 && posicoesXDasDiagonais[0] < 0;
            if (vaiBaterNaDireita || vaiBaterNaEsquerda) {
                direcaoDoGrupo *= -1;
            }
            for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
                int xBase = posicoesXDasDiagonais[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = random.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;
                arvoresNaTela.add(new ArvoreParallax(novoX + randomOffsetX, -tamanhoBase, tamanhoBase, velocidadeAtual, imagemFundo2));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
        for (ArvoreParallax arvore : arvoresNaTela) {
            arvore.mover(velocidadeAtual);
        }
        arvoresNaTela.removeIf(arvore -> arvore.estaForaDaTela(getHeight()));
    }
    
    public void toggleOscilacao() {
        this.oscilacaoAtiva = !this.oscilacaoAtiva;
        System.out.println("Oscilação de velocidade: " + (oscilacaoAtiva ? "ATIVADA" : "DESATIVADA"));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (imagemFundo1 == null) return;
        int larguraTela = getWidth();
        int alturaTela = getHeight();

        int yPos = (int) scrollY;
        g2d.drawImage(imagemFundo1, 0, yPos, larguraTela, alturaTela, this);
        g2d.drawImage(imagemFundo1, 0, yPos - alturaTela, larguraTela, alturaTela, this);

        int escuridaoGeral = 150;
        g2d.setColor(new Color(0, 0, 50, escuridaoGeral));
        g2d.fillRect(0, 0, larguraTela, alturaTela);
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, alturaTela);
        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { new Color(0, 0, 50, 255), new Color(0, 0, 50, 100), new Color(0, 0, 50, 0) };
        LinearGradientPaint gradiente = new LinearGradientPaint(start, end, fractions, colors);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, larguraTela, alturaTela);
        for (ArvoreParallax arvore : arvoresNaTela) {
            arvore.desenhar(g2d, alturaTela);
        }
        g2d.dispose();
    }

    public void startGameThread() {
        if (!isInitialized) {
            preencherCenarioInicial();
            if (this.proximoSpawnY >= DISTANCIA_ENTRE_ONDAS_Y) {
                this.distanciaTotalRolada = this.proximoSpawnY - DISTANCIA_ENTRE_ONDAS_Y;
            }
            this.scrollY = this.distanciaTotalRolada % getHeight();
            isInitialized = true;
        }
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                atualizar();
                repaint();
                delta--;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cenário com Efeito Parallax");
            TesteBackground panel = new TesteBackground();
            frame.add(panel);
            
            // --- MUDANÇA 3: O KeyListener foi REMOVIDO do frame ---
            
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);
            panel.startGameThread();
        });
    }
}