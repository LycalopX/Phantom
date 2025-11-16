package Modelo.Inimigos;

import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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

    protected abstract class Ataque extends Estado {
        // Classes
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

        // Variaveis
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

            if(repeticoes >= padroes.get(padraoAtual).repeticoes){
                repeticoes = 0;
                padraoAtual++;
            }
            else{
                repeticoes++;
                atirar(padroes.get(padraoAtual));
            }
            
            contadorTempo -= intervaloAtaque;
        }

        /**
         * @brief Executa o padrão de ataque específico.
         * @param padrao O padrão de ataque a ser executado.
         */
        protected abstract void atirar(PadraoAtaque padrao);
    }

    protected abstract class AtaqueEmColuna extends Ataque {
        // Classes
        protected class Coluna{
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

            // Get
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
         * Atira projéteis em linha distribuídos uniformemente entre duas posições.
         * 
         * @param posicaoInicial Posição inicial da linha (Point2D.Double)
         * @param posicaoFinal Posição final da linha (Point2D.Double)
         * @param quantidade Número de projéteis a serem disparados na linha
         * @param rotacao Ângulo de disparo em graus (0 = direita, 90 = baixo, 180 = esquerda, 270 = cima)
         */
        protected void atirarEmLinha(Point2D.Double posicaoInicial, Point2D.Double posicaoFinal, int quantidade, double rotacao) {
            if (faseReferencia == null) {
                return;
            }

            // Calcular o espaçamento entre cada projétil
            double espacamentoX = (quantidade > 1) ? (posicaoFinal.x - posicaoInicial.x) / (quantidade - 1) : 0;
            double espacamentoY = (quantidade > 1) ? (posicaoFinal.y - posicaoInicial.y) / (quantidade - 1) : 0;

            // Criar projéteis distribuídos uniformemente na linha
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

    protected abstract class AtaqueEmUmaLinha extends AtaqueEmColuna {

        protected final Coluna coluna;

        public AtaqueEmUmaLinha(Boss boss, Point2D.Double posicaoInicial, Point2D.Double posicaoFinal) {
            super(boss);
            this.coluna = new Coluna(posicaoInicial, posicaoFinal);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            atirarEmLinha(coluna.getPosicaoInicial(), coluna.getPosicaoFinal(), padrao.getQuantidadeAtaques(), padrao.getRotacao());
        }
    }

    protected class AtaqueEmUmaLinhaNoJogador extends AtaqueEmUmaLinha {

        public AtaqueEmUmaLinhaNoJogador(Boss boss, Point2D.Double posicaoInicial, Point2D.Double posicaoFinal) {
            super(boss, posicaoInicial, posicaoFinal);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            atirarEmLinhaNoJogador(coluna.getPosicaoInicial(), coluna.getPosicaoFinal(), padrao.getQuantidadeAtaques(), padrao.getRotacao());
        }

        protected void atirarEmLinhaNoJogador(Point2D.Double posicaoInicial, Point2D.Double posicaoFinal, int quantidade, double offset) {
            if (faseReferencia == null) {
                return;
            }

            // Calcular o espaçamento entre cada projétil
            double espacamentoX = (quantidade > 1) ? (posicaoFinal.x - posicaoInicial.x) / (quantidade - 1) : 0;
            double espacamentoY = (quantidade > 1) ? (posicaoFinal.y - posicaoInicial.y) / (quantidade - 1) : 0;

            // Criar projéteis distribuídos uniformemente na linha
            for (int i = 0; i < quantidade; i++) {
                double posX = posicaoInicial.x + (espacamentoX * i);
                double posY = posicaoInicial.y + (espacamentoY * i);

                Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(posX, posY, velocidadeProjetil, getAnguloEmDirecaoAoHeroi(posX, posY) + offset, TipoProjetil.INIMIGO, tipoProjetil);
                }
            }
        }
    }

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
            for (Estado estado : estados) {
                estado.reset();
            }
        }
    }

    protected abstract class AtaqueEmLeque extends Ataque {
        // Classes
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

            // Get
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
                atirarEmLeque(inimigo.getX(), inimigo.getY(), leque.getRotacao(), leque.getQuantidadeAtaques(), leque.getAmplitude());
            }
        }

        /**
         * Atira projéteis em leque
         *
         * @param posicaoX Posição X 
         * @param posicaoY Posição Y 
         * @param anguloInicial Ângulo inicial em graus (0 = direita, 90 =
         * baixo, 180 = esquerda, 270 = cima)
         * @param quantidadeTiros Número de projéteis no leque
         * @param amplitude Amplitude total do leque em graus
         */
        protected void atirarEmLeque(double posicaoX, double posicaoY, double anguloInicial, int quantidadeTiros, double amplitude) {
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

    protected class AtaqueEmLequeNoJogador extends AtaqueEmLeque {        
        public AtaqueEmLequeNoJogador(Boss boss) {
            super(boss);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(inimigo.getX(), inimigo.getY(), getAnguloEmDirecaoAoHeroi() + padrao.getRotacao(), leque.getQuantidadeAtaques(), leque.getAmplitude());
            }
        }
    }

    protected class AtaqueEmLequeNaPosicao extends AtaqueEmLeque {
        protected Point2D.Double posicaoAtaque;

        public AtaqueEmLequeNaPosicao(Boss boss) {
            super(boss);
            this.posicaoAtaque = new Point2D.Double(inimigo.getX(), inimigo.getY());
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(posicaoAtaque.x, posicaoAtaque.y, leque.getRotacao(), leque.getQuantidadeAtaques(), leque.getAmplitude());
            }
        }
    }    
}
