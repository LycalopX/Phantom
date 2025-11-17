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

public class Keine extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Keine(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (46 * BODY_PROPORTION);
        int scaledHeight = (int) (76 * BODY_PROPORTION);

        // 46x76
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss3_spreadsheet.png",
                46, 76, 0, 4, 4,
                true, // resize = true
                scaledWidth,
                scaledHeight,
                true // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        setupEstados();
    }

    private void setupEstados() {
        // Movimento
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.2, 0.2));

        // Ataque
        Estado espera1 = new Esperar(this, 300);
        Estado espera2 = new Esperar(this, 300);
        Estado ataqueCeu = new AtaqueCeu(this);
        Estado ataqueHorizontal = new AtaqueHorizontal(this);
        Estado ataqueEmV = new AtaqueEmV(this);

        estado = irCentro;
        irCentro.setProximoEstado(ataqueCeu);
        ataqueCeu.setProximoEstado(espera1);
        espera1.setProximoEstado(ataqueHorizontal);
        ataqueHorizontal.setProximoEstado(espera2);
        espera2.setProximoEstado(ataqueEmV);
        ataqueEmV.setProximoEstado(ataqueCeu);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (46 * BODY_PROPORTION);
        int scaledHeight = (int) (76 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss3_spreadsheet.png",
                46, 76, 0, 4, 4,
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

    // Ataque
    private class AtaqueCeu extends AtaqueEmUmaLinha {
        public AtaqueCeu(Boss boss) {
            super(boss, new Point2D.Double(0, 0), new Point2D.Double((MUNDO_LARGURA - 2) + 2, 0));

            // Padrões de ataque
            int quantidadeAtaques = 7;
            for(int i = 0; i < quantidadeAtaques; i ++){
                padroes.add(new PadraoAtaque(90, 9));
                padroes.add(new PadraoAtaque(90, 10));
            }

            this.intervaloAtaque = 30;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.OVAL_VERMELHO_ESCURO;
        }
    }


    private class AtaqueCeuComEspaco extends AtaqueEmUmaLinha {
        public AtaqueCeuComEspaco(Boss boss) {
            super(boss, new Point2D.Double(0, 0), new Point2D.Double((MUNDO_LARGURA - 2) + 2, 0));

            // Padrões de ataque
            int quantidadeAtaques = 12;
            for(int i = 0; i < quantidadeAtaques; i ++){
                padroes.add(new PadraoAtaque(90, 9));
                padroes.add(new PadraoAtaque(90, 10));
                padroes.add(new PadraoAtaque(90, 0));
            }

            this.intervaloAtaque = 30;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.OVAL_VERMELHO_ESCURO;
        }
    }

    private class AtaqueHorizontal extends MultiplosEstados {
        private final double VELOCIDADE_PROJETIL = 0.05;
        private final int INTERVALO_ATAQUE = 40;
        private final int QUANTIDADE_ATAQUES = 12;

        private class AtaqueEsquerda extends AtaqueEmUmaLinha {
            public AtaqueEsquerda(Boss boss) {
                super(boss, new Point2D.Double(0, 0), new Point2D.Double(0, MUNDO_ALTURA));
                
                for(int i = 0; i < QUANTIDADE_ATAQUES; i++){
                    padroes.add(new PadraoAtaque(0, 10));
                    padroes.add(new PadraoAtaque(0, 14));
                }

                this.intervaloAtaque = INTERVALO_ATAQUE;
                this.velocidadeProjetil = VELOCIDADE_PROJETIL;
                this.tipoProjetil = TipoProjetilInimigo.ESFERA_ROXA;
            }
        }

        private class AtaqueDireita extends AtaqueEmUmaLinha {
            public AtaqueDireita(Boss boss) {
                super(boss, new Point2D.Double((MUNDO_LARGURA - 2) + 2, 0), new Point2D.Double((MUNDO_LARGURA - 2) + 2, MUNDO_ALTURA));
                
                for(int i = 0; i < QUANTIDADE_ATAQUES; i++){
                    padroes.add(new PadraoAtaque(180, 11));
                    padroes.add(new PadraoAtaque(180, 15));
                }

                this.intervaloAtaque = INTERVALO_ATAQUE;
                this.velocidadeProjetil = VELOCIDADE_PROJETIL;
                this.tipoProjetil = TipoProjetilInimigo.ESFERA_ROXA;
            }
        }

        public AtaqueHorizontal(Boss boss) {
            super(boss);

            // Linhas de ataque
            estados.add(new AtaqueEsquerda(boss));
            estados.add(new AtaqueDireita(boss));
        }
    }

    private class AtaqueEmV extends MultiplosEstados {
        private final double VELOCIDADE_PROJETIL = 0.25;
        private final int INTERVALO_ATAQUE = 10;
        private final int INTERVALO_ESPACAMENTO = 60;
        private final int QUANTIDADE_ATAQUES = 20;
        private final double ESPACAMENTO_Y = 2;
        private final TipoProjetilInimigo TIPO_PROJETIL = TipoProjetilInimigo.ESFERA_ROXA;
        private final double OFFSET_ALTURA_TELA = 2;

        private class AtaqueEsquerda extends MultiplosEstados {
            
            public AtaqueEsquerda(Boss boss) {
                super(boss);
                for(int i = 0; i < QUANTIDADE_ATAQUES; i++){
                    AtaqueEmLequeNaPosicao ataqueEmLequeNaPosicao = new AtaqueEmLequeNaPosicao(boss);
                    ataqueEmLequeNaPosicao.posicaoAtaque.x = 0;
                    ataqueEmLequeNaPosicao.posicaoAtaque.y = OFFSET_ALTURA_TELA + i * ESPACAMENTO_Y;
                    ataqueEmLequeNaPosicao.intervaloAtaque = INTERVALO_ATAQUE + i * INTERVALO_ESPACAMENTO;
                    ataqueEmLequeNaPosicao.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataqueEmLequeNaPosicao.tipoProjetil = TIPO_PROJETIL;
                    ataqueEmLequeNaPosicao.padroes.add(ataqueEmLequeNaPosicao.new PadraoLeque(0, 0, 1));
                    
                    estados.add(ataqueEmLequeNaPosicao);
                }
            }
        }

        private class AtaqueDireita extends MultiplosEstados {
            
            public AtaqueDireita(Boss boss) {
                super(boss);
                for(int i = 0; i < QUANTIDADE_ATAQUES; i++){
                    AtaqueEmLequeNaPosicao ataqueEmLequeNaPosicao = new AtaqueEmLequeNaPosicao(boss);
                    ataqueEmLequeNaPosicao.posicaoAtaque.x = MUNDO_LARGURA;
                    ataqueEmLequeNaPosicao.posicaoAtaque.y = OFFSET_ALTURA_TELA +i * ESPACAMENTO_Y;
                    ataqueEmLequeNaPosicao.intervaloAtaque = INTERVALO_ATAQUE + i * INTERVALO_ESPACAMENTO;
                    ataqueEmLequeNaPosicao.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataqueEmLequeNaPosicao.tipoProjetil = TIPO_PROJETIL;
                    ataqueEmLequeNaPosicao.padroes.add(ataqueEmLequeNaPosicao.new PadraoLeque(180, 0, 1));
                    
                    estados.add(ataqueEmLequeNaPosicao);
                }
            }
        }

        public AtaqueEmV(Boss boss) {
            super(boss);

            // Linhas de ataque
            estados.add(new AtaqueEsquerda(boss));
            estados.add(new AtaqueDireita(boss));
            estados.add(new AtaqueCeuComEspaco(boss));
        }
    }

}
