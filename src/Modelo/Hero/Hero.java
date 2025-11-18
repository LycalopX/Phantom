package Modelo.Hero;

import Auxiliar.Debug.DebugManager;
import static Auxiliar.ConfigMapa.*;
import Modelo.Personagem;
import Modelo.Projeteis.BombaProjetil;
import Modelo.RenderLayer;
import Modelo.Fases.Fase;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * @brief Representa o personagem principal do jogo, controlado pelo jogador.
 * 
 * Gerencia o estado do herói (HP, bombas, poder), a lógica de movimento,
 * dano, e interage com seus gerenciadores de animação e armas.
 */
public class Hero extends Personagem {

    public static final double HITBOX_RAIO = 5.5;
    public static final double HITBOX_RAIO_FANTASY_SEAL = 25.0;
    public static final double HERO_VELOCITY = (((double) LARGURA_TELA) * 0.03763);
    public static final int DANO_BALA = 40;
    public static final int DANO_BALA_TELEGUIADA = 3;
    public static final int REQ_MISSIL_POWER = 300;
    public static final int REQ_TIROS_POWER = 100;
    public static final int SLOW_MOTION_FRAMES = 4;
    private final int DURACAO_BOMBA_FRAMES = 240;
    private final int duracaoInvencibilidadeRespawn = 120;

    private int HP = 3;
    private int bombas = 3;
    private int power = 0;
    private int score = 0;

    private int invencibilidadeTimer = 0;
    private int efeitoBombaTimer = 0;

    private double grabHitboxRaio;
    private boolean isFocoAtivo = false;

    private transient GerenciadorDeAnimacaoHeroi animador;
    private GerenciadorDeArmasHeroi sistemaDeArmas;

    private HeroState estado = HeroState.IDLE;

    /**
     * @brief Construtor do Herói.
     */
    public Hero(String sNomeImagePNG, double x, double y) {
        super(sNomeImagePNG, x, y);

        this.hitboxRaio = HITBOX_RAIO / CELL_SIDE;

        int tamanhoHitboxColeta = 100;
        this.grabHitboxRaio = ((double) (tamanhoHitboxColeta / 2) / CELL_SIDE) / 2.0;

        this.animador = new GerenciadorDeAnimacaoHeroi(this.largura, this.altura);
        this.sistemaDeArmas = new GerenciadorDeArmasHeroi();
        activate();
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.PLAYER_LAYER;
    }

    /**
     * @brief Atualiza o estado interno do Herói, como timers de invencibilidade e bomba.
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
     * @brief Atualiza a máquina de estados da animação com base no input de movimento.
     */
    public void atualizarAnimacao(boolean isMovingLeft, boolean isMovingRight) {
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

    
    public GerenciadorDeArmasHeroi getSistemaDeArmas() {
        return this.sistemaDeArmas;
    }

    /**
     * @brief Move o herói para uma nova posição.
     */
    public void mover(double novoX, double novoY) {
        this.x = novoX;
        this.y = novoY;
    }

    /**
     * @brief Desenha o herói e seus elementos visuais (hitbox de foco, etc.).
     */
    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive())
            return;

        int telaX = (int) Math.round(x * CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * CELL_SIDE) - (this.altura / 2);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transformOriginal = g2d.getTransform();
        ImageIcon imagemParaDesenhar = animador.getImagemAtual(estado);

        // Desenha o sprite do herói, invertendo-o horizontalmente se movendo para a direita.
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

        // Desenha a hitbox de foco com efeitos de rotação e fade.
        float hitboxAlpha = animador.getHitboxAlpha();
        if (hitboxAlpha > 0) {
            ImageIcon hitboxSprite = animador.getImagemHitboxFoco();
            if (hitboxSprite != null) {
                AffineTransform oldTransform = g2d.getTransform();
                Composite originalComposite = g2d.getComposite();

                int centroX = (int) Math.round(this.x * CELL_SIDE);
                int centroY = (int) Math.round(this.y * CELL_SIDE);
                double anguloRad = Math.toRadians(animador.getAnguloRotacaoHitbox());

                int w = (int) (hitboxSprite.getIconWidth() * BODY_PROPORTION);
                int h = (int) (hitboxSprite.getIconHeight() * BODY_PROPORTION);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hitboxAlpha));
                g2d.translate(centroX, centroY);
                g2d.rotate(anguloRad);
                g2d.drawImage(hitboxSprite.getImage(), -w / 2, -h / 2, w, h, null);

                g2d.setTransform(oldTransform);
                g2d.setComposite(originalComposite);
            }
        }

        // Desenha a hitbox de coleta de itens para debug.
        if (DebugManager.isActive()) {
            g2d.setColor(new Color(22, 100, 7, 100));
            int centroX = (int) (this.x * CELL_SIDE);
            int centroY = (int) (this.y * CELL_SIDE);
            int coletaRaioPixels = (int) (this.grabHitboxRaio * CELL_SIDE);
            g2d.drawOval(centroX - coletaRaioPixels, centroY - coletaRaioPixels, coletaRaioPixels * 2,
                    coletaRaioPixels * 2);
        }
    }

    /**
     * @brief Reinicia o estado do herói após a morte (respawn).
     */
    public void respawn() {
        this.bombas = 3;
        this.power = 0;
        this.invencibilidadeTimer = duracaoInvencibilidadeRespawn;
        this.estado = HeroState.IDLE;
        super.activate();
    }

    /**
     * @brief Utiliza uma bomba, se disponível.
     * @return O objeto `BombaProjetil` criado, ou `null` se não houver bombas.
     */
    public BombaProjetil usarBomba(Fase fase) {
        if (bombas > 0 && !isBombing()) {
            this.bombas--;
            this.efeitoBombaTimer = DURACAO_BOMBA_FRAMES;
            this.invencibilidadeTimer = DURACAO_BOMBA_FRAMES;
            return new BombaProjetil(this.x, this.y, fase, this);
        }
        return null;
    }

    /**
     * @brief Método customizado para desserialização. Recarrega os componentes `transient`.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacaoHeroi(this.largura, this.altura);
        if (this.sistemaDeArmas == null) {
            this.sistemaDeArmas = new GerenciadorDeArmasHeroi();
        }
    }

    /**
     * @brief Processa o dano recebido pelo herói.
     * @return `true` se o dano foi efetivamente recebido, `false` se estava invencível.
     */
    public boolean takeDamage() {
        if (isInvencivel()) {
            return false;
        }
        return true;
    }

    /**
     * @brief Processa a lógica de morte do herói.
     */
    public void processarMorte() {
        this.HP--;
        this.power = 0;
        this.deactivate();
    }

    public void toggleCheats() {

        if (this.HP <= 10) {
            this.HP = 999;
            this.bombas = 999;
            this.power = 9999;
            System.out.println("Cheats ativados: HP, Bombas e Power máximos.");
        } else {
            this.HP = 3;
            this.bombas = 3;
            this.power = 0;
            System.out.println("Cheats desativados.");
        }
        System.out.println("Cheats ativados: HP, Bombas e Power máximos.");
    }

    
    public boolean isInvencivel() {
        return this.invencibilidadeTimer > 0 || isBombing();
    }

    
    public void deactivate() {
        super.deactivate();
    }

    
    public void activate() {
        super.activate();
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

    public double getGrabHitboxRaio() {
        return this.grabHitboxRaio;
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
        return this.sistemaDeArmas.getNivelTiro(this.power);
    }

    
    public boolean isBombing() {
        return this.efeitoBombaTimer > 0;
    }

    
    public java.awt.Rectangle getBounds() {
        int centroX = (int) (this.x * CELL_SIDE);
        int centroY = (int) (this.y * CELL_SIDE);
        int raioPixels = (int) (this.hitboxRaio * CELL_SIDE);
        int diametroPixels = raioPixels * 2;
        int topLeftX = centroX - raioPixels;
        int topLeftY = centroY - raioPixels;
        return new java.awt.Rectangle(topLeftX, topLeftY, diametroPixels, diametroPixels);
    }

    /**
     * @brief Ativa ou desativa o modo de foco, controlando a visualização da hitbox.
     */
    public void setFocoAtivo(boolean isFocoAtivo) {
        if (this.isFocoAtivo != isFocoAtivo) {
            this.isFocoAtivo = isFocoAtivo;
            if (isFocoAtivo) {
                animador.iniciarFadeInHitbox();
            } else {
                animador.iniciarFadeOutHitbox();
            }
        }
    }
}