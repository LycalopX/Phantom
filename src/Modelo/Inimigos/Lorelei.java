package Modelo.Inimigos;

import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Lorelei extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private boolean isMoving = false; // Simple state for now

    public Lorelei(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;
        
        int scaledWidth = (int) (43 * BODY_PROPORTION);
        int scaledHeight = (int) (62 * BODY_PROPORTION);

        this.animador = new GerenciadorDeAnimacaoInimigo(
            "imgs/inimigos/boss2_spreadsheet.png",
            43, 62, 0, 4, 4,
            true, // resize = true
            scaledWidth,
            scaledHeight,
            true // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (43 * BODY_PROPORTION);
        int scaledHeight = (int) (62 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
            "imgs/inimigos/boss2_spreadsheet.png",
            43, 62, 0, 4, 4,
            true,
            scaledWidth,
            scaledHeight,
            true // holdLastStrafingFrame
        );
    }

    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public boolean isStrafing() {
        return this.isMoving;
    }

    @Override
    public void atualizar() {
        super.atualizar(); // Basic movement from Inimigo
        
        boolean wasMoving = this.isMoving;
        if (Math.random() > 0.99) {
            isMoving = !isMoving;
        }

        if (isMoving != wasMoving) {
            animador.resetFrame();
        }

        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }
}
