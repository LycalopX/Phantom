/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author alexweber
 */
public class Inimigo extends Personagem{
    
    public Inimigo (String sNomeImagePNG, double x, double y) {
        super(sNomeImagePNG, x, y);
        
        this.bMortal = true;
    }
    
    // Construtor para inimigos com tamanhos diferentes
    public Inimigo(String sNomeImagePNG, double x, double y, int tamanho) {
        // Usa o construtor que calcula a hitbox baseada no tamanho fornecido
        super(sNomeImagePNG, x, y, tamanho, tamanho);
        this.bMortal = true;
    }
    
    public void atualizar() {
        // Lógica de movimento do inimigo
        this.y += 0.2;
    }
}
