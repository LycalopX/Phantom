package Modelo.Projeteis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Personagem;

public class BombaProjetil extends Personagem {
    private int duracao = 20;
    private final int DURACAO_MAXIMA = 20;
    private double raioMaximoGrid;
    private double raioAtualGrid = 0;

    private transient Fase faseReferencia;
    private transient Hero heroReferencia;

    public BombaProjetil(double x, double y, Fase fase, Hero hero) {
        super("hero/hero_s0.png", x, y);
        this.bTransponivel = true;
        this.bMortal = false;
        this.raioMaximoGrid = (LARGURA_TELA / (double) CELL_SIDE) / 2.0;

        this.faseReferencia = fase;
        this.heroReferencia = hero;
    }

    @Override
    public void atualizar() {
        if (duracao > 0) {
            duracao--;
            double progress = 1.0 - ((double) duracao / DURACAO_MAXIMA);
            raioAtualGrid = raioMaximoGrid * Math.sqrt(progress);
            this.hitboxRaio = raioAtualGrid;
        } else {
            deactivate();
        }
    }

    @Override
    public void deactivate() {
        // Só executa a lógica de spawn uma vez, se já estiver ativo.
        if (isActive()) {
            lancarMisseis();
        }
        // Chama o método da classe pai para definir 'isActive = false'.
        super.deactivate();
    }

    @Override
    public void autoDesenho(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int centroX = (int) (this.x * CELL_SIDE);
        int centroY = (int) (this.y * CELL_SIDE);
        int raioPixels = (int) (this.raioAtualGrid * CELL_SIDE);

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(centroX - raioPixels, centroY - raioPixels, raioPixels * 2, raioPixels * 2);

        super.autoDesenho(g);
    }

    private void lancarMisseis() {
        if (faseReferencia == null || heroReferencia == null)
            return;

        ProjetilPool pool = faseReferencia.getProjetilPool();
        final int NUMERO_DE_MISSEIS = 16;

        for (int i = 0; i < NUMERO_DE_MISSEIS; i++) {
            ProjetilBombaHoming p = pool.getProjetilBombaHoming();
            if (p != null) {
                // Calcula o ângulo para formar um círculo (360 / 16 = 22.5 graus por míssil)
                double anguloExpansao = i * 22.5;
                double velocidadeMissil = 5.0 / CELL_SIDE;

                // Configura o míssil para começar na posição ATUAL do herói
                p.resetBombaHoming(
                        heroReferencia.x,
                        heroReferencia.y,
                        velocidadeMissil, // velocidadeGrid
                        anguloExpansao,
                        TipoProjetil.JOGADOR,
                        TipoProjetilHeroi.BOMBA);
            }
        }
    }

    public double getRaioAtualGrid() {
        return this.raioAtualGrid;
    }
}