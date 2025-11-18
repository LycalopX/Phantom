package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @brief Elemento de cenário que renderiza um chão texturizado com efeito de perspectiva 3D.
 * 
 * Este efeito é simulado desenhando a textura do chão linha por linha (scanline),
 * esticando cada linha horizontalmente para criar a ilusão de um plano que se
 * afasta em direção a um ponto de fuga no horizonte.
 */
public class ChaoPerspectiva implements ElementoCenario {

    private transient BufferedImage textura;
    private double scrollY = 0;
    private double speedMultiplier = 1.0;
    private final double velocidadeRelativa;
    private final float larguraNoHorizonte;
    private final float fatorLarguraNaBase;

    public ChaoPerspectiva(BufferedImage textura, double velocidadeRelativa, float larguraNoHorizonte,
            float fatorLarguraNaBase) {
        this.textura = textura;
        this.velocidadeRelativa = velocidadeRelativa;
        this.larguraNoHorizonte = larguraNoHorizonte;
        this.fatorLarguraNaBase = fatorLarguraNaBase;
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
        this.scrollY += velocidadeAtualDoFundo * this.velocidadeRelativa * this.speedMultiplier;
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (textura == null)
            return;

        int horizonteY = alturaTela / 2; 
        int pontoDeFugaX = larguraTela / 2;

        
        float larguraMaximaNaBase = larguraTela * fatorLarguraNaBase;
        float limiteLarguraParaDesenhar = larguraMaximaNaBase * 0.07f; 

        for (int y = horizonteY; y < alturaTela; y++) {
            
            float p = (float) (y - horizonteY) / (float) (alturaTela - horizonteY);

            
            float z = 1.0f / (p + 0.0001f); 

            
            final float TEXTURE_SCALE = 80.0f;
            int yTextura = (int) ((scrollY + z) * TEXTURE_SCALE) % textura.getHeight();
            if (yTextura < 0)
                yTextura += textura.getHeight();

            
            float larguraDaFatia = larguraNoHorizonte + p * (larguraMaximaNaBase - larguraNoHorizonte);

            
            if (larguraDaFatia <= limiteLarguraParaDesenhar) {
                continue; 
            }

            int xEsq = (int) (pontoDeFugaX - larguraDaFatia / 2);
            int xDir = (int) (pontoDeFugaX + larguraDaFatia / 2);

            
            g2d.drawImage(textura,
                    xEsq, y, xDir, y + 1,
                    0, yTextura, textura.getWidth(), yTextura + 1,
                    null);

        }
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false; // O chão está sempre visível.
    }
}
