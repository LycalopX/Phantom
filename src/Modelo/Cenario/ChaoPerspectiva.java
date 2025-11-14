package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import Auxiliar.ConfigMapa;

public class ChaoPerspectiva implements ElementoCenario {

    private transient BufferedImage textura;
    private double scrollY = 0;
    private double speedMultiplier = 1.0;
    private final double velocidadeRelativa;

    public ChaoPerspectiva(BufferedImage textura, double velocidadeRelativa) {
        this.textura = textura;
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
        this.scrollY += velocidadeAtualDoFundo * this.velocidadeRelativa * this.speedMultiplier;
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (textura == null) return;

        int horizonteY = alturaTela / 2; // Ponto de fuga no meio da tela
        int pontoDeFugaX = larguraTela / 2;

        // Largura do "corredor" no ponto de fuga. Pode ser ajustado.
        float larguraNoHorizonte = 40; 
        // A largura na base da tela. Um valor > 1.0 faz parecer que o chão "sai" das laterais.
        float fatorLarguraNaBase = 2.0f; 

        // Calcula o limite de largura para parar de desenhar
        float larguraMaximaNaBase = larguraTela * fatorLarguraNaBase;
        float limiteLarguraParaDesenhar = larguraMaximaNaBase * 0.15f; // 25% da largura máxima

        for (int y = horizonteY; y < alturaTela; y++) {
            // Fator de perspectiva (p): 0.0 no horizonte, 1.0 na base da tela.
            float p = (float)(y - horizonteY) / (float)(alturaTela - horizonteY);

            // Profundidade (z) é o inverso de p.
            float z = 1.0f / (p + 0.0001f); // Epsilon para evitar divisão por zero

            // Coordenada Y da textura para amostrar, baseada na profundidade e rolagem.
            int yTextura = (int)(scrollY + z) % textura.getHeight();
            if (yTextura < 0) yTextura += textura.getHeight();

            // Largura da fatia do chão é interpolada para formar o trapézio.
            float larguraDaFatia = larguraNoHorizonte + p * (larguraMaximaNaBase - larguraNoHorizonte);
            
            // Condição para parar de desenhar
            if (larguraDaFatia <= limiteLarguraParaDesenhar) {
                continue; // Pula o desenho desta fatia se for muito estreita
            }

            int xEsq = (int)(pontoDeFugaX - larguraDaFatia / 2);
            int xDir = (int)(pontoDeFugaX + larguraDaFatia / 2);

            // Desenha uma única linha da textura, esticada para a largura calculada.
            g2d.drawImage(textura,
                xEsq, y, xDir, y + 1,
                0, yTextura, textura.getWidth(), yTextura + 1,
                null);
        }
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false;
    }
}
