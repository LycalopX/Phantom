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
 * @brief Classe abstrata base para todos os personagens do jogo (herói,
 *        inimigos, itens, etc.).
 *        Define propriedades e comportamentos comuns.
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
     * @brief Construtor principal (manual) que define todas as propriedades do
     *        personagem.
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
     * @brief Construtor secundário (manual) que calcula o raio da hitbox
     *        automaticamente.
     */
    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura) {
        this(sNomeImagePNG, x, y, largura, altura, (largura / 2.0) / CELL_SIDE);
    }

    /**
     * @brief Construtor automático que calcula largura, altura e hitbox com base na
     *        imagem e proporções globais.
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
     * @brief Carrega a imagem do sprite do personagem a partir do nome do arquivo.
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
     */
    public abstract void atualizar();

    /**
     * @brief Retorna a camada de renderização do personagem para ordenação (Z-order).
     * @return O RenderLayer do personagem.
     */
    public abstract RenderLayer getRenderLayer();

    /**
     * @brief Desenha a hitbox de debug do personagem, se o modo de debug estiver
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
     * @brief Método para desserialização, recarrega a imagem transient.
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

    /**
     * @brief Verifica se o personagem pode ser atravessado por outros.
     */
    public boolean isTransponivel() {
        return bTransponivel;
    }

    /**
     * @brief Define se o personagem pode ser atravessado.
     */
    public void setbTransponivel(boolean bTransponivel) {
        this.bTransponivel = bTransponivel;
    }

    /**
     * @brief Verifica se o personagem pode causar dano ou ser destruído.
     */
    public boolean isMortal() {
        return bMortal;
    }

    /**
     * @brief Retorna a tabela de loot do personagem.
     */
    public LootTable getLootTable() {
        return this.lootTable;
    }

    /**
     * @brief Retorna a vida atual do personagem.
     */
    public double getVida() {
        return this.vida;
    }

    /**
     * @brief Define a vida do personagem.
     */
    public void setVida(double vida) {
        this.vida = vida;
    }

    /**
     * @brief Define a animação de morte do personagem.
     */
    public void animacaoMorte() {
    }

    /**
     * @brief Retorna a altura do personagem em pixels.
     */
    public int getAltura() {
        return this.altura;
    }

    /**
     * @brief Retorna a largura do personagem em pixels.
     */
    public int getLargura() {
        return this.largura;
    }

    /**
     * @brief Verifica se o personagem está ativo no jogo.
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * @brief Ativa o personagem no jogo.
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * @brief Desativa o personagem, marcando-o para remoção.
     */
    public void deactivate() {
        this.isActive = false;
    }
}