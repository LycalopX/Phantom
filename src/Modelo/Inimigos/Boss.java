package Modelo.Inimigos;

import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @brief Classe abstrata base para todos os chefes do jogo.
 * 
 *        Fornece uma estrutura para a criação de múltiplos estados de ataque
 *        e comportamentos complexos, estendendo a funcionalidade de `Inimigo`.
 */
public abstract class Boss extends Inimigo {

    boolean isBombed = false;

    public Boss(String sNomeImagePNG, double x, double y, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y, lootTable, vida);
    }

    public void setBombed(boolean bombed) {
        isBombed = bombed;
    }

    public boolean isBombed() {
        return this.isBombed;
    }

    /**
     * @brief Classe base abstrata para um estado de ataque de um chefe.
     * 
     *        Define a estrutura para um padrão de ataque, incluindo a temporização,
     *        repetições e a lógica de disparo.
     */
    protected abstract class Ataque extends Estado {

        /**
         * @brief Define os parâmetros de um padrão de ataque específico.
         */
        protected class PadraoAtaque {
            private final int rotacao;
            private final int quantidadeAtaques;
            private final int repeticoes;

            public PadraoAtaque(int rotacao, int quantidadeAtaques, int repeticoes) {
                this.rotacao = rotacao;
                this.quantidadeAtaques = quantidadeAtaques;
                this.repeticoes = repeticoes;
            }

            public PadraoAtaque(int rotacao, int quantidadeAtaques) {
                this(rotacao, quantidadeAtaques, 1);
            }

            public int getRotacao() {
                return this.rotacao;
            }

            public int getQuantidadeAtaques() {
                return this.quantidadeAtaques;
            }
        }

        protected int intervaloAtaque;
        protected double velocidadeProjetil;
        protected final ArrayList<PadraoAtaque> padroes;
        protected int padraoAtual;
        protected int repeticoes;
        protected TipoProjetilInimigo tipoProjetil;

        public Ataque(Boss boss) {
            super(boss);

            this.padroes = new ArrayList<>();
            intervaloAtaque = 60;
            velocidadeProjetil = 0.15;
            this.padraoAtual = 0;
            this.repeticoes = 0;
        }

        @Override
        public void reset() {
            super.reset();
            this.padraoAtual = 0;
            this.repeticoes = 0;
        }

        /**
         * @brief Avança a lógica do ataque com base no tempo.
         * 
         *        A cada `intervaloAtaque` frames, dispara o padrão de ataque atual.
         *        Avança para o próximo padrão após completar as repetições.
         */
        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) {
                return;
            }

            contadorTempo += tempo;
            if (padraoAtual >= padroes.size()) {
                estadoCompleto = true;
                return;
            }

            if (fase == null || contadorTempo < intervaloAtaque) {
                return;
            }

            if (repeticoes >= padroes.get(padraoAtual).repeticoes) {
                repeticoes = 0;
                padraoAtual++;
            } else {
                repeticoes++;
                atirar(padroes.get(padraoAtual));
            }

            contadorTempo -= intervaloAtaque;
        }

        protected abstract void atirar(PadraoAtaque padrao);
    }

    /**
     * @brief Subclasse de `Ataque` para ataques que ocorrem em colunas ou linhas.
     */
    protected abstract class AtaqueEmColuna extends Ataque {

        protected class Coluna {
            protected final Point2D.Double posicaoInicial;
            protected final Point2D.Double posicaoFinal;

            public Coluna(Point2D.Double posicaoInicial, Point2D.Double posicaoFinal) {
                this.posicaoInicial = posicaoInicial;
                this.posicaoFinal = posicaoFinal;
            }

            public Coluna() {
                this.posicaoInicial = new Point2D.Double(0, 0);
                this.posicaoFinal = new Point2D.Double(0, 0);
            }

            public Point2D.Double getPosicaoInicial() {
                return this.posicaoInicial;
            }

            public Point2D.Double getPosicaoFinal() {
                return this.posicaoFinal;
            }
        }

        public AtaqueEmColuna(Boss boss) {
            super(boss);
        }

        /**
         * @brief Atira projéteis em linha, distribuídos uniformemente entre dois
         *        pontos.
         * 
         * @param posicaoInicial Posição inicial da linha.
         * @param posicaoFinal   Posição final da linha.
         * @param quantidade     Número de projéteis a serem disparados.
         * @param rotacao        Ângulo de disparo em graus.
         */
        protected void atirarEmLinha(Point2D.Double posicaoInicial, Point2D.Double posicaoFinal, int quantidade,
                double rotacao) {
            if (faseReferencia == null) {
                return;
            }

            double espacamentoX = (quantidade > 1) ? (posicaoFinal.x - posicaoInicial.x) / (quantidade - 1) : 0;
            double espacamentoY = (quantidade > 1) ? (posicaoFinal.y - posicaoInicial.y) / (quantidade - 1) : 0;

            for (int i = 0; i < quantidade; i++) {
                double posX = posicaoInicial.x + (espacamentoX * i);
                double posY = posicaoInicial.y + (espacamentoY * i);

                Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(posX, posY, velocidadeProjetil, rotacao, TipoProjetil.INIMIGO, tipoProjetil);
                }
            }
        }
    }

    /**
     * @brief Especialização de `AtaqueEmColuna` para disparar de uma única linha
     *        pré-definida.
     */
    protected abstract class AtaqueEmUmaLinha extends AtaqueEmColuna {

        protected final Coluna coluna;

        public AtaqueEmUmaLinha(Boss boss, Point2D.Double posicaoInicial, Point2D.Double posicaoFinal) {
            super(boss);
            this.coluna = new Coluna(posicaoInicial, posicaoFinal);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            atirarEmLinha(coluna.getPosicaoInicial(), coluna.getPosicaoFinal(), padrao.getQuantidadeAtaques(),
                    padrao.getRotacao());
        }
    }

    /**
     * @brief Variação de `AtaqueEmUmaLinha` que mira na direção do jogador.
     */
    protected class AtaqueEmUmaLinhaNoJogador extends AtaqueEmUmaLinha {

        public AtaqueEmUmaLinhaNoJogador(Boss boss, Point2D.Double posicaoInicial, Point2D.Double posicaoFinal) {
            super(boss, posicaoInicial, posicaoFinal);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            atirarEmLinhaNoJogador(coluna.getPosicaoInicial(), coluna.getPosicaoFinal(), padrao.getQuantidadeAtaques(),
                    padrao.getRotacao());
        }

        /**
         * @brief Atira projéteis em linha, com cada projétil mirando no jogador com um
         *        offset.
         */
        protected void atirarEmLinhaNoJogador(Point2D.Double posicaoInicial, Point2D.Double posicaoFinal,
                int quantidade, double offset) {
            if (faseReferencia == null) {
                return;
            }

            double espacamentoX = (quantidade > 1) ? (posicaoFinal.x - posicaoInicial.x) / (quantidade - 1) : 0;
            double espacamentoY = (quantidade > 1) ? (posicaoFinal.y - posicaoInicial.y) / (quantidade - 1) : 0;

            for (int i = 0; i < quantidade; i++) {
                double posX = posicaoInicial.x + (espacamentoX * i);
                double posY = posicaoInicial.y + (espacamentoY * i);

                Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(posX, posY, velocidadeProjetil, getAnguloEmDirecaoAoHeroi(posX, posY) + offset,
                            TipoProjetil.INIMIGO, tipoProjetil);
                }
            }
        }
    }

    /**
     * @brief Contêiner que executa múltiplos estados de ataque em sequência ou
     *        paralelo.
     */
    protected abstract class MultiplosEstados extends Estado {

        protected final ArrayList<Estado> estados;
        protected int repeticoes;
        protected int contadorRepeticoes = 0;

        public MultiplosEstados(Boss boss) {
            super(boss);
            this.estados = new ArrayList<>();
            this.repeticoes = 1;
        }

        public MultiplosEstados(Boss boss, int repeticoes) {
            this(boss);
            this.repeticoes = repeticoes;
        }

        /**
         * @brief Incrementa o tempo para todos os estados contidos.
         * 
         *        O estado geral é considerado completo apenas quando todos os
         *        sub-estados
         *        terminam e o número de repetições é atingido.
         */
        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) {
                return;
            }
            contadorTempo += tempo;
            boolean todasLinhasCompletas = true;
            for (Estado estado : estados) {
                estado.incrementarTempo(fase, tempo);
                if (!estado.getEstadoCompleto()) {
                    todasLinhasCompletas = false;
                }
            }
            if (todasLinhasCompletas) {
                contadorRepeticoes++;
                if (contadorRepeticoes >= repeticoes) {
                    estadoCompleto = true;
                } else {
                    for (Estado estado : estados) {
                        estado.reset();
                    }
                }
            }
        }

        @Override
        public void reset() {
            super.reset();
            contadorRepeticoes = 0;
            for (Estado estado : estados) {
                estado.reset();
            }
        }
    }

    /**
     * @brief Subclasse de `Ataque` para ataques em forma de leque.
     */
    protected abstract class AtaqueEmLeque extends Ataque {

        public class PadraoLeque extends PadraoAtaque {

            private final int amplitude;

            public PadraoLeque(int rotacao, int amplitude, int quantidadeAtaques) {
                super(rotacao, quantidadeAtaques);
                this.amplitude = amplitude;
            }

            public PadraoLeque(int rotacao, int amplitude, int quantidadeAtaques, int repeticoes) {
                super(rotacao, quantidadeAtaques, repeticoes);
                this.amplitude = amplitude;
            }

            public int getAmplitude() {
                return this.amplitude;
            }
        }

        public AtaqueEmLeque(Boss boss) {
            super(boss);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(inimigo.getX(), inimigo.getY(), leque.getRotacao(), leque.getQuantidadeAtaques(),
                        leque.getAmplitude());
            }
        }

        /**
         * @brief Atira projéteis em um padrão de leque.
         *
         * @param posicaoX        Posição X de origem.
         * @param posicaoY        Posição Y de origem.
         * @param anguloInicial   Ângulo central do leque em graus.
         * @param quantidadeTiros Número de projéteis no leque.
         * @param amplitude       Amplitude total do leque em graus.
         */
        protected void atirarEmLeque(double posicaoX, double posicaoY, double anguloInicial, int quantidadeTiros,
                double amplitude) {
            if (faseReferencia == null) {
                return;
            }

            double espacamento = (quantidadeTiros > 1) ? amplitude / (quantidadeTiros - 1) : 0;

            for (int i = 0; i < quantidadeTiros; i++) {
                double angle = anguloInicial - (amplitude / 2.0) + (espacamento * i);

                Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(posicaoX, posicaoY, velocidadeProjetil, angle, TipoProjetil.INIMIGO, tipoProjetil);
                }
            }
        }
    }

    /**
     * @brief Variação de `AtaqueEmLeque` que mira no jogador.
     */
    protected class AtaqueEmLequeNoJogador extends AtaqueEmLeque {
        public AtaqueEmLequeNoJogador(Boss boss) {
            super(boss);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(inimigo.getX(), inimigo.getY(), getAnguloEmDirecaoAoHeroi() + padrao.getRotacao(),
                        leque.getQuantidadeAtaques(), leque.getAmplitude());
            }
        }
    }

    /**
     * @brief Variação de `AtaqueEmLeque` que dispara de uma posição fixa.
     */
    protected class AtaqueEmLequeNaPosicao extends AtaqueEmLeque {
        protected Point2D.Double posicaoAtaque;

        public AtaqueEmLequeNaPosicao(Boss boss) {
            super(boss);
            this.posicaoAtaque = new Point2D.Double(inimigo.getX(), inimigo.getY());
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(posicaoAtaque.x, posicaoAtaque.y, leque.getRotacao(), leque.getQuantidadeAtaques(),
                        leque.getAmplitude());
            }
        }
    }
}
