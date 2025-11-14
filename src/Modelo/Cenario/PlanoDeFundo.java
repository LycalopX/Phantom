package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PlanoDeFundo implements ElementoCenario {

    private transient BufferedImage textura;
    private final String id;
    private final Rectangle bounds; // Onde na tela este plano é desenhado
    private final double shearX;
    private final double shearY;
    private final double velocidadeRelativa;

    private double scrollX = 0;
    private double scrollY = 0;
    private double speedMultiplier = 1.0;

    public PlanoDeFundo(String id, BufferedImage textura, Rectangle bounds, double shearX, double shearY, double velocidadeRelativa) {
        this.id = id;
        this.textura = textura;
        this.bounds = bounds;
        this.shearX = shearX;
        this.shearY = shearY;
        this.velocidadeRelativa = velocidadeRelativa;
    }

    public void relinkImage(BufferedImage textura) {
        this.textura = textura;
    }
    
    public String getId() {
        return this.id;
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
        double velocidadeAjustada = velocidadeAtualDoFundo * this.velocidadeRelativa * this.speedMultiplier;
        
        if (id.equals("chao")) {
            this.scrollY += velocidadeAjustada;
        } else if (id.startsWith("parede_")) {
            this.scrollX += velocidadeAjustada;
        }
    }

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (textura == null) return;

        Graphics2D g = (Graphics2D) g2d.create();
        
        // Salva a transformação original
        AffineTransform oldTransform = g.getTransform();

        // Cria a transformação de shear e a aplica
        AffineTransform shearTransform = new AffineTransform();
        shearTransform.translate(bounds.x, bounds.y);
        shearTransform.shear(shearX, shearY);
        g.transform(shearTransform);

        // Define a área de clip para não desenhar fora dos limites
        // O clip é aplicado no espaço transformado, então usamos (0,0)
        g.setClip(0, 0, bounds.width, bounds.height);

        // Calcula o offset da rolagem
        int offsetX = (int) (this.scrollX % textura.getWidth());
        int offsetY = (int) (this.scrollY % textura.getHeight());

        // Desenha a textura duas vezes em cada eixo para um loop perfeito
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                g.drawImage(textura, (i * textura.getWidth()) - offsetX, (j * textura.getHeight()) - offsetY, null);
            }
        }
        
        // Restaura a transformação original
        g.setTransform(oldTransform);
        g.dispose();
    }

    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false;
    }
}
