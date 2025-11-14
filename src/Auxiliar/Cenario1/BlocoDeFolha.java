package Auxiliar.Cenario1;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class BlocoDeFolha implements Serializable {

    private double x, y;
    private final int largura, altura;
    private final double velocidadeOriginal;
    private final double rotationAngle; // Em radianos

    private transient BufferedImage imagem;
    private final float opacidadeMaxima, opacidadeMinima;

    /**
     * @brief Construtor principal para um bloco de folha individual, que compõe uma árvore de
     *        parallax.
     */
    public BlocoDeFolha(double x, double y, int largura, int altura, double velocidade, BufferedImage imagem,
            float opacidadeMaxima, float opacidadeMinima, double rotationAngle) {
        this.x = x;
        this.y = y;
        this.imagem = imagem;
        
        this.largura = largura;
        this.altura = altura;
        this.velocidadeOriginal = velocidade;
        this.opacidadeMaxima = opacidadeMaxima;
        this.opacidadeMinima = opacidadeMinima;
        this.rotationAngle = rotationAngle;
    }
    
    /**
     * @brief Construtor para um bloco de folha individual, que compõe uma árvore de
     *        parallax.
     */
    public BlocoDeFolha(double x, double y, int largura, int altura, double velocidade, BufferedImage imagem,
            float opacidadeMaxima, float opacidadeMinima) {
        this(x, y, largura, altura, velocidade, imagem, opacidadeMaxima, opacidadeMinima, 0.0);
    }

    /**
     * @brief Move o bloco verticalmente, ajustando sua velocidade com base em um
     *        fator
     *        que representa a velocidade de rolagem do cenário.
     */
    public void moverComAjuste(double fatorDeAjuste) {
        this.y += this.velocidadeOriginal * fatorDeAjuste;
    }

    /**
     * @brief Desenha o bloco na tela, aplicando um efeito de fade (esmaecimento)
     *        conforme ele se aproxima da parte inferior da tela.
     */
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        Composite originalComposite = g2d.getComposite();
        AffineTransform originalTransform = g2d.getTransform();

        float faixaDeOpacidade = this.opacidadeMaxima - this.opacidadeMinima;
        float pontoFinalFade = alturaTela * 0.85f;
        float fracaoDoFade = (float) this.y / pontoFinalFade;
        fracaoDoFade = Math.max(0.0f, Math.min(1.0f, fracaoDoFade));
        float alphaFinal = this.opacidadeMinima + (fracaoDoFade * faixaDeOpacidade);

        alphaFinal = Math.max(0.0f, Math.min(1.0f, alphaFinal));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFinal));
        
        // Aplica rotação
        g2d.translate(x, y + this.altura);
        g2d.rotate(this.rotationAngle);
        g2d.translate(-x, -(y + this.altura));
        
        g2d.drawImage(imagem, (int) x, (int) y, this.largura, this.altura, null);

        g2d.setComposite(originalComposite);
        g2d.setTransform(originalTransform);
    }

    /**
     * @brief Define a imagem (textura) do bloco, usado na desserialização.
     */
    public void setImagem(BufferedImage imagem) {
        this.imagem = imagem;
    }

    /**
     * @brief Retorna a posição Y (vertical) atual do bloco.
     */
    public double getY() {
        return this.y;
    }
}