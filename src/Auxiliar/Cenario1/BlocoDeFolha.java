package Auxiliar.Cenario1;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.io.Serializable;

public class BlocoDeFolha implements Serializable {

    private double x, y;
    private final int largura, altura;
    private final double velocidadeOriginal;

    private transient BufferedImage imagem;
    private final float opacidadeMaxima, opacidadeMinima;

    /**
     * @brief Construtor para um bloco de folha individual, que compõe uma árvore de
     *        parallax.
     */
    public BlocoDeFolha(double x, double y, int largura, int altura, double velocidade, BufferedImage imagem,
            float opacidadeMaxima, float opacidadeMinima) {
        this.x = x;
        this.y = y;
        this.imagem = imagem;
        
        this.largura = largura;
        this.altura = altura;
        this.velocidadeOriginal = velocidade;
        this.opacidadeMaxima = opacidadeMaxima;
        this.opacidadeMinima = opacidadeMinima;
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
    public void desenhar(Graphics2D g2d, int alturaDaTela) {
        Composite originalComposite = g2d.getComposite();

        float faixaDeOpacidade = this.opacidadeMaxima - this.opacidadeMinima;
        float pontoFinalFade = alturaDaTela * 0.85f;
        float fracaoDoFade = (float) this.y / pontoFinalFade;
        fracaoDoFade = Math.max(0.0f, Math.min(1.0f, fracaoDoFade));
        float alphaFinal = this.opacidadeMinima + (fracaoDoFade * faixaDeOpacidade);

        alphaFinal = Math.max(0.0f, Math.min(1.0f, alphaFinal));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaFinal));
        g2d.drawImage(imagem, (int) x, (int) y, this.largura, this.altura, null);
        g2d.setComposite(originalComposite);
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