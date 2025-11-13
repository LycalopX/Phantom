package Modelo.Inimigos;

import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Nightbug extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private boolean isMoving = false; // Simple state for now

    public Nightbug(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;
        
        int scaledWidth = (int) (35 * BODY_PROPORTION);
        int scaledHeight = (int) (60 * BODY_PROPORTION);

        this.animador = new GerenciadorDeAnimacaoInimigo(
            "imgs/inimigos/boss1_spreadsheet.png",
            35, 60, 13, 4, 4,
            true, // resize = true
            scaledWidth,
            scaledHeight
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (35 * BODY_PROPORTION);
        int scaledHeight = (int) (60 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
            "imgs/inimigos/boss1_spreadsheet.png",
            35, 60, 13, 4, 4,
            true,
            scaledWidth,
            scaledHeight
        );
    }

    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public void atualizar() {
        super.atualizar(); // Basic movement from Inimigo
        animador.atualizar();
        
        // Simple logic to switch between idle and moving for demonstration
        if (Math.random() > 0.99) {
            isMoving = !isMoving;
        }
    }

    @Override
    public void autoDesenho(Graphics g) {
        AnimationState animState = isMoving ? AnimationState.STRAFING : AnimationState.IDLE;
        this.iImage = animador.getImagemAtual(animState);
        
        super.autoDesenho(g);
    }
}
