package Auxiliar.Cenario4;

import Modelo.Cenario.ElementoCenario;
import Modelo.Cenario.DrawLayer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import Auxiliar.Cenario1.BlocoDeFolha;
import Modelo.Personagem;

public class BambuParallax implements ElementoCenario {

    private List<BlocoDeFolha> blocos = new ArrayList<>();
    private final BlocoDeFolha blocoTopo;
    private final double velocidadeBaseOriginal;
    private double currentSpeedMultiplier = 1.0;
    private final boolean isFlipped;

    public BambuParallax(int x, int y, int larguraBase, double velocidadeBase, BufferedImage stalk, BufferedImage leaves1, BufferedImage leaves2, double rotationAngle, boolean isFlipped) {
        this.velocidadeBaseOriginal = velocidadeBase;
        this.isFlipped = isFlipped;

        // Calcula a altura do caule com base na proporção da imagem
        int heightBambu = (int)(stalk.getHeight() * Personagem.BODY_PROPORTION);
        int widthBambu = (int)(stalk.getWidth() * Personagem.BODY_PROPORTION);

        int heightFolha1 = (int)(leaves1.getHeight() * Personagem.BODY_PROPORTION);
        int widthFolha1 = (int)(leaves1.getWidth() * Personagem.BODY_PROPORTION);

        int heightFolha2 = (int)(leaves2.getHeight() * Personagem.BODY_PROPORTION);
        int widthFolha2 = (int)(leaves2.getWidth() * Personagem.BODY_PROPORTION);
        
        // Camada de base (caule) - usa altura calculada
        System.out.println("Criando BambuParallax na posição: " + x + ", " + y + " | Largura: " + widthBambu + " | Altura: " + heightBambu);
        blocos.add(new BlocoDeFolha(x, y, (int)widthBambu, (int)heightBambu, velocidadeBase, stalk, 1f, 0.9f, rotationAngle));

        // Camada do meio (folhas);
        double velocidadeMedia = velocidadeBase * 1.01;

        int xMedio = x - widthFolha1 / 2;
        int yMedio = y + (int) (heightBambu * 0.2); // Posição relativa à altura do caule

        blocos.add(new BlocoDeFolha(xMedio, yMedio, widthFolha1, heightFolha1, velocidadeMedia, leaves1, 1f, 0.9f, rotationAngle));

        // Camada do topo (folhas)
        double velocidadePequena = velocidadeBase * 1.02;

        int xPequeno = x - widthFolha2 / 2;
        int yPequeno = y - (int) (heightBambu * 0.3); // Posição relativa à altura do caule

        this.blocoTopo = new BlocoDeFolha(xPequeno, yPequeno, widthFolha2, heightFolha2, velocidadePequena, leaves2, 1f, 0.9f, rotationAngle);
        blocos.add(this.blocoTopo);
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void relinkImages(BufferedImage stalk, BufferedImage leaves1, BufferedImage leaves2) {
        if (blocos.size() >= 3) {
            blocos.get(0).setImagem(stalk);
            blocos.get(1).setImagem(leaves1);
            blocos.get(2).setImagem(leaves2);
        }
    }

    @Override
    public void setSpeedMultiplier(double multiplier) {
        this.currentSpeedMultiplier = multiplier;
    }

    public void setImagem(BufferedImage imagem) {
        // Este método pode não ser mais necessário ou pode ser adaptado
    }

    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.BACKGROUND;
    }

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

    @Override
    public void desenhar(Graphics2D g2d, int larguraTela, int alturaTela) {
        for (BlocoDeFolha bloco : blocos) {
            bloco.desenhar(g2d, larguraTela, alturaTela);
        }
    }

    @Override
    public boolean estaForaDaTela(int alturaDaTela) {
        return this.blocoTopo.getY() > alturaDaTela;
    }
}
