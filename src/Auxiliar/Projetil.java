package Auxiliar;

import java.awt.Color;
import java.awt.Graphics2D;

public class Projetil {

    private double x, y; // Posição
    private final double dx, dy; // Vetor de movimento (delta x, delta y)
    private final int tamanho;
    private final Color cor;
    private TipoProjetil tipo;

    /**
     * Cria um novo projétil.
     * @param x Posição inicial X.
     * @param y Posição inicial Y.
     * @param anguloEmGraus O ângulo de movimento (0 = direita, 90 = baixo).
     * @param velocidade A velocidade em pixels por frame.
     * @param tamanho O diâmetro do projétil.
     * @param cor A cor do projétil.
     */
    public Projetil(double x, double y, double anguloEmGraus, double velocidade, int tamanho, Color cor, TipoProjetil tipo) {
        this.x = x;
        this.y = y;
        this.tamanho = tamanho;
        this.cor = cor;
        this.tipo = tipo;

        // Converte o ângulo e a velocidade em um vetor de movimento (dx, dy)
        double anguloEmRadianos = Math.toRadians(anguloEmGraus);
        this.dx = Math.cos(anguloEmRadianos) * velocidade;
        this.dy = Math.sin(anguloEmRadianos) * velocidade;
    }

    // Atualiza a posição do projétil
    public void mover() {
        this.x += this.dx;
        this.y += this.dy;
    }

    // Desenha o projétil na tela
    public void desenhar(Graphics2D g2d) {
        g2d.setColor(this.cor);
        // fillOval é muito eficiente para desenhar círculos simples
        g2d.fillOval((int)this.x, (int)this.y, this.tamanho, this.tamanho);
    }

    // Verifica se o projétil saiu completamente da tela
    public boolean estaForaDaTela(int larguraTela, int alturaTela) {
        return (this.x + this.tamanho < 0 || this.x > larguraTela ||
                this.y + this.tamanho < 0 || this.y > alturaTela);
    }
    
    public TipoProjetil getTipo() {
        return this.tipo;
    }
}