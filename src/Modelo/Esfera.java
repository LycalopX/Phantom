package Modelo;

import java.awt.Graphics;

/**
 *
 * @author Jose F Rodrigues-Jr
 */
public class Esfera extends Personagem{
    public Esfera(String sNomeImagePNG, int linha, int coluna) {
        super(sNomeImagePNG, linha, coluna);
        this.bMortal = false;
        this.bTransponivel = false;
    }

    public void autoDesenho(Graphics g) {
        super.autoDesenho(g);
    }    
}
