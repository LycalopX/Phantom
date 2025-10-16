package Modelo;

import Auxiliar.Consts;
import Auxiliar.DebugManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
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

    protected boolean bTransponivel;
    protected boolean bMortal;

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
            iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + this.nomeSprite);
            Image img = iImage.getImage();
            BufferedImage bi = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, this.largura, this.altura, null);
            iImage = new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
        }
    }

    public void atualizar(ArrayList<Personagem> personagens) {
        // Por padrão, personagens estáticos não fazem nada.
        // Inimigos, Projéteis e o Herói vão sobrepor (override) este método.
    }

    // Construtor para Inimigos/Itens (calcula hitbox automaticamente)
    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura) {
        this(sNomeImagePNG, x, y, largura, altura, (largura / 2.0) / Consts.CELL_SIDE);
    }

    // Construtor legado (para manter compatibilidade)
    protected Personagem(String sNomeImagePNG, double x, double y) {
        this(sNomeImagePNG, x, y, Consts.CELL_SIDE, Consts.CELL_SIDE);
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
        in.defaultReadObject(); // Carrega os campos não-transient (x, y, nomeSprite, etc.)
        carregarImagem(); // Recarrega a imagem
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
}