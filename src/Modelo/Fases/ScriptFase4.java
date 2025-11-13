package Modelo.Fases;

import Auxiliar.Cenario4.BambuParallax;
import Modelo.Cenario.FundoInfinito;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Random;


import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import Auxiliar.ConfigMapa;

public class ScriptFase4 extends ScriptDeFase {

    private transient BufferedImage bg4_1, bamboo_stalk, leaves1, leaves2;
    private long proximoSpawnY = 0;
    private int[] posicoesX;
    private int direcaoDoGrupo = 1;
    private double distanciaTotalRolada = 0;
    private Random rand = new Random();

    private static final int DISTANCIA_ENTRE_ONDAS_Y = 100;
    private static final int OFFSET_DIAGONAL_X = 70;
    private static final int VARIACAO_ALEATORIA_PIXELS = 30;
    private static final int NUMERO_DE_GRUPOS = 4;
    private static final int ESPACO_ENTRE_GRUPOS_X = 400;

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            bg4_1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bg4_1.png"));
            bamboo_stalk = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bamboo.png"));
            leaves1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/leaves1.png"));
            leaves2 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/leaves2.png"));

            fase.adicionarElementoCenario(new FundoInfinito("bg4_1", bg4_1, 0.8, Modelo.Cenario.DrawLayer.BACKGROUND, 1.0f));

            posicoesX = new int[NUMERO_DE_GRUPOS];
            for (int i = 0; i < NUMERO_DE_GRUPOS; i++) {
                posicoesX[i] = 50 + (i * ESPACO_ENTRE_GRUPOS_X);
            }

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 4: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Color getBackgroundOverlayColor() {
        return new Color(255, 255, 255, 20); // Branco com baixa opacidade
    }

    @Override
    public LinearGradientPaint getBackgroundGradient() {
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, ConfigMapa.ALTURA_TELA * 0.15f); // Apenas 15% da tela

        float[] fractions = { 0.0f, 1.0f };
        Color[] colors = { new Color(255, 255, 255, 20), new Color(255, 255, 255, 0) }; // Reduzir intensidade
        return new LinearGradientPaint(start, end, fractions, colors);
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        // Implementation for deserialization
    }

    @Override
    public void atualizarInimigos(Fase fase) {
        // Logic for enemy spawning in stage 4
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        if (bamboo_stalk == null) return;

        this.distanciaTotalRolada += velocidadeScroll;

        if (this.distanciaTotalRolada >= proximoSpawnY) {
            for (int i = 0; i < NUMERO_DE_GRUPOS; i++) {
                int xBase = posicoesX[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = rand.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;
                
                // Spawn a cluster of bamboos for each group
                spawnBambooCluster(fase, novoX + randomOffsetX, -200);

                posicoesX[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;

            // Reverse direction at screen edges
            if (posicoesX[NUMERO_DE_GRUPOS - 1] > 1200 || posicoesX[0] < -100) {
                direcaoDoGrupo *= -1;
            }
        }
    }
    
    private void spawnBambooCluster(Fase fase, int x, int y) {
        // Far layer
        fase.adicionarElementoCenario(new BambuParallax(x + rand.nextInt(40) - 20, y, 60, 1.2, bamboo_stalk, leaves1));
        // Mid layer
        fase.adicionarElementoCenario(new BambuParallax(x + rand.nextInt(80) - 40, y, 80, 1.5, bamboo_stalk, leaves2));
        // Near layer
        fase.adicionarElementoCenario(new BambuParallax(x + rand.nextInt(120) - 60, y, 100, 2.0, bamboo_stalk, leaves1));
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Pre-populate the screen with some bamboos
    }
}