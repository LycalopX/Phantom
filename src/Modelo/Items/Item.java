package Modelo.Items;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.image.RasterFormatException;
import static Auxiliar.ConfigMapa.*;

/**
 * @brief Representa um item coletável no jogo, com física própria e comportamento de atração.
 */
public class Item extends Personagem {

    private ItemType tipo;
    private transient ImageIcon spriteRecortado;
    private Hero hero;

    private double velX = 0;
    private double velY = 0;
    private static final double GRAVIDADE = 0.003;
    private static final double MAX_FALL_SPEED = 0.3;
    private static final double VELOCIDADE_ATRACAO = 1;

    private int launchTimer = 0;
    private static final int DURACAO_LANCAMENTO = 60;

    /**
     * @brief Construtor do item.
     */
    public Item(ItemType tipo, double x, double y, Hero hero) {
        super("items.png", x, y, (int) (tipo.getLargura() * BODY_PROPORTION), (int) (tipo.getAltura() * BODY_PROPORTION));
        this.tipo = tipo;
        this.bTransponivel = true;
        this.bMortal = false;
        this.hero = hero;
        recortarSprite();
    }

    /**
     * @brief Lança o item com um impulso inicial, usado para drops do jogador.
     */
    public void lancarItem(double anguloEmGraus, double forca) {
        this.launchTimer = DURACAO_LANCAMENTO;
        double anguloRad = Math.toRadians(anguloEmGraus);
        this.velX = Math.cos(anguloRad) * forca;
        this.velY = Math.sin(anguloRad) * forca;
    }

    /**
     * @brief Atualiza a posição do item, aplicando gravidade, atração pelo herói ou movimento de lançamento.
     */
    @Override
    public void atualizar() {
        boolean deveAtrair = hero != null && (hero.isBombing() || hero.y < (ALTURA_TELA / CELL_SIDE) * 0.15);

        if (deveAtrair) {
            double dx = hero.x - this.x;
            double dy = hero.y - this.y;
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            if (magnitude > 0) {
                this.x += (dx / magnitude) * VELOCIDADE_ATRACAO;
                this.y += (dy / magnitude) * VELOCIDADE_ATRACAO;
            }
        } else {
            if (this.launchTimer > 0) {
                this.x += velX;
                this.y += velY;
                this.launchTimer--;
                if (this.launchTimer <= 0) {
                    this.velX = 0;
                    this.velY = 0;
                }
            } else {
                this.velY += GRAVIDADE;
                if (this.velY > MAX_FALL_SPEED) {
                    this.velY = MAX_FALL_SPEED;
                }
                this.y += velY;
            }
        }
    }

    /**
     * @brief Recorta o sprite correto para o tipo de item a partir do spritesheet.
     */
    private void recortarSprite() {
        if (this.iImage != null) {
            BufferedImage sheet = new BufferedImage(iImage.getIconWidth(), iImage.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = sheet.createGraphics();
            iImage.paintIcon(null, g, 0, 0);
            g.dispose();

            try {
                BufferedImage subImagem = sheet.getSubimage(tipo.getSpriteX(), tipo.getSpriteY(), tipo.getLargura(), tipo.getAltura());
                this.spriteRecortado = new ImageIcon(subImagem);
            } catch (RasterFormatException e) {
                System.err.println("ERRO ao recortar sprite para o item: " + tipo.name());
                System.err.println("Verifique as coordenadas em ItemType.java e o tamanho de items.png");
                e.printStackTrace();
                this.spriteRecortado = null;
            }
        }
    }

    /**
     * @brief Desenha o sprite do item na tela.
     */
    @Override
    public void autoDesenho(Graphics g) {
        if (spriteRecortado == null)
            recortarSprite();

        int telaX = (int) Math.round(x * CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * CELL_SIDE) - (this.altura / 2);

        if (spriteRecortado != null) {
            g.drawImage(spriteRecortado.getImage(), telaX, telaY, this.largura, this.altura, null);
        }
        super.autoDesenho(g);
    }

    /**
     * @brief Desativa o item.
     */
    public void deactivate() {
        super.deactivate();
    }
    
    /**
     * @brief Ativa o item.
     */
    public void activate() {
        super.activate();
    }

    /**
     * @brief Retorna o tipo do item.
     */
    public ItemType getTipo() {
        return this.tipo;
    }
}