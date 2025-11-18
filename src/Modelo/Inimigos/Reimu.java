package Modelo.Inimigos;

import Auxiliar.ConfigMapa;
import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Auxiliar.SoundManager;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @brief Implementação do chefe Reimu.
 * 
 *        Este chefe utiliza múltiplas máquinas de estado que rodam em paralelo
 *        para movimento e diferentes tipos de ataque, criando padrões de
 *        projéteis
 *        densos e complexos.
 */
public class Reimu extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estadoMovimento;
    private transient Estado estadoTeia;
    private transient Estado estadoAtaque;
    private boolean podeAtacar;

    public Reimu(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (57 * BODY_PROPORTION);
        int scaledHeight = (int) (74 * BODY_PROPORTION);

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss4_spreadsheet.png",
                57, 74, 0, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true);
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        podeAtacar = false;
        setupEstados();
        SoundManager.getInstance().playMusic("Maiden's Capriccio ~ Dream Battle", true);
    }

    /**
     * @brief Configura as máquinas de estado paralelas para movimento e ataques.
     */
    private void setupEstados() {

        Estado mudarPodeAtacar = new MudarPodeAtacar(this, true);
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        estadoMovimento = irCentro;
        irCentro.setProximoEstado(mudarPodeAtacar);

        Estado teia = new TeiaDeTiros(this, 1);
        this.estadoTeia = teia;
        estadoTeia.setProximoEstado(teia);

        Estado ataqueTeliguiado = new AtaqueTeliguiadoDosLados(this);
        Estado espera = new Esperar(this, 180);
        estadoAtaque = ataqueTeliguiado;
        ataqueTeliguiado.setProximoEstado(espera);
        espera.setProximoEstado(ataqueTeliguiado);
    }

    /**
     * @brief Restaura o animador e as máquinas de estado após a desserialização.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (57 * BODY_PROPORTION);
        int scaledHeight = (int) (74 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss4_spreadsheet.png",
                57, 74, 0, 4, 4,
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
        if (estadoMovimento instanceof IrPara irPara) {
            return irPara.getMovimento().proximoMovimento(this.x, this.y).x != 0;
        } else {
            return false;
        }
    }

    /**
     * @brief Atualiza todas as máquinas de estado do chefe a cada frame.
     * 
     *        O estado de movimento é sempre processado, enquanto os estados de
     *        ataque
     *        só são processados após a flag `podeAtacar` ser ativada.
     */
    @Override
    public void atualizar() {
        estadoMovimento = processarEstado(estadoMovimento, 1);
        if (podeAtacar) {
            estadoAtaque = processarEstado(estadoAtaque, 1);
            estadoTeia = processarEstado(estadoTeia, 1);
        }
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    /**
     * @brief Estado simples que ativa a flag `podeAtacar` do chefe.
     */
    private final class MudarPodeAtacar extends Estado {

        protected final boolean podeAtacar;

        public MudarPodeAtacar(Boss boss, boolean podeAtacar) {
            super(boss);
            this.podeAtacar = podeAtacar;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (inimigo instanceof Reimu reimu) {
                reimu.podeAtacar = this.podeAtacar;
                estadoCompleto = true;
            }
        }
    }

    private final double ALTURA = ConfigMapa.MUNDO_ALTURA * 0.2;

    private class IrParaOCentro extends IrPara {

        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade);
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            super.incrementarTempo(fase, tempo);
        }
    }

    /**
     * @brief Ataque composto que dispara projéteis teleguiados das duas laterais da
     *        tela.
     */
    private class AtaqueTeliguiadoDosLados extends MultiplosEstados {

        private final int QUANTIDADE_ATAQUES = 5;
        private final TipoProjetilInimigo TIPO_PROJETIL = TipoProjetilInimigo.ESFERA_GRANDE_AMARELA_OCA;

        private class AtaqueEsquerda extends AtaqueEmUmaLinhaNoJogador {
            public AtaqueEsquerda(Boss boss, int quantidadeAtaques, int intervaloAtaque) {
                super(boss,
                        new Point2D.Double(0.0, 0.0),
                        new Point2D.Double(0.0, ConfigMapa.MUNDO_ALTURA));
                this.velocidadeProjetil = 0.3;
                this.tipoProjetil = TIPO_PROJETIL;
                this.padroes.add(new PadraoAtaque(0, quantidadeAtaques));
                this.intervaloAtaque = intervaloAtaque;
            }
        }

        private class AtaqueDireita extends AtaqueEmUmaLinhaNoJogador {
            public AtaqueDireita(Boss boss, int quantidadeAtaques, int intervaloAtaque) {
                super(boss,
                        new Point2D.Double(ConfigMapa.MUNDO_LARGURA, 0.0),
                        new Point2D.Double(ConfigMapa.MUNDO_LARGURA, ConfigMapa.MUNDO_ALTURA));
                this.velocidadeProjetil = 0.3;
                this.tipoProjetil = TIPO_PROJETIL;
                this.padroes.add(new PadraoAtaque(0, quantidadeAtaques));
                this.intervaloAtaque = intervaloAtaque;
            }
        }

        public AtaqueTeliguiadoDosLados(Boss boss) {
            super(boss);

            estados.add(new AtaqueEsquerda(boss, QUANTIDADE_ATAQUES, 10));
            estados.add(new AtaqueDireita(boss, QUANTIDADE_ATAQUES, 10));
        }
    }

    /**
     * @brief Ataque em leque que mira no jogador, disparando projéteis de forma
     *        contínua.
     */
    private class AtaqueLateralTeia extends AtaqueEmLequeNoJogador {

        private final double VELOCIDADE_PROJETIL = 0.5;
        private final TipoProjetilInimigo TIPO_PROJETIL = TipoProjetilInimigo.OVAL_CINZA;
        private final int TEMPO_ENTRE_ATAQUES = 10;

        public AtaqueLateralTeia(Boss boss, int duracao) {
            super(boss);
            PadraoLeque padrao = new PadraoLeque(0, 270, 8, duracao / TEMPO_ENTRE_ATAQUES);
            velocidadeProjetil = VELOCIDADE_PROJETIL;
            tipoProjetil = TIPO_PROJETIL;
            intervaloAtaque = TEMPO_ENTRE_ATAQUES;
            padroes.add(padrao);
        }
    }

    /**
     * @brief Ataque em leque que dispara um grande número de projéteis a partir do
     *        chefe.
     */
    private class AtaqueMeioTeia extends AtaqueEmLequeNoJogador {

        private final double VELOCIDADE_PROJETIL = 0.15;
        private final TipoProjetilInimigo TIPO_PROJETIL = TipoProjetilInimigo.ESFERA_GRANDE_VERMELHA_OCA;

        public AtaqueMeioTeia(Boss boss, int tempoEntreAtaques) {
            super(boss);
            velocidadeProjetil = VELOCIDADE_PROJETIL;
            tipoProjetil = TIPO_PROJETIL;
            intervaloAtaque = tempoEntreAtaques;
            padroes.add(new PadraoLeque(0, 270, 30));
            padroes.add(new PadraoLeque(0, 270, 31));
        }
    }

    /**
     * @brief Ataque composto que combina um ataque em leque a partir do chefe com
     *        um ataque contínuo que também mira no jogador, criando uma "teia" de
     *        projéteis.
     */
    private class TeiaDeTiros extends MultiplosEstados {
        public TeiaDeTiros(Boss boss, int repeticoes) {
            super(boss, repeticoes);
            int tempoEntreAtaques = 80;
            estados.add(new AtaqueMeioTeia(boss, tempoEntreAtaques));
            estados.add(new AtaqueLateralTeia(boss, tempoEntreAtaques * 3));
        }
    }
}
