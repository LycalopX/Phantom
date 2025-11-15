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

    // Estados
    protected Estado processarEstado(Estado estado, int tempo) {
        if (estado != null) {
            estado.incrementarTempo(faseReferencia, tempo);
            if (estado.getEstadoCompleto()) {
                estado = estado.getProximoEstado();
                if (estado == null) {
                    estado = new EsperarIndefinidamente(this); // Para evitar eventual null pointer
                }

                estado.reset();
            }
        }
        return estado;
    }

    protected abstract class Estado {

        // Variaveis
        protected Boss boss;
        private Estado proximoEstado;

        protected int contadorTempo;
        protected boolean estadoCompleto;

        public Estado(Boss boss) {
            this.boss = boss;
            this.contadorTempo = 0;
        }

        public abstract void incrementarTempo(Fase fase, int tempo);

        public void reset() {
            this.contadorTempo = 0;
            this.estadoCompleto = false;
        }

        // Set
        public void setProximoEstado(Estado proximoEstado) {
            this.proximoEstado = proximoEstado;
        }

        // Get
        public Estado getProximoEstado() {
            return this.proximoEstado;
        }

        public boolean getEstadoCompleto() {
            return this.estadoCompleto;
        }
    }

    protected class Esperar extends Estado {
        protected int duracao;
        public Esperar(Boss boss) {
            super(boss);
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) {
                return;
            }

            contadorTempo += tempo;
            if (contadorTempo >= duracao) {
                estadoCompleto = true;
            }
        }
    }

    protected class EsperarIndefinidamente extends Estado {
        public EsperarIndefinidamente(Boss boss) {
            super(boss);
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            // Não faz nada, espera indefinidamente
        }
    }

    protected abstract class IrPara extends Estado {
        // Classes
        public static class Movimento {

            private Point2D.Double velocidade;
            private Point2D.Double alvo;

            public Movimento(Point2D.Double velocidade, Point2D.Double alvo) {
                this.velocidade = velocidade;
                this.alvo = alvo;
            }

            public Movimento(double velocidadeX, double velocidadeY, double alvoX, double alvoY) {
                this.velocidade = new Point2D.Double(velocidadeX, velocidadeY);
                this.alvo = new Point2D.Double(alvoX, alvoY);
            }

            public Point2D.Double proximoMovimento(Point2D.Double posicaoAtual) {
                double movimentoX = Math.clamp(alvo.x - posicaoAtual.x, -velocidade.x, velocidade.x);
                double movimentoY = Math.clamp(alvo.y - posicaoAtual.y, -velocidade.y, velocidade.y);
                return new Point2D.Double(movimentoX, movimentoY);
            }

            public Point2D.Double proximoMovimento(double posicaoX, double posicaoY) {
                double movimentoX = Math.clamp(alvo.x - posicaoX, -velocidade.x, velocidade.x);
                double movimentoY = Math.clamp(alvo.y - posicaoY, -velocidade.y, velocidade.y);
                return new Point2D.Double(movimentoX, movimentoY);
            }

            public static boolean isZero(Point2D.Double movimento) {
                return movimento.x == 0 && movimento.y == 0;
            }

            // Set
            public void setVelocidade(Point2D.Double velocidade) {
                this.velocidade = velocidade;
            }

            public void setVelocidade(double x, double y) {
                this.velocidade = new Point2D.Double(x, y);
            }

            public void setAlvo(Point2D.Double alvo) {
                this.alvo = alvo;
            }

            public void setAlvo(double x, double y) {
                this.alvo = new Point2D.Double(x, y);
            }

            // Get
            public Point2D.Double getVelocidade() {
                return this.velocidade;
            }

            public Point2D.Double getAlvo() {
                return this.alvo;
            }
        }

        // Variaveis
        private Movimento movimento;

        public IrPara(Boss boss, double alvoX, double alvoY, double velocidadeX, double velocidadeY) {
            super(boss);
            movimento = new Movimento(
                    velocidadeX, velocidadeY,
                    alvoX, alvoY
            );
        }

        public IrPara(Boss boss, Point2D.Double alvo, Point2D.Double velocidade) {
            super(boss);
            movimento = new Movimento(
                    velocidade,
                    alvo
            );
        }

        @Override
        public void reset() {
            super.reset();
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) {
                return;
            }

            Point2D.Double proximo = movimento.proximoMovimento(boss.getX(), boss.getY());
            Point2D.Double velocidade = movimento.getVelocidade();
            Point2D.Double alvo = movimento.getAlvo();
            if (Math.abs(proximo.x) < velocidade.x) {
                boss.setPosition(alvo.x, boss.getY());
                proximo.x = 0;
            } else {
                boss.setPosition(boss.getX() + proximo.x, boss.getY());
            }
            if (Math.abs(proximo.y) < velocidade.y) {
                boss.setPosition(boss.getX(), alvo.y);
                proximo.y = 0;
            } else {
                boss.setPosition(boss.getX(), boss.getY() + proximo.y);
            }
            estadoCompleto = Movimento.isZero(proximo);
        }

        // Set
        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        // Get
        public Movimento getMovimento() {
            return this.movimento;
        }
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

            // Set
            public void setRotacao(int rotacao) {
                this.rotacao = rotacao;
            }

            public void setQuantidadeAtaques(int quantidadeAtaques) {
                this.quantidadeAtaques = quantidadeAtaques;
            }

            // Get
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

            private int amplitude;

            public PadraoLeque(int rotacao, int amplitude, int quantidadeAtaques) {
                super(rotacao, quantidadeAtaques);
                this.amplitude = amplitude;
            }

            // Set
            public void setAmplitude(int amplitude) {
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
                atirarEmLeque(boss.x, boss.y, leque.getRotacao(), leque.getQuantidadeAtaques(), leque.getAmplitude());
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

    protected abstract class AtaqueEmLequeNaPosicao extends AtaqueEmLeque {
        protected Point2D.Double posicaoAtaque;

        public AtaqueEmLequeNaPosicao(Boss boss) {
            super(boss);
            this.posicaoAtaque = new Point2D.Double(boss.x, boss.y);
        }

        @Override
        protected void atirar(PadraoAtaque padrao) {
            if (padrao instanceof PadraoLeque leque) {
                atirarEmLeque(posicaoAtaque.x, posicaoAtaque.y, leque.getRotacao(), leque.getQuantidadeAtaques(), leque.getAmplitude());
            }
        }
    }
}
