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

public class FadaComum extends Inimigo {

    private enum State {
        ENTERING,
        SHOOTING,
        EXITING
    }

    private State currentState;
    private double targetY = 8;
    private double initialX;
    private double amplitude = 4;
    private double frequency = 0.5;
    private int shootTimer = 0;
    private int shootInterval = 60; // Atira a cada 60 frames
    private int shootDuration = 300; // Atira por 300 frames (5 segundos)

    private transient GerenciadorDeAnimacaoInimigo animador;

    public FadaComum(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, 50);
        this.currentState = State.ENTERING;
        this.initialX = x;
        this.faseReferencia = fase;
        this.animador = new GerenciadorDeAnimacaoInimigo();
        
        this.largura = (int) (30.0 * BODY_PROPORTION);
        this.altura = (int) (30.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacaoInimigo();
    }
    
    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public void atualizar() {
        animador.atualizar();

        switch (currentState) {
            case ENTERING:
                // Movimento em arco para baixo
                y += 0.1; // Velocidade vertical
                x = initialX + Math.sin(y * frequency) * amplitude;

                if (y >= targetY) {
                    y = targetY;
                    currentState = State.SHOOTING;
                }
                break;

            case SHOOTING:
                shootDuration--;
                shootTimer--;

                if (shootTimer <= 0) {
                    atirar();
                    shootTimer = shootInterval;
                }

                if (shootDuration <= 0) {
                    currentState = State.EXITING;
                }
                break;

            case EXITING:
                // Move para cima para sair da tela
                y -= 0.05;
                if (y < -1) {
                    deactivate();
                }
                break;
        }
    }

    private void atirar() {
        if (faseReferencia == null) return;

        Personagem hero = faseReferencia.getHero();
        if (hero == null) return; // Não atira se o herói não existe

        double angle = 90.0; // Ângulo padrão (para baixo)

        // Calcula o ângulo em direção ao herói
        double dx = hero.x - this.x;
        double dy = hero.y - this.y;
        angle = Math.toDegrees(Math.atan2(dy, dx));

        Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
        if (p != null) {
            p.reset(this.x, this.y, 0.1, angle, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);
        }
    }

    @Override
    public void autoDesenho(Graphics g) {
        AnimationState animState = AnimationState.IDLE;
        if (currentState == State.ENTERING || currentState == State.EXITING) {
            animState = AnimationState.STRAFING;
        }

        this.iImage = animador.getImagemAtual(animState);
        super.autoDesenho(g);
    }
}
