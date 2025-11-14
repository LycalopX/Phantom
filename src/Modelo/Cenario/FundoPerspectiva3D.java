package Modelo.Cenario;

import Auxiliar.ConfigMapa;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FundoPerspectiva3D implements ElementoCenario {

    private transient BufferedImage texturaChao;
    private transient BufferedImage texturaParede;

    private double scrollChao = 0;
    private double scrollParede = 0;
    private double speedMultiplier = 1.0;

    private final int horizonteY;
    private final int pontoDeFugaX;

    public FundoPerspectiva3D(BufferedImage chao, BufferedImage parede, int horizonteY) {
        this.texturaChao = chao;
        this.texturaParede = parede;
        this.horizonteY = horizonteY;
        this.pontoDeFugaX = ConfigMapa.LARGURA_TELA / 2;
    }

    public void relinkImages(BufferedImage chao, BufferedImage parede) {
        this.texturaChao = chao;
        this.texturaParede = parede;
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
        double velocidadeAjustada = velocidadeAtualDoFundo * speedMultiplier;
        scrollChao += velocidadeAjustada * 0.5; // Chão se move mais devagar
        scrollParede += velocidadeAjustada * 2; // Paredes passam mais rápido
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (texturaChao == null || texturaParede == null) {
            return;
        }

        for (int y = horizonteY; y < alturaTela; y++) {
            // Fator de perspectiva: 0.0 no horizonte, 1.0 na base da tela.
            float p = ((float) y - horizonteY) / (alturaTela - horizonteY);

            if (p <= 0) continue;

            // Profundidade (z) é inversamente proporcional a 'p'.
            // Quanto mais perto do horizonte, maior o 'z'.
            float z = 1.0f / p;

            // --- Desenho do Chão ---
            int yTexturaChao = (int) (scrollChao + z) % texturaChao.getHeight();
            if (yTexturaChao < 0) yTexturaChao += texturaChao.getHeight();

            // Largura da fatia do chão que vemos na tela
            float larguraFatiaChao = p * larguraTela;
            int xEsqChao = (int) (pontoDeFugaX - larguraFatiaChao);
            int xDirChao = (int) (pontoDeFugaX + larguraFatiaChao);

            g2d.drawImage(texturaChao,
                    xEsqChao, y, xDirChao, y + 1,
                    0, yTexturaChao, texturaChao.getWidth(), yTexturaChao + 1,
                    null);

            // --- Desenho das Paredes ---
            // A coordenada Y na textura da parede também depende da profundidade
            int yTexturaParede = (int) (z) % texturaParede.getHeight();
            if (yTexturaParede < 0) yTexturaParede += texturaParede.getHeight();

            // A coordenada X na textura da parede depende da rolagem horizontal
            int xTexturaParede = (int) (scrollParede) % texturaParede.getWidth();
            if (xTexturaParede < 0) xTexturaParede += texturaParede.getWidth();

            // Desenha a parede esquerda (da borda da tela até a borda do chão)
            g2d.drawImage(texturaParede,
                    0, y, xEsqChao, y + 1,
                    xTexturaParede, yTexturaParede, xTexturaParede + 1, yTexturaParede + 1,
                    null);

            // Desenha a parede direita (da borda do chão até a borda da tela)
            g2d.drawImage(texturaParede,
                    xDirChao, y, larguraTela, y + 1,
                    xTexturaParede, yTexturaParede, xTexturaParede + 1, yTexturaParede + 1,
                    null);
        }
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false; // Este elemento nunca sai da tela
    }
}
