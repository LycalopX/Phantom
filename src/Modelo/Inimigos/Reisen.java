package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @brief Implementação do chefe final, Reisen.
 * 
 *        Este chefe possui a máquina de estados mais complexa do jogo, com uma
 *        longa sequência de movimentos e ataques compostos que se encadeiam,
 *        criando uma batalha longa e com múltiplas fases.
 */
public class Reisen extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Reisen(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (41 * BODY_PROPORTION);
        int scaledHeight = (int) (82 * BODY_PROPORTION);

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss5_spreadsheet.png",
                41, 82, 0, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true);
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        setupEstados();
    }

    /**
     * @brief Configura a complexa máquina de estados sequencial do chefe.
     * 
     *        Define a longa cadeia de movimentos, ataques e pausas que compõem
     *        a batalha, com cada estado transicionando para o próximo até o ciclo
     *        se repetir.
     */
    private void setupEstados() {

        Estado irCentroInicio = new IrParaOCentro(this, new Point2D.Double(0.05, 0.05));

        Estado irCentro1 = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        Estado irCentro2 = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        Estado irCentro3 = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        Estado irCentro4 = new IrParaOCentro(this, new Point2D.Double(0.3, 0.3));
        Estado irEsquerda1 = new IrParaEsquerda(this, new Point2D.Double(0.4, 0.3));
        Estado irDireita1 = new IrParaDireita(this, new Point2D.Double(0.4, 0.3));
        Estado irEsquerda2 = new IrParaEsquerda(this, new Point2D.Double(0.4, 0.3));
        Estado irDireita2 = new IrParaDireita(this, new Point2D.Double(0.4, 0.3));

        Estado ataqueEspiral = new AtaqueEspiral(this);
        Estado ondaTeleguiada1 = new OndaTeleguiada(this);
        Estado chuvaVertical1 = new ChuvaVertical(this);
        Estado ataqueEspiralMovel = new AtaqueEspiralMovel(this);
        Estado ondaTeleguiada2 = new OndaTeleguiada(this);
        Estado chuvaVertical2 = new ChuvaVertical(this);
        Estado chuvaVertical3 = new ChuvaVertical(this);
        Estado chuvaDeOrbsNoTopo = new ChuvaDeOrbsNoTopo(this);

        Estado espera1 = new Esperar(this, 60);
        Estado espera2 = new Esperar(this, 60);
        Estado espera3 = new Esperar(this, 60);
        Estado espera4 = new Esperar(this, 60);
        Estado espera5 = new Esperar(this, 60);
        Estado espera6 = new Esperar(this, 60);

        estado = irCentroInicio;
        irCentroInicio.setProximoEstado(ataqueEspiral);

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

        espera4.setProximoEstado(irCentro3);
        irCentro3.setProximoEstado(chuvaVertical3);
        chuvaVertical3.setProximoEstado(espera5);

        espera5.setProximoEstado(irCentro4);
        irCentro4.setProximoEstado(chuvaDeOrbsNoTopo);
        chuvaDeOrbsNoTopo.setProximoEstado(espera6);

        espera6.setProximoEstado(irCentro1);
    }

    /**
     * @brief Restaura o animador e a máquina de estados após a desserialização.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (41 * BODY_PROPORTION);
        int scaledHeight = (int) (82 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss5_spreadsheet.png",
                41, 82, 0, 4, 4,
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

    private class IrParaOCentro extends IrPara {

        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (MUNDO_LARGURA - 2) + 2, 0.2 * MUNDO_ALTURA),
                    velocidade);
        }
    }

    private class IrParaEsquerda extends IrPara {

        public IrParaEsquerda(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.1 * (MUNDO_LARGURA - 2) + 2, 0.1 * MUNDO_ALTURA),
                    velocidade);
        }
    }

    private class IrParaDireita extends IrPara {

        public IrParaDireita(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.9 * (MUNDO_LARGURA - 2) + 2, 0.1 * MUNDO_ALTURA),
                    velocidade);
        }
    }

    /**
     * @brief Ataque composto que cria duas espirais de projéteis girando em
     *        direções opostas.
     */
    private class AtaqueEspiral extends MultiplosEstados {

        public AtaqueEspiral(Boss boss) {
            super(boss);

            estados.add(new EspiralRotacional(boss, 8, 0.18, TipoProjetilInimigo.ESFERA_ROXA, 24, 15, 12));
            estados.add(new EspiralRotacional(boss, 8, 0.18, TipoProjetilInimigo.ESFERA_AMARELA, -24, 15, 13));
        }
    }

    /**
     * @brief Ataque composto que cria espirais que alternam de direção.
     */
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
                estados.add(new EspiralRotacional(boss, 6, 0.1, TipoProjetilInimigo.OVAL_ROSA, 0, 1, 8));
            }
        }
    }

    /**
     * @brief Estado de ataque que gera uma única espiral de projéteis com rotação
     *        contínua.
     */
    private class EspiralRotacional extends AtaqueEmLeque {
        public EspiralRotacional(Boss boss, int intervaloAtaque, double velocidade, TipoProjetilInimigo tipoProjetil,
                int velocidadeRotacao, int ataques, int tiros) {
            super(boss);
            this.intervaloAtaque = intervaloAtaque;
            this.velocidadeProjetil = velocidade;
            this.tipoProjetil = tipoProjetil;

            for (int i = 0; i < ataques; i++) {
                padroes.add(new PadraoLeque(i * velocidadeRotacao, 360, tiros));
            }
        }
    }

    /**
     * @brief Ataque composto que dispara ondas de projéteis teleguiados das
     *        laterais da tela.
     */
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
                        new Point2D.Double(0, MUNDO_ALTURA));
                this.intervaloAtaque = INTERVALO_ATAQUE;
                this.velocidadeProjetil = VELOCIDADE_PROJETIL;
                this.tipoProjetil = TipoProjetilInimigo.ESFERA_GRANDE_VERMELHA_OCA;

                for (int i = 0; i < QUANTIDADE_ATAQUES; i++) {
                    int angleOffset = (int) (Math.random() * (2 * ANGLE_RANDOMNESS)) - ANGLE_RANDOMNESS;
                    this.padroes.add(new PadraoAtaque(angleOffset, 8));
                }
            }
        }

        private class AtaqueDireitaTeleguiado extends AtaqueEmUmaLinhaNoJogador {

            public AtaqueDireitaTeleguiado(Boss boss) {
                super(boss,
                        new Point2D.Double(MUNDO_LARGURA, 0),
                        new Point2D.Double(MUNDO_LARGURA, MUNDO_ALTURA));
                this.intervaloAtaque = INTERVALO_ATAQUE;
                this.velocidadeProjetil = VELOCIDADE_PROJETIL;
                this.tipoProjetil = TipoProjetilInimigo.ESFERA_GRANDE_AMARELA_OCA;

                for (int i = 0; i < QUANTIDADE_ATAQUES; i++) {
                    int angleOffset = (int) (Math.random() * (2 * ANGLE_RANDOMNESS)) - ANGLE_RANDOMNESS;
                    this.padroes.add(new PadraoAtaque(angleOffset, 8));
                }
            }
        }
    }

    /**
     * @brief Ataque composto que cria uma "chuva" de projéteis em leque a partir do
     *        topo.
     */
    private class ChuvaVertical extends MultiplosEstados {

        private final double VELOCIDADE_PROJETIL = 0.15;
        private final int INTERVALO_ENTRE_ATAQUES = 15;

        public ChuvaVertical(Boss boss) {
            super(boss);

            repeticoes = 3;
            estados.add(new ChuvaEsquerda(boss));
            estados.add(new ChuvaDireita(boss));
        }

        private class ChuvaEsquerda extends MultiplosEstados {
            public ChuvaEsquerda(Boss boss) {
                super(boss);

                int quantidadeAtaques = 5;
                double espacamentoX = MUNDO_LARGURA / (quantidadeAtaques + 1);

                for (int i = 0; i < quantidadeAtaques; i++) {
                    AtaqueEmLequeNaPosicao ataque = new AtaqueEmLequeNaPosicao(boss);
                    ataque.posicaoAtaque = new Point2D.Double(espacamentoX * (i + 1), 0);
                    ataque.intervaloAtaque = i * INTERVALO_ENTRE_ATAQUES;
                    ataque.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataque.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;
                    ataque.padroes.add(ataque.new PadraoLeque(90, 30, 5));
                    estados.add(ataque);
                }
            }
        }

        private class ChuvaDireita extends MultiplosEstados {
            public ChuvaDireita(Boss boss) {
                super(boss);

                int quantidadeAtaques = 5;
                double espacamentoX = MUNDO_LARGURA / (quantidadeAtaques + 1);

                for (int i = 0; i < quantidadeAtaques; i++) {
                    AtaqueEmLequeNaPosicao ataque = new AtaqueEmLequeNaPosicao(boss);
                    ataque.posicaoAtaque = new Point2D.Double(MUNDO_LARGURA - espacamentoX * (i + 1), 0);
                    ataque.intervaloAtaque = i * INTERVALO_ENTRE_ATAQUES;
                    ataque.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataque.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;
                    ataque.padroes.add(ataque.new PadraoLeque(90, 30, 5));
                    estados.add(ataque);
                }
            }
        }
    }

    /**
     * @brief Ataque final que cria três "orbs" no topo da tela, cada uma disparando
     *        padrões de projéteis rotativos, além de ataques laterais para
     *        limitar o espaço do jogador.
     */
    private class ChuvaDeOrbsNoTopo extends MultiplosEstados {

        private final double VELOCIDADE_PROJETIL = 0.05;
        private final int INTERVALO_ENTRE_ATAQUES = 20;
        private final int QUANTIDADE_ONDAS = 20;
        private final int ANGULO_INICIAL = 20;
        private final int ANGULO_FINAL = 160;
        private final int PROJETEIS_POR_ARCO = 3;

        public ChuvaDeOrbsNoTopo(Boss boss) {
            super(boss);

            repeticoes = 2;

            double orbEsquerda = 0.2 * MUNDO_LARGURA;
            double orbCentro = 0.5 * MUNDO_LARGURA;
            double orbDireita = 0.8 * MUNDO_LARGURA;
            double orbY = 0;

            estados.add(new ChuvaDeUmaOrbAntiHorario(boss, orbEsquerda, orbY, TipoProjetilInimigo.ESFERA_ROXA));

            estados.add(new ChuvaDeUmaOrbDuasDirecoes(boss, orbCentro, orbY, TipoProjetilInimigo.ESFERA_AMARELA));

            estados.add(new ChuvaDeUmaOrbHorario(boss, orbDireita, orbY, TipoProjetilInimigo.ESFERA_VERDE));

            estados.add(new ArcoLateralEsquerda(boss));
            estados.add(new ArcoLateralDireita(boss));
        }

        private class ChuvaDeUmaOrbHorario extends MultiplosEstados {
            public ChuvaDeUmaOrbHorario(Boss boss, double posX, double posY, TipoProjetilInimigo tipoProjetil) {
                super(boss);

                double incrementoAngulo = (ANGULO_FINAL - ANGULO_INICIAL) / (double) (QUANTIDADE_ONDAS - 1);

                for (int i = 0; i < QUANTIDADE_ONDAS; i++) {
                    AtaqueEmLequeNaPosicao ataque = new AtaqueEmLequeNaPosicao(boss);
                    ataque.posicaoAtaque = new Point2D.Double(posX, posY);
                    ataque.intervaloAtaque = i * INTERVALO_ENTRE_ATAQUES;
                    ataque.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataque.tipoProjetil = tipoProjetil;

                    int anguloAtual = (int) (ANGULO_INICIAL + i * incrementoAngulo);
                    ataque.padroes.add(ataque.new PadraoLeque(anguloAtual, 40, PROJETEIS_POR_ARCO));
                    estados.add(ataque);
                }
            }
        }

        private class ChuvaDeUmaOrbAntiHorario extends MultiplosEstados {
            public ChuvaDeUmaOrbAntiHorario(Boss boss, double posX, double posY, TipoProjetilInimigo tipoProjetil) {
                super(boss);

                double incrementoAngulo = (ANGULO_FINAL - ANGULO_INICIAL) / (double) (QUANTIDADE_ONDAS - 1);

                for (int i = 0; i < QUANTIDADE_ONDAS; i++) {
                    AtaqueEmLequeNaPosicao ataque = new AtaqueEmLequeNaPosicao(boss);
                    ataque.posicaoAtaque = new Point2D.Double(posX, posY);
                    ataque.intervaloAtaque = i * INTERVALO_ENTRE_ATAQUES;
                    ataque.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataque.tipoProjetil = tipoProjetil;

                    int anguloAtual = (int) (ANGULO_FINAL - i * incrementoAngulo);
                    ataque.padroes.add(ataque.new PadraoLeque(anguloAtual, 40, PROJETEIS_POR_ARCO));
                    estados.add(ataque);
                }
            }
        }

        private class ChuvaDeUmaOrbDuasDirecoes extends MultiplosEstados {
            public ChuvaDeUmaOrbDuasDirecoes(Boss boss, double posX, double posY, TipoProjetilInimigo tipoProjetil) {
                super(boss);

                double incrementoAngulo = (ANGULO_FINAL - ANGULO_INICIAL) / (double) (QUANTIDADE_ONDAS - 1);

                for (int i = 0; i < QUANTIDADE_ONDAS; i++) {

                    AtaqueEmLequeNaPosicao ataqueHorario = new AtaqueEmLequeNaPosicao(boss);
                    ataqueHorario.posicaoAtaque = new Point2D.Double(posX, posY);
                    ataqueHorario.intervaloAtaque = i * INTERVALO_ENTRE_ATAQUES;
                    ataqueHorario.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataqueHorario.tipoProjetil = tipoProjetil;
                    int anguloHorario = (int) (ANGULO_INICIAL + i * incrementoAngulo);
                    ataqueHorario.padroes.add(ataqueHorario.new PadraoLeque(anguloHorario, 40, PROJETEIS_POR_ARCO));
                    estados.add(ataqueHorario);

                    AtaqueEmLequeNaPosicao ataqueAntiHorario = new AtaqueEmLequeNaPosicao(boss);
                    ataqueAntiHorario.posicaoAtaque = new Point2D.Double(posX, posY);
                    ataqueAntiHorario.intervaloAtaque = i * INTERVALO_ENTRE_ATAQUES;
                    ataqueAntiHorario.velocidadeProjetil = VELOCIDADE_PROJETIL;
                    ataqueAntiHorario.tipoProjetil = tipoProjetil;
                    int anguloAntiHorario = (int) (ANGULO_FINAL - i * incrementoAngulo);
                    ataqueAntiHorario.padroes
                            .add(ataqueAntiHorario.new PadraoLeque(anguloAntiHorario, 40, PROJETEIS_POR_ARCO));
                    estados.add(ataqueAntiHorario);
                }
            }
        }

        private class ArcoLateralEsquerda extends AtaqueEmLequeNaPosicao {
            public ArcoLateralEsquerda(Boss boss) {
                super(boss);

                double posX = 0;
                double posY = MUNDO_ALTURA * 0.2;

                posicaoAtaque = new Point2D.Double(posX, posY);
                intervaloAtaque = INTERVALO_ENTRE_ATAQUES * 3;
                velocidadeProjetil = VELOCIDADE_PROJETIL * 2;
                tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;

                padroes.add(new PadraoLeque(85, 10, 5, (int) (((double) QUANTIDADE_ONDAS) / 1.5)));
            }
        }

        private class ArcoLateralDireita extends AtaqueEmLequeNaPosicao {
            public ArcoLateralDireita(Boss boss) {
                super(boss);

                double posX = MUNDO_LARGURA;
                double posY = MUNDO_ALTURA * 0.2;

                posicaoAtaque = new Point2D.Double(posX, posY);
                intervaloAtaque = INTERVALO_ENTRE_ATAQUES * 3;
                velocidadeProjetil = VELOCIDADE_PROJETIL * 2;
                tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;

                padroes.add(new PadraoLeque(95, 10, 5, (int) (((double) QUANTIDADE_ONDAS) / 1.5)));
            }
        }
    }

}