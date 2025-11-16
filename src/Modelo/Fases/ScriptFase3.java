package Modelo.Fases;

import Auxiliar.ConfigMapa;
import static Auxiliar.ConfigMapa.MUNDO_LARGURA;
import Auxiliar.LootTable;
import Auxiliar.Personagem.LootItem;
import Auxiliar.SoundManager;
import Controler.Engine;
import Modelo.Cenario.FundoInfinito;
import Modelo.Cenario.FundoOscilante;
import Modelo.Inimigos.Keine;
import Modelo.Items.ItemType;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * @brief Script de eventos e spawns para a Fase 3 (placeholder).
 */
public class ScriptFase3 extends ScriptDeFase {

    private transient BufferedImage bg3_1;
    private transient BufferedImage bg3_3;
    private transient BufferedImage blackPixel;
    private long lastSpeedupTrigger = 0;

    private static final int SPEEDUP_INTERVAL = 500;
    private static final int SPEEDUP_DURATION = 540;
    private static final double SPEEDUP_AMPLITUDE = 3.0;

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
            Logger.getLogger(ScriptFase3.class.getName()).log(Level.SEVERE, "Erro ao carregar recursos da Fase 3", e);
        }
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        try {
            this.bg3_1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_1.png"));
            this.bg3_3 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_3.png"));
            createBlackPixel();
        } catch (Exception e) {
            Logger.getLogger(ScriptFase3.class.getName()).log(Level.SEVERE, "Erro ao relinkar recursos da Fase 3", e);
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
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        lastSpeedupTrigger++;
        if (lastSpeedupTrigger > SPEEDUP_INTERVAL) { 
            Fase.triggerGlobalSpeedup(SPEEDUP_DURATION, SPEEDUP_AMPLITUDE); 
            lastSpeedupTrigger = 0;
        }
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Nada a preencher inicialmente
    }

    // Onda
    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        ondas.add(new OndaBoss(fase));
        ondas.add(new OndaFadaComum2(fase));
        ondas.add(new OndaDeEspera(fase, 200));
        return ondas;
    }

    private class OndaFadaComum2 extends OndaDeEspera {
        public OndaFadaComum2(Fase fase) {
            super(fase, 3000); // A onda vai durar 3000 frames (50 segundos)
            // Adiciona inimigos à onda
            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));

            inimigos.add(0, new InimigoSpawn(new Modelo.Inimigos.FadaComum2(MUNDO_LARGURA / 2, -1.0, lootTable, 200, fase, ""), 0));
            inimigos.add(1, new InimigoSpawn(new Modelo.Inimigos.FadaComum2(MUNDO_LARGURA * 1/3, -1.0, lootTable, 500, fase, ""), 0));
            inimigos.add(2, new InimigoSpawn(new Modelo.Inimigos.FadaComum2(MUNDO_LARGURA * 2/3, -1.0, lootTable, 200, fase, ""), 0));
        }
    }

    private class OndaBoss extends OndaDeBoss{
        public OndaBoss(Fase fase) {
            super("Deaf to All but the Song");
            lootTable.addItem(new LootItem(ItemType.ONE_UP, 1, 1, 1, false, true));
            boss = new Keine(0, ConfigMapa.MUNDO_ALTURA * 0.05, lootTable, 10000, fase);

            inimigos.add(new InimigoSpawn(boss, 0));
        }
    }
}