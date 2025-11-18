package Auxiliar.Cenario1;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

/**
 * @brief Representa um único bloco de folhas, usado como componente para
 *        construir
 *        elementos de cenário mais complexos, como a `ArvoreParallax`.
 */
public class BlocoDeFolha implements Serializable {

    private double x, y;
    private final int largura, altura;
    private final double velocidadeOriginal;
    private final double rotationAngle;

    private transient BufferedImage imagem;
    private final float opacidadeMaxima, opacidadeMinima;

    /**
     * @brief Construtor principal para um bloco de folha individual.
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
     * @brief Construtor secundário para um bloco de folha sem rotação.
     */
    public BlocoDeFolha(double x, double y, int largura, int altura, double velocidade, BufferedImage imagem,
            float opacidadeMaxima, float opacidadeMinima) {
        this(x, y, largura, altura, velocidade, imagem, opacidadeMaxima, opacidadeMinima, 0.0);
    }

    /**
     * @brief Move o bloco verticalmente com base em um fator de ajuste.
     * 
     *        Este método permite que a velocidade do bloco seja sincronizada com a
     *        velocidade de rolagem geral do cenário, mantendo o efeito de parallax.
     */
    public void moverComAjuste(double fatorDeAjuste) {
        this.y += this.velocidadeOriginal * fatorDeAjuste;
    }

    /**
     * @brief Desenha o bloco na tela com um efeito de fade (esmaecimento).
     * 
     *        A opacidade do bloco é calculada com base em sua posição vertical,
     *        tornando-o mais transparente à medida que se aproxima da parte
     *        inferior da tela.
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

    public double getY() {
        return this.y;
    }
}