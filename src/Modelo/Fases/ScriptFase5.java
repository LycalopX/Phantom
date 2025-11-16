package Modelo.Fases;

import java.util.ArrayList;

import Auxiliar.SoundManager;
import Auxiliar.ConfigMapa;
import Auxiliar.LootTable;
import Modelo.Items.ItemType;
import Controler.Engine;
import Modelo.Cenario.ChaoPerspectiva;
import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.ParedeVertical;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ScriptFase5 extends ScriptDeFase {

    private transient BufferedImage texturaChao;
    private transient BufferedImage texturaParede;

    // Parâmetros de perspectiva compartilhados
    private final float larguraNoHorizonte = 40; // Largura do corredor no ponto de fuga
    private final float fatorPerspectivaChao = 2f; // Controla a "abertura" da perspectiva
    private final float fatorPerspectivaParede = 0.9f; // Controla a "abertura" da perspectiva
    private final int translacaoParedeX = 0; // Offset horizontal para as paredes
    private final int translacaoParedeY = (int) (-ConfigMapa.ALTURA_TELA * 0.08); // Offset vertical para as paredes

    public ScriptFase5(Engine engine) {
        super(engine);
        // Auxiliar.SoundManager.getInstance().playMusic("NomeDaMusica", true);
    }

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_1.png"));
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_2.png"));

            // 1. O Chão
            ChaoPerspectiva chao = new ChaoPerspectiva(texturaChao, 0.03, larguraNoHorizonte, fatorPerspectivaChao);
            fase.adicionarElementoCenario(chao);

            // 2. Parede Esquerda
            ParedeVertical paredeEsq = new ParedeVertical(texturaParede, true, 0.015, larguraNoHorizonte,
                    fatorPerspectivaParede, translacaoParedeX, translacaoParedeY);
            fase.adicionarElementoCenario(paredeEsq);

            // 3. Parede Direita
            ParedeVertical paredeDir = new ParedeVertical(texturaParede, false, 0.015, larguraNoHorizonte,
                    fatorPerspectivaParede, translacaoParedeX, translacaoParedeY);
            fase.adicionarElementoCenario(paredeDir);

        } catch (Exception e) {
            Logger.getLogger(ScriptFase5.class.getName()).log(Level.SEVERE, "Erro ao carregar recursos da Fase 5", e);
        }

        SoundManager.getInstance().playMusic("Love-Colored Master Spark", false);
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        try {
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_1.png"));
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_2.png"));
        } catch (Exception e) {
            Logger.getLogger(ScriptFase5.class.getName()).log(Level.SEVERE, "Erro ao relinkar recursos da Fase 5", e);
        }

        for (ElementoCenario elemento : fase.getElementosCenario()) {
            if (elemento instanceof ParedeVertical) {
                ((ParedeVertical) elemento).relinkImage(texturaParede);
            } else if (elemento instanceof ChaoPerspectiva) {
                ((ChaoPerspectiva) elemento).relinkImage(texturaChao);
            }
        }
    }

    // Onda
    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        // LootTable para a FadaComum4
        LootTable lootFada4 = new LootTable();
        lootFada4.addItem(new Auxiliar.Personagem.LootItem(ItemType.SCORE_POINT, 1, 1, 1.0, false, false));
        lootFada4.addItem(new Auxiliar.Personagem.LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, false, false));

        // Cria uma nova onda para a FadaComum4
        Onda ondaFada4 = new Onda() {{
            inimigos.add(new InimigoSpawn(new Modelo.Inimigos.FadaComum4(ConfigMapa.MUNDO_LARGURA / 2.0, -1.0, lootFada4, 50, fase, null), 120)); // Spawn após 2 segundos
            inimigos.add(new InimigoSpawn(new Modelo.Inimigos.FadaComum4(ConfigMapa.MUNDO_LARGURA / 2.0, -1.0, lootFada4, 50, fase, null), 120)); // Spawn após 2 segundos
            inimigos.add(new InimigoSpawn(new Modelo.Inimigos.FadaComum4(ConfigMapa.MUNDO_LARGURA / 2.0, -1.0, lootFada4, 50, fase, null), 120)); // Spawn após 2 segundos
        }};
        ondas.add(ondaFada4);
        ondas.add(new OndaDeEspera(fase, 300)); // Espera 5 segundos após a fada
        return ondas;
    }

    @Override
    public Color getBackgroundOverlayColor() {
        return new Color(0, 0, 0, 0); // Sem overlay, o fundo preto serve como o "vazio"
    }

    @Override
    public LinearGradientPaint getBackgroundGradient() {
        // Sem gradiente para manter o fundo preto puro
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, 1);
        float[] fractions = { 0.0f, 1.0f };
        Color[] colors = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 0) };
        return new LinearGradientPaint(start, end, fractions, colors);
    }
}
