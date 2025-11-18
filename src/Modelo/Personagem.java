package Modelo;

import Auxiliar.Debug.DebugManager;
import Auxiliar.LootTable;
import static Auxiliar.ConfigMapa.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * @brief Classe abstrata base para todos os objetos interativos do jogo.
 * 
 *        Define propriedades e comportamentos comuns a herói, inimigos,
 *        projéteis,
 *        itens, etc., como posição, imagem, estado de atividade e hitbox.
 */
public abstract class Personagem implements Serializable {

    public static final String PATH = "Assets/";
    public static final double BODY_PROPORTION = ((double) ALTURA_TELA) / 375;

    protected transient ImageIcon iImage;
    private String nomeSprite;
    protected double x, y;
    protected boolean isActive = true;
    protected double hitboxRaio;
    protected double vida = 1;

    protected int largura;
    protected int altura;

    protected transient int originalSpriteWidth;
    protected transient int originalSpriteHeight;

    protected boolean bTransponivel;
    protected boolean bMortal;

    protected LootTable lootTable;

    /**
     * @brief Construtor principal que define todas as propriedades do personagem.
     */
    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura, double hitboxRaio) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.hitboxRaio = hitboxRaio * FATOR_ESCALA_ALTURA;
        this.bTransponivel = true;
        this.bMortal = false;
        this.nomeSprite = sNomeImagePNG;
        carregarImagem();
    }

    /**
     * @brief Construtor secundário que calcula o raio da hitbox automaticamente.
     */
    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura) {
        this(sNomeImagePNG, x, y, largura, altura, (largura / 2.0) / CELL_SIDE);
    }

    /**
     * @brief Construtor que calcula largura, altura e hitbox com base na imagem.
     */
    protected Personagem(String sNomeImagePNG, double x, double y) {
        this.x = x;
        this.y = y;
        this.nomeSprite = sNomeImagePNG;
        this.bTransponivel = true;
        this.bMortal = false;
        carregarImagem();
        this.largura = (int) (((double) this.originalSpriteWidth) * BODY_PROPORTION);
        this.altura = (int) (((double) this.originalSpriteHeight) * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0 * FATOR_ESCALA_ALTURA) / CELL_SIDE;
    }

    /**
     * @brief Carrega a imagem do sprite a partir do nome do arquivo.
     */
    private void carregarImagem() {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(PATH + this.nomeSprite);
            if (imgURL == null) {
                System.err.println("Recurso não encontrado: " + PATH + this.nomeSprite);
                return;
            }
            iImage = new ImageIcon(imgURL);
            this.originalSpriteWidth = iImage.getIconWidth();
            this.originalSpriteHeight = iImage.getIconHeight();
        } catch (Exception ex) {
            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
        }
    }

    /**
     * @brief Método abstrato para atualizar o estado do personagem a cada frame.
     *        Deve ser implementado pelas subclasses.
     */
    public abstract void atualizar();

    /**
     * @brief Retorna a camada de renderização para ordenação (Z-order).
     * @return O RenderLayer do personagem.
     */
    public abstract RenderLayer getRenderLayer();

    /**
     * @brief Desenha a hitbox de debug do personagem se o modo de debug estiver
     *        ativo.
     */
    public void autoDesenho(Graphics g) {
        if (DebugManager.isActive()) {
            Graphics2D g2d = (Graphics2D) g;
            int centroX = (int) (this.x * CELL_SIDE);
            int centroY = (int) (this.y * CELL_SIDE);
            g2d.setColor(Color.BLUE);
            int danoRaioPixels = (int) (this.hitboxRaio * CELL_SIDE);
            g2d.drawOval(centroX - danoRaioPixels, centroY - danoRaioPixels, danoRaioPixels * 2, danoRaioPixels * 2);
        }
    }

    /**
     * @brief Método customizado para desserialização.
     * 
     *        Garante que a imagem do sprite (`iImage`), que é `transient`, seja
     *        recarregada a partir do `nomeSprite` quando o objeto é lido de um
     *        arquivo.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        carregarImagem();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getHitboxRaio() {
        return hitboxRaio;
    }

    public boolean isTransponivel() {
        return bTransponivel;
    }

    public void setbTransponivel(boolean bTransponivel) {
        this.bTransponivel = bTransponivel;
    }

    public boolean isMortal() {
        return bMortal;
    }

    public LootTable getLootTable() {
        return this.lootTable;
    }

    public double getVida() {
        return this.vida;
    }

    public void setVida(double vida) {
        this.vida = vida;
    }

    public void animacaoMorte() {
    }

    public int getAltura() {
        return this.altura;
    }

    public int getLargura() {
        return this.largura;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}