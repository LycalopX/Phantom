package Controler;

import javax.swing.*;
import Auxiliar.*;
import Modelo.*;
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
import java.util.HashSet;
import java.util.Set;

public class Cenario extends JPanel implements Runnable {

    // --- LÓGICA DO JOGO ---
    private Hero hero;
    private ArrayList<Personagem> faseAtual;
    private ControleDeJogo cj = new ControleDeJogo();
    
    // --- LÓGICA DO CENÁRIO DINÂMICO ---
    private BufferedImage imagemFundo1, imagemFundo2;
    private double scrollY = 0;
    private final Random random = new Random();
    private final List<ArvoreParallax> arvoresNaTela = new ArrayList<>();
    private final List<Projetil> projeteis = new ArrayList<>();
    private final ContadorFPS contadorFPS = new ContadorFPS();
    private double velocidadeAtual;
    private boolean oscilacaoAtiva = false;
    private double cicloOnda = 0;
    private Thread gameThread;
    private double distanciaTotalRolada = 0;
    private long proximoSpawnY = 0;
    private final int[] posicoesXDasDiagonais;
    private int direcaoDoGrupo = 1;

    // --- VARIÁVEIS PARA MOVIMENTO SUAVE E HUD ---
    private final Set<Integer> teclasPressionadas = new HashSet<>();
    private boolean exibirHUD = true;

    // --- CONSTANTES ---
    private static final int LARGURA_TELA = 640;
    private static final int ALTURA_TELA = 640;
    private static final int FPS = 60;
    private static final String BG1_PATH = "imgs/stage1/stage_1_bg1.png";
    private static final String BG2_PATH = "imgs/stage1/stage_1_bg2.png";
    private static final double VELOCIDADE_BASE = 2.0;
    private static final double AMPLITUDE_VELOCIDADE = 1.0;
    private static final double PERIODO_ONDA = 600;
    private static final double VELOCIDADE_HERO = 4.0; // Velocidade do herói em células/segundo
    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;
    private static final int OFFSET_DIAGONAL_X = 100;
    private static final int VARIACAO_ALEATORIA_PIXELS = 40;
    private static final int NUMERO_DE_DIAGONAIS = 3;
    private static final int ESPACO_ENTRE_DIAGONAIS_X = 500;

    public Cenario() {
        this.setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        this.setFocusable(true);
        this.setBackground(Color.BLACK);

        carregarImagens();
        iniciarJogo();
        configurarTeclado();
        
        posicoesXDasDiagonais = new int[NUMERO_DE_DIAGONAIS];
        for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
            posicoesXDasDiagonais[i] = 50 + (i * ESPACO_ENTRE_DIAGONAIS_X);
        }
    }

    private void carregarImagens() {
        try {
            imagemFundo1 = ImageIO.read(new File(BG1_PATH));
            imagemFundo2 = ImageIO.read(new File(BG2_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarJogo() {
        faseAtual = new ArrayList<>();
        // Apenas o herói é adicionado, no centro da tela
        double xInicial = (LARGURA_TELA / 2.0) / Consts.CELL_SIDE;
        double yInicial = (ALTURA_TELA / 2.0) / Consts.CELL_SIDE;
        hero = new Hero("Hero.png", xInicial, yInicial);
        faseAtual.add(hero);
    }

    private void configurarTeclado() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclasPressionadas.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    exibirHUD = !exibirHUD;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                teclasPressionadas.remove(e.getKeyCode());
            }
        });
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        this.requestFocusInWindow();
    }

    public void atualizar() {
        // Atualiza velocidade do fundo
        velocidadeAtual = oscilacaoAtiva ? VELOCIDADE_BASE + (Math.sin(cicloOnda) * AMPLITUDE_VELOCIDADE) : VELOCIDADE_BASE;
        if(oscilacaoAtiva) cicloOnda += (2 * Math.PI) / PERIODO_ONDA; else cicloOnda = 0;
        scrollY = (scrollY + velocidadeAtual) % ALTURA_TELA;
        distanciaTotalRolada += velocidadeAtual;
        
        atualizarArvores();
        atualizarMovimentoHeroi();
        cj.processaTudo(faseAtual);
        
        for(Projetil p : projeteis) { p.mover(); }
        projeteis.removeIf(p -> p.estaForaDaTela(LARGURA_TELA, ALTURA_TELA));
    }

    private void atualizarMovimentoHeroi() {
        double delta = VELOCIDADE_HERO / FPS;
        double dx = 0, dy = 0;

        if (teclasPressionadas.contains(KeyEvent.VK_UP)) dy -= delta;
        if (teclasPressionadas.contains(KeyEvent.VK_DOWN)) dy += delta;
        if (teclasPressionadas.contains(KeyEvent.VK_LEFT)) dx -= delta;
        if (teclasPressionadas.contains(KeyEvent.VK_RIGHT)) dx += delta;
        if (teclasPressionadas.contains(KeyEvent.VK_B)) spawnProjeteis(50);
        if (teclasPressionadas.contains(KeyEvent.VK_SPACE)) toggleOscilacao();

        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        if (cj.ehPosicaoValida(faseAtual, hero, hero.x + dx, hero.y + dy)) {
            hero.x += dx;
            hero.y += dy;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        desenharFundo(g2d);
        cj.desenhaTudo(faseAtual, g2d);
        for (Projetil p : projeteis) { p.desenhar(g2d); }
        if (exibirHUD) {
            desenharHUD(g2d);
        }
    }

    private void desenharFundo(Graphics2D g2d) {
        if (imagemFundo1 == null) return;
        int yPos = (int) scrollY;
        g2d.drawImage(imagemFundo1, 0, yPos, LARGURA_TELA, ALTURA_TELA, this);
        g2d.drawImage(imagemFundo1, 0, yPos - ALTURA_TELA, LARGURA_TELA, ALTURA_TELA, this);
        
        int escuridaoGeral = 150;
        g2d.setColor(new Color(0, 0, 50, escuridaoGeral));
        g2d.fillRect(0, 0, LARGURA_TELA, ALTURA_TELA);

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, ALTURA_TELA);
        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { new Color(0, 0, 50, 255), new Color(0, 0, 50, 100), new Color(0, 0, 50, 0) };
        LinearGradientPaint gradiente = new LinearGradientPaint(start, end, fractions, colors);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, LARGURA_TELA, ALTURA_TELA);
        
        for(ArvoreParallax arvore : arvoresNaTela){
            arvore.desenhar(g2d, ALTURA_TELA);
        }
    }

    private void desenharHUD(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Projéteis: " + projeteis.size(), 10, 30);
        g2d.drawString(contadorFPS.getFPSString(), 10, 55);
    }
    
    @Override
    public void run() {
        preencherCenarioInicial();
        
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                atualizar();
                repaint();
                contadorFPS.atualizar();
                delta--;
            }
        }
    }

    private void atualizarArvores() {
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

    public void spawnProjeteis(int quantidade) {
        int centroX = (int)(hero.x * Consts.CELL_SIDE);
        int centroY = (int)(hero.y * Consts.CELL_SIDE);
        for (int i = 0; i < quantidade; i++) {
            double angulo = (360.0 / quantidade) * i;
            Color cor = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, 255);
            projeteis.add(new Projetil(centroX, centroY, angulo, 2.5, 12, cor));
        }
    }

    public void toggleOscilacao() { 
        this.oscilacaoAtiva = !this.oscilacaoAtiva;
    }
    
    private void preencherCenarioInicial() {
        int alturaTela = getHeight();
        if (alturaTela <= 0) alturaTela = ALTURA_TELA;

        int larguraTela = getWidth();
        if (larguraTela <= 0) larguraTela = LARGURA_TELA;

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
        
        if (proximoSpawnY >= DISTANCIA_ENTRE_ONDAS_Y) {
            distanciaTotalRolada = proximoSpawnY - DISTANCIA_ENTRE_ONDAS_Y;
        }
        scrollY = distanciaTotalRolada % alturaTela;
    }
}