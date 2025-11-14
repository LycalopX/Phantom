package Modelo.Fases;

import Modelo.Cenario.FundoInfinito;
import Modelo.Cenario.FundoOscilante;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import Auxiliar.ConfigMapa;
import Auxiliar.SoundManager;
import Controler.Engine;

/**
 * @brief Script de eventos e spawns para a Fase 3 (placeholder).
 */
public class ScriptFase3 extends ScriptDeFase {

    private transient BufferedImage bg3_1;
    private transient BufferedImage bg3_3;
    private transient BufferedImage blackPixel;
    private long lastSpeedupTrigger = 0;

    /**
     * @brief Construtor do script da Fase 2.
     */
    public ScriptFase3(Engine engine) {
        super(engine);
        SoundManager.getInstance().playMusic("Nostalgic Blood of the East ~ Old World", true);
    }

    private void createBlackPixel() {
        this.blackPixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        this.blackPixel.setRGB(0, 0, new Color(0, 0, 0).getRGB());
    }

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            this.bg3_1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_1.png"));
            this.bg3_3 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_3.png"));
            createBlackPixel();

            fase.adicionarElementoCenario(
                    new FundoInfinito("bg3_1", this.bg3_1, 1.0, Modelo.Cenario.DrawLayer.FOREGROUND, 1.0f));
            fase.adicionarElementoCenario(
                    new FundoInfinito("darken_layer", this.blackPixel, 0, Modelo.Cenario.DrawLayer.BACKGROUND, 0.8f));
            fase.adicionarElementoCenario(new FundoOscilante("bg3_3", this.bg3_3, 1.2,
                    Modelo.Cenario.DrawLayer.FOREGROUND, 1f, 0.1f, 0.2f, 100.0f));

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 3: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        try {
            this.bg3_1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_1.png"));
            this.bg3_3 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_3.png"));
            createBlackPixel();
        } catch (Exception e) {
            System.err.println("Erro ao relinkar recursos da Fase 3: " + e.getMessage());
        }

        for (var elemento : fase.getElementosCenario()) {
            if (elemento instanceof FundoInfinito) {
                FundoInfinito fundo = (FundoInfinito) elemento;
                switch (fundo.getId()) {
                    case "bg3_1":
                        fundo.setImagem(this.bg3_1);
                        break;
                    case "bg3_3":
                        fundo.setImagem(this.bg3_3);
                        break;
                    case "darken_layer":
                        fundo.setImagem(this.blackPixel);
                        break;
                }
            }
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
    public void atualizarInimigos(Fase fase) {
        // Lógica de spawn de inimigos da fase 3 aqui
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        lastSpeedupTrigger++;
        if (lastSpeedupTrigger > 500) { // Aciona a cada 500 frames
            Fase.triggerGlobalSpeedup(540, 3.0); // Dura 2 segundos (120 frames) com amplitude de 3x
            lastSpeedupTrigger = 0;
        }
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Nada a preencher inicialmente
    }
}
