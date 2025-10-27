package Modelo.Projeteis;

import Auxiliar.Projeteis.HitboxType;
import Auxiliar.Projeteis.ProjetilTipo;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;
import Modelo.Personagem;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * @brief Classe base para todos os projéteis do jogo.
 */
public class Projetil extends Personagem {

    protected double velocidade;
    protected double anguloRad;
    protected TipoProjetil tipo;
    protected ProjetilTipo tipoDetalhado;

    /**
     * @brief Construtor do Projétil.
     */
    public Projetil(String sNomeImagePNG) {
        super(sNomeImagePNG, 0, 0);
        this.bMortal = true;
        this.bTransponivel = true;
        deactivate();
    }

    /**
     * @brief Reinicia o estado de um projétil da pool de objetos para um novo
     *        disparo.
     */
    public void reset(double x, double y, double velocidadeGrid, double angulo, TipoProjetil tipo,
            ProjetilTipo tipoDetalhado) {
        this.x = x;
        this.y = y;
        this.velocidade = velocidadeGrid;
        this.anguloRad = Math.toRadians(angulo);
        this.tipo = tipo;
        this.tipoDetalhado = tipoDetalhado;

        if (tipo == TipoProjetil.JOGADOR) {
            this.largura = (int) ((double) tipoDetalhado.getSpriteWidth() * FATOR_ESCALA_ALTURA);
            this.altura = (int) ((double) tipoDetalhado.getSpriteHeight() * FATOR_ESCALA_ALTURA);

            if (tipoDetalhado.getHitboxType() == HitboxType.CIRCULAR) {
                this.hitboxRaio = tipoDetalhado.getHitboxWidth() / 2.0 / CELL_SIDE;
            } else {
                this.hitboxRaio = 0;
            }
            if (this instanceof ProjetilBombaHoming) {
                this.hitboxRaio *= 5;
            }
        } else {
            this.largura = (int) ((double) tipoDetalhado.getSpriteWidth() * BODY_PROPORTION * FATOR_ESCALA_ALTURA);
            this.altura = (int) ((double) tipoDetalhado.getSpriteHeight() * BODY_PROPORTION * FATOR_ESCALA_ALTURA);

            if (tipoDetalhado.getHitboxType() == HitboxType.CIRCULAR) {
                this.hitboxRaio = (tipoDetalhado.getHitboxWidth() * BODY_PROPORTION) / 2.0 / CELL_SIDE;
            } else {
                this.hitboxRaio = 0;
            }
        }

        this.iImage = tipoDetalhado.getImagem();
        activate();
    }

    /**
     * @brief Atualiza a posição do projétil com base em sua velocidade e ângulo.
     */
    @Override
    public void atualizar() {
        if (!isActive())
            return;

        this.x += Math.cos(this.anguloRad) * this.velocidade;
        this.y += Math.sin(this.anguloRad) * this.velocidade;
    }

    /**
     * @brief Desenha o projétil na tela, aplicando rotação e transparência.
     */
    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive())
            return;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transformOriginal = g2d.getTransform();
        int telaX = (int) Math.round(x * CELL_SIDE);
        int telaY = (int) Math.round(y * CELL_SIDE);

        Composite compositeOriginal = g2d.getComposite();
        if (this.tipo == TipoProjetil.JOGADOR) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        g2d.translate(telaX, telaY);
        g2d.rotate(this.anguloRad);
        g2d.drawImage(iImage.getImage(), -this.largura / 2, -this.altura / 2, this.largura, this.altura, null);
        g2d.setComposite(compositeOriginal);
        g2d.setTransform(transformOriginal);

        super.autoDesenho(g);
    }

    /**
     * @brief Retorna o tipo da hitbox do projétil.
     */
    public HitboxType getTipoHitbox() {
        if (tipoDetalhado != null) {
            return tipoDetalhado.getHitboxType();
        }
        return HitboxType.CIRCULAR;
    }

    /**
     * @brief Retorna os limites retangulares da hitbox do projétil.
     */
    public java.awt.Rectangle getBounds() {
        int centroX = (int) (this.x * CELL_SIDE);
        int centroY = (int) (this.y * CELL_SIDE);

        if (tipoDetalhado != null && tipoDetalhado.getHitboxType() == HitboxType.RECTANGULAR) {
            int hitboxW = tipoDetalhado.getHitboxWidth();
            int hitboxH = tipoDetalhado.getHitboxHeight();
            return new java.awt.Rectangle(centroX - hitboxW / 2, centroY - hitboxH / 2, hitboxW, hitboxH);
        } else {
            int raioPixels = (int) (this.hitboxRaio * CELL_SIDE);
            int diametroPixels = raioPixels * 2;
            int topLeftX = centroX - raioPixels;
            int topLeftY = centroY - raioPixels;
            return new java.awt.Rectangle(topLeftX, topLeftY, diametroPixels, diametroPixels);
        }
    }

    /**
     * @brief Verifica se o projétil está fora dos limites da tela.
     */
    public boolean estaForaDaTela() {
        if (!isActive())
            return false;

        double limiteX = (double) LARGURA_TELA / CELL_SIDE;
        double limiteY = (double) ALTURA_TELA / CELL_SIDE;
        return (x < 0 || x > limiteX || y < -1 || y > limiteY);
    }

    /**
     * @brief Desativa o projétil.
     */
    public void deactivate() {
        super.deactivate();
    }

    /**
     * @brief Ativa o projétil.
     */
    public void activate() {
        super.activate();
    }

    /**
     * @brief Retorna o tipo do projétil (JOGADOR ou INIMIGO).
     */
    public TipoProjetil getTipo() {
        return this.tipo;
    }
}