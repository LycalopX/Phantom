package Modelo.Inimigos;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import Modelo.Personagem;

/**
 * @brief Gerencia as animações para os inimigos, carregando e alternando os sprites.
 */
public class GerenciadorDeAnimacaoInimigo {

    public enum AnimationState {
        IDLE,
        STRAFING
    }

    private static ImageIcon[] iImagesIdle;
    private static ImageIcon[] iImagesStrafing;

    private static final int MAX_FRAMES = 4;
    private static final int DELAY = 5;

    private int frameAtual = 0;
    private int delayFrame = 0;

    /**
     * @brief Construtor do gerenciador. Carrega os spritesheets do inimigo se ainda não foram carregados.
     */
    public GerenciadorDeAnimacaoInimigo() {
        if (iImagesIdle == null) {
            int size = (int) (30.0 * Personagem.BODY_PROPORTION);
            iImagesIdle = carregarFramesDoSpriteSheet("imgs/inimigos/enemy1_spreadsheet.png", 0, MAX_FRAMES, size);
            iImagesStrafing = carregarFramesDoSpriteSheet("imgs/inimigos/enemy1_spreadsheet.png", MAX_FRAMES, MAX_FRAMES, size);
        }
    }

    /**
     * @brief Atualiza o frame atual da animação com base em um delay.
     */
    public void atualizar() {
        delayFrame++;
        if (delayFrame >= DELAY) {
            frameAtual = (frameAtual + 1) % MAX_FRAMES;
            delayFrame = 0;
        }
    }

    /**
     * @brief Retorna a imagem do frame atual com base no estado de animação (parado ou movendo).
     */
    public ImageIcon getImagemAtual(AnimationState state) {
        if (state == AnimationState.STRAFING) {
            return iImagesStrafing[frameAtual];
        } else {
            return iImagesIdle[frameAtual];
        }
    }

    /**
     * @brief Carrega e recorta frames de uma imagem de spritesheet.
     */
    private ImageIcon[] carregarFramesDoSpriteSheet(String nomeArquivo, int startFrame, int numFrames, int size) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(nomeArquivo);
            if (imgURL == null) {
                System.err.println("Spritesheet não encontrado: " + nomeArquivo);
                return null;
            }
            BufferedImage spriteSheet = ImageIO.read(imgURL);

            ImageIcon[] frames = new ImageIcon[numFrames];
            final int spriteSize = 30;
            final int gap = 2;

            for (int i = 0; i < numFrames; i++) {
                int x = (startFrame + i) * (spriteSize + gap);
                BufferedImage frameImg = spriteSheet.getSubimage(x, 0, spriteSize, spriteSize);
                
                BufferedImage resizedImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics g = resizedImg.createGraphics();
                g.drawImage(frameImg, 0, 0, size, size, null);
                g.dispose();

                frames[i] = new ImageIcon(resizedImg);
            }
            return frames;
        } catch (Exception ex) {
            System.out.println("Erro ao carregar/recortar spritesheet: " + nomeArquivo + " -> " + ex.getMessage());
            return null;
        }
    }
}
