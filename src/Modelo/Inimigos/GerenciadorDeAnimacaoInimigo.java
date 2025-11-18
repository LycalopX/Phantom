package Modelo.Inimigos;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;

/**
 * @brief Gerencia as animações para os inimigos.
 * 
 * Esta classe é responsável por carregar sequências de sprites a partir de um
 * spritesheet, gerenciar a transição entre os frames e fornecer a imagem
 * correta com base no estado de animação do inimigo (IDLE ou STRAFING).
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
    private boolean holdLastStrafingFrame;
    private AnimationState currentState = AnimationState.IDLE;

    public GerenciadorDeAnimacaoInimigo(String spritesheetPath, int spriteWidth, int spriteHeight, int gap,
            int idleFrames, int movingFrames, boolean resize, int newWidth, int newHeight,
            boolean holdLastStrafingFrame) {
        this.maxFrames = idleFrames; 
        this.iImagesIdle = carregarFramesDoSpriteSheet(spritesheetPath, 0, idleFrames, spriteWidth, spriteHeight, gap,
                resize, newWidth, newHeight);
        this.iImagesStrafing = carregarFramesDoSpriteSheet(spritesheetPath, idleFrames, movingFrames, spriteWidth,
                spriteHeight, gap, resize, newWidth, newHeight);
        this.holdLastStrafingFrame = holdLastStrafingFrame;
    }

    /**
     * @brief Atualiza o frame atual da animação com base no estado e em um delay.
     * @param state O estado de animação atual (IDLE ou STRAFING).
     */
    public void atualizar(AnimationState state) {
        if (state != currentState) {
            frameAtual = 0; 
            delayFrame = 0; 
            currentState = state; 
        }

        delayFrame++;
        if (delayFrame >= DELAY) {
            if (state == AnimationState.STRAFING && holdLastStrafingFrame) {
                if (frameAtual < maxFrames - 1) {
                    frameAtual++;
                }
            } else {
                frameAtual = (frameAtual + 1) % maxFrames;
            }
            delayFrame = 0;
        }
    }

    public void resetFrame() {
        this.frameAtual = 0;
        this.delayFrame = 0;
    }

    /**
     * @brief Retorna a imagem do frame atual com base no estado de animação.
     */
    public ImageIcon getImagemAtual(AnimationState state) {
        if (state == AnimationState.STRAFING) {
            return iImagesStrafing[frameAtual];
        } else {
            return iImagesIdle[frameAtual];
        }
    }

    
    
    

    
    
    
    /**
     * @brief Carrega e recorta uma sequência de frames a partir de um spritesheet.
     * @param nomeArquivo O caminho para o arquivo do spritesheet.
     * @param startFrame O índice do primeiro frame a ser recortado.
     * @param numFrames O número de frames a serem recortados.
     * @param spriteWidth A largura de um único sprite.
     * @param spriteHeight A altura de um único sprite.
     * @param gap O espaço (em pixels) entre cada sprite no arquivo.
     * @param resize Se os frames devem ser redimensionados.
     * @param newWidth A nova largura se `resize` for true.
     * @param newHeight A nova altura se `resize` for true.
     * @return Um array de `ImageIcon` contendo os frames da animação.
     */
    private ImageIcon[] carregarFramesDoSpriteSheet(String nomeArquivo, int startFrame, int numFrames, int spriteWidth,
            int spriteHeight, int gap, boolean resize, int newWidth, int newHeight) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(nomeArquivo);
            if (imgURL == null) {
                System.out.println("Erro ao carregar spritesheet: " + nomeArquivo + " não encontrado.");
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
