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
import Modelo.Inimigos.FadaComum1;
import Modelo.Inimigos.FadaComum2;
import Modelo.Inimigos.FadaComum3;
import Modelo.Inimigos.FadaComum4;
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
                    Modelo.Cenario.DrawLayer.FOREGROUND, 1f, 0.1f, 0.8f, 50.0f));

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

    // Onda
    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        ondas.add(new OndaDeEspera(fase, 240));
        ondas.add(new Onda1(fase));
        ondas.add(new OndaDeEspera(fase, 200));

        ondas.add(new OndaDeSpeedup(540, 1.25));
        ondas.add(new Onda2(fase));
        ondas.add(new OndaDeEspera(fase, 200));

        ondas.add(new Onda3(fase));
        ondas.add(new OndaDeEspera(fase, 200));

        ondas.add(new OndaDeSpeedup(540, 1.25));
        ondas.add(new Onda4(fase));

        ondas.add(new OndaDeSpeedup(200, 5.0));
        ondas.add(new OndaDeEspera(fase, 200));

        ondas.add(new OndaBoss(fase));
        ondas.add(new OndaDeEspera(fase, 200));
        return ondas;
    }

    private class Onda1 extends Onda {
        public Onda1(Fase fase) {
            
            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, false, false));
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, false, false));

            inimigos.add(
                    new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.2, -1.0, lootTable, 600, fase, "_hat", 2), 200));
            inimigos.add(
                    new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.8, -1.0, lootTable, 600, fase, "_hat", 2), 200));
            inimigos.add(
                    new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.5, -1.0, lootTable, 600, fase, "_hat", 2), 200));
        }
    }

    private class Onda2 extends Onda {
        public Onda2(Fase fase) {
            double xInicial;

            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, false, false));
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, false, false));


            for (int i = 0; i < 5; i++) {
                xInicial = (4 + ((MUNDO_LARGURA - 8) * random.nextDouble()));
                inimigos.add(
                        new InimigoSpawn(new FadaComum1(xInicial, -1.0, lootTable, 320, fase, "", 1),
                                40));
            }
            for (int i = 0; i < 4; i++) {
                xInicial = (MUNDO_LARGURA * (i + 1) / 6);
                inimigos.add(
                        new InimigoSpawn(new FadaComum1(xInicial, -1.0, lootTable, 800, fase, "", 2),
                                80));
            }
        }
    }

    private class Onda3 extends Onda {
        public Onda3(Fase fase) {

            // Adiciona inimigos à onda
            double xInicial;
            LootTable lootTable = new LootTable();

            // Loot table
            lootTable.addItem(new LootItem(ItemType.POWER_UP, 1, 1, 0.4, true, false));
            lootTable.addItem(new LootItem(ItemType.BOMB, 1, 1, 0.02, true, false));

            // Inimigos
            for (int i = 0; i < 5; i++) {
                xInicial = (4 + ((MUNDO_LARGURA - 8) * random.nextDouble()));
                inimigos.add(
                        new InimigoSpawn(new FadaComum2(xInicial, -1.0, lootTable, 400, fase, "", 1),
                                80));
            }

            for (int i = 0; i < 3; i++) {
                xInicial = ((MUNDO_LARGURA) * (i + 2) / 6);
                inimigos.add(
                        new InimigoSpawn(new FadaComum4(xInicial, -1.0, lootTable, 600, fase, "_bears", 1), 80));
            }

        }
    }

    private class Onda4 extends Onda {
        public Onda4(Fase fase) {

            // Adiciona inimigos à onda
            double xInicial;

            // Loot table
            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, false, false));
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, false, false));

            // Inimigos
            for (int i = 0; i < 3; i++) {
                xInicial = ((MUNDO_LARGURA) * (i + 2) / 6);
                inimigos.add(
                        new InimigoSpawn(new FadaComum3(xInicial, -1.0, xInicial, lootTable, 600, fase, "_hat", 1), 300));
            }

        }
    }

    private class OndaBoss extends OndaDeBoss {
        public OndaBoss(Fase fase) {
            super("Plain Asia");
            lootTable.addItem(new LootItem(ItemType.ONE_UP, 1, 1, 1, false, true));
            boss = new Keine(0, ConfigMapa.MUNDO_ALTURA * 0.05, lootTable, 45000, fase);

            inimigos.add(new InimigoSpawn(boss, 0));
        }
    }
}