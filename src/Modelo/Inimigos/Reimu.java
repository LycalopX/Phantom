package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Reimu extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Reimu(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (57 * BODY_PROPORTION);
        int scaledHeight = (int) (74 * BODY_PROPORTION);

        // 57x74
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss4_spreadsheet.png",
                57, 74, 0, 4, 4,
                true, // resize = true
                scaledWidth,
                scaledHeight,
                true // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        setupEstados();
        SoundManager.getInstance().playMusic("Maiden's Capriccio ~ Dream Battle", true);
    }

    private void setupEstados() {
        
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (57 * BODY_PROPORTION);
        int scaledHeight = (int) (74 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss4_spreadsheet.png",
                57, 74, 0, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true // holdLastStrafingFrame
        );

        // Estado
        setupEstados();
    }

    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public boolean isStrafing() {
        if (estado instanceof IrPara irPara) {
            return irPara.getMovimento().proximoMovimento(this.x, this.y).x != 0;
        } else {
            return false;
        }
    }

    @Override
    public void atualizar() {
        estado = processarEstado(estado, 1);
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    // Movimento
    private class IrParaOCentro extends IrPara {
        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (MUNDO_LARGURA - 2) + 2, 0.2 * MUNDO_ALTURA),
                    velocidade
            );
        }
    }
}
