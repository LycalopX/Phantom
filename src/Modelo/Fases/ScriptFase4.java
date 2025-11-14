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
    private int direcaoGrupoEsquerda = -1;
    private int direcaoGrupoDireita = 1;
    private double distanciaTotalRolada = 0;
    private Random rand = new Random();

    // Configurações de Spawn
    private static final int DISTANCIA_ENTRE_ONDAS_Y = 150; // Aumentada a frequência
    private static final int OFFSET_DIAGONAL_X = 20;
    private static final int NUMERO_DE_GRUPOS_POR_LADO = 3; // Aumentada a quantidade
    private static final int NUMERO_DE_GRUPOS = NUMERO_DE_GRUPOS_POR_LADO * 2;
    private static final int ESPACO_ENTRE_GRUPOS_X = 100; // Diminuído o espaço

    // Configurações de Rotação
    private static final double MIN_ROTATION_DEG = 0;
    private static final double MAX_ROTATION_DEG = 25;

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            bg4_1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bg4_1.png"));
            bamboo_stalk = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bamboo.png"));
            leaves1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/leaves1.png"));
            leaves2 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/leaves2.png"));

            fase.adicionarElementoCenario(
                    new FundoInfinito("bg4_1", bg4_1, 0.8, Modelo.Cenario.DrawLayer.BACKGROUND, 1.0f));

            posicoesX = new int[NUMERO_DE_GRUPOS];
            // Grupos da Esquerda
            for (int i = 0; i < NUMERO_DE_GRUPOS_POR_LADO; i++) {
                posicoesX[i] = (ConfigMapa.LARGURA_TELA / 6) - (i * ESPACO_ENTRE_GRUPOS_X);
            }
            // Grupos da Direita
            for (int i = 0; i < NUMERO_DE_GRUPOS_POR_LADO; i++) {
                posicoesX[i + NUMERO_DE_GRUPOS_POR_LADO] = (5 * ConfigMapa.LARGURA_TELA / 6)
                        + (i * ESPACO_ENTRE_GRUPOS_X);
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
        if (bamboo_stalk == null)
            return;

        this.distanciaTotalRolada += velocidadeScroll;

        if (this.distanciaTotalRolada >= proximoSpawnY) {
            for (int i = 0; i < NUMERO_DE_GRUPOS; i++) {
                int xBase = posicoesX[i];
                int novoX;

                if (i < NUMERO_DE_GRUPOS_POR_LADO) { // Grupos da esquerda
                    novoX = xBase + (OFFSET_DIAGONAL_X * direcaoGrupoEsquerda);
                } else { // Grupos da direita
                    novoX = xBase + (OFFSET_DIAGONAL_X * direcaoGrupoDireita);
                }

                spawnBambooCluster(fase, novoX, -200);
                posicoesX[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;

            // Inverte a direção dos grupos da esquerda
            if (posicoesX[0] < -100 || posicoesX[NUMERO_DE_GRUPOS_POR_LADO - 1] > (ConfigMapa.LARGURA_TELA / 2) - 200) {
                direcaoGrupoEsquerda *= -1;
            }
            // Inverte a direção dos grupos da direita
            if (posicoesX[NUMERO_DE_GRUPOS_POR_LADO] < (ConfigMapa.LARGURA_TELA / 2)
                    || posicoesX[NUMERO_DE_GRUPOS - 1] > ConfigMapa.LARGURA_TELA + 100) {
                direcaoGrupoDireita *= -1;
            }
        }
    }

    private void spawnBambooCluster(Fase fase, int x, int y) {
        double screenCenterX = ConfigMapa.LARGURA_TELA / 2.0;
        double angle;
        double randAngle = (MIN_ROTATION_DEG) + rand.nextDouble() * (MAX_ROTATION_DEG - MIN_ROTATION_DEG);

        if (x > screenCenterX) { // Lado esquerdo: sentido horário
            angle = Math.toRadians(randAngle);
        } else { // Lado direito: sentido anti-horário
            angle = -Math.toRadians(randAngle);
        }

        BufferedImage leaf = rand.nextBoolean() ? leaves1 : leaves2;
        int clusterSpread = 120;

        // Spawn gradual com offsets em Y
        fase.adicionarElementoCenario(new BambuParallax(x + rand.nextInt(clusterSpread) - clusterSpread / 2, y - 40, 60, 1.2,
                bamboo_stalk, leaf, angle));
        fase.adicionarElementoCenario(new BambuParallax(x + rand.nextInt(clusterSpread) - clusterSpread / 2, y - 20, 80, 1.5,
                bamboo_stalk, leaf, angle));
        fase.adicionarElementoCenario(new BambuParallax(x + rand.nextInt(clusterSpread) - clusterSpread / 2, y, 100,
                2.0, bamboo_stalk, leaf, angle));
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Pre-populate the screen with some bamboos
    }
}