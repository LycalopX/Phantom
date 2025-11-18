package Modelo.Cenario;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Composite;
import java.awt.AlphaComposite;

/**
 * @brief Elemento de cenário que cria um fundo com rolagem vertical infinita.
 * 
 * Ideal para camadas de background que precisam se repetir continuamente.
 */
public class FundoInfinito implements ElementoCenario {

    protected transient BufferedImage imagem;
    protected double y;
    private final double velocidadeRelativa;
    private final DrawLayer camada;
    private final String id;
    protected float opacidade;
    private double speedMultiplier = 1.0;

    /**
     * @brief Construtor do FundoInfinito.
     * @param id Um identificador único para este elemento.
     * @param imagem A imagem a ser desenhada.
     * @param velocidadeRelativa Multiplicador de velocidade para efeito de parallax.
     * @param camada A camada de renderização (BACKGROUND ou FOREGROUND).
     * @param opacidade A opacidade do elemento (0.0f a 1.0f).
     */
    public FundoInfinito(String id, BufferedImage imagem, double velocidadeRelativa, DrawLayer camada, float opacidade) {
        this.id = id;
        this.imagem = imagem;
        this.velocidadeRelativa = velocidadeRelativa;
        this.camada = camada;
        this.y = 0;
        this.opacidade = opacidade;
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = multiplier;
    }

    /**
     * @brief Define a imagem (textura) do fundo, usado na desserialização.
     */
    public void setImagem(BufferedImage imagem) {
        this.imagem = imagem;
    }

    
    public String getId() {
        return this.id;
    }

    
    public double getVelocidadeRelativa() {
        return this.velocidadeRelativa;
    }

    
    @Override
    public DrawLayer getDrawLayer() {
        return this.camada;
    }

    /**
     * @brief Move o fundo verticalmente com base na velocidade de rolagem do jogo.
     */
    @Override
    public void mover(double velocidadeBase) {
        this.y += velocidadeBase * this.velocidadeRelativa * this.speedMultiplier;
    }

    /**
     * @brief Desenha a imagem de fundo, repetindo-a para criar a ilusão de infinito.
     * 
     * O efeito de rolagem infinita é alcançado desenhando a imagem duas vezes.
     * A posição Y é calculada com o operador de módulo (`%`) para que, quando
     * uma imagem saia completamente da tela, ela seja reposicionada acima,
     * criando um loop contínuo.
     */
    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        if (imagem == null) return;

        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.opacidade));

        int yPos = (int) (this.y % alturaTela);

        g2d.drawImage(this.imagem, 0, yPos, larguraTela, alturaTela, null);
        g2d.drawImage(this.imagem, 0, yPos - alturaTela, larguraTela, alturaTela, null);

        g2d.setComposite(originalComposite);
    }

    /**
     * @brief Um fundo infinito nunca está "fora da tela".
     * @return Sempre falso.
     */
    @Override
    public boolean estaForaDaTela(int alturaTela) {
        return false;
    }
}