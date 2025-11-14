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

public class Nightbug extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Nightbug(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (35 * BODY_PROPORTION);
        int scaledHeight = (int) (60 * BODY_PROPORTION);

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss1_spreadsheet.png",
                35, 60, 13, 4, 4,
                true, // resize = true
                scaledWidth,
                scaledHeight,
                true // holdLastStrafingFrame
        );
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;
        
        this.estado = new IrParaOCentro(this);
        Estado ataque1 = new AtaqueParaBaixo1(this);
        this.estado.setProximoEstado(ataque1);
        ataque1.setProximoEstado(ataque1);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (35 * BODY_PROPORTION);
        int scaledHeight = (int) (60 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/boss1_spreadsheet.png",
                35, 60, 13, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true // holdLastStrafingFrame
        );

        // Estado
        this.estado = new IrParaOCentro(this); 
        Estado ataque1 = new AtaqueParaBaixo1(this);
        this.estado.setProximoEstado(ataque1);
        ataque1.setProximoEstado(ataque1);
    }

    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public boolean isStrafing() {
        return this.estado.getMovimento().proximoMovimento(this.x, this.y).x != 0 ||
               this.estado.getMovimento().proximoMovimento(this.x, this.y).y != 0;
    }

    @Override
    public void atualizar() {
        super.atualizar(); // Basic movement from Inimigo

        // Simple logic to switch between idle and moving for demonstration

        estado.incrementarTempo(faseReferencia, 1);
        if(estado.getEstadoCompleto()){
            estado = estado.getProximoEstado();
            if(estado == null){
                estado = new IrParaOCentro(this); // Para evitar eventual null pointer
            }
        }
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    private abstract class Estado {

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
        protected Boss boss;
        protected Movimento movimento;
        private Estado proximoEstado;

        protected int contadorTempo;
        protected boolean estadoCompleto;

        public Estado(Boss boss, Movimento movimento) {
            this.boss = boss;
            this.movimento = movimento;

            this.contadorTempo = 0;
        }

        public abstract void incrementarTempo(Fase fase, int tempo);

        // Set
        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public void setProximoEstado(Estado proximoEstado) {
            this.proximoEstado = proximoEstado;
        }

        // Get
        public Estado.Movimento getMovimento() {
            return this.movimento;
        }

        public Estado getProximoEstado() {
            return this.proximoEstado;
        }

        public boolean getEstadoCompleto() {
            return this.estadoCompleto;
        }
    }

    private class IrParaOCentro extends Estado {
        private Point2D.Double centro;

        public IrParaOCentro(Boss boss) {
            super(boss, new Estado.Movimento(
                0.3, 0.3,
                0, 0
            ));
            this.centro = new Point2D.Double(
                0.5 * (MUNDO_LARGURA - 2) + 2,
                0.2 * MUNDO_ALTURA
            );
            this.movimento.setAlvo(centro);
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if(estadoCompleto)
                return;

            Point2D.Double proximo = movimento.proximoMovimento(boss.x, boss.y);
            estadoCompleto = Movimento.isZero(proximo);
            boss.x += proximo.x;
            boss.y += proximo.y;
        }
    }

    private class AtaqueParaBaixo1 extends Estado {
        private class PadraoAtaque{
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

        private final int intervaloAtaque;
        private final double velocidadeProjetil;
        private final PadraoAtaque[] padroes;
        private int padraoAtual;

        public AtaqueParaBaixo1(Boss boss) {
            super(boss, new Estado.Movimento(
                0, 0,
                0.5 * (MUNDO_LARGURA - 2) + 2, MUNDO_ALTURA - 2
            ));
            
            this.intervaloAtaque = 120;
            this.velocidadeProjetil = 0.2;
            
            this.padroes = new PadraoAtaque[3];
            padroes[0] = new PadraoAtaque(90, 9, 11);
            padroes[1] = new PadraoAtaque(90, 6, 25);
            padroes[2] = new PadraoAtaque(40, 10, 10);

            this.padraoAtual = 0;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            contadorTempo += tempo;
            
            if (fase == null || contadorTempo < intervaloAtaque)
                return;

            atirarEmLeque(padroes[padraoAtual].getRotacaoInicial(), padroes[padraoAtual].getQuantidadeAtaques(), padroes[padraoAtual].getAmplitude());
            padraoAtual++;
            contadorTempo = 0;
        }

        /**
         * Atira projéteis em leque a partir do centro do boss.
         * @param anguloInicial Ângulo inicial em graus (0 = direita, 90 = baixo, 180 = esquerda, 270 = cima)
         * @param quantidadeTiros Número de projéteis no leque
         * @param amplitude Amplitude total do leque em graus
         */
        private void atirarEmLeque(double anguloInicial, int quantidadeTiros, double amplitude) {
            if (faseReferencia == null)
                return;

            // Calcular o espaçamento entre cada tiro
            double espacamento = (quantidadeTiros > 1) ? amplitude / (quantidadeTiros - 1) : 0;
            
            // Criar projéteis distribuídos uniformemente no leque
            for (int i = 0; i < quantidadeTiros; i++) {
                double angle = anguloInicial - (amplitude / 2.0) + (espacamento * i);
                
                Projetil p = faseReferencia.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(boss.x, boss.y, velocidadeProjetil, angle, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_AZUL);
                }
            }
        }
    }
}