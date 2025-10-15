package Auxiliar;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ArvoreParallax {

    private final List<BlocoDeFolha> blocos = new ArrayList<>();
    private final BlocoDeFolha blocoTopo;
    private final double velocidadeBaseOriginal; // Guarda a velocidade com que foi criada

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
        
        this.blocoTopo = new BlocoDeFolha(xPequeno, yPequeno, tamanhoPequeno, tamanhoPequeno, velocidadePequena, imagem, 1f, 0.3f);
        blocos.add(this.blocoTopo);
    }

    // MUDANÇA: O método mover agora aceita a velocidade atual do fundo
    // para que a velocidade relativa das árvores permaneça consistente.
    public void mover(double velocidadeAtualDoFundo) {
        // Calcula a diferença entre a velocidade atual e a velocidade com que a árvore foi criada
        double fatorDeAjuste = velocidadeAtualDoFundo / this.velocidadeBaseOriginal;
        if (Double.isNaN(fatorDeAjuste) || Double.isInfinite(fatorDeAjuste)) {
            fatorDeAjuste = 1.0; // Evita divisão por zero se a velocidade original for 0
        }

        for (BlocoDeFolha bloco : blocos) {
            bloco.moverComAjuste(fatorDeAjuste);
        }
    }

    public void desenhar(Graphics2D g2d, int alturaDaTela) {
        for (BlocoDeFolha bloco : blocos) {
            bloco.desenhar(g2d, alturaDaTela);
        }
    }
    
    public boolean estaForaDaTela(int alturaDaTela) {
        return this.blocoTopo.getY() > alturaDaTela;
    }
}