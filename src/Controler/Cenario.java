package Controler;
// Pacote Controler ou View

import Modelo.Personagem;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.Item;
import Modelo.Projeteis.Projetil;
import Auxiliar.TipoProjetil;
import Auxiliar.ArvoreParallax;
import Auxiliar.Consts;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import Auxiliar.ContadorFPS;
import Auxiliar.DebugManager;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Cenario extends JPanel {
    private Fase faseAtual;
    private ContadorFPS contadorFPS;
    private Engine.GameState estadoDoJogo;
    private BufferedImage imagemGameOver;
    
    public Cenario() {
        this.setPreferredSize(new Dimension(Consts.largura, Consts.altura));
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
                || estadoDoJogo == Engine.GameState.RESPAWNANDO) {

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

            // 3. Desenha Projéteis do JOGADOR
            for (Personagem p : personagensParaDesenhar) {
                if (p instanceof Projetil && ((Projetil) p).getTipo() == TipoProjetil.JOGADOR) {
                    p.autoDesenho(g);
                }
            }

            for (Personagem p : personagensParaDesenhar) {
                if (p instanceof Hero || p instanceof Item) {
                    p.autoDesenho(g);
                }
            }

            for (Personagem p : personagensParaDesenhar) {
                if (p instanceof Inimigo
                        || (p instanceof Projetil && ((Projetil) p).getTipo() == TipoProjetil.INIMIGO)) {
                    p.autoDesenho(g);
                }
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

        g2d.drawString(contadorFPS.getFPSString(), 10, 55);

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
            
            String basePath = new File(".").getCanonicalPath() + Consts.PATH;
            imagemGameOver = ImageIO.read(new File(basePath + "gameover.png"));

        } catch (IOException e) {
            System.out.println("Erro ao carregar imagem de Game Over: " + e.getMessage());
            e.printStackTrace();
            imagemGameOver = null; // Garante que é null se falhar
        }
    }

    public void atualizarContadorFPS() {
        this.contadorFPS.atualizar();
    }
}