package Controler;
// Pacote Controler ou View

import Modelo.Fase;
import Modelo.Hero;
import Modelo.Personagem;
import Auxiliar.Projetil;
import Auxiliar.ArvoreParallax;
import Auxiliar.Consts;

import java.awt.*;
import javax.swing.*;
import Auxiliar.ContadorFPS;
import Auxiliar.DebugManager;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Cenario extends JPanel {
    private Fase faseAtual;
    private ContadorFPS contadorFPS;
    private boolean exibirHUD = true;

    public Cenario() {
        this.setPreferredSize(new Dimension(Consts.largura, Consts.altura));
        this.setFocusable(false);
        this.setBackground(Color.BLACK);
        this.contadorFPS = new ContadorFPS();
    }

    public void setFase(Fase fase) {
        this.faseAtual = fase;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (faseAtual == null)
            return;

        Graphics2D g2d = (Graphics2D) g;

        desenharFundo(g2d);

        // Pede os elementos para a fase e os desenha
        // CORRIGIDO: Pega a lista de árvores da faseAtual
        for (ArvoreParallax arvore : faseAtual.getArvores()) {
            arvore.desenhar(g2d, getHeight());
        }

        for (Personagem p : faseAtual.getPersonagens()) {
            p.autoDesenho(g);
        }

        // CORRIGIDO: Pega a lista de projéteis da faseAtual
        for (Projetil p : faseAtual.getProjeteis()) {
            p.desenhar(g2d);
        }

        if (DebugManager.isActive()) { // <<-- Nova linha
            desenharHUD(g2d);
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
        
        g2d.drawString("Projéteis: " + faseAtual.getProjeteis().size(), 10, 30);
        g2d.drawString(contadorFPS.getFPSString(), 10, 55);

        Personagem heroi = faseAtual.getHero(); // Você precisará criar este método
        if (heroi instanceof Hero) { // Boa prática verificar o tipo
            g2d.drawString("Bombas: " + ((Hero) heroi).getBombas(), 10, 80);
        }

    }

    public void atualizarContadorFPS() {
        this.contadorFPS.atualizar();
    }
}