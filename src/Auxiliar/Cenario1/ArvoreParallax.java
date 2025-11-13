package Auxiliar.Cenario1;

import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.DrawLayer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ArvoreParallax implements ElementoCenario {

    private final List<BlocoDeFolha> blocos = new ArrayList<>();
    private final BlocoDeFolha blocoTopo;
    private final double velocidadeBaseOriginal;
    private double currentSpeedMultiplier = 1.0;

    /**
     * @brief Construtor da árvore de parallax. Cria uma árvore composta por três
     *        blocos de folhas
     *        com diferentes tamanhos e velocidades para simular profundidade.
     */
    public ArvoreParallax(int x, int y, int tamanhoBase, double velocidadeBase, BufferedImage imagem) {
        this.velocidadeBaseOriginal = velocidadeBase;

        blocos.add(new BlocoDeFolha(x, y, tamanhoBase, tamanhoBase, velocidadeBase, imagem, 0.6f, 0.1f));

        int tamanhoMedio = (int) (tamanhoBase * 0.8);
        double velocidadeMedia = velocidadeBase * 1.01;

        int xMedio = x + (tamanhoBase - tamanhoMedio) / 2;
        int yMedio = y - (int) (tamanhoBase * 0.15);

        blocos.add(new BlocoDeFolha(xMedio, yMedio, tamanhoMedio, tamanhoMedio, velocidadeMedia, imagem, 0.9f, 0.2f));

        int tamanhoPequeno = (int) (tamanhoBase * 0.6);
        double velocidadePequena = velocidadeBase * 1.02;
        int xPequeno = x + (tamanhoBase - tamanhoPequeno) / 2;
        int yPequeno = y - (int) (tamanhoBase * 0.3);

        this.blocoTopo = new BlocoDeFolha(xPequeno, yPequeno, tamanhoPequeno, tamanhoPequeno, velocidadePequena, imagem,
                1f, 0.3f);
        blocos.add(this.blocoTopo);
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        this.currentSpeedMultiplier = multiplier;
    }

    /**
     * @brief Define a imagem para todos os blocos que compõem a árvore.
     */
    public void setImagem(BufferedImage imagem) {
        for (BlocoDeFolha bloco : blocos) {
            bloco.setImagem(imagem);
        }
    }

    /**
     * @brief Retorna a camada de desenho para este elemento.
     * @return Sempre DrawLayer.FOREGROUND.
     */
    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.FOREGROUND;
    }

    /**
     * @brief Move a árvore verticalmente com base na velocidade de rolagem do
     *        fundo,
     *        ajustando a velocidade de cada bloco para manter o efeito de parallax.
     */
    @Override
    public void mover(double velocidadeAtualDoFundo) {
        double fatorDeAjuste = (velocidadeAtualDoFundo / this.velocidadeBaseOriginal) * this.currentSpeedMultiplier;
        if (Double.isNaN(fatorDeAjuste) || Double.isInfinite(fatorDeAjuste)) {
            fatorDeAjuste = 1.0 * this.currentSpeedMultiplier;
        }

        for (BlocoDeFolha bloco : blocos) {
            bloco.moverComAjuste(fatorDeAjuste);
        }
    }

    /**
     * @brief Desenha cada bloco da árvore na tela.
     */
    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        for (BlocoDeFolha bloco : blocos) {
            bloco.desenhar(g2d, larguraTela, alturaTela);
        }
    }

    /**
     * @brief Verifica se o topo da árvore já saiu completamente da tela.
     */
    @Override
    public boolean estaForaDaTela(int alturaDaTela) {
        return this.blocoTopo.getY() > alturaDaTela;
    }
}