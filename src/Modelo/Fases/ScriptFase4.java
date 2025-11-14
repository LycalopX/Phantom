package Modelo.Fases;

import Auxiliar.Cenario4.BambuParallax;
import Modelo.Cenario.FundoInfinito;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Random;
import Auxiliar.ConfigMapa;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import Modelo.Cenario.DrawLayer;
import Modelo.Personagem;

public class ScriptFase4 extends ScriptDeFase {

    private transient BufferedImage bg4_1, bamboo_stalk, leaves1, leaves2;
    private transient BufferedImage flipped_bamboo_stalk, flipped_leaves1, flipped_leaves2;
    private long proximoSpawnY = 0;
    private double distanciaTotalRolada = 0;
    private Random rand = new Random();

    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;

    private BufferedImage flipImageHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g = flippedImage.createGraphics();
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-width, 0));
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return flippedImage;
    }

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            bg4_1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bg4_1.png"));
            bamboo_stalk = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bamboo.png"));
            leaves1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/leaves1.png"));
            leaves2 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/leaves2.png"));

            flipped_bamboo_stalk = flipImageHorizontally(bamboo_stalk);
            flipped_leaves1 = flipImageHorizontally(leaves1);
            flipped_leaves2 = flipImageHorizontally(leaves2);

            fase.adicionarElementoCenario(
                    new FundoInfinito("bg4_1", bg4_1, 0.8, Modelo.Cenario.DrawLayer.BACKGROUND, 1.0f));

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 4: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Color getBackgroundOverlayColor() {
        return new Color(255, 255, 255, 20);
    }

    @Override
    public LinearGradientPaint getBackgroundGradient() {
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, ConfigMapa.ALTURA_TELA * 0.15f);
        float[] fractions = { 0.0f, 1.0f };
        Color[] colors = { new Color(255, 255, 255, 20), new Color(255, 255, 255, 0) };
        return new LinearGradientPaint(start, end, fractions, colors);
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        // Implementação para desserialização, se necessário
    }

    @Override
    public void atualizarInimigos(Fase fase) {
        // Lógica de inimigos da Fase 4
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        if (bamboo_stalk == null)
            return;

        this.distanciaTotalRolada += velocidadeScroll;

        if (this.distanciaTotalRolada >= proximoSpawnY) {
            int tamanhoBase = (int) (14 * Personagem.BODY_PROPORTION); // Tamanho base fixo e razoável
            int yInicial = -tamanhoBase * 2; // Spawn acima da tela

            // Gera bambu na zona esquerda (0 a 1/6 da tela) com sprites originais
            double xEsquerda = rand.nextDouble() * (ConfigMapa.LARGURA_TELA / 6.0);
            fase.adicionarElementoCenario(new BambuParallax((int)xEsquerda, yInicial, tamanhoBase, velocidadeScroll, this.bamboo_stalk, this.leaves1, this.leaves2));

            // Gera bambu na zona direita (5/6 a 100% da tela) com sprites invertidos
            double inicioFaixaDireita = ConfigMapa.LARGURA_TELA * 5.0 / 6.0;
            double larguraFaixaDireita = ConfigMapa.LARGURA_TELA / 6.0;
            double xDireita = inicioFaixaDireita + rand.nextDouble() * larguraFaixaDireita;
            fase.adicionarElementoCenario(new BambuParallax((int)xDireita, yInicial, tamanhoBase, velocidadeScroll, this.flipped_bamboo_stalk, this.flipped_leaves1, this.flipped_leaves2));
            
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Lógica para preencher o cenário inicial removida para simplificar
    }
}