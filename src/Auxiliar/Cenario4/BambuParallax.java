package Auxiliar.Cenario4;

import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.DrawLayer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BambuParallax implements ElementoCenario {

    private double x, y;
    private double initialWidth, initialHeight;
    private double scrollSpeed;
    private double currentSpeedMultiplier = 1.0;

    private transient BufferedImage stalkImage;
    private transient BufferedImage leafImage;

    private final double minScale = 0.2;
    private final double maxScale = 2.5;

    public BambuParallax(double x, double y, double size, double scrollSpeed, BufferedImage stalkImage, BufferedImage leafImage) {
        this.x = x;
        this.y = y;
        this.initialWidth = size;
        this.initialHeight = size * (stalkImage.getHeight() / (double) stalkImage.getWidth()); // Maintain aspect ratio
        this.scrollSpeed = scrollSpeed;
        this.stalkImage = stalkImage;
        this.leafImage = leafImage;
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        this.currentSpeedMultiplier = multiplier;
    }

    @Override
    public void mover(double velocidadeAtualDoFundo) {
        this.y += velocidadeAtualDoFundo * this.scrollSpeed * this.currentSpeedMultiplier;
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (stalkImage == null || leafImage == null) return;

        // Calculate scale based on Y position
        double scaleFactor = minScale + (maxScale - minScale) * (this.y / alturaTela);
        scaleFactor = Math.max(minScale, scaleFactor); // Clamp scale

        double currentWidth = initialWidth * scaleFactor;
        double currentHeight = initialHeight * scaleFactor;

        // Draw stalk
        g2d.drawImage(stalkImage, (int) (x - currentWidth / 2), (int) y, (int) currentWidth, (int) currentHeight, null);

        // Draw leaves on top
        g2d.drawImage(leafImage, (int) (x - currentWidth / 2), (int) y, (int) currentWidth, (int) currentHeight, null);
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return this.y > alturaTela;
    }

    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.FOREGROUND; // Bamboos are in the foreground
    }

    public void setImages(BufferedImage stalk, BufferedImage leaves) {
        this.stalkImage = stalk;
        this.leafImage = leaves;
    }
}
