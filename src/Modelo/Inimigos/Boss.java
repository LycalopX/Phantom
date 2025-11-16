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
        protected abstract class PadraoAtaque {
            private int rotacao;
            private int quantidadeAtaques;

            public PadraoAtaque(int rotacao, int quantidadeAtaques) {
                this.rotacao = rotacao;
                this.quantidadeAtaques = quantidadeAtaques;
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
        protected TipoProjetilInimigo tipoProjetil;

        public Ataque(Boss boss) {
            super(boss);

            this.padroes = new ArrayList<>();
            intervaloAtaque = 60;
            velocidadeProjetil = 0.15;
            this.padraoAtual = 0;
        }

        @Override
        public void reset() {
            super.reset();
            this.padraoAtual = 0;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) {
                return;
            }

            contadorTempo += tempo;
            if (fase == null || contadorTempo < intervaloAtaque) {
                return;
            }

            if (padraoAtual >= padroes.size()) {
                estadoCompleto = true;
                return;
            }

            atirar(padroes.get(padraoAtual));
            this.padraoAtual++;
            contadorTempo -= intervaloAtaque;
        }

        /**
         * @brief Executa o padrão de ataque específico.
         * @param padrao O padrão de ataque a ser executado.
         */
        protected abstract void atirar(PadraoAtaque padrao);
    }

    protected abstract class AtaqueEmLeque extends Ataque {
        // Classes
        protected class PadraoLeque extends PadraoAtaque {

            private final int amplitude;

            public PadraoLeque(int rotacao, int amplitude, int quantidadeAtaques) {
                super(rotacao, quantidadeAtaques);
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

    protected abstract class AtaqueEmLequeNoJogador extends AtaqueEmLeque {
        public AtaqueEmLequeNoJogador(Boss boss) {
            super(boss);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(inimigo.getX(), inimigo.getY(), getAnguloEmDirecaoAoHeroi(), leque.getQuantidadeAtaques(), leque.getAmplitude());
            }
        }
    }

    protected abstract class AtaqueEmLequeNaPosicao extends AtaqueEmLeque {
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
