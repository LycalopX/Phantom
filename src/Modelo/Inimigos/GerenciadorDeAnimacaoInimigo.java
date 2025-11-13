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

    private ImageIcon[] iImagesIdle;
    private ImageIcon[] iImagesStrafing;

    private final int maxFrames;
    private static final int DELAY = 5;

    private int frameAtual = 0;
    private int delayFrame = 0;

    /**
     * @brief Construtor do gerenciador. Carrega os spritesheets do inimigo se ainda não foram carregados.
     */
    public GerenciadorDeAnimacaoInimigo() {
        this(
            "imgs/inimigos/enemy1_spreadsheet.png",
            30, 30, 2, 4, 4,
            true, 
            (int) (30.0 * Personagem.BODY_PROPORTION),
            (int) (30.0 * Personagem.BODY_PROPORTION)
        );
    }

    public GerenciadorDeAnimacaoInimigo(String spritesheetPath, int spriteWidth, int spriteHeight, int gap, int idleFrames, int movingFrames, boolean resize, int newWidth, int newHeight) {
        this.maxFrames = idleFrames; // Assuming idle and moving have the same number of frames
        this.iImagesIdle = carregarFramesDoSpriteSheet(spritesheetPath, 0, idleFrames, spriteWidth, spriteHeight, gap, resize, newWidth, newHeight);
        this.iImagesStrafing = carregarFramesDoSpriteSheet(spritesheetPath, idleFrames, movingFrames, spriteWidth, spriteHeight, gap, resize, newWidth, newHeight);
    }

    /**
     * @brief Atualiza o frame atual da animação com base em um delay.
     */
    public void atualizar() {
        delayFrame++;
        if (delayFrame >= DELAY) {
            frameAtual = (frameAtual + 1) % maxFrames;
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

    // 35x60 - boss 1
    // 13 de distancia

    // 43x61 - boss 2
    // 4 de distancia

    /**
     * @brief Carrega e recorta frames de uma imagem de spritesheet.
     */
    private ImageIcon[] carregarFramesDoSpriteSheet(String nomeArquivo, int startFrame, int numFrames, int spriteWidth, int spriteHeight, int gap, boolean resize, int newWidth, int newHeight) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(nomeArquivo);
            if (imgURL == null) {
                System.err.println("Spritesheet não encontrado: " + nomeArquivo);
                return null;
            }
            BufferedImage spriteSheet = ImageIO.read(imgURL);

            ImageIcon[] frames = new ImageIcon[numFrames];

            for (int i = 0; i < numFrames; i++) {
                int x = (startFrame + i) * (spriteWidth + gap);
                BufferedImage frameImg = spriteSheet.getSubimage(x, 0, spriteWidth, spriteHeight);
                
                if (resize) {
                    BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics g = resizedImg.createGraphics();
                    g.drawImage(frameImg, 0, 0, newWidth, newHeight, null);
                    g.dispose();
                    frames[i] = new ImageIcon(resizedImg);
                } else {
                    frames[i] = new ImageIcon(frameImg);
                }
            }
            return frames;
        } catch (Exception ex) {
            System.out.println("Erro ao carregar/recortar spritesheet: " + nomeArquivo + " -> " + ex.getMessage());
            return null;
        }
    }
}
