package Modelo.Fases;

import Auxiliar.Cenario4.BambuParallax;
import Auxiliar.ConfigMapa;
import static Auxiliar.ConfigMapa.MUNDO_LARGURA;
import Auxiliar.LootTable;
import Auxiliar.Personagem.LootItem;
import Auxiliar.SoundManager;
import Controler.Engine;
import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.FundoInfinito;
import Modelo.Inimigos.FadaComum1;
import Modelo.Inimigos.FadaComum2;
import Modelo.Inimigos.FadaComum3;
import Modelo.Inimigos.Reimu;
import Modelo.Items.ItemType;
import Modelo.Personagem;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * @brief Script de eventos e spawns para a Fase 4, o "Bamboo Forest of the
 *        Lost".
 * 
 *        Define a sequência de ondas de inimigos, o cenário com bambus gerados
 *        proceduralmente e a batalha contra o chefe da fase.
 */
public class ScriptFase4 extends ScriptDeFase {

    private transient BufferedImage bg4_1, bamboo_stalk, leaves1, leaves2;
    private transient BufferedImage flipped_bamboo_stalk, flipped_leaves1, flipped_leaves2;
    private long proximoSpawnY = 0;
    private double distanciaTotalRolada = 0;
    private Random rand = new Random();

    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;
    private static final double MIN_ROTATION_DEG = 25;
    private static final double MAX_ROTATION_DEG = 17;

    public ScriptFase4(Engine engine) {
        super(engine);
        SoundManager.getInstance().playMusic("Retribution for the Eternal Night ~ Imperishable Night", true);
    }

    /**
     * @brief Inverte uma imagem horizontalmente.
     * @param image A imagem a ser invertida.
     * @return A imagem invertida.
     */
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

    /**
     * @brief Carrega os recursos visuais específicos da fase.
     */
    @Override
    public void carregarRecursos(Fase fase) {
        try {
            bg4_1 = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage4/bg4_1.png"));
            bamboo_stalk = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage4/bamboo.png"));
            leaves1 = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage4/leaves1.png"));
            leaves2 = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage4/leaves2.png"));

            flipped_bamboo_stalk = flipImageHorizontally(bamboo_stalk);
            flipped_leaves1 = flipImageHorizontally(leaves1);
            flipped_leaves2 = flipImageHorizontally(leaves2);

            fase.adicionarElementoCenario(
                    new FundoInfinito("bg4_1", bg4_1, 0.8, Modelo.Cenario.DrawLayer.BACKGROUND, 1.0f));

        } catch (Exception e) {
            Logger.getLogger(ScriptFase4.class.getName()).log(Level.SEVERE, "Erro ao carregar recursos da Fase 4", e);
        }
    }

    @Override
    public Color getBackgroundOverlayColor() {
        return new Color(255, 255, 255, 20);
    }

    @Override
    public LinearGradientPaint getBackgroundGradient() {
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, ConfigMapa.ALTURA_TELA * 0.6f);

        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { new Color(171, 200, 245, 100), new Color(255, 255, 255, 20), new Color(255, 255, 255, 0) };
        return new LinearGradientPaint(start, end, fractions, colors);
    }

    /**
     * @brief Restaura as referências de imagens após a desserialização.
     */
    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        for (ElementoCenario elemento : fase.getElementosCenario()) {
            if (elemento instanceof BambuParallax) {
                BambuParallax bambu = (BambuParallax) elemento;
                if (bambu.isFlipped()) {
                    bambu.relinkImages(flipped_bamboo_stalk, flipped_leaves1, flipped_leaves2);
                } else {
                    bambu.relinkImages(bamboo_stalk, leaves1, leaves2);
                }
            }
        }
    }

    @Override
    public void atualizarInimigos(Fase fase) {
        super.atualizarInimigos(fase);

    }

    /**
     * @brief Atualiza a lógica de spawn procedural dos bambus.
     */
    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        if (bamboo_stalk == null)
            return;

        this.distanciaTotalRolada += velocidadeScroll;

        if (this.distanciaTotalRolada >= proximoSpawnY) {
            spawnBamboos(fase, velocidadeScroll);
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    /**
     * @brief Gera um novo conjunto de bambus nas laterais da tela.
     */
    private void spawnBamboos(Fase fase, double velocidadeScroll) {
        int tamanhoBase = (int) (14 * Personagem.BODY_PROPORTION);
        int yInicial = (int) (-ConfigMapa.ALTURA_TELA * 1.5);

        double randAngle = (MIN_ROTATION_DEG) + rand.nextDouble() * (MAX_ROTATION_DEG - MIN_ROTATION_DEG);

        double angleLeft = Math.toRadians(randAngle);
        int nBambu = 3;

        for (int i = 0; i < nBambu; i++) {
            double xEsquerda = rand.nextDouble() * (ConfigMapa.LARGURA_TELA / 12.0) - ConfigMapa.LARGURA_TELA / 3;

            fase.adicionarElementoCenario(
                    new BambuParallax((int) xEsquerda, yInicial, tamanhoBase, velocidadeScroll,
                            this.bamboo_stalk, this.leaves1, this.leaves2, angleLeft, false));
        }

        double angleRight = -Math.toRadians(randAngle);

        double inicioFaixaDireita = ConfigMapa.LARGURA_TELA * 11 / 12.0;
        double larguraFaixaDireita = ConfigMapa.LARGURA_TELA / 12.0;

        for (int i = 0; i < nBambu; i++) {

            double xDireita = inicioFaixaDireita + rand.nextDouble() * larguraFaixaDireita
                    + ConfigMapa.LARGURA_TELA / 3;
            fase.adicionarElementoCenario(new BambuParallax((int) xDireita, yInicial, tamanhoBase, velocidadeScroll,
                    this.flipped_bamboo_stalk, this.flipped_leaves1, this.flipped_leaves2, angleRight, true));
        }
    }

    /**
     * @brief Preenche o cenário com bambus iniciais para evitar um início vazio.
     */
    @Override
    public void preencherCenarioInicial(Fase fase) {

        spawnBamboos(fase, 0);
        proximoSpawnY = DISTANCIA_ENTRE_ONDAS_Y;
    }

    /**
     * @brief Define a sequência de ondas de inimigos para esta fase.
     */
    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        ondas.add(new OndaDeEspera(fase, 250));
        ondas.add(new Onda1(fase));

        ondas.add(new OndaDeEspera(fase, 250));
        ondas.add(new Onda2(fase));

        ondas.add(new OndaDeSpeedup(540, 1.3));
        ondas.add(new OndaDeEspera(fase, 200));
        ondas.add(new Onda3(fase));

        ondas.add(new OndaDeEspera(fase, 300));
        ondas.add(new Onda4(fase));

        ondas.add(new OndaDeSpeedup(200, 2.5));
        ondas.add(new OndaDeEspera(fase, 700));

        ondas.add(new OndaBoss(fase));
        ondas.add(new OndaDeEspera(fase, 300));
        return ondas;
    }

    private class Onda1 extends Onda {
        public Onda1(Fase fase) {
            super();

            double xInicial = MUNDO_LARGURA / 2.0;
            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, true, false));
            inimigos.add(0, new InimigoSpawn(new FadaComum3(xInicial, -1.0, xInicial, lootTable, 180, fase, "", 2), 0));
            inimigos.add(0, new InimigoSpawn(
                    new FadaComum3(xInicial * 0.66, -1.0, xInicial * 0.66, lootTable, 180, fase, "", 2), 0));
            inimigos.add(0, new InimigoSpawn(
                    new FadaComum3(xInicial * 1.33, -1.0, xInicial * 1.33, lootTable, 180, fase, "", 2), 0));
        }
    }

    private class Onda2 extends Onda {
        public Onda2(Fase fase) {
            super();

            double xInicial;
            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, true, false));

            for (int i = 0; i < 5; i++) {
                xInicial = ((i + 1) / 7.0) * MUNDO_LARGURA;
                inimigos.add(
                        new InimigoSpawn(new FadaComum3(xInicial, -1.0, xInicial, lootTable, 300, fase, "_bears", 1),
                                0));
            }
        }
    }

    private class Onda3 extends Onda {
        public Onda3(Fase fase) {
            super();

            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, true, false));

            inimigos.add(
                    new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.1, -1, lootTable, 400, fase, "_hat", 2), 20));
            inimigos.add(
                    new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.9, -1, lootTable, 400, fase, "_hat", 2), 200));
            inimigos.add(
                    new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.1, -1, lootTable, 400, fase, "_hat", 2), 20));
            inimigos.add(new InimigoSpawn(new FadaComum2(MUNDO_LARGURA * 0.9, -1, lootTable, 400, fase, "_hat", 2), 0));
        }
    }

    private class Onda4 extends Onda {
        public Onda4(Fase fase) {
            super();

            LootTable lootTable = new LootTable();
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, true, false));

            for (int i = 0; i < 10; i++) {
                double xInicial1 = ((MUNDO_LARGURA - 2) * (1 - i / 24.0));
                double xInicial2 = ((MUNDO_LARGURA - 2) * (i / 24.0));

                inimigos.add(new InimigoSpawn(new FadaComum1(xInicial1, -1.0, lootTable, 300, fase, "_bears", 2), 40));
                inimigos.add(new InimigoSpawn(new FadaComum1(xInicial2, -1.0, lootTable, 300, fase, "_bears", 2), 40));
            }
        }
    }

    private class OndaBoss extends OndaDeBoss {
        public OndaBoss(Fase fase) {
            super("Retribution for the Eternal Night ~ Imperishable Night");
            lootTable.addItem(new LootItem(ItemType.ONE_UP, 1, 1, 1, false, true));
            boss = new Reimu(0, ConfigMapa.MUNDO_ALTURA * 0.05, lootTable, 12000, fase);

            inimigos.add(new InimigoSpawn(boss, 0));
        }
    }
}