package Modelo.Fases;

import Auxiliar.ConfigMapa;
import Controler.Engine;
import Modelo.Cenario.ChaoPerspectiva;
import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.PlanoDeFundo;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ScriptFase5 extends ScriptDeFase {

    private transient BufferedImage texturaChao;
    private transient BufferedImage texturaParede;

    public ScriptFase5(Engine engine) {
        super(engine);
        // Auxiliar.SoundManager.getInstance().playMusic("NomeDaMusica", true);
    }

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_1.png"));
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_2.png"));

            int larguraTela = ConfigMapa.LARGURA_TELA;
            int alturaTela = ConfigMapa.ALTURA_TELA;
            int larguraParede = larguraTela / 2;

            // 1. O Chão (com a nova classe de perspectiva correta)
            ChaoPerspectiva chao = new ChaoPerspectiva(texturaChao, 1.0);
            fase.adicionarElementoCenario(chao);

            // 2. Parede Esquerda (continua usando PlanoDeFundo com shear)
            Rectangle boundsParedeEsq = new Rectangle(0, 0, larguraParede, alturaTela);
            PlanoDeFundo paredeEsq = new PlanoDeFundo("parede_esq", texturaParede, boundsParedeEsq, -0.2, 0, 2.5);
            fase.adicionarElementoCenario(paredeEsq);

            /*
            // 3. Parede Direita (continua usando PlanoDeFundo com shear)
            Rectangle boundsParedeDir = new Rectangle(larguraTela - larguraParede, 0, larguraParede, alturaTela);
            PlanoDeFundo paredeDir = new PlanoDeFundo("parede_dir", texturaParede, boundsParedeDir, -0.4, 0, 2.5);
            fase.adicionarElementoCenario(paredeDir);
            */

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 5: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        try {
            this.texturaParede = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_1.png"));
            this.texturaChao = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage5/bg5_2.png"));
        } catch (Exception e) {
            System.err.println("Erro ao relinkar recursos da Fase 5: " + e.getMessage());
        }

        for (ElementoCenario elemento : fase.getElementosCenario()) {
            if (elemento instanceof PlanoDeFundo) {
                ((PlanoDeFundo) elemento).relinkImage(texturaParede);
            } else if (elemento instanceof ChaoPerspectiva) {
                ((ChaoPerspectiva) elemento).relinkImage(texturaChao);
            }
        }
    }

    @Override
    public void atualizarInimigos(Fase fase) {
        // Lógica de spawn de inimigos para a Fase 5 virá aqui
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        // A lógica de movimento já está no próprio PlanoDeFundo
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Não é necessário, pois os planos são contínuos
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
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {new Color(0,0,0,0), new Color(0,0,0,0)};
        return new LinearGradientPaint(start, end, fractions, colors);
    }
}
