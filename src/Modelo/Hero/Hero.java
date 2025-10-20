// Em Modelo/Hero.java

package Modelo.Hero;

import Auxiliar.Debug.DebugManager;
import static Auxiliar.ConfigMapa.*;
import Modelo.Personagem;
import Modelo.Projeteis.BombaProjetil;
import Modelo.Fases.Fase;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Color;

public class Hero extends Personagem {

    // --- Configurações do Herói movidas de Consts.java ---
    public static final double HITBOX_RAIO = 5.75;
    public static final double HITBOX_RAIO_FANTASY_SEAL = 25.0;
    public static final double HERO_VELOCITY = 28.9764;
    public static final int DANO_BALA = 40;
    public static final int DANO_BALA_TELEGUIADA = 3;
    public static final int REQ_MISSIL_POWER = 120; // Power necessário para aumenta o nível
    public static final int REQ_TIROS_POWER = 150; // Power necessário para aumenta o nível
    public static final int SLOW_MOTION_FRAMES = 4; // Usado na Engine
    // -----------------------------------------------------

    private int HP = 3;
    private int bombas = 3;
    private int power = 0;
    private int score = 0;

    private int invencibilidadeTimer = 0;
    private int efeitoBombaTimer = 0;

    public double grabHitboxRaio;

    private transient GerenciadorDeAnimacaoHeroi animador;
    private GerenciadorDeArmasHeroi sistemaDeArmas;
    private final int DURACAO_BOMBA_FRAMES = 180;
    private final int duracaoInvencibilidadeRespawn = 120;

    // Para animação
    private HeroState estado = HeroState.IDLE;

    public Hero(String sNomeImagePNG, double x, double y) {
        
        super(sNomeImagePNG, x, y);

        this.hitboxRaio = HITBOX_RAIO / CELL_SIDE;

        // O resto do construtor permanece idêntico
        int tamanhoHitboxColeta = 100;
        this.grabHitboxRaio = ((double) (tamanhoHitboxColeta / 2) / CELL_SIDE) / 2.0;

        this.animador = new GerenciadorDeAnimacaoHeroi(this.largura, this.altura);
        this.sistemaDeArmas = new GerenciadorDeArmasHeroi();
        activate();
    }

    /**
     * Atualiza o estado interno do Herói que não depende de input.
     * (Timers, animações, etc.)
     */
    @Override
    public void atualizar() {
        if (invencibilidadeTimer > 0)
            invencibilidadeTimer--;
        if (efeitoBombaTimer > 0)
            efeitoBombaTimer--;

        sistemaDeArmas.atualizarTimers();
    }

    /**
     * Atualiza apenas a lógica de animação com base no input do jogador.
     * É chamado pelo ControladorDoHeroi.
     */
    public void atualizarAnimacao(boolean isMovingLeft, boolean isMovingRight) {

        // 2. Lógica de atualização da animação
        switch (estado) {
            case IDLE:
                if (isMovingLeft)
                    estado = HeroState.STRAFING_LEFT;
                else if (isMovingRight)
                    estado = HeroState.STRAFING_RIGHT;
                break;
            case STRAFING_LEFT:
                if (!isMovingLeft) {
                    estado = HeroState.DE_STRAFING_LEFT;
                    animador.iniciarDeStrafing();
                } else if (isMovingRight) {
                    estado = HeroState.STRAFING_RIGHT;
                }
                break;
            case STRAFING_RIGHT:
                if (!isMovingRight) {
                    estado = HeroState.DE_STRAFING_RIGHT;
                    animador.iniciarDeStrafing();
                } else if (isMovingLeft) {
                    estado = HeroState.STRAFING_LEFT;
                }
                break;
            case DE_STRAFING_LEFT:
                if (isMovingRight)
                    estado = HeroState.STRAFING_RIGHT;
                else if (isMovingLeft)
                    estado = HeroState.STRAFING_LEFT;
                break;
            case DE_STRAFING_RIGHT:
                if (isMovingLeft)
                    estado = HeroState.STRAFING_LEFT;
                else if (isMovingRight)
                    estado = HeroState.STRAFING_RIGHT;
                break;
        }

        if (animador.atualizar(estado)) {
            estado = HeroState.IDLE;
        }
    }

    /**
     * Fornece acesso ao sistema de armas do herói.
     * 
     * @return A instância do GerenciadorDeArmas.
     */
    public GerenciadorDeArmasHeroi getSistemaDeArmas() {
        return this.sistemaDeArmas;
    }

    /**
     * Aplica o movimento ao Herói. A validação já foi feita pelo controlador.
     */
    public void mover(double novoX, double novoY) {
        this.x = novoX;
        this.y = novoY;
    }

    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive())
            return;

        int telaX = (int) Math.round(x * CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * CELL_SIDE) - (this.altura / 2);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transformOriginal = g2d.getTransform();
        ImageIcon imagemParaDesenhar = animador.getImagemAtual(estado);

        if (imagemParaDesenhar != null) {
            if (estado == HeroState.STRAFING_RIGHT || estado == HeroState.DE_STRAFING_RIGHT) {
                g2d.translate(telaX + largura, telaY);
                g2d.scale(-1, 1);
                g2d.drawImage(imagemParaDesenhar.getImage(), 0, 0, largura, altura, null);
            } else {
                g2d.drawImage(imagemParaDesenhar.getImage(), telaX, telaY, largura, altura, null);
            }
        }
        g2d.setTransform(transformOriginal);
        super.autoDesenho(g);

        if (DebugManager.isActive()) {
            g2d.setColor(new Color(22, 100, 7, 100));
            int centroX = (int) (this.x * CELL_SIDE);
            int centroY = (int) (this.y * CELL_SIDE);
            int coletaRaioPixels = (int) (this.grabHitboxRaio * CELL_SIDE);
            g2d.drawOval(centroX - coletaRaioPixels, centroY - coletaRaioPixels, coletaRaioPixels * 2,
                    coletaRaioPixels * 2);
        }
    }

    public void respawn() {
        this.bombas = 3;
        this.power = 0;
        this.invencibilidadeTimer = duracaoInvencibilidadeRespawn;
        this.estado = HeroState.IDLE;
        super.activate();
    }

    /**
     * Cria e retorna um objeto BombaProjetil para ser adicionado à fase.
     * Também ativa os timers de invencibilidade e atração de itens.
     * 
     * @return O objeto BombaProjetil, ou null se não houver bombas.
     */
    public BombaProjetil usarBomba(Fase fase) {
        if (bombas > 0 && !isBombing()) { // Usa o método isBombing()
            this.bombas--;
            this.efeitoBombaTimer = DURACAO_BOMBA_FRAMES;
            this.invencibilidadeTimer = DURACAO_BOMBA_FRAMES;

            return new BombaProjetil(this.x, this.y, fase, this);
        }
        return null;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacaoHeroi(this.largura, this.altura);
        if (this.sistemaDeArmas == null) { // Garante compatibilidade com saves antigos
            this.sistemaDeArmas = new GerenciadorDeArmasHeroi();
        }
    }

    // GETTERS E SETTERS SIMPLES
    public boolean takeDamage() {
        if (isInvencivel()) {
            return false;
        }
        return true;
    }

    public void processarMorte() {
        this.HP--;
        this.power = 0;
        this.deactivate();
    }

    public boolean isInvencivel() {
        return this.invencibilidadeTimer > 0 || isBombing(); // Usa o método isBombing()
    }

    public void deactivate() {
        super.deactivate(); // Usa o método da classe pai
    }

    public void activate() {
        super.activate(); // Usa o método da classe pai
    }

    public int getBombas() {
        return this.bombas;
    }

    public int getHP() {
        return this.HP;
    }

    public int getPower() {
        return this.power;
    }

    public int getScore() {
        return this.score;
    }

    public void addPower(int quantidade) {
        this.power += quantidade;
    }

    public void addBomb(int quantidade) {
        this.bombas += quantidade;
    }

    public void addHP(int quantidade) {
        this.HP += quantidade;
    }

    public void addScore(int quantidade) {
        this.score += quantidade;
    }

    public int getNivelDeMisseis() {
        return this.sistemaDeArmas.getNivelDeMisseis(this.power);
    }

    public boolean isBombing() {
        return this.efeitoBombaTimer > 0;
    }

    public java.awt.Rectangle getBounds() {
        // Posição central em PIXELS
        int centroX = (int) (this.x * CELL_SIDE);
        int centroY = (int) (this.y * CELL_SIDE);

        // Retorna um retângulo que inscreve o círculo da hitbox para a checagem.
        int raioPixels = (int) (this.hitboxRaio * CELL_SIDE);
        int diametroPixels = raioPixels * 2;
        int topLeftX = centroX - raioPixels;
        int topLeftY = centroY - raioPixels;
        return new java.awt.Rectangle(topLeftX, topLeftY, diametroPixels, diametroPixels);
    }
}