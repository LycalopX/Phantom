package Modelo.Inimigos;

import Auxiliar.ConfigMapa;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Lorelei extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estadoDeMovimento;
    private transient Estado estadoDeAtaque;
    private transient Estado estadoDeAtaqueLateral;
    private boolean chegouAoCentro;

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
            false // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;
        this.chegouAoCentro = false;
        
        setupEstados();
        SoundManager.getInstance().playMusic("Deaf to All but the Song", true);
    }

    private void setupEstados(){
        // Movimento
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.4, 0.4));
        Estado irEsquerda = new IrParaEsquerda(this, new Point2D.Double(0.2, 0.2));
        Estado irDireita = new IrParaDireita(this, new Point2D.Double(0.2, 0.2));
        estadoDeMovimento = irCentro;
        irCentro.setProximoEstado(irEsquerda);
        irEsquerda.setProximoEstado(irDireita);
        irDireita.setProximoEstado(irEsquerda);

        // Ataque para baixo
        Estado ataqueParaBaixo = new AtaqueParaBaixo(this);
        estadoDeAtaque = ataqueParaBaixo;
        ataqueParaBaixo.setProximoEstado(ataqueParaBaixo);

        // Ataque lateral
        Estado ataqueLateralDireita = new AtaqueDoLadoDireito(this);
        estadoDeAtaqueLateral = ataqueLateralDireita;
        ataqueLateralDireita.setProximoEstado(ataqueLateralDireita);
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
        if (estadoDeMovimento instanceof IrPara irPara) {
            return irPara.getMovimento().proximoMovimento(this.x, this.y).x != 0;
        } else {
            return false;
        }
    }

    @Override
    public void atualizar() {
        estadoDeMovimento = processarEstado(estadoDeMovimento, 1);
        if(chegouAoCentro) {
            estadoDeAtaque = processarEstado(estadoDeAtaque, 1);
        }
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    // Movimento
    private final double ALTURA = ConfigMapa.MUNDO_ALTURA * 0.25; 
    private class IrParaOCentro extends IrPara {
        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade
            );
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo){
            super.incrementarTempo(fase, tempo);
            if (estadoCompleto) {
                chegouAoCentro = true;
            }
        }
    }

    private class IrParaEsquerda extends IrPara {
        public IrParaEsquerda(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade
            );
        }
    }

    private class IrParaDireita extends IrPara {
        public IrParaDireita(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(1 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade
            );
        }
    }

    // Ataque
    private class AtaqueParaBaixo extends AtaqueEmLeque {
        public AtaqueParaBaixo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 10;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AMARELA;

            padroes.add(new PadraoLeque(90, 0, 10));
        }
    }

    private class AtaqueDoLadoDireito extends AtaqueEmLequeNaPosicao {
        public AtaqueDoLadoDireito(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.FLECHA_AMARELO_ESCURO;
            this.posicaoAtaque.x = 0;
            this.posicaoAtaque.y = 0;

            padroes.add(new PadraoLeque(50, 140, 10));
            padroes.add(new PadraoLeque(50, 80, 10));
        }
    }
}
