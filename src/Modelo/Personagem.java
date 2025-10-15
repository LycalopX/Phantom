package Modelo;

import Auxiliar.Consts;
import Auxiliar.Desenho;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.ImageIcon;

public abstract class Personagem implements Serializable {

    protected ImageIcon iImage;
    public double x; // Coordenada x em unidades de grid (ex: 10.5)
    public double y; // Coordenada y em unidades de grid (ex: 10.5)
    public double raio;
    
    protected int largura;
    protected int altura;
    
    protected boolean bTransponivel;
    protected boolean bMortal;

    protected Personagem(String sNomeImagePNG, double x, double y, int largura, int altura) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.raio = (this.largura / 2.0) / Consts.CELL_SIDE; // Raio em unidades de grid
        
        this.bTransponivel = true;
        this.bMortal = false;
        try {
            iImage = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + sNomeImagePNG);
            Image img = iImage.getImage();
            BufferedImage bi = new BufferedImage(Consts.CELL_SIDE, Consts.CELL_SIDE, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, Consts.CELL_SIDE, Consts.CELL_SIDE, null);
            iImage = new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    protected Personagem(String sNomeImagePNG, double x, double y) {
        this(sNomeImagePNG, x, y, Consts.CELL_SIDE, Consts.CELL_SIDE);
    }

    public void autoDesenho(Graphics g) {
        // O cálculo da posição de desenho agora usa a largura e altura do personagem
        int telaX = (int)Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int)Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);
        
        // Agora passamos as coordenadas de PIXEL corretas para o método de desenho
        Desenho.desenhar(g, this.iImage, telaX, telaY);
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