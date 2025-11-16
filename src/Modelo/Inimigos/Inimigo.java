package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Personagem;
import Modelo.RenderLayer;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * @brief Classe abstrata base para todos os inimigos do jogo.
 */
public abstract class Inimigo extends Personagem {

    private static final long serialVersionUID = 1L;

    public double vida;
    protected transient Fase faseReferencia;
    protected double initialX;

    /**
     * @brief Construtor automático que calcula o tamanho do inimigo com base na imagem.
     */
    public Inimigo(String sNomeImagePNG, double x, double y, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y);
        this.bMortal = true;
        this.lootTable = lootTable;
        this.vida = vida;
        this.initialX = x;
    }

    /**
     * @brief Construtor manual que permite definir um tamanho específico para o inimigo.
     */
    public Inimigo(String sNomeImagePNG, double x, double y, int tamanho, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y, tamanho, tamanho);
        this.bMortal = true;
        this.lootTable = lootTable;
        this.vida = vida;
        this.initialX = x;
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.ENEMY_LAYER;
    }

    /**
     * @brief Atualiza a lógica de movimento padrão do inimigo.
     */
    @Override
    public void atualizar() {
        this.y += 0.02;
    }
    
    /**
     * @brief Inicializa a referência da fase para o inimigo, usado após a desserialização.
     */
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    public void setInitialX(double initialX) {
        this.initialX = initialX;
    }

    public abstract boolean isStrafing();

    /**
     * @brief Desenha o sprite do inimigo na tela.
     */
    @Override
    public void autoDesenho(Graphics g) {
        int telaX = (int) Math.round(x * CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * CELL_SIDE) - (this.altura / 2);
        g.drawImage(iImage.getImage(), telaX, telaY, largura, altura, null);
        super.autoDesenho(g);
    }

    /**
     * @brief Retorna a vida atual do inimigo.
     */
    public double getVida() {
        return this.vida;
    }

    /**
     * @brief Aplica dano ao inimigo e o desativa se a vida chegar a zero.
     */
    public void takeDamage(double damage) {
        this.vida -= damage;
        Auxiliar.SoundManager.getInstance().playSfx("se_damage01", 0.5f);
        if (this.vida <= 0) {
            this.vida = 0;
            deactivate();
            Auxiliar.SoundManager.getInstance().playSfx("se_enep00", 0.5f);
        }
    }

    /**
     * @brief Retorna o ângulo em graus do inimigo em direção ao herói.
     * @return O ângulo em graus (0 = direita, 90 = baixo, 180 = esquerda, 270 = cima).
     */
    public double getAnguloEmDirecaoAoHeroi() {
        if (faseReferencia == null || faseReferencia.getHero() == null) {
            return 0; // Ou um valor padrão, se o herói não estiver disponível
        }
        Personagem hero = faseReferencia.getHero();
        double dx = hero.getX() - this.x;
        double dy = hero.getY() - this.y;
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    // --- Sistema de Estados ---

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

    protected abstract class Estado implements Serializable {
        protected Inimigo inimigo;
        private Estado proximoEstado;
        protected int contadorTempo;
        protected boolean estadoCompleto;

        public Estado(Inimigo inimigo) {
            this.inimigo = inimigo;
            this.contadorTempo = 0;
        }

        public abstract void incrementarTempo(Fase fase, int tempo);

        public void reset() {
            this.contadorTempo = 0;
            this.estadoCompleto = false;
        }

        public void setProximoEstado(Estado proximoEstado) {
            this.proximoEstado = proximoEstado;
        }

        public Estado getProximoEstado() {
            return this.proximoEstado;
        }

        public boolean getEstadoCompleto() {
            return this.estadoCompleto;
        }
    }

    protected class Esperar extends Estado {
        protected int duracao;
        public Esperar(Inimigo inimigo, int duracao) {
            super(inimigo);
            this.duracao = duracao;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) return;
            contadorTempo += tempo;
            if (contadorTempo >= duracao) {
                estadoCompleto = true;
            }
        }
    }

    protected class EsperarIndefinidamente extends Estado {
        public EsperarIndefinidamente(Inimigo inimigo) {
            super(inimigo);
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            // Não faz nada, espera indefinidamente
        }
    }

    protected class IrPara extends Estado {
        public static class Movimento implements Serializable {
            private final Point2D.Double velocidade;
            private Point2D.Double alvo;

            public Movimento(double velocidadeX, double velocidadeY, double alvoX, double alvoY) {
                this.velocidade = new Point2D.Double(velocidadeX, velocidadeY);
                this.alvo = new Point2D.Double(alvoX, alvoY);
            }

            public Movimento(Point2D.Double velocidade, Point2D.Double alvo) {
                this.velocidade = velocidade;
                this.alvo = alvo;
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
            public void setAlvo(Point2D.Double novoAlvo) {
                this.alvo = novoAlvo;
            }
            
            public void setAlvo(double alvoX, double alvoY) {
                this.alvo = new Point2D.Double(alvoX, alvoY);
            }

            public void setAlvoX(double alvoX) {
                this.alvo.x = alvoX;
            }

            public void setAlvoY(double alvoY) {
                this.alvo.y = alvoY;
            }

            // Get
            public Point2D.Double getAlvo() {
                return this.alvo;
            }

            public double getAlvoX() {
                return this.alvo.x;
            }

            public double getAlvoY() {
                return this.alvo.y;
            }

            public Point2D.Double getVelocidade() {
                return this.velocidade;
            }
        }

        private Movimento movimento;

        public IrPara(Inimigo inimigo, double alvoX, double alvoY, double velocidade) {
            super(inimigo);
            movimento = new Movimento(velocidade, velocidade, alvoX, alvoY);
        }
        
        public IrPara(Inimigo inimigo, double alvoX, double alvoY, double velX, double velY) {
            super(inimigo);
            movimento = new Movimento(velX, velY, alvoX, alvoY);
        }

        public IrPara(Inimigo inimigo, Point2D.Double alvo, Point2D.Double velocidade) {
            super(inimigo);
            movimento = new Movimento(velocidade, alvo);
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) return;

            Point2D.Double proximo = movimento.proximoMovimento(inimigo.getX(), inimigo.getY());
            
            inimigo.setPosition(inimigo.getX() + proximo.x, inimigo.getY() + proximo.y);

            if (Movimento.isZero(proximo)) {
                 inimigo.setPosition(movimento.alvo.x, movimento.alvo.y);
                 estadoCompleto = true;
            }
        }
        
        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Movimento getMovimento() {
            return this.movimento;
        }
    }
}