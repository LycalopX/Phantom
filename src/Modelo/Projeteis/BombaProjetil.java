package Modelo.Projeteis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;
import Modelo.Personagem;
import Modelo.RenderLayer;
import java.util.List;
import Modelo.Inimigos.Inimigo;

/**
 * @brief Representa o efeito visual e funcional da bomba do herói.
 * 
 *        Cria uma área de dano circular que se expande e, ao terminar, lança
 *        uma
 *        barragem de mísseis teleguiados.
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
     * @brief Construtor da BombaProjetil.
     * @param x    Posição X inicial.
     * @param y    Posição Y inicial.
     * @param fase A referência da fase para interações.
     * @param hero A referência do herói.
     */
    public BombaProjetil(double x, double y, Fase fase, Hero hero) {
        super("hero/hero_s0.png", x, y);
        this.bTransponivel = true;
        this.bMortal = false;
        this.raioMaximoGrid = (LARGURA_TELA / (double) CELL_SIDE) / 2.0;
        this.faseReferencia = fase;
        this.heroReferencia = hero;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.PLAYER_LAYER;
    }

    /**
     * @brief Atualiza o estado da bomba a cada frame.
     * 
     *        Controla a expansão do raio da bomba ao longo de sua duração. Quando
     *        a duração termina, a bomba é desativada.
     */
    @Override
    public void atualizar() {
        if (duracao > 0) {
            duracao--;
            // A progressão do raio usa Math.sqrt para um efeito de "ease-out",
            // expandindo mais rápido no início e desacelerando no final.
            double progress = 1.0 - ((double) duracao / DURACAO_MAXIMA);
            raioAtualGrid = raioMaximoGrid * Math.sqrt(progress);
            this.hitboxRaio = raioAtualGrid;
        } else {
            deactivate();
        }
    }

    /**
     * @brief Desativa a bomba e dispara a barragem de mísseis.
     * 
     *        Garante que os mísseis sejam lançados apenas uma vez, na primeira
     *        vez que a bomba é desativada.
     */
    @Override
    public void deactivate() {
        if (isActive()) {
            lancarMisseis();
        }
        super.deactivate();
    }

    /**
     * @brief Desenha o efeito visual da bomba na tela.
     * 
     *        Renderiza um círculo translúcido que representa a área de efeito
     *        da bomba.
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
                raioPixels * 2);

        super.autoDesenho(g);
    }

    /**
     * @brief Lança uma barragem de mísseis teleguiados.
     * 
     *        Cria múltiplos projéteis do tipo `ProjetilBombaHoming` em um padrão
     *        circular, que irão perseguir os inimigos na tela.
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
                double velocidadeMissil = FATOR_ESCALA_ALTURA * 5.0 / CELL_SIDE;
                p.resetBombaHoming(
                        heroReferencia.getX(),
                        heroReferencia.getY(),
                        velocidadeMissil,
                        anguloExpansao,
                        TipoProjetil.JOGADOR,
                        TipoProjetilHeroi.BOMBA,
                        (List<Inimigo>) faseReferencia.getInimigos());
            }
        }
    }

    /**
     * @brief Retorna o raio atual da bomba em unidades de grid.
     * @return O raio atual.
     */
    public double getRaioAtualGrid() {
        return this.raioAtualGrid;
    }
}