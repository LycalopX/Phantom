package Modelo.Inimigos;

import Auxiliar.ConfigMapa;
import Auxiliar.LootTable;
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
    private transient Estado estadoDeAtaqueLateralDireita;
    private transient Estado estadoDeAtaqueLateralEsquerda;
    private boolean atacarParaBaixo;

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
        this.atacarParaBaixo = false;
        
        setupEstados();
    }

    private void setupEstados(){
        // Pode atacar
        Estado podeAtacar = new MudarPodeAtacarParaBaixo(this, true);
        Estado naoPodeAtacar = new MudarPodeAtacarParaBaixo(this, false);

        // Movimento
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.4, 0.4));
        Estado irEsquerda = new IrParaEsquerda(this, new Point2D.Double(0.2, 0.2));
        Estado irDireita = new IrParaDireita(this, new Point2D.Double(0.2, 0.2));
        estadoDeMovimento = naoPodeAtacar;
        naoPodeAtacar.setProximoEstado(irCentro);
        irCentro.setProximoEstado(podeAtacar);
        podeAtacar.setProximoEstado(irEsquerda);
        irEsquerda.setProximoEstado(irDireita);

        Estado irCentro2 = new IrParaOCentro(this, new Point2D.Double(0.2, 0.2));
        irDireita.setProximoEstado(irCentro2);
        Estado naoPodeAtacar2 = new MudarPodeAtacarParaBaixo(this, false);
        irCentro2.setProximoEstado(naoPodeAtacar2);
        Estado espera = new Esperar(this, 120);
        naoPodeAtacar2.setProximoEstado(espera);
        Estado podeAtacar2 = new MudarPodeAtacarParaBaixo(this, true);
        espera.setProximoEstado(podeAtacar2);
        podeAtacar2.setProximoEstado(irEsquerda);

        // Ataque para baixo
        Estado ataqueParaBaixo = new AtaqueParaBaixo(this);
        estadoDeAtaque = ataqueParaBaixo;
        ataqueParaBaixo.setProximoEstado(ataqueParaBaixo);

        // Ataque lateral
        Estado ataqueLateralDireita = new AtaqueDoLadoDireito(this);
        Estado ataqueLateralEsquerda = new AtaqueDoLadoEsquerdo(this);
        estadoDeAtaqueLateralDireita = ataqueLateralDireita;
        ataqueLateralDireita.setProximoEstado(ataqueLateralDireita);
        estadoDeAtaqueLateralEsquerda = ataqueLateralEsquerda;
        ataqueLateralEsquerda.setProximoEstado(ataqueLateralEsquerda);
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
        setupEstados();
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
        if(atacarParaBaixo) {
            estadoDeAtaque = processarEstado(estadoDeAtaque, 1);
        }
        estadoDeAtaqueLateralDireita = processarEstado(estadoDeAtaqueLateralDireita, 1);
        estadoDeAtaqueLateralEsquerda = processarEstado(estadoDeAtaqueLateralEsquerda, 1);
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    // Condicao
    private final class MudarPodeAtacarParaBaixo extends Estado {
        protected final boolean atacarParaBaixo;

        public MudarPodeAtacarParaBaixo(Boss boss, boolean atacarParaBaixo) {
            super(boss);
            this.atacarParaBaixo = atacarParaBaixo;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo){
            if(inimigo instanceof Lorelei lorelei) {
                lorelei.atacarParaBaixo = this.atacarParaBaixo;
                estadoCompleto = true;
            }
        }
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
    private class AtaqueParaBaixo extends AtaqueEmLequeNoJogador {
        public AtaqueParaBaixo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 20;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AMARELA;

            padroes.add(new PadraoLeque(0, 0, 10));
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

    private class AtaqueDoLadoEsquerdo extends AtaqueEmLequeNaPosicao {
        public AtaqueDoLadoEsquerdo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.FLECHA_AMARELO_ESCURO;
            this.posicaoAtaque.x = ConfigMapa.MUNDO_LARGURA;
            this.posicaoAtaque.y = 0;

            padroes.add(new PadraoLeque(130, 140, 10));
            padroes.add(new PadraoLeque(130, 80, 10));
        }
    }
}
