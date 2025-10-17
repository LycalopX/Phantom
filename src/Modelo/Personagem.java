package Modelo;

import Auxiliar.Consts;
import Auxiliar.DebugManager;
import Auxiliar.LootTable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public abstract class Personagem implements Serializable {

    protected transient ImageIcon iImage;
    private String nomeSprite;
    public double x, y; // Coordenadas de grid
    public double hitboxRaio; // Raio de colisão em grid

    protected int largura; // Largura visual em pixels
    protected int altura; // Altura visual em pixels

    // Para aplicar a proporção a tudo
    protected transient int originalSpriteWidth;
    protected transient int originalSpriteHeight;

    protected boolean bTransponivel;
    protected boolean bMortal;

    protected LootTable lootTable;

    // Construtor principal que define tudo
    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura, double hitboxRaio) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.hitboxRaio = hitboxRaio;
        this.bTransponivel = true;
        this.bMortal = false;

        this.nomeSprite = sNomeImagePNG;
        carregarImagem();
    }

    private void carregarImagem() {
        try {
            // SIMPLESMENTE CARREGUE A IMAGEM ORIGINAL. NÃO REDIMENSIONE AQUI.
            iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + this.nomeSprite);

            // Armazene as dimensões originais
            this.originalSpriteWidth = iImage.getIconWidth();
            this.originalSpriteHeight = iImage.getIconHeight();

        } catch (IOException ex) {

            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
            // Define padrões de falha
            this.originalSpriteWidth = Consts.CELL_SIDE;
            this.originalSpriteHeight = Consts.CELL_SIDE;
        }
    }

    public void atualizar(ArrayList<Personagem> personagens) {
        // Por padrão, personagens estáticos não fazem nada.
        // Inimigos, Projéteis e o Herói vão sobrepor (override) este método.
    }

    // Construtor para tamanho MANUAL (usado pelo segundo construtor do Inimigo)
    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura) {
        this(sNomeImagePNG, x, y, largura, altura, (largura / 2.0) / Consts.CELL_SIDE);
    }

    // Construtor AUTOMÁTICO (usado pelo primeiro construtor do Inimigo)
    protected Personagem(String sNomeImagePNG, double x, double y) {
        this.x = x;
        this.y = y;
        this.nomeSprite = sNomeImagePNG;
        this.bTransponivel = true;
        this.bMortal = false;

        // 1. Carrega a imagem E define originalSpriteWidth/Height
        carregarImagem();

        // 2. Calcula largura/altura com base na proporção global
        this.largura = (int) (this.originalSpriteWidth * Consts.BODY_PROPORTION);
        this.altura = (int) (this.originalSpriteHeight * Consts.BODY_PROPORTION);

        // 3. Calcula a hitbox com base no novo tamanho
        this.hitboxRaio = (this.largura / 2.0) / Consts.CELL_SIDE;
    }

    // O autoDesenho da classe pai só desenha o DEBUG.
    // A subclasse (Hero, Inimigo) DEVE desenhar seu próprio sprite.
    public void autoDesenho(Graphics g) {
        if (DebugManager.isActive()) {
            Graphics2D g2d = (Graphics2D) g;

            // Posição central em PIXELS
            int centroX = (int) (this.x * Consts.CELL_SIDE);
            int centroY = (int) (this.y * Consts.CELL_SIDE);

            // Desenha a hitbox de DANO (vermelha) para TODOS os personagens
            g2d.setColor(Color.RED);
            int danoRaioPixels = (int) (this.hitboxRaio * Consts.CELL_SIDE);
            g2d.drawOval(centroX - danoRaioPixels, centroY - danoRaioPixels, danoRaioPixels * 2, danoRaioPixels * 2);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        carregarImagem(); // Recarrega a imagem E os tamanhos originais
    }

    public boolean isbTransponivel() {
        return bTransponivel;
    }

    public void setbTransponivel(boolean bTransponivel) {
        this.bTransponivel = bTransponivel;
    }

    public boolean isbMortal() {
        return bMortal;
    }

    public LootTable getLootTable() {
        return this.lootTable;
    }

    public void animaçãoMorte() {
        // Pode ser sobreposto por subclasses para animações específicas
    }
    
    public int getAltura() {
        return this.altura;
    }

    public int getLargura() {
        return this.largura;
    }
}