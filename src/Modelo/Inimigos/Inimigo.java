// Em Modelo/Inimigo.java
package Modelo.Inimigos;

import Auxiliar.Consts; // Import necessário
import java.awt.Graphics; // Import necessário
import java.util.ArrayList; // Import necessário
import Auxiliar.LootTable;
import Modelo.Personagem;
import Modelo.Hero.Hero;

public class Inimigo extends Personagem {

    public LootTable lootTable;
    public double vida = 100;

    /**
     * Construtor AUTOMÁTICO.
     * O tamanho (largura/altura) será calculado automaticamente
     * com base no tamanho do 'sNomeImagePNG' e 'Consts.BODY_PROPORTION'.
     */
    public Inimigo(String sNomeImagePNG, double x, double y, LootTable lootTable, double vida) {
        // Chama o construtor automático de Personagem
        super(sNomeImagePNG, x, y);

        this.bMortal = true;
        this.lootTable = lootTable;
        this.vida = vida;
    }

    /**
     * Construtor MANUAL.
     * Permite forçar um tamanho (em pixels) para o inimigo.
     * 
     * @param tamanho O tamanho final (largura e altura) em pixels.
     */
    public Inimigo(String sNomeImagePNG, double x, double y, int tamanho, LootTable lootTable, double vida) {
        
        // Chama o construtor manual de Personagem
        super(sNomeImagePNG, x, y, tamanho, tamanho);

        this.bMortal = true;
        this.lootTable = lootTable;
        this.vida = vida;
    }

    @Override
    public void atualizar(ArrayList<Personagem> personagens, Hero hero) {
        // Lógica de movimento do inimigo
        this.y += 0.02; // (Velocidade em grid, 0.2 era muito rápido)
    }

    @Override
    public void autoDesenho(Graphics g) {
        // 1. Desenha o sprite do Inimigo
        // Converte a posição de grid (centro) para pixels (canto superior esquerdo)
        int telaX = (int) Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);

        // Desenha a imagem (iImage) que foi carregada no construtor do Personagem
        g.drawImage(iImage.getImage(), telaX, telaY, largura, altura, null);

        // 2. Chama a superclasse (Personagem) para desenhar o círculo de debug (hitbox)
        super.autoDesenho(g);
    }

    public LootTable getLootTable() {
        return this.lootTable;
    }

    public double getVida() {
        return this.vida;
    }

    public void takeDamage(double damage) {
        this.vida -= damage;
        System.out.println("Inimigo tomou " + damage + " de dano. Vida restante: " + this.vida);

        if (this.vida < 0) {
            this.vida = 0;
            // Lógica para lidar com a morte do inimigo (ex: drop de loot)
        }
    }
}