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

public class FadaComum4 extends Inimigo {

    private Estado estadoAtual;
    private transient GerenciadorDeAnimacaoInimigo animador;

    public FadaComum4(double x, double y, LootTable lootTable, double vida, Fase fase, String skin, int behaviorType) {
        super("", x, y, lootTable, vida); // Vida maior para durar mais
        this.faseReferencia = fase;

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy1_spreadsheet" + skin + ".png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false);
        this.largura = (int) (32.0 * BODY_PROPORTION);
        this.altura = (int) (32.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;

        switch (behaviorType) {
            case 1:
            default:
                // Define a sequência de estados para a "Espiral da Morte"
                Estado irParaCentro = new IrPara(this, MUNDO_LARGURA / 2.0, 6, 0.1);
                Estado ataqueEspiral = new AtaqueEspiral(this, 3, 2.0, 480); // 3 frames de cooldown, 2 graus de rotação, 8 segundos de duração
                Estado sair = new IrPara(this, MUNDO_LARGURA / 2.0, -2, 0.1);

                irParaCentro.setProximoEstado(ataqueEspiral);
                ataqueEspiral.setProximoEstado(sair);

                this.estadoAtual = irParaCentro;
                break;
            case 2:
                // Novo comportamento a ser implementado
                break;
        }
    }

    @Override
    public void atualizar() {
        this.estadoAtual = processarEstado(this.estadoAtual, 1);
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public boolean isStrafing() {
        // Animação de strafing durante o ataque para parecer mais ativa
        return estadoAtual instanceof AtaqueEspiral;
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy4_spreadsheet.png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false);
    }

    // --- Novo Estado para o Ataque Espiral ---

    private class AtaqueEspiral extends Estado {
        private double anguloAtual = 0;
        private int cooldownTiro;
        private final int cooldownTiroInicial;
        private final double velocidadeRotacao;
        private int duracaoAtaque;

        public AtaqueEspiral(Inimigo inimigo, int cooldownTiro, double velocidadeRotacao, int duracao) {
            super(inimigo);
            this.cooldownTiroInicial = cooldownTiro;
            this.cooldownTiro = 0;
            this.velocidadeRotacao = velocidadeRotacao;
            this.duracaoAtaque = duracao;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) return;

            duracaoAtaque -= tempo;
            if (duracaoAtaque <= 0) {
                this.estadoCompleto = true;
                return;
            }

            cooldownTiro -= tempo;
            if (cooldownTiro <= 0) {
                if (fase != null) {
                    Projetil p = fase.getProjetilPool().getProjetilInimigo();
                    if (p != null) {
                        p.reset(inimigo.getX(), inimigo.getY(), 0.08, anguloAtual, TipoProjetil.INIMIGO, TipoProjetilInimigo.FLECHA_VERMELHO_ESCURO);
                    }
                    Auxiliar.SoundManager.getInstance().playSfx("se_tan00", 0.6f);
                }
                cooldownTiro = cooldownTiroInicial;
            }

            // Rotaciona o ângulo para o próximo tiro
            anguloAtual += velocidadeRotacao;
            if (anguloAtual >= 360) {
                anguloAtual -= 360;
            }
        }
    }
}
