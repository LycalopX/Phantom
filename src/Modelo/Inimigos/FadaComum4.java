package Modelo.Inimigos;

import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Personagem;
import Modelo.Projeteis.Projetil;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import static Auxiliar.ConfigMapa.*;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

// Implementar IA melhorada

/**
 * @brief Representa um inimigo comum do tipo "fada", com um padrão de movimento
 *        e ataque predefinido.
 */
public class FadaComum4 extends Inimigo {

    private enum State {
        ENTERING,
    }

    private State currentState;
    private double targetY = 8;
    private double amplitude = 4;
    private double frequency = 0.5;
    private int shootTimer = 0;
    private int shootInterval = 60;
    private int shootDuration = 300;

    private transient GerenciadorDeAnimacaoInimigo animador;

    /**
     * @brief Construtor da FadaComum.
     */
    public FadaComum4(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, 40);
        this.currentState = State.ENTERING;
        this.faseReferencia = fase;
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy4_spreadsheet.png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false);
        this.largura = (int) (32.0 * BODY_PROPORTION);
        this.altura = (int) (32.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;
    }

    /**
     * @brief Método para desserialização, recarrega o gerenciador de animação.
     */
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

    /**
     * @brief Inicializa a referência da fase para o inimigo.
     */
    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public boolean isStrafing() {
        return false;
    }

    /**
     * @brief Atualiza a lógica do inimigo, incluindo sua máquina de estados de
     *        movimento e ataque.
     */
    @Override
    public void atualizar() {

        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    /**
     * @brief Cria e dispara um projétil em direção ao herói.
     */
    private void atirar() {
        if (faseReferencia == null)
            return;

        Personagem hero = faseReferencia.getHero();
        if (hero == null)
            return;

            /*
        double angle = 90.0;
        double dx = hero.getX() - this.x;
        double dy = hero.getY() - this.y;
        angle = Math.toDegrees(Math.atan2(dy, dx));

        Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
        if (p != null) {
            p.reset(this.x, this.y, 0.1, angle, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);
        }
            */
    }

    /**
     * @brief Desenha o inimigo na tela, selecionando a animação correta com base em
     *        seu estado.
     */
    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }
}
