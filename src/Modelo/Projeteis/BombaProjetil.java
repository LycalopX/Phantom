package Modelo.Projeteis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import Auxiliar.Consts;
import Modelo.Personagem;
import Modelo.Hero.Hero;

public class BombaProjetil extends Personagem {
    private int duracao = 20;
    private final int DURACAO_MAXIMA = 20;
    private double raioMaximoGrid;
    private double raioAtualGrid = 0;

    public BombaProjetil(double x, double y) {
        // Construtor chama o de Personagem
        super("hero/hero_s0.png", x, y); 
        this.bTransponivel = true;
        this.bMortal = false; // Não é mortal para o herói
        this.raioMaximoGrid = (Consts.largura / (double)Consts.CELL_SIDE) / 2.0;
        this.vida = 1; // Garante que começa vivo
    }

    @Override
    public void atualizar() {
        if (duracao > 0) {
            duracao--;
            double progress = 1.0 - ((double)duracao / DURACAO_MAXIMA);
            raioAtualGrid = raioMaximoGrid * Math.sqrt(progress);
            this.hitboxRaio = raioAtualGrid;
        } else {
            // Sinaliza para si mesmo ser removido
            this.setVida(0); 
            deactivate();
        }
    }

    @Override
    public void autoDesenho(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int centroX = (int) (this.x * Consts.CELL_SIDE);
        int centroY = (int) (this.y * Consts.CELL_SIDE);
        int raioPixels = (int) (this.raioAtualGrid * Consts.CELL_SIDE);
        
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(centroX - raioPixels, centroY - raioPixels, raioPixels * 2, raioPixels * 2);
        
        super.autoDesenho(g);
    }
}