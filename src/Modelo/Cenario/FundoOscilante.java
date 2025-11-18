package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;

/**
 * @brief Estende `FundoInfinito` para adicionar um efeito de oscilação na opacidade.
 * 
 * Cria um efeito de "pulsar" ou "piscar" para uma camada de fundo,
 * variando sua transparência ao longo do tempo usando uma função seno.
 */
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
        time++; // Incrementa o tempo para a função de oscilação
    }

    /**
     * @brief Desenha o fundo com a opacidade oscilante.
     * 
     * A opacidade atual é calculada a cada frame usando uma função seno,
     * que mapeia o tempo para um valor entre `minOpacity` and `maxOpacity`.
     */
    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (imagem == null) return;

        // Calcula a opacidade atual baseada na função seno.
        float currentOpacity = (float) (minOpacity + (maxOpacity - minOpacity) * (0.5 * (1 + Math.sin(time / oscillationSpeed))));

        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentOpacity));

        // Reutiliza a lógica de desenho de FundoInfinito.
        int yPos = (int) (this.y % alturaTela);
        g2d.drawImage(this.imagem, 0, yPos, larguraTela, alturaTela, null);
        g2d.drawImage(this.imagem, 0, yPos - alturaTela, larguraTela, alturaTela, null);

        g2d.setComposite(originalComposite);
    }
}
