package Modelo.Projeteis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Personagem;

/**
 * @brief Representa o efeito da bomba do herói, que cria uma área de dano em expansão
 *        e lança mísseis teleguiados ao terminar.
 */
public class BombaProjetil extends Personagem {
    private int duracao = 20;
    private final int DURACAO_MAXIMA = 20;
    private double raioMaximoGrid;
    private double raioAtualGrid = 0;

    private transient Fase faseReferencia;
    private transient Hero heroReferencia;

    private static final Color BOMB_COLOR = new Color(255, 255, 255, 100);

    /**
     * @brief Construtor da bomba.
     */
    public BombaProjetil(double x, double y, Fase fase, Hero hero) {
        super("hero/hero_s0.png", x, y);
        this.bTransponivel = true;
        this.bMortal = false;
        this.raioMaximoGrid = (LARGURA_TELA / (double) CELL_SIDE) / 2.0;
        this.faseReferencia = fase;
        this.heroReferencia = hero;
    }

    /**
     * @brief Atualiza o raio de expansão da bomba a cada frame.
     */
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

    /**
     * @brief Desativa a bomba e lança os mísseis teleguiados.
     */
    @Override
    public void deactivate() {
        if (isActive()) {
            lancarMisseis();
        }
        super.deactivate();
    }

    /**
     * @brief Desenha a área de efeito circular da bomba.
     */
    @Override
    public void autoDesenho(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int centroX = (int) (this.x * CELL_SIDE);
        int centroY = (int) (this.y * CELL_SIDE);
        int raioPixels = (int) (this.raioAtualGrid * CELL_SIDE);

        g2d.setColor(BOMB_COLOR);
        g2d.fillOval(
            centroX - raioPixels,
            centroY - raioPixels,
            raioPixels * 2,
            raioPixels * 2
        );

        super.autoDesenho(g);
    }

    /**
     * @brief Lança uma barragem de mísseis teleguiados em um padrão circular.
     */
    private void lancarMisseis() {
        if (faseReferencia == null || heroReferencia == null)
            return;

        ProjetilPool pool = faseReferencia.getProjetilPool();
        final int NUMERO_DE_MISSEIS = 16;

        for (int i = 0; i < NUMERO_DE_MISSEIS; i++) {
            ProjetilBombaHoming p = pool.getProjetilBombaHoming();
            if (p != null) {
                double anguloExpansao = i * 22.5;
                double velocidadeMissil = 5.0 / CELL_SIDE;
                p.resetBombaHoming(
                    heroReferencia.x,
                    heroReferencia.y,
                    velocidadeMissil,
                    anguloExpansao,
                    TipoProjetil.JOGADOR,
                    TipoProjetilHeroi.BOMBA
                );
            }
        }
    }

    /**
     * @brief Retorna o raio atual da bomba em unidades de grid.
     */
    public double getRaioAtualGrid() {
        return this.raioAtualGrid;
    }
}