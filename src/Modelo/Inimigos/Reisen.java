package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import Modelo.Projeteis.Projetil;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Reisen extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Reisen(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("boss5_spreadsheet", x, y, lootTable, vida);
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
    }

    private void setupEstados() {
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.2, 0.2));
        Estado irEsquerda = new IrParaEsquerda(this, new Point2D.Double(0.5, 0.2));
        Estado irDireita = new IrParaDireita(this, new Point2D.Double(0.5, 0.2));

        Estado ataqueParaBaixo = new AtaqueParaBaixo(this);
        Estado ataqueParaDireita = new AtaqueParaDireita(this);
        Estado ataqueParaEsquerda = new AtaqueParaEsquerda(this);

        estado = irCentro;
        irCentro.setProximoEstado(ataqueParaBaixo);

        ataqueParaBaixo.setProximoEstado(irEsquerda);
        irEsquerda.setProximoEstado(ataqueParaDireita);

        ataqueParaDireita.setProximoEstado(irDireita);
        irDireita.setProximoEstado(ataqueParaEsquerda);
        ataqueParaEsquerda.setProximoEstado(irCentro);
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
        estado.incrementarTempo(faseReferencia, 1);
        if (estado.getEstadoCompleto()) {
            estado = estado.getProximoEstado();
            if (estado == null) {
                estado = new IrParaOCentro(this, new Point2D.Double(0.2, 0.2)); // Para evitar eventual null pointer
            }

            estado.reset();
        }
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    private abstract class Estado {
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

    // Movimento
    private abstract class IrPara extends Estado {

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

            Point2D.Double proximo = movimento.proximoMovimento(boss.x, boss.y);
            Point2D.Double velocidade = movimento.getVelocidade();
            Point2D.Double alvo = movimento.getAlvo();
            if (Math.abs(proximo.x) < velocidade.x) {
                boss.x = alvo.x;
                proximo.x = 0;
            } else {
                boss.x += proximo.x;
            }
            if (Math.abs(proximo.y) < velocidade.y) {
                boss.y = alvo.y;
                proximo.y = 0;
            } else {
                boss.y += proximo.y;
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

    // Ataque
    private abstract class Ataque extends Estado {
        
        protected class PadraoAtaque {

            private int rotacaoInicial;
            private int amplitude;
            private int quantidadeAtaques;

            public PadraoAtaque(int rotacaoInicial, int amplitude, int quantidadeAtaques) {
                this.rotacaoInicial = rotacaoInicial;
                this.amplitude = amplitude;
                this.quantidadeAtaques = quantidadeAtaques;
            }

            // Set
            public void setRotacaoInicial(int rotacaoInicial) {
                this.rotacaoInicial = rotacaoInicial;
            }

            public void setAmplitude(int amplitude) {
                this.amplitude = amplitude;
            }

            public void setQuantidadeAtaques(int quantidadeAtaques) {
                this.quantidadeAtaques = quantidadeAtaques;
            }

            // Get
            public int getRotacaoInicial() {
                return this.rotacaoInicial;
            }

            public int getAmplitude() {
                return this.amplitude;
            }

            public int getQuantidadeAtaques() {
                return this.quantidadeAtaques;
            }

        }

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
            PadraoAtaque padrao = padroes.get(padraoAtual);
            atirarEmLeque(padrao.getRotacaoInicial(), padrao.getQuantidadeAtaques(), padrao.getAmplitude());
            this.padraoAtual++;
            contadorTempo = 0;
        }

        /**
         * Atira projéteis em leque a partir do centro do boss.
         *
         * @param anguloInicial Ângulo inicial em graus (0 = direita, 90 =
         * baixo, 180 = esquerda, 270 = cima)
         * @param quantidadeTiros Número de projéteis no leque
         * @param amplitude Amplitude total do leque em graus
         */
        private void atirarEmLeque(double anguloInicial, int quantidadeTiros, double amplitude) {
            if (faseReferencia == null) {
                return;
            }

            double espacamento = (quantidadeTiros > 1) ? amplitude / (quantidadeTiros - 1) : 0;

            for (int i = 0; i < quantidadeTiros; i++) {
                double angle = anguloInicial - (amplitude / 2.0) + (espacamento * i);

                Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(boss.x, boss.y, velocidadeProjetil, angle, TipoProjetil.INIMIGO, tipoProjetil);
                }
            }
        }
    }

    private class AtaqueParaBaixo extends Ataque {
        public AtaqueParaBaixo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;

            padroes.add(new PadraoAtaque(90, 140, 10));
            padroes.add(new PadraoAtaque(90, 80, 10));
        }
    }

    private class AtaqueParaDireita extends Ataque {
        public AtaqueParaDireita(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AZUL;

            padroes.add(new PadraoAtaque(50, 140, 10));
            padroes.add(new PadraoAtaque(50, 80, 10));
        }
    }

    private class AtaqueParaEsquerda extends Ataque {
        public AtaqueParaEsquerda(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AZUL;

            padroes.add(new PadraoAtaque(130, 140, 10));
            padroes.add(new PadraoAtaque(130, 80, 10));
        }
    }
}
