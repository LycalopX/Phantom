package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Nightbug extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

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
                scaledHeight,
                true // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        setupEstados();
        SoundManager.getInstance().playMusic("Wriggling Autumn Moon ~ Mooned Insect", true);
    }

    private void setupEstados() {
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.2, 0.2));
        Estado irEsquerda = new IrParaEsquerda(this, new Point2D.Double(0.5, 0.2));
        Estado irDireita = new IrParaDireita(this, new Point2D.Double(0.5, 0.2));

        Estado ataqueParaBaixo = new AtaqueParaBaixo(this);
        Estado ataqueParaDireita = new AtaqueParaDireita(this);
        Estado ataqueParaEsquerda = new AtaqueParaEsquerda(this);

        estado = irCentro;
        irCentro.setProximoEstado(ataqueParaBaixo);

        ataqueParaBaixo.setProximoEstado(irEsquerda);
        irEsquerda.setProximoEstado(ataqueParaDireita);

        ataqueParaDireita.setProximoEstado(irDireita);
        irDireita.setProximoEstado(ataqueParaEsquerda);
        ataqueParaEsquerda.setProximoEstado(irCentro);
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

    private class IrParaEsquerda extends IrPara {
        public IrParaEsquerda(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.1 * (MUNDO_LARGURA - 2) + 2, 0.1 * MUNDO_ALTURA),
                    velocidade
            );
        }
    }

    private class IrParaDireita extends IrPara {
        public IrParaDireita(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.9 * (MUNDO_LARGURA - 2) + 2, 0.1 * MUNDO_ALTURA),
                    velocidade
            );
        }
    }

    // Ataque
    private class AtaqueParaBaixo extends AtaqueEmLeque {
        public AtaqueParaBaixo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;

            padroes.add(new PadraoLeque(90, 140, 10));
            padroes.add(new PadraoLeque(90, 80, 10));
        }
    }

    private class AtaqueParaDireita extends AtaqueEmLeque {
        public AtaqueParaDireita(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AZUL;

            padroes.add(new PadraoLeque(50, 140, 10));
            padroes.add(new PadraoLeque(50, 80, 10));
        }
    }

    private class AtaqueParaEsquerda extends AtaqueEmLeque {
        public AtaqueParaEsquerda(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AZUL;

            padroes.add(new PadraoLeque(130, 140, 10));
            padroes.add(new PadraoLeque(130, 80, 10));
        }
    }
}
