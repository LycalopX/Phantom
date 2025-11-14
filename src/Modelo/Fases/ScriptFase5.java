package Modelo.Fases;

import Auxiliar.ConfigMapa;
import Controler.Engine;
import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.FundoPerspectiva3D;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ScriptFase5 extends ScriptDeFase {

    private transient BufferedImage texturaChao;
    private transient BufferedImage texturaParede;

    public ScriptFase5(Engine engine) {
        super(engine);
        // Definir música da fase 5 aqui, se houver
        // Auxiliar.SoundManager.getInstance().playMusic("NomeDaMusica", true);
    }

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            // Usando texturas de outras fases como placeholders
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_1.png"));
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_2.png"));

            // O horizonte ficará a 40% da altura da tela
            int horizonteY = (int) (ConfigMapa.ALTURA_TELA * 0.4);
            FundoPerspectiva3D fundo3D = new FundoPerspectiva3D(texturaChao, texturaParede, horizonteY);
            
            fase.adicionarElementoCenario(fundo3D);

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 5: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        try {
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage3/bg3_1.png"));
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage4/bamboo.png"));
        } catch (Exception e) {
            System.err.println("Erro ao relinkar recursos da Fase 5: " + e.getMessage());
        }

        for (ElementoCenario elemento : fase.getElementosCenario()) {
            if (elemento instanceof FundoPerspectiva3D) {
                ((FundoPerspectiva3D) elemento).relinkImages(texturaChao, texturaParede);
            }
        }
    }

    @Override
    public void atualizarInimigos(Fase fase) {
        // Lógica de spawn de inimigos para a Fase 5 virá aqui
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        // Lógica adicional de cenário para a Fase 5 virá aqui, se necessário
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Não é necessário preencher, pois o FundoPerspectiva3D é contínuo
    }

    @Override
    public Color getBackgroundOverlayColor() {
        // Um overlay escuro para simular a noite na floresta de bambu
        return new Color(10, 0, 20, 100);
    }

    @Override
    public LinearGradientPaint getBackgroundGradient() {
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, ConfigMapa.ALTURA_TELA);
        float[] fractions = {0.0f, 0.4f, 1.0f};
        Color[] colors = {new Color(10, 0, 20, 150), new Color(50, 0, 60, 50), new Color(10, 0, 20, 150)};
        return new LinearGradientPaint(start, end, fractions, colors);
    }
}
