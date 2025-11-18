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

/**
 * @brief Implementação do chefe Lorelei.
 * 
 *        Este chefe utiliza múltiplas máquinas de estado que rodam em paralelo:
 *        uma para movimento e outras para diferentes tipos de ataque, criando
 *        comportamentos complexos e variados.
 */
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
                "Assets/inimigos/boss2_spreadsheet.png",
                43, 62, 0, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                false);
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;
        this.atacarParaBaixo = false;

        setupEstados();
    }

    /**
     * @brief Configura as máquinas de estado para movimento e ataques.
     * 
     *        Define as sequências de estados que controlam o movimento do chefe
     *        (ir para o centro, esquerda, direita) e os ataques (para baixo,
     *        laterais),
     *        encadeando-os para criar o padrão de comportamento do chefe.
     */
    private void setupEstados() {

        Estado podeAtacar = new MudarPodeAtacarParaBaixo(this, true);
        Estado naoPodeAtacar = new MudarPodeAtacarParaBaixo(this, false);

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

        Estado ataqueParaBaixo = new AtaqueParaBaixo(this);
        estadoDeAtaque = ataqueParaBaixo;
        ataqueParaBaixo.setProximoEstado(ataqueParaBaixo);

        Estado ataqueLateralDireita = new AtaqueDoLadoDireito(this);
        Estado ataqueLateralEsquerda = new AtaqueDoLadoEsquerdo(this);
        estadoDeAtaqueLateralDireita = ataqueLateralDireita;
        ataqueLateralDireita.setProximoEstado(ataqueLateralDireita);
        estadoDeAtaqueLateralEsquerda = ataqueLateralEsquerda;
        ataqueLateralEsquerda.setProximoEstado(ataqueLateralEsquerda);
    }

    /**
     * @brief Restaura o animador e as máquinas de estado após a desserialização.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (43 * BODY_PROPORTION);
        int scaledHeight = (int) (62 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss2_spreadsheet.png",
                43, 62, 0, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true);
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

    /**
     * @brief Atualiza todas as máquinas de estado do chefe a cada frame.
     */
    @Override
    public void atualizar() {
        estadoDeMovimento = processarEstado(estadoDeMovimento, 1);
        if (atacarParaBaixo) {
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

    /**
     * @brief Estado que simplesmente muda uma flag para habilitar ou desabilitar o
     *        ataque para baixo.
     */
    private final class MudarPodeAtacarParaBaixo extends Estado {
        protected final boolean atacarParaBaixo;

        public MudarPodeAtacarParaBaixo(Boss boss, boolean atacarParaBaixo) {
            super(boss);
            this.atacarParaBaixo = atacarParaBaixo;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (inimigo instanceof Lorelei lorelei) {
                lorelei.atacarParaBaixo = this.atacarParaBaixo;
                estadoCompleto = true;
            }
        }
    }

    private final double ALTURA = ConfigMapa.MUNDO_ALTURA * 0.25;

    private class IrParaOCentro extends IrPara {
        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade);
        }
    }

    private class IrParaEsquerda extends IrPara {
        public IrParaEsquerda(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade);
        }
    }

    private class IrParaDireita extends IrPara {
        public IrParaDireita(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(1 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade);
        }
    }

    /**
     * @brief Define o ataque em leque que mira no jogador.
     */
    private class AtaqueParaBaixo extends AtaqueEmLequeNoJogador {
        public AtaqueParaBaixo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 20;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AMARELA;

            padroes.add(new PadraoLeque(0, 0, 10));
        }
    }

    /**
     * @brief Define o ataque em leque que se origina da lateral direita da tela.
     */
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

    /**
     * @brief Define o ataque em leque que se origina da lateral esquerda da tela.
     */
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
