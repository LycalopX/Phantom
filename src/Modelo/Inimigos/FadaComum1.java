package Modelo.Inimigos;

import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import static Auxiliar.ConfigMapa.*;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @brief Implementação de um tipo de inimigo "Fada" com múltiplos
 *        comportamentos.
 * 
 *        Este inimigo pode seguir duas lógicas de movimento e ataque distintas,
 *        selecionadas pelo `behaviorType` no construtor.
 */
public class FadaComum1 extends Inimigo {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private int behaviorType;
    private Estado estadoAtual;

    public FadaComum1(double x, double y, LootTable lootTable, double vida, Fase fase, String skin, int behaviorType) {
        super("", x, y, lootTable, vida);

        this.faseReferencia = fase;
        this.behaviorType = behaviorType;
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/enemy1_spreadsheet" + skin + ".png",
                30, 30, 2, 4, 4,
                true,
                (int) (30.0 * BODY_PROPORTION),
                (int) (30.0 * BODY_PROPORTION),
                false);

        this.largura = (int) (30.0 * BODY_PROPORTION);
        this.altura = (int) (30.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;

        // O `behaviorType` seleciona qual sequência de estados o inimigo seguirá.
        switch (behaviorType) {
            case 2:
                // Comportamento 2: Move-se para uma posição, atira, move-se para outra, atira
                // novamente e sai.
                Estado entrada2 = new IrPara(this, this.x, 8, 0.1);
                Estado atirando1_2 = new AtirandoMirado(this, 30, 150);
                Estado mover = new IrPara(this, MUNDO_LARGURA - this.x, 8, 0.3);
                Estado atirando2_2 = new AtirandoMirado(this, 30, 150);
                Estado saida2 = new IrPara(this, MUNDO_LARGURA - this.x, -2, 0.05);

                entrada2.setProximoEstado(atirando1_2);
                atirando1_2.setProximoEstado(mover);
                mover.setProximoEstado(atirando2_2);
                atirando2_2.setProximoEstado(saida2);
                this.estadoAtual = entrada2;
                break;
            case 1:
            default:
                // Comportamento 1: Entra com movimento senoidal, para e atira, depois sai.
                Estado entrada1 = new MovimentoSinusoidalEntrando(this, 8, 4, 0.5);
                Estado atirando1 = new AtirandoMirado(this, 60, 300);
                Estado saida1 = new IrPara(this, this.initialX, -2, 0.05);

                entrada1.setProximoEstado(atirando1);
                atirando1.setProximoEstado(saida1);
                this.estadoAtual = entrada1;
                break;
        }
    }

    /**
     * @brief Restaura o animador após a desserialização.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/enemy1_spreadsheet.png",
                30, 30, 2, 4, 4,
                true,
                (int) (30.0 * BODY_PROPORTION),
                (int) (30.0 * BODY_PROPORTION),
                false);
    }

    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public boolean isStrafing() {
        return !(estadoAtual instanceof AtirandoMirado);
    }

    @Override
    public void atualizar() {
        this.estadoAtual = processarEstado(this.estadoAtual, 1);
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    private void atirar() {
        if (faseReferencia == null || faseReferencia.getHero() == null)
            return;

        double angle = getAnguloEmDirecaoAoHeroi();

        if (this.behaviorType == 2) {
            double spread = 15.0;
            Projetil p1 = faseReferencia.getProjetilPool().getProjetilInimigo();
            if (p1 != null)
                p1.reset(this.x, this.y, 0.1, angle, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);

            Projetil p2 = faseReferencia.getProjetilPool().getProjetilInimigo();
            if (p2 != null)
                p2.reset(this.x, this.y, 0.1, angle - spread, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);

            Projetil p3 = faseReferencia.getProjetilPool().getProjetilInimigo();
            if (p3 != null)
                p3.reset(this.x, this.y, 0.1, angle + spread, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);
        } else {
            Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
            if (p != null) {
                p.reset(this.x, this.y, 0.1, angle, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);
            }
        }
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    /**
     * @brief Estado que move o inimigo para uma posição Y com um movimento
     *        senoidal.
     */
    private class MovimentoSinusoidalEntrando extends Estado {
        private double targetY;
        private double amplitude;
        private double frequency;

        public MovimentoSinusoidalEntrando(Inimigo inimigo, double targetY, double amplitude, double frequency) {
            super(inimigo);
            this.targetY = targetY;
            this.amplitude = amplitude;
            this.frequency = frequency;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto)
                return;
            y += 0.1 * tempo;
            x = initialX + Math.sin(y * frequency) * amplitude;
            if (y >= targetY) {
                y = targetY;
                estadoCompleto = true;
            }
        }
    }

    /**
     * @brief Estado em que o inimigo permanece parado e atira no jogador em
     *        intervalos regulares.
     */
    private class AtirandoMirado extends Estado {
        private int shootTimer;
        private int shootInterval;
        private int shootDuration;

        public AtirandoMirado(Inimigo inimigo, int interval, int duration) {
            super(inimigo);
            this.shootInterval = interval;
            this.shootDuration = duration;
            this.shootTimer = 0;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto)
                return;

            shootDuration -= tempo;
            shootTimer -= tempo;

            if (shootTimer <= 0) {
                atirar();
                if (shootDuration % 2 == 0) {
                    Auxiliar.SoundManager.getInstance().playSfx("se_tan01", 1.0f);
                } else {
                    Auxiliar.SoundManager.getInstance().playSfx("se_tan02", 1.0f);
                }
                shootTimer = shootInterval;
            }

            if (shootDuration <= 0) {
                estadoCompleto = true;
            }
        }
    }
}
