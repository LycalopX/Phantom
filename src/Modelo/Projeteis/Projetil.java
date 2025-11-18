package Modelo.Projeteis;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.Projeteis.HitboxType;
import Auxiliar.Projeteis.ProjetilTipo;
import Auxiliar.Projeteis.TipoProjetil;
import Modelo.Personagem;
import Modelo.RenderLayer;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @brief Classe base para todos os projéteis do jogo.
 * 
 *        Define o comportamento fundamental de um projétil, como movimento,
 *        renderização e gerenciamento de estado (ativo/inativo) para uso em
 *        piscinas de objetos (Object Pooling).
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
     * @brief Método customizado para desserialização.
     * 
     *        Garante que a imagem do sprite, que é `transient`, seja recarregada
     *        a partir da definição em `tipoDetalhado`.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.tipoDetalhado != null) {
            this.iImage = this.tipoDetalhado.getImagem();
            if (this.iImage != null) {
                this.originalSpriteWidth = this.iImage.getIconWidth();
                this.originalSpriteHeight = this.iImage.getIconHeight();
            }
        }
    }

    @Override
    public RenderLayer getRenderLayer() {
        return RenderLayer.PROJETILE_LAYER;
    }

    /**
     * @brief Reinicia o estado de um projétil da pool de objetos para um novo
     *        disparo.
     * 
     *        Este método é central para o padrão de Object Pooling, permitindo que
     *        um
     *        projétil inativo seja reconfigurado e reutilizado sem a necessidade de
     *        criar um novo objeto.
     * 
     * @param tipoDetalhado A definição do tipo de projétil (e.g., de
     *                      `TipoProjetilHeroi`),
     *                      que contém informações sobre imagem, hitbox, etc.
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

                double raioMedio = (tipoDetalhado.getHitboxWidth() + tipoDetalhado.getHitboxHeight()) / 2.0;
                this.hitboxRaio = (raioMedio * BODY_PROPORTION) / 2.0 / CELL_SIDE;
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
        if (this.tipo == TipoProjetil.INIMIGO) {
            g2d.rotate(this.anguloRad + Math.toRadians(90));
        } else {
            g2d.rotate(this.anguloRad);
        }
        g2d.drawImage(iImage.getImage(), -this.largura / 2, -this.altura / 2, this.largura, this.altura, null);
        g2d.setComposite(compositeOriginal);
        g2d.setTransform(transformOriginal);

        super.autoDesenho(g);
    }

    public HitboxType getTipoHitbox() {
        if (tipoDetalhado != null) {
            return tipoDetalhado.getHitboxType();
        }
        return HitboxType.CIRCULAR;
    }

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

    public boolean estaForaDaTela() {
        if (!isActive())
            return false;

        double limiteX = (double) LARGURA_TELA / CELL_SIDE;
        double limiteY = (double) ALTURA_TELA / CELL_SIDE;
        return (x < 0 || x > limiteX || y < -1 || y > limiteY);
    }

    public void deactivate() {
        super.deactivate();
    }

    public void activate() {
        super.activate();
    }

    public TipoProjetil getTipo() {
        return this.tipo;
    }
}