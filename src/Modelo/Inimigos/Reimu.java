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

        podeAtacar = false;
        setupEstados();
        SoundManager.getInstance().playMusic("Maiden's Capriccio ~ Dream Battle", true);
    }

    private void setupEstados() {
        // Movimento
        Estado mudarPodeAtacar = new MudarPodeAtacar(this, true);
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        estadoMovimento = irCentro;
        irCentro.setProximoEstado(mudarPodeAtacar);

        // Teia
        Estado teia = new TeiaDeTiros(this, 1);
        this.estadoTeia = teia;
        mudarPodeAtacar.setProximoEstado(teia);

        // Ataque
        Estado ataqueTeia = new TeiaDeTiros(this, 4);
        estadoAtaque = ataqueTeia;
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
        if (estadoMovimento instanceof IrPara irPara) {
            return irPara.getMovimento().proximoMovimento(this.x, this.y).x != 0;
        } else {
            return false;
        }
    }

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

    // Condicao
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

    // Movimento
    private final double ALTURA = ConfigMapa.MUNDO_ALTURA * 0.2;

    private class IrParaOCentro extends IrPara {

        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (ConfigMapa.MUNDO_LARGURA - 2) + 2, ALTURA),
                    velocidade
            );
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            super.incrementarTempo(fase, tempo);
        }
    }

    // Teia
    private class AtaqueLateralTeia extends AtaqueEmLequeNoJogador {

        private final double VELOCIDADE_PROJETIL = 0.5;
        private final TipoProjetilInimigo TIPO_PROJETIL = TipoProjetilInimigo.OVAL_CINZA;
        private final int TEMPO_ENTRE_ATAQUES = 5;

        public AtaqueLateralTeia(Boss boss, int duracao) {
            super(boss);
            PadraoLeque padrao = new PadraoLeque(0, 270, 8, duracao / TEMPO_ENTRE_ATAQUES);
            velocidadeProjetil = VELOCIDADE_PROJETIL;
            tipoProjetil = TIPO_PROJETIL;
            intervaloAtaque = TEMPO_ENTRE_ATAQUES;
            padroes.add(padrao);
        }
    }

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

    private class TeiaDeTiros extends MultiplosEstados {
        public TeiaDeTiros(Boss boss, int repeticoes) {
            super(boss, repeticoes);
            int tempoEntreAtaques = 80;
            estados.add(new AtaqueMeioTeia(boss, tempoEntreAtaques));
            estados.add(new AtaqueLateralTeia(boss, tempoEntreAtaques * 3));
        }
    }
}
