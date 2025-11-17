package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Auxiliar.SoundManager;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Reisen extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Reisen(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (41 * BODY_PROPORTION);
        int scaledHeight = (int) (82 * BODY_PROPORTION);

        // 41x82
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss5_spreadsheet.png",
                41, 82, 0, 4, 4,
                true, // resize = true
                scaledWidth,
                scaledHeight,
                true // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        setupEstados();
        SoundManager.getInstance().playMusic("Cinderella Cage ~ Kagome-Kagome", true);
    }

    private void setupEstados() {
        // Movimentos
        Estado irCentro1 = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        Estado irCentro2 = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        Estado irEsquerda1 = new IrParaEsquerda(this, new Point2D.Double(0.4, 0.3));
        Estado irDireita1 = new IrParaDireita(this, new Point2D.Double(0.4, 0.3));
        Estado irEsquerda2 = new IrParaEsquerda(this, new Point2D.Double(0.4, 0.3));
        Estado irDireita2 = new IrParaDireita(this, new Point2D.Double(0.4, 0.3));

        // Ataques
        Estado ataqueEspiral = new AtaqueEspiral(this);
        Estado ondaTeleguiada1 = new OndaTeleguiada(this);
        Estado chuvaVertical1 = new ChuvaVertical(this);
        Estado ataqueEspiralMovel = new AtaqueEspiralMovel(this);
        Estado ondaTeleguiada2 = new OndaTeleguiada(this);
        Estado chuvaVertical2 = new ChuvaVertical(this);

        // Esperas
        Estado espera1 = new Esperar(this, 60);
        Estado espera2 = new Esperar(this, 60);
        Estado espera3 = new Esperar(this, 60);
        Estado espera4 = new Esperar(this, 60);

        // Sequência de estados: Espiral dupla -> teleguiado -> chuva -> horário/anti-horário -> teleguiado -> chuva -> loop
        estado = irCentro1;
        irCentro1.setProximoEstado(ataqueEspiral);
        
        ataqueEspiral.setProximoEstado(espera1);
        espera1.setProximoEstado(irEsquerda1);
        
        irEsquerda1.setProximoEstado(ondaTeleguiada1);
        ondaTeleguiada1.setProximoEstado(irDireita1);
        
        irDireita1.setProximoEstado(chuvaVertical1);
        chuvaVertical1.setProximoEstado(espera2);
        
        espera2.setProximoEstado(irCentro2);
        irCentro2.setProximoEstado(ataqueEspiralMovel);
        ataqueEspiralMovel.setProximoEstado(espera3);
        
        espera3.setProximoEstado(irEsquerda2);
        irEsquerda2.setProximoEstado(ondaTeleguiada2);
        ondaTeleguiada2.setProximoEstado(irDireita2);
        
        irDireita2.setProximoEstado(chuvaVertical2);
        chuvaVertical2.setProximoEstado(espera4);
        
        espera4.setProximoEstado(irCentro1);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (41 * BODY_PROPORTION);
        int scaledHeight = (int) (82 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss5_spreadsheet.png",
                41, 82, 0, 4, 4,
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

    // Ataques
    // Ataque 1: Espiral dupla rotativa
    private class AtaqueEspiral extends MultiplosEstados {

        public AtaqueEspiral(Boss boss) {
            super(boss);

            estados.add(new EspiralRotacional(boss, 8, 0.18, TipoProjetilInimigo.ESFERA_ROXA, 24, 15, 12));
            estados.add(new EspiralRotacional(boss, 8, 0.18, TipoProjetilInimigo.ESFERA_AMARELA, -24, 15, 13));
        }
    }
    
    // Espiral que alterna direção
    private class AtaqueEspiralMovel extends MultiplosEstados {

        public AtaqueEspiralMovel(Boss boss) {
            super(boss);

            repeticoes = 3;
            for (int i = 0; i < 6; i++) {
                if (i % 2 == 0) {
                    estados.add(new EspiralRotacional(boss, 6, 0.2, TipoProjetilInimigo.ESFERA_ROXA, 45, 8, 8));
                } else {
                    estados.add(new EspiralRotacional(boss, 6, 0.2, TipoProjetilInimigo.ESFERA_VERDE, -45, 8, 8));
                }
            }
        }
    }

    private class EspiralRotacional extends AtaqueEmLeque {
        public EspiralRotacional(Boss boss, int intervaloAtaque, double velocidade, TipoProjetilInimigo tipoProjetil, int velocidadeRotacao, int ataques, int tiros) {
            super(boss);
            this.intervaloAtaque = intervaloAtaque;
            this.velocidadeProjetil = velocidade;
            this.tipoProjetil = tipoProjetil;

            for (int i = 0; i < ataques; i++) {
                padroes.add(new PadraoLeque(i * velocidadeRotacao, 360, tiros));
            }
        }
    }

    // Ataque 2: Onda teleguiada das laterais
    private class OndaTeleguiada extends MultiplosEstados {

        private final int ANGLE_RANDOMNESS = 20;
        private final int INTERVALO_ATAQUE = 30;
        private final double VELOCIDADE_PROJETIL = 0.2;
        private final int QUANTIDADE_ATAQUES = 3;

        public OndaTeleguiada(Boss boss) {
            super(boss);

            estados.add(new AtaqueEsquerdaTeleguiado(boss));
            estados.add(new AtaqueDireitaTeleguiado(boss));
        }

        private class AtaqueEsquerdaTeleguiado extends AtaqueEmUmaLinhaNoJogador {

            public AtaqueEsquerdaTeleguiado(Boss boss) {
                super(boss,
                        new Point2D.Double(0, 0),
                        new Point2D.Double(0, MUNDO_ALTURA)
                );
                this.intervaloAtaque = INTERVALO_ATAQUE;
                this.velocidadeProjetil = VELOCIDADE_PROJETIL;
                this.tipoProjetil = TipoProjetilInimigo.ESFERA_GRANDE_VERMELHA_OCA;

                for(int i = 0; i < QUANTIDADE_ATAQUES; i++){
                    int angleOffset = (int)(Math.random() * (2 * ANGLE_RANDOMNESS)) - ANGLE_RANDOMNESS;
                    this.padroes.add(new PadraoAtaque(angleOffset, 8));
                }
            }
        }

        private class AtaqueDireitaTeleguiado extends AtaqueEmUmaLinhaNoJogador {

            public AtaqueDireitaTeleguiado(Boss boss) {
                super(boss,
                        new Point2D.Double(MUNDO_LARGURA, 0),
                        new Point2D.Double(MUNDO_LARGURA, MUNDO_ALTURA)
                );
                this.intervaloAtaque = INTERVALO_ATAQUE;
                this.velocidadeProjetil = VELOCIDADE_PROJETIL;
                this.tipoProjetil = TipoProjetilInimigo.ESFERA_GRANDE_AMARELA_OCA;

                for(int i = 0; i < QUANTIDADE_ATAQUES; i++){
                    int angleOffset = (int)(Math.random() * (2 * ANGLE_RANDOMNESS)) - ANGLE_RANDOMNESS;
                    this.padroes.add(new PadraoAtaque(angleOffset, 8));
                }
            }
        }
    }

    // Ataque 3: Chuva vertical de projéteis em leque
    private class ChuvaVertical extends MultiplosEstados {

        public ChuvaVertical(Boss boss) {
            super(boss);

            int quantidadeAtaques = 5;
            double espacamentoX = MUNDO_LARGURA / (quantidadeAtaques + 1);

            for (int i = 0; i < quantidadeAtaques; i++) {
                AtaqueEmLequeNaPosicao ataque = new AtaqueEmLequeNaPosicao(boss);
                ataque.posicaoAtaque = new Point2D.Double(espacamentoX * (i + 1), 2.0);
                ataque.intervaloAtaque = 18 + i * 8;
                ataque.velocidadeProjetil = 0.22;
                ataque.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;
                ataque.padroes.add(ataque.new PadraoLeque(90, 30, 5));

                estados.add(ataque);
            }

            for (int i = 0; i < quantidadeAtaques; i++) {
                AtaqueEmLequeNaPosicao ataque = new AtaqueEmLequeNaPosicao(boss);
                ataque.posicaoAtaque = new Point2D.Double(espacamentoX * (i + 1), 2.0);
                ataque.intervaloAtaque = 18 - i * 8;
                ataque.velocidadeProjetil = 0.22;
                ataque.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;
                ataque.padroes.add(ataque.new PadraoLeque(90, 30, 5));

                estados.add(ataque);
            }
        }
    }
}
