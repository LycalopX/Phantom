package Modelo.Projeteis;

import java.util.ArrayList;
import java.util.List;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import Modelo.Inimigos.Inimigo;
import Auxiliar.Projeteis.ProjetilTipo;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;

/**
 * @brief Projétil teleguiado especial, lançado pela bomba do herói.
 * 
 *        Este projétil possui uma fase inicial de expansão em linha reta antes
 *        de
 *        ativar seu comportamento de perseguição, herdado de `ProjetilHoming`.
 */
public class ProjetilBombaHoming extends ProjetilHoming {
    private enum EstadoBomba {
        EXPANDINDO,
        PERSEGUINDO
    }

    private EstadoBomba estadoAtual;
    private int tempoDeVida, tempoDeExpansao, animationTimer;

    private List<Point2D.Double> positionHistory = new ArrayList<>();
    private static final int TRAIL_LENGTH = 6;

    private static final int DURACAO_TOTAL_VIDA = 240;
    private static final int DURACAO_EXPANSAO = 15;

    /**
     * @brief Construtor do projétil teleguiado da bomba.
     */
    public ProjetilBombaHoming(String sNomeImagePNG) {
        super(sNomeImagePNG);
    }

    /**
     * @brief Configura ou reseta o míssil a partir da ProjetilPool.
     * 
     *        Define seu estado inicial para a fase de expansão e inicializa seus
     *        timers.
     */
    public void resetBombaHoming(double x, double y, double velocidadeGrid, double anguloExpansao, TipoProjetil tipo,
            ProjetilTipo tipoDetalhado, List<Inimigo> inimigos) {
        super.resetHoming(
                x,
                y,
                velocidadeGrid,
                anguloExpansao,
                tipo,
                tipoDetalhado,
                inimigos);

        this.estadoAtual = EstadoBomba.EXPANDINDO;
        this.tempoDeVida = DURACAO_TOTAL_VIDA;
        this.tempoDeExpansao = DURACAO_EXPANSAO;
        this.positionHistory.clear();
        this.animationTimer = 0;
    }

    /**
     * @brief Atualiza a lógica do míssil a cada frame.
     * 
     *        Gerencia a transição do estado de expansão para o de perseguição,
     *        além de controlar o tempo de vida e o rastro do projétil.
     */
    @Override
    public void atualizar() {
        if (!isActive())
            return;

        // Adiciona a posição atual ao histórico para criar o efeito de rastro.
        positionHistory.add(0, new Point2D.Double(this.x, this.y));
        if (positionHistory.size() > TRAIL_LENGTH) {
            positionHistory.remove(positionHistory.size() - 1);
        }
        animationTimer++;

        tempoDeVida--;
        if (tempoDeVida <= 0) {
            deactivate();
            return;
        }

        switch (estadoAtual) {
            // Na fase de expansão, o projétil se move em linha reta.
            case EXPANDINDO:
                this.x += Math.cos(this.anguloRad) * this.velocidade;
                this.y += Math.sin(this.anguloRad) * this.velocidade;

                tempoDeExpansao--;
                if (tempoDeExpansao <= 0) {
                    this.estadoAtual = EstadoBomba.PERSEGUINDO;
                }
                break;

            // Na fase de perseguição, delega a lógica para a superclasse `ProjetilHoming`.
            case PERSEGUINDO:
                super.atualizar();
                break;
        }
    }

    private static final Color AURA_COLOR = new Color(255, 120, 120);
    private static final Color TRAIL_BASE_COLOR = new Color(255, 100, 100);

    /**
     * @brief Desenha o míssil com efeitos visuais customizados.
     * 
     *        Renderiza um rastro de partículas, uma aura pulsante e a imagem
     *        do projétil com transparência.
     */
    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive() || iImage == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();

        // Desenha o rastro (trail) do projétil.
        for (int i = 0; i < positionHistory.size(); i++) {
            Point2D.Double pos = positionHistory.get(i);
            float alpha = (1.0f - (float) i / TRAIL_LENGTH);
            g2d.setColor(new Color(TRAIL_BASE_COLOR.getRed(), TRAIL_BASE_COLOR.getGreen(), TRAIL_BASE_COLOR.getBlue(),
                    (int) (200 * alpha)));
            int telaX = (int) Math.round(pos.x * CELL_SIDE);
            int telaY = (int) Math.round(pos.y * CELL_SIDE);

            int particleSize = 8;
            g2d.fillOval(telaX - particleSize / 2, telaY - particleSize / 2, particleSize, particleSize);
        }

        int telaX = (int) Math.round(this.x * CELL_SIDE);
        int telaY = (int) Math.round(this.y * CELL_SIDE);

        // Efeito de pulsação usando uma função seno para escalar a imagem.
        double scale = 1.0 + 2.5 * Math.abs(Math.sin(animationTimer * 0.03));

        int scaledWidth = (int) (this.largura * scale);
        int scaledHeight = (int) (this.altura * scale);

        // Desenha a aura e a imagem principal com transparência.
        float auraAlpha = 0.3f;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, auraAlpha));
        g.setColor(AURA_COLOR);
        g.fillOval(telaX - (scaledWidth / 2) - 4, telaY - (scaledHeight / 2) - 4, scaledWidth + 8, scaledHeight + 8);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.drawImage(iImage.getImage(), telaX - (scaledWidth / 2), telaY - (scaledHeight / 2), scaledWidth,
                scaledHeight, null);

        g2d.setComposite(originalComposite);
        super.autoDesenho(g);
    }
}