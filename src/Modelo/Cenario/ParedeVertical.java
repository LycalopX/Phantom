package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParedeVertical implements ElementoCenario {

    private transient BufferedImage textura;
    private double scrollX = 0;
    private double speedMultiplier = 1.0;
    private final double velocidadeRelativa;
    private final boolean isParedeEsquerda;
    private final float alturaNoPontoDeFuga;
    private final float fatorAlturaNaBorda;
    private int translacaoX;
    private int translacaoY;

    public ParedeVertical(BufferedImage textura, boolean isParedeEsquerda, double velocidadeRelativa,
            float alturaNoPontoDeFuga, float fatorAlturaNaBorda, int translacaoX, int translacaoY) {
        this.textura = textura;
        this.isParedeEsquerda = isParedeEsquerda;
        this.velocidadeRelativa = velocidadeRelativa;
        this.alturaNoPontoDeFuga = alturaNoPontoDeFuga;
        this.fatorAlturaNaBorda = fatorAlturaNaBorda;
        this.translacaoX = translacaoX;
        this.translacaoY = translacaoY;
    }

    public void relinkImage(BufferedImage textura) {
        this.textura = textura;
    }

    public void setTranslacaoX(int translacaoX) {
        this.translacaoX = translacaoX;
    }

    public void setTranslacaoY(int translacaoY) {
        this.translacaoY = translacaoY;
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = multiplier;
    }

    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.BACKGROUND;
    }

    @Override
    public void mover(double velocidadeAtualDoFundo) {
        this.scrollX += velocidadeAtualDoFundo * this.velocidadeRelativa * this.speedMultiplier;
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (textura == null)
            return;

        int pontoDeFugaX = larguraTela / 2;
        int horizonteY = alturaTela / 2;

        float alturaMaximaNaBorda = alturaTela * fatorAlturaNaBorda;
        float limiteAlturaParaDesenhar = alturaMaximaNaBorda * 0.2f;

        int startX = isParedeEsquerda ? 0 : pontoDeFugaX;
        int endX = isParedeEsquerda ? pontoDeFugaX : larguraTela;

        for (int x = startX; x < endX; x++) {
            float p;
            if (isParedeEsquerda) {
                p = (float) (pontoDeFugaX - x) / (float) pontoDeFugaX;
            } else {
                p = (float) (x - pontoDeFugaX) / (float) (larguraTela - pontoDeFugaX);
            }

            float alturaDaFatia = alturaNoPontoDeFuga + p * (alturaMaximaNaBorda - alturaNoPontoDeFuga);

            if (alturaDaFatia <= limiteAlturaParaDesenhar) {
                continue;
            }

            float z = 1.0f / (p + 0.0001f);

            final float TEXTURE_SCALE = 80.0f;
            int xTextura = (int) ((scrollX + z) * TEXTURE_SCALE) % textura.getWidth();
            if (xTextura < 0)
                xTextura += textura.getWidth();

            int yCima = (int) (horizonteY - alturaDaFatia / 2) + translacaoY;
            int yBaixo = (int) (horizonteY + alturaDaFatia / 2) + translacaoY;

            g2d.drawImage(textura,
                    x + translacaoX, yCima, x + 1 + translacaoX, yBaixo,
                    xTextura, 0, xTextura + 1, textura.getHeight(),
                    null);

        }
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false;
    }
}