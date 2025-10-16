// Em Modelo/Inimigo.java
package Modelo;

import Auxiliar.Consts; // Import necessário
import java.awt.Graphics; // Import necessário
import java.util.ArrayList; // Import necessário

public class Inimigo extends Personagem {
    
    public Inimigo (String sNomeImagePNG, double x, double y) {
        // Este construtor usa o tamanho padrão (32x32)
        super(sNomeImagePNG, x, y); 
        this.bMortal = true;
    }
    
    // Construtor para inimigos com tamanhos diferentes
    public Inimigo(String sNomeImagePNG, double x, double y, int tamanho) {
        // Chama o construtor de Personagem que define o tamanho
        super(sNomeImagePNG, x, y, tamanho, tamanho);
        this.bMortal = true;
    }
    
    @Override
    public void atualizar(ArrayList<Personagem> personagens) {
        // Lógica de movimento do inimigo
        this.y += 0.02; // (Velocidade em grid, 0.2 era muito rápido)
    }
    
    @Override
    public void autoDesenho(Graphics g) {
        // 1. Desenha o sprite do Inimigo
        // Converte a posição de grid (centro) para pixels (canto superior esquerdo)
        int telaX = (int)Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int)Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);
        
        // Desenha a imagem (iImage) que foi carregada no construtor do Personagem
        g.drawImage(iImage.getImage(), telaX, telaY, largura, altura, null);
        
        // 2. Chama a superclasse (Personagem) para desenhar o círculo de debug (hitbox)
        super.autoDesenho(g); 
    }
}