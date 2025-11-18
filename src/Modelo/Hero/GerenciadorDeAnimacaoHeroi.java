package Modelo.Hero;

import Modelo.Personagem;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @brief Gerencia as animações e os sprites do herói.
 * 
 * Esta classe controla a transição entre diferentes estados de animação
 * (parado, movendo), carrega os sprites correspondentes de arquivos e
 * spritesheets, e gerencia os efeitos visuais da hitbox de foco.
 */
public class GerenciadorDeAnimacaoHeroi implements java.io.Serializable {
    private ImageIcon[] iImagesStrafingEsquerda;
    private ImageIcon[] iImagesIdle;
    private ImageIcon[] iImagesStrafingMax;
    private ImageIcon imagemHitboxFoco;

    private double anguloRotacaoHitbox = 0;
    private int frameCounter = 0;

    private enum FadeState { FADE_IN, FADE_OUT, VISIBLE, HIDDEN }
    private FadeState hitboxFadeState = FadeState.HIDDEN;
    private float hitboxAlpha = 0.0f;
    private static final float FADE_SPEED = 0.05f;

    private static final int MAX_FRAMES_STRAFING = 4;
    private static final int DELAY_STRAFING = 4;
    private static final int MAX_FRAMES_STRAFING_MAX = 4;
    private static final int DELAY_STRAFING_MAX = 5;
    private static final int MAX_FRAMES_IDLE = 4;
    private static final int DELAY_IDLE = 8;

    private int frameAtualStrafing = 0, delayFrameStrafing = 0;
    private int frameAtualStrafingMax = 0, delayFrameStrafingMax = 0;
    private int frameAtualIdle = 0, delayFrameIdle = 0;

    /**
     * @brief Construtor que carrega todos os sprites para as animações do herói.
     */
    public GerenciadorDeAnimacaoHeroi(int largura, int altura) {
        imagemHitboxFoco = carregarImagem("hero/sprite_hitbox.png", 64, 61);
        iImagesStrafingEsquerda = new ImageIcon[MAX_FRAMES_STRAFING];
        for (int i = 0; i < MAX_FRAMES_STRAFING; i++) {
            iImagesStrafingEsquerda[i] = carregarImagem("hero/hero_s" + (i + 1) + ".png", largura, altura);
        }
        iImagesIdle = carregarFramesDoSpriteSheet("hero/hero_standing_still.png", MAX_FRAMES_IDLE, largura, altura);
        iImagesStrafingMax = carregarFramesDoSpriteSheet("hero/hero_strafing_max.png", MAX_FRAMES_STRAFING_MAX, largura, altura);
    }

    /**
     * @brief Atualiza o estado da animação com base no estado atual do herói.
     * 
     * Gerencia a progressão dos frames para cada tipo de animação (idle, strafing)
     * e controla a animação de rotação e fade da hitbox de foco.
     * 
     * @param estado O estado atual do herói.
     * @return true se uma animação de transição (como de-strafing) terminou.
     */
    public boolean atualizar(HeroState estado) {
        frameCounter++;
        if (frameCounter >= 2) {
            anguloRotacaoHitbox = (anguloRotacaoHitbox + 1) % 360;
            frameCounter = 0;
        }

        // Máquina de estados para o fade da hitbox.
        switch (hitboxFadeState) {
            case FADE_IN:
                hitboxAlpha += FADE_SPEED;
                if (hitboxAlpha >= 1.0f) {
                    hitboxAlpha = 1.0f;
                    hitboxFadeState = FadeState.VISIBLE;
                }
                break;
            case FADE_OUT:
                hitboxAlpha -= FADE_SPEED;
                if (hitboxAlpha <= 0.0f) {
                    hitboxAlpha = 0.0f;
                    hitboxFadeState = FadeState.HIDDEN;
                }
                break;
            case VISIBLE:
            case HIDDEN:
                break;
        }

        boolean animacaoTerminou = false;

        // Máquina de estados para a animação do sprite do herói.
        switch (estado) {
            case IDLE:
                resetarAnimacaoStrafing();
                delayFrameIdle++;
                if (delayFrameIdle >= DELAY_IDLE) {
                    frameAtualIdle = (frameAtualIdle + 1) % MAX_FRAMES_IDLE;
                    delayFrameIdle = 0;
                }
                break;
            case STRAFING_LEFT:
            case STRAFING_RIGHT:
                resetarAnimacaoIdle();
                if (frameAtualStrafing < MAX_FRAMES_STRAFING - 1) {
                    delayFrameStrafing++;
                    if (delayFrameStrafing >= DELAY_STRAFING) {
                        frameAtualStrafing++;
                        delayFrameStrafing = 0;
                    }
                } else {
                    delayFrameStrafingMax++;
                    if (delayFrameStrafingMax >= DELAY_STRAFING_MAX) {
                        frameAtualStrafingMax = (frameAtualStrafingMax + 1) % MAX_FRAMES_STRAFING_MAX;
                        delayFrameStrafingMax = 0;
                    }
                }
                break;
            case DE_STRAFING_LEFT:
            case DE_STRAFING_RIGHT:
                resetarAnimacaoIdle();
                delayFrameStrafing++;
                if (delayFrameStrafing >= DELAY_STRAFING) {
                    frameAtualStrafing--;
                    delayFrameStrafing = 0;
                    if (frameAtualStrafing < 0) {
                        frameAtualStrafing = 0;
                        animacaoTerminou = true;
                    }
                }
                break;
        }

        return animacaoTerminou;
    }

    /**
     * @brief Retorna o ImageIcon do frame de animação atual com base no estado do herói.
     */
    public ImageIcon getImagemAtual(HeroState estado) {
        switch (estado) {
            case STRAFING_LEFT:
            case STRAFING_RIGHT:
                if (frameAtualStrafing < MAX_FRAMES_STRAFING - 1) {
                    return iImagesStrafingEsquerda[frameAtualStrafing];
                } else {
                    return iImagesStrafingMax[frameAtualStrafingMax];
                }
            case DE_STRAFING_LEFT:
            case DE_STRAFING_RIGHT:
                int frame = Math.max(0, frameAtualStrafing);
                return iImagesStrafingEsquerda[frame];
            case IDLE:
            default:
                return iImagesIdle[frameAtualIdle];
        }
    }

    
    private void resetarAnimacaoStrafing() {
        frameAtualStrafing = 0;
        delayFrameStrafing = 0;
        frameAtualStrafingMax = 0;
        delayFrameStrafingMax = 0;
    }

    
    private void resetarAnimacaoIdle() {
        frameAtualIdle = 0;
        delayFrameIdle = 0;
    }

    /**
     * @brief Carrega uma imagem individual e a redimensiona.
     */
    private ImageIcon carregarImagem(String nomeArquivo, int largura, int altura) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(Personagem.PATH + nomeArquivo);
            if (imgURL == null) {
                System.err.println("Recurso não encontrado: " + Personagem.PATH + nomeArquivo);
                return null;
            }
            ImageIcon imagem = new ImageIcon(imgURL);
            Image img = imagem.getImage();
            BufferedImage bi = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, largura, altura, null);
            g.dispose();
            return new ImageIcon(bi);
        } catch (Exception ex) {
            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
            return null;
        }
    }

    /**
     * @brief Carrega e recorta uma sequência de frames de um único spritesheet.
     */
    private ImageIcon[] carregarFramesDoSpriteSheet(String nomeArquivo, int numFrames, int largura, int altura) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(Personagem.PATH + nomeArquivo);
            if (imgURL == null) {
                System.err.println("Spritesheet não encontrado: " + Personagem.PATH + nomeArquivo);
                return null;
            }
            BufferedImage spriteSheet = ImageIO.read(imgURL);

            ImageIcon[] frames = new ImageIcon[numFrames];
            final int gap = 2;
            int spriteLargura = (spriteSheet.getWidth() - (gap * (numFrames - 1))) / numFrames;
            int spriteAltura = spriteSheet.getHeight();

            for (int i = 0; i < numFrames; i++) {
                int startX = i * (spriteLargura + gap);
                BufferedImage frameImg = spriteSheet.getSubimage(startX, 0, spriteLargura, spriteAltura);
                BufferedImage frameRedimensionado = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
                Graphics g = frameRedimensionado.createGraphics();
                
                g.drawImage(frameImg, 0, 0, largura, altura, null);
                g.dispose();
                frames[i] = new ImageIcon(frameRedimensionado);
            }
            return frames;
        } catch (Exception ex) {
            System.out.println("Erro ao carregar/recortar spritesheet: " + nomeArquivo + " -> " + ex.getMessage());
            return null;
        }
    }

    /**
     * @brief Força o início da animação de "de-strafing" a partir do seu último frame.
     */
    public void iniciarDeStrafing() {
        this.frameAtualStrafing = MAX_FRAMES_STRAFING - 1;
        this.delayFrameStrafing = 0;
    }

    /**
     * @brief Inicia o efeito de fade-in para a visualização da hitbox de foco.
     */
    public void iniciarFadeInHitbox() {
        hitboxFadeState = FadeState.FADE_IN;
    }

    /**
     * @brief Inicia o efeito de fade-out para a visualização da hitbox de foco.
     */
    public void iniciarFadeOutHitbox() {
        hitboxFadeState = FadeState.FADE_OUT;
    }

    
    public float getHitboxAlpha() {
        return hitboxAlpha;
    }

    
    public ImageIcon getImagemHitboxFoco() {
        return this.imagemHitboxFoco;
    }

    
    public double getAnguloRotacaoHitbox() {
        return this.anguloRotacaoHitbox;
    }
}