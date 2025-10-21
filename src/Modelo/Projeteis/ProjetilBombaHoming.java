package Modelo.Projeteis;

import java.util.ArrayList;
import java.util.List;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import Modelo.Personagem;
import Auxiliar.Projeteis.ProjetilTipo;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;

/**
 * @brief Projétil teleguiado especial lançado pela bomba do herói.
 *        Possui uma fase inicial de expansão antes de começar a perseguir os
 *        alvos.
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
    public ProjetilBombaHoming(String sNomeImagePNG, ArrayList<Personagem> personagens) {
        super(sNomeImagePNG, personagens);
    }

    /**
     * @brief Configura ou reseta o míssil a partir da ProjetilPool, definindo seu
     *        estado inicial para a fase de expansão.
     */
    public void resetBombaHoming(double x, double y, double velocidadeGrid, double anguloExpansao, TipoProjetil tipo,
            ProjetilTipo tipoDetalhado) {
        super.resetHoming(
                x,
                y,
                velocidadeGrid,
                anguloExpansao,
                tipo,
                tipoDetalhado);

        this.estadoAtual = EstadoBomba.EXPANDINDO;
        this.tempoDeVida = DURACAO_TOTAL_VIDA;
        this.tempoDeExpansao = DURACAO_EXPANSAO;
        this.positionHistory.clear();
        this.animationTimer = 0;
    }

    /**
     * @brief Atualiza a lógica do míssil, que transita de um estado de expansão
     *        para um de perseguição.
     */
    @Override
    public void atualizar() {
        if (!isActive())
            return;

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
            case EXPANDINDO:
                this.x += Math.cos(this.anguloRad) * this.velocidade;
                this.y += Math.sin(this.anguloRad) * this.velocidade;

                tempoDeExpansao--;
                if (tempoDeExpansao <= 0) {
                    this.estadoAtual = EstadoBomba.PERSEGUINDO;
                }
                break;

            case PERSEGUINDO:
                super.atualizar();
                break;
        }
    }

    /**
     * @brief Desenha o míssil com um efeito de rastro e pulsação.
     */
    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive() || iImage == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();

        for (int i = 0; i < positionHistory.size(); i++) {
            Point2D.Double pos = positionHistory.get(i);
            int alpha = (int) (200 * (1.0f - (float) i / TRAIL_LENGTH));
            g2d.setColor(new Color(255, 100, 100, alpha));
            int telaX = (int) Math.round(pos.x * CELL_SIDE);
            int telaY = (int) Math.round(pos.y * CELL_SIDE);
            int particleSize = 8;
            g2d.fillOval(telaX - particleSize / 2, telaY - particleSize / 2, particleSize, particleSize);
        }

        int telaX = (int) Math.round(this.x * CELL_SIDE);
        int telaY = (int) Math.round(this.y * CELL_SIDE);

        double scale = 1.0 + 2.5 * Math.abs(Math.sin(animationTimer * 0.03));

        int scaledWidth = (int) (this.largura * scale);
        int scaledHeight = (int) (this.altura * scale);

        float auraAlpha = 0.3f;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, auraAlpha));
        g.setColor(new Color(255, 120, 120));
        g.fillOval(telaX - (scaledWidth / 2) - 4, telaY - (scaledHeight / 2) - 4, scaledWidth + 8, scaledHeight + 8);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.drawImage(iImage.getImage(), telaX - (scaledWidth / 2), telaY - (scaledHeight / 2), scaledWidth,
                scaledHeight, null);

        g2d.setComposite(originalComposite);
        super.autoDesenho(g);
    }
}