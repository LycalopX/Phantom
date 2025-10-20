package Controler;
// Pacote Controler ou View

import Modelo.Personagem;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.Item;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.BombaProjetil;
import Auxiliar.ConfigMapa;
import Auxiliar.Cenario1.ArvoreParallax;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import Auxiliar.Debug.ContadorFPS;
import Auxiliar.Debug.DebugManager;
import Auxiliar.Projeteis.TipoProjetil;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Cenario extends JPanel {
    private Fase faseAtual;
    private ContadorFPS contadorFPS;
    private Engine.GameState estadoDoJogo;
    private BufferedImage imagemGameOver;

    // Listas para controlar a ordem de desenho
    private final ArrayList<Personagem> ProjeteisJogador = new ArrayList<>();
    private final ArrayList<Personagem> ProjeteisInimigos = new ArrayList<>();
    private final ArrayList<Personagem> HeroItemBombaProjetil = new ArrayList<>();

    public Cenario() {
        this.setPreferredSize(new Dimension(ConfigMapa.LARGURA_TELA, ConfigMapa.ALTURA_TELA));
        this.setFocusable(false);
        this.setBackground(Color.BLACK);
        this.contadorFPS = new ContadorFPS();
    }

    public void setFase(Fase fase) {
        this.faseAtual = fase;
    }
    // Em Controler/Cenario.java

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (estadoDoJogo == null || estadoDoJogo == Engine.GameState.JOGANDO
                || estadoDoJogo == Engine.GameState.RESPAWNANDO || estadoDoJogo == Engine.GameState.DEATHBOMB_WINDOW) {

            if (faseAtual == null)
                return;
            Graphics2D g2d = (Graphics2D) g;

            // 1. Fundo
            desenharFundo(g2d);
            // 2. Árvores
            for (ArvoreParallax arvore : faseAtual.getArvores()) {
                arvore.desenhar(g2d, getHeight());
            }

            ArrayList<Personagem> personagensParaDesenhar = new ArrayList<>(faseAtual.getPersonagens());

            if (estadoDoJogo == Engine.GameState.DEATHBOMB_WINDOW) {
                // Define a cor para um vermelho com 80/255 de opacidade (cerca de 31%)
                g.setColor(new Color(255, 0, 0, 30));
                // Desenha um retângulo que cobre a tela inteira
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            ProjeteisJogador.clear();
            HeroItemBombaProjetil.clear();
            ProjeteisInimigos.clear();

            // 3. Desenha Projéteis do JOGADOR
            for (Personagem p : personagensParaDesenhar) {
                if (p instanceof Projetil && ((Projetil) p).getTipo() == TipoProjetil.JOGADOR) {
                    ProjeteisJogador.add(p);
                }
                if (p instanceof Hero || p instanceof Item || p instanceof BombaProjetil) {
                    HeroItemBombaProjetil.add(p);
                }
                if (p instanceof Inimigo
                        || (p instanceof Projetil && ((Projetil) p).getTipo() == TipoProjetil.INIMIGO)) {
                    ProjeteisInimigos.add(p);
                }
            }

            for (Personagem p : ProjeteisJogador) {
                p.autoDesenho(g);
            }
            for (Personagem p : HeroItemBombaProjetil) {
                p.autoDesenho(g);
            }
            for (Personagem p : ProjeteisInimigos) {
                p.autoDesenho(g);
            }

            // 6. HUD
            if (DebugManager.isActive()) {
                desenharHUD(g2d);
            }
        } else if (estadoDoJogo == Engine.GameState.GAME_OVER) {
            desenharTelaGameOver(g); // Chama o método de desenho do Game Over
        }

    }

    private void desenharFundo(Graphics2D g2d) {
        // CORRIGIDO: Pega a imagem e a posição do scroll da faseAtual
        BufferedImage imagemFundo1 = faseAtual.getImagemFundo1();
        if (imagemFundo1 == null)
            return;

        int yPos = (int) faseAtual.getScrollY();
        g2d.drawImage(imagemFundo1, 0, yPos, getWidth(), getHeight(), this);
        g2d.drawImage(imagemFundo1, 0, yPos - getHeight(), getWidth(), getHeight(), this);

        int escuridaoGeral = 150;
        g2d.setColor(new Color(0, 0, 50, escuridaoGeral));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, getHeight());
        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { new Color(0, 0, 50, 255), new Color(0, 0, 50, 100), new Color(0, 0, 50, 0) };
        LinearGradientPaint gradiente = new LinearGradientPaint(start, end, fractions, colors);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void desenharHUD(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        g2d.drawString(contadorFPS.getFPSString(), 10, 60);

        Personagem heroi = faseAtual.getHero(); // Você precisará criar este método
        if (heroi instanceof Hero) { // Boa prática verificar o tipo
            Hero h = (Hero) heroi;

            g2d.drawString("Bombas: " + h.getBombas(), 10, 80);
            g2d.drawString("HP: " + h.getHP(), 10, 100);
            g2d.drawString("Power: " + h.getPower(), 10, 120);
            g2d.drawString("Score: " + h.getScore(), 10, 140);
            g2d.drawString("Mísseis: " + h.getNivelDeMisseis(), 10, 160);
        }

    }

    public void setEstadoDoJogo(Engine.GameState estado) { // << Adapte o GameState para ser visível publicamente na
                                                           // Engine
        this.estadoDoJogo = estado;
    }

    private void desenharTelaGameOver(Graphics g) {
        // 1. Desenha a imagem de fundo
        if (imagemGameOver != null) {
            g.drawImage(imagemGameOver, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback para um fundo preto se a imagem não carregar
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        carregarImagensGameOver();
    }

    private void carregarImagensGameOver() {
        try {
            // Usa o ClassLoader
            imagemGameOver = ImageIO.read(getClass().getClassLoader().getResource("imgs/gameover.png"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem de Game Over: " + e.getMessage());
            e.printStackTrace();
            imagemGameOver = null;
        }
    }

    public void atualizarContadorFPS() {
        this.contadorFPS.atualizar();
    }
}
