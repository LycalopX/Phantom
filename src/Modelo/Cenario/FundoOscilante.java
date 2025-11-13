package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;

public class FundoOscilante extends FundoInfinito {

    private final float minOpacity;
    private final float maxOpacity;
    private final float oscillationSpeed;
    private int time = 0;

    public FundoOscilante(String id, BufferedImage imagem, double velocidadeRelativa, DrawLayer camada, float initialOpacity, float minOpacity, float maxOpacity, float oscillationSpeed) {
        super(id, imagem, velocidadeRelativa, camada, initialOpacity);
        this.minOpacity = minOpacity;
        this.maxOpacity = maxOpacity;
        this.oscillationSpeed = oscillationSpeed;
    }

    @Override
    public void mover(double velocidadeBase) {
        super.mover(velocidadeBase);
        time++;
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (imagem == null) return;

        float currentOpacity = (float) (minOpacity + (maxOpacity - minOpacity) * (0.5 * (1 + Math.sin(time / oscillationSpeed))));

        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentOpacity));

        int yPos = (int) (this.y % alturaTela);

        g2d.drawImage(this.imagem, 0, yPos, larguraTela, alturaTela, null);
        g2d.drawImage(this.imagem, 0, yPos - alturaTela, larguraTela, alturaTela, null);

        g2d.setComposite(originalComposite);
    }
}
