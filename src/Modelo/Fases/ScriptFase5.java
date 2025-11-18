package Modelo.Fases;

import Auxiliar.ConfigMapa;
import Auxiliar.Personagem.LootItem;
import Auxiliar.SoundManager;
import Controler.Engine;
import Modelo.Cenario.ChaoPerspectiva;
import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.ParedeVertical;
import Modelo.Inimigos.Reisen;
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
 * @brief Script de eventos e spawns para a Fase 5, o estágio final.
 * 
 *        Define a aparência do cenário, a trilha sonora e as ondas de inimigos,
 *        culminando na batalha contra o chefe final e a transição para os
 *        créditos.
 */
public class ScriptFase5 extends ScriptDeFase {

    private transient BufferedImage texturaChao;
    private transient BufferedImage texturaParede;

    private final float larguraNoHorizonte = 40;
    private final float fatorPerspectivaChao = 2f;
    private final float fatorPerspectivaParede = 0.9f;
    private final int translacaoParedeX = 0;
    private final int translacaoParedeY = (int) (-ConfigMapa.ALTURA_TELA * 0.08);

    public ScriptFase5(Engine engine) {
        super(engine);

    }

    /**
     * @brief Carrega os recursos visuais e de áudio para a fase.
     * 
     *        Inicializa os elementos de cenário com efeito de perspectiva, como o
     *        chão
     *        e as paredes, e define a música da fase.
     */
    @Override
    public void carregarRecursos(Fase fase) {
        try {
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage5/bg5_1.png"));
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage5/bg5_2.png"));

            ChaoPerspectiva chao = new ChaoPerspectiva(texturaChao, 0.03, larguraNoHorizonte, fatorPerspectivaChao);
            fase.adicionarElementoCenario(chao);

            ParedeVertical paredeEsq = new ParedeVertical(texturaParede, true, 0.015, larguraNoHorizonte,
                    fatorPerspectivaParede, translacaoParedeX, translacaoParedeY);
            fase.adicionarElementoCenario(paredeEsq);

            ParedeVertical paredeDir = new ParedeVertical(texturaParede, false, 0.015, larguraNoHorizonte,
                    fatorPerspectivaParede, translacaoParedeX, translacaoParedeY);
            fase.adicionarElementoCenario(paredeDir);

        } catch (Exception e) {
            Logger.getLogger(ScriptFase5.class.getName()).log(Level.SEVERE, "Erro ao carregar recursos da Fase 5", e);
        }

        SoundManager.getInstance().playMusic("Cinderella Cage ~ Kagome-Kagome", false);
    }

    /**
     * @brief Restaura as referências de imagem após a desserialização.
     */
    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        try {
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage5/bg5_1.png"));
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("Assets/stage5/bg5_2.png"));
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

    /**
     * @brief Define a sequência de ondas de inimigos para esta fase.
     * 
     *        A fase consiste em uma espera inicial, a batalha contra o chefe,
     *        e um gatilho para iniciar os créditos após a vitória.
     */
    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        ondas.add(new OndaDeEspera(fase, 270));
        ondas.add(new OndaBoss(fase));
        ondas.add(new OndaDeEspera(fase, 300));
        ondas.add(new OndaTriggerCreditos());
        return ondas;
    }

    @Override
    public Color getBackgroundOverlayColor() {
        return new Color(0, 0, 0, 0);
    }

    @Override
    public LinearGradientPaint getBackgroundGradient() {

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, 1);
        float[] fractions = { 0.0f, 1.0f };
        Color[] colors = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 0) };
        return new LinearGradientPaint(start, end, fractions, colors);
    }

    /**
     * @brief Define a onda do chefe final da fase.
     */
    private class OndaBoss extends OndaDeBoss {
        public OndaBoss(Fase fase) {
            super(null);
            lootTable.addItem(new LootItem(ItemType.ONE_UP, 1, 1, 1, false, true));
            boss = new Reisen(0, ConfigMapa.MUNDO_ALTURA * 0.05, lootTable, 30000, fase);

            inimigos.add(new InimigoSpawn(boss, 0));
        }
    }

    /**
     * @brief Onda especial que serve como gatilho para iniciar a tela de créditos.
     * 
     *        Assim que ativada, para a música da fase e chama o método
     *        `carregarCreditos`
     *        da engine.
     */
    private class OndaTriggerCreditos extends Onda {
        private boolean triggered = false;

        @Override
        public void incrementarTempo(int tempo, Fase fase) {
            if (!triggered) {
                SoundManager.getInstance().stopAllMusic();
                engine.carregarCreditos();
                triggered = true;
            }
        }

        @Override
        public boolean getFinalizado() {
            return triggered;
        }
    }
}
