// Em src/Modelo/Projeteis/ProjetilBombaHoming.java
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
import Auxiliar.Consts;
import Auxiliar.TipoProjetil;

public class ProjetilBombaHoming extends ProjetilHoming {
    private enum EstadoBomba {
        EXPANDINDO, PERSEGUINDO
    }

    private EstadoBomba estadoAtual;
    private int tempoDeVida, tempoDeExpansao, animationTimer;

    private List<Point2D.Double> positionHistory = new ArrayList<>();
    private static final int TRAIL_LENGTH = 6;

    private static final int DURACAO_TOTAL_VIDA = 240;
    private static final int DURACAO_EXPANSAO = 15;

    public ProjetilBombaHoming(String sNomeImagePNG, ArrayList<Personagem> personagens) {
        super(sNomeImagePNG, personagens);
    }

    /**
     * Configura ou reseta o míssil a partir da ProjetilPool.
     * Define seu estado inicial para a fase de expansão.
     */
    public void resetBombaHoming(double x, double y, int largura, int altura, double hitboxRaio,
            double velocidadeGrid, double anguloExpansao, TipoProjetil tipo) {

        // Chama o reset da classe pai para configurar os atributos básicos
        super.reset(x, y, largura, altura, hitboxRaio, velocidadeGrid, anguloExpansao, tipo);

        // Configura o estado inicial e os timers para este tipo específico de míssil
        this.estadoAtual = EstadoBomba.EXPANDINDO;
        this.tempoDeVida = DURACAO_TOTAL_VIDA;
        this.tempoDeExpansao = DURACAO_EXPANSAO;
        this.positionHistory.clear();
        this.animationTimer = 0;
    }

    // (O método 'atualizar' não muda)

    @Override
    public void atualizar() {
        if (!isActive())
            return;

        // Adiciona a posição atual ao histórico para o rastro
        positionHistory.add(0, new Point2D.Double(this.x, this.y));
        if (positionHistory.size() > TRAIL_LENGTH) {
            positionHistory.remove(positionHistory.size() - 1);
        }
        animationTimer++;

        // O timer de vida total é decrementado independentemente do estado.
        tempoDeVida--;
        if (tempoDeVida <= 0) {
            deactivate(); // Desativa o míssil quando seu tempo de vida acaba.
            return;
        }

        // --- A Máquina de Estados em Ação ---
        switch (estadoAtual) {
            case EXPANDINDO:
                // Fase 1: Move-se reto na direção inicial.
                // A lógica de movimento já está no método 'atualizar' da classe Projetil.
                // Para evitar chamar a lógica de homing do pai, copiamos a linha de movimento
                // diretamente.
                this.x += Math.cos(this.anguloRad) * this.velocidade;
                this.y += Math.sin(this.anguloRad) * this.velocidade;

                tempoDeExpansao--;
                if (tempoDeExpansao <= 0) {
                    // Quando o tempo de expansão acaba, muda para a fase de perseguição.
                    this.estadoAtual = EstadoBomba.PERSEGUINDO;
                }
                break;

            case PERSEGUINDO:
                // Fase 2: Ativa a lógica de homing da classe pai (ProjetilHoming).
                // O método 'atualizar' de ProjetilHoming já contém a lógica para
                // encontrar um alvo, ajustar o ângulo e se mover.
                super.atualizar();
                break;
        }
    }

    // MÉTODO 'autoDesenho' COMPLETAMENTE REESCRITO PARA PERFORMANCE
    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive() || iImage == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();

        // Este loop agora desenha apenas as partículas do rastro.
        for (int i = 0; i < positionHistory.size(); i++) {

            Point2D.Double pos = positionHistory.get(i);

            // Calcula a opacidade (alpha) com base na idade da partícula.
            // O valor de alpha para a cor vai de 0 (transparente) a 255 (opaco).
            // '200' é a opacidade máxima da partícula mais recente.
            int alpha = (int) (200 * (1.0f - (float) i / TRAIL_LENGTH));

            // CORREÇÃO: Cria uma cor que já contém a informação de transparência.
            // Isso é mais robusto e resolve o bug da opacidade.
            g2d.setColor(new Color(255, 100, 100, alpha));

            int telaX = (int) Math.round(pos.x * Consts.CELL_SIDE);
            int telaY = (int) Math.round(pos.y * Consts.CELL_SIDE);

            int particleSize = 8;
            g2d.fillOval(telaX - particleSize / 2, telaY - particleSize / 2, particleSize, particleSize);
        }

        // --- 2. DESENHAR O SPRITE PRINCIPAL (COM AURA E PULSO) ---
        // Esta lógica agora está FORA do loop e é executada apenas UMA VEZ por míssil.
        int telaX = (int) Math.round(this.x * Consts.CELL_SIDE);
        int telaY = (int) Math.round(this.y * Consts.CELL_SIDE);

        double scale = 1.0 + 0.1 * Math.sin(animationTimer * 0.3);
        int scaledWidth = (int) (this.largura * scale);
        int scaledHeight = (int) (this.altura * scale);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2d.drawImage(iImage.getImage(), telaX - (scaledWidth / 2), telaY - (scaledHeight / 2), scaledWidth,
                scaledHeight, null);

        // Desenha a Aura
        float auraAlpha = 0.3f + 0.2f * (float) Math.sin(animationTimer * 0.2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, auraAlpha));
        g.setColor(new Color(255, 50, 50));
        g.fillOval(telaX - (scaledWidth / 2) - 4, telaY - (scaledHeight / 2) - 4, scaledWidth + 8, scaledHeight + 8);

        // Restaura o composite e desenha a hitbox de debug por último
        g2d.setComposite(originalComposite);
        super.autoDesenho(g);
    }
}