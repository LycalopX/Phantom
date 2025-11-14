package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParedeVertical implements ElementoCenario {

    private transient BufferedImage textura;
    private double scrollX = 0;
    private double speedMultiplier = 1.0;
    private final double velocidadeRelativa;
    private final boolean isParedeEsquerda;

    public ParedeVertical(BufferedImage textura, boolean isParedeEsquerda, double velocidadeRelativa) {
        this.textura = textura;
        this.isParedeEsquerda = isParedeEsquerda;
        this.velocidadeRelativa = velocidadeRelativa;
    }

    public void relinkImage(BufferedImage textura) {
        this.textura = textura;
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
        if (textura == null) return;

        int pontoDeFugaX = larguraTela / 2;
        int horizonteY = alturaTela / 2;

        float alturaNoPontoDeFuga = 40;
        float fatorAlturaNaBorda = 1.5f; // Efeito de distorção reduzido

        float alturaMaximaNaBorda = alturaTela * fatorAlturaNaBorda;
        float limiteAlturaParaDesenhar = alturaMaximaNaBorda * 0.15f;

        int startX = isParedeEsquerda ? 0 : pontoDeFugaX;
        int endX = isParedeEsquerda ? pontoDeFugaX : larguraTela;

        for (int x = startX; x < endX; x++) {
            float p;
            if (isParedeEsquerda) {
                p = (float)(pontoDeFugaX - x) / (float)pontoDeFugaX;
            } else {
                p = (float)(x - pontoDeFugaX) / (float)(larguraTela - pontoDeFugaX);
            }

            float alturaDaFatia = alturaNoPontoDeFuga + p * (alturaMaximaNaBorda - alturaNoPontoDeFuga);
            
            if (alturaDaFatia <= limiteAlturaParaDesenhar) {
                continue;
            }

            float z = 1.0f / (p + 0.0001f);

            int xTextura = (int)(scrollX + z) % textura.getWidth();
            if (xTextura < 0) xTextura += textura.getWidth();
            
            int yCima = (int)(horizonteY - alturaDaFatia / 2);
            int yBaixo = (int)(horizonteY + alturaDaFatia / 2);

            g2d.drawImage(textura,
                x, yCima, x + 1, yBaixo,
                xTextura, 0, xTextura + 1, textura.getHeight(),
                null);
        }
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false;
    }
}
