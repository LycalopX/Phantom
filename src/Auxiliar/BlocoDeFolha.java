package Auxiliar;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.io.Serializable;

public class BlocoDeFolha implements Serializable {

    private double x, y;
    private final int largura, altura;
    private final double velocidadeOriginal; // MUDANÇA: Renomeado de 'velocidade'
    
    private transient BufferedImage imagem;
    private final float opacidadeMaxima, opacidadeMinima;

    public BlocoDeFolha(double x, double y, int largura, int altura, double velocidade, BufferedImage imagem, float opacidadeMaxima, float opacidadeMinima) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.velocidadeOriginal = velocidade; // MUDANÇA: Atribuído à nova variável
        this.imagem = imagem;
        this.opacidadeMaxima = opacidadeMaxima;
        this.opacidadeMinima = opacidadeMinima;
    }

    // MUDANÇA: Novo método que ajusta a velocidade do bloco com base na velocidade do fundo
    public void moverComAjuste(double fatorDeAjuste) {
        this.y += this.velocidadeOriginal * fatorDeAjuste;
    }

    public void desenhar(Graphics2D g2d, int alturaDaTela) {
        Composite originalComposite = g2d.getComposite();
        float faixaDeOpacidade = this.opacidadeMaxima - this.opacidadeMinima;
        float pontoFinalFade = alturaDaTela * 0.85f;
        float fracaoDoFade = (float)this.y / pontoFinalFade;
        fracaoDoFade = Math.max(0.0f, Math.min(1.0f, fracaoDoFade));
        float alphaFinal = this.opacidadeMinima + (fracaoDoFade * faixaDeOpacidade);
        alphaFinal = Math.max(0.0f, Math.min(1.0f, alphaFinal));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFinal));
        g2d.drawImage(imagem, (int)x, (int)y, this.largura, this.altura, null);
        g2d.setComposite(originalComposite);
    }
    
    public void setImagem(BufferedImage imagem) {
        this.imagem = imagem;
    }
    
    public double getY() {
        return this.y;
    }
}