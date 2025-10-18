// Em Modelo/Hero.java

package Modelo.Hero;

import Auxiliar.Consts;
import Auxiliar.DebugManager;
import Modelo.Personagem;
import Modelo.Projeteis.BombaProjetil;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Color;

public class Hero extends Personagem {

    private int HP = 3;
    private int bombas = 3;
    private int power = 0;
    private int score = 0;

    private int invencibilidadeTimer = 0;
    private int efeitoBombaTimer = 0;

    public double grabHitboxRaio;

    private transient GerenciadorDeAnimacao animador;
    private GerenciadorDeArmas sistemaDeArmas;
    private final int DURACAO_BOMBA_FRAMES = 300;
    private final int duracaoInvencibilidadeRespawn = 120;

    // Para animação
    private HeroState estado = HeroState.IDLE;

    public Hero(String sNomeImagePNG, double x, double y) {
        
        super(sNomeImagePNG, x, y);

        this.hitboxRaio = Consts.HITBOX_RAIO / Consts.CELL_SIDE;

        // O resto do construtor permanece idêntico
        int tamanhoHitboxColeta = 100;
        this.grabHitboxRaio = ((double) (tamanhoHitboxColeta / 2) / Consts.CELL_SIDE) / 2.0;

        this.animador = new GerenciadorDeAnimacao(this.largura, this.altura);
        this.sistemaDeArmas = new GerenciadorDeArmas();
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
    public GerenciadorDeArmas getSistemaDeArmas() {
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

        int telaX = (int) Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);

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
            int centroX = (int) (this.x * Consts.CELL_SIDE);
            int centroY = (int) (this.y * Consts.CELL_SIDE);
            int coletaRaioPixels = (int) (this.grabHitboxRaio * Consts.CELL_SIDE);
            g2d.drawOval(centroX - coletaRaioPixels, centroY - coletaRaioPixels, coletaRaioPixels * 2,
                    coletaRaioPixels * 2);
        }
    }

    public void respawn() {
        this.x = Consts.respawnX;
        this.y = Consts.respawnY;
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
    public BombaProjetil usarBomba() {
        if (bombas > 0 && !isBombing()) { // Usa o método isBombing()
            this.bombas--;
            this.efeitoBombaTimer = DURACAO_BOMBA_FRAMES;
            this.invencibilidadeTimer = DURACAO_BOMBA_FRAMES;
            return new BombaProjetil(this.x, this.y);
        }
        return null;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacao(this.largura, this.altura);
        if (this.sistemaDeArmas == null) { // Garante compatibilidade com saves antigos
            this.sistemaDeArmas = new GerenciadorDeArmas();
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
}