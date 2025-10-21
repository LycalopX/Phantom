package Modelo.Hero;

import Modelo.Personagem;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GerenciadorDeAnimacaoHeroi implements java.io.Serializable {
    // Arrays para guardar todos os sprites
    private ImageIcon[] iImagesStrafingEsquerda;
    private ImageIcon[] iImagesIdle;
    private ImageIcon[] iImagesStrafingMax;
    private ImageIcon imagemHitboxFoco;

    // Rotação da hitbox
    private double anguloRotacaoHitbox = 0;
    private int frameCounter = 0;

    // Lógica de Fade da Hitbox
    private enum FadeState { FADE_IN, FADE_OUT, VISIBLE, HIDDEN }
    private FadeState hitboxFadeState = FadeState.HIDDEN;
    private float hitboxAlpha = 0.0f;
    private static final float FADE_SPEED = 0.05f;

    // Constantes de animação
    private static final int MAX_FRAMES_STRAFING = 4;
    private static final int DELAY_STRAFING = 4;
    private static final int MAX_FRAMES_STRAFING_MAX = 4;
    private static final int DELAY_STRAFING_MAX = 5;
    private static final int MAX_FRAMES_IDLE = 4;
    private static final int DELAY_IDLE = 8;

    // Variáveis de estado da animação
    private int frameAtualStrafing = 0, delayFrameStrafing = 0;
    private int frameAtualStrafingMax = 0, delayFrameStrafingMax = 0;
    private int frameAtualIdle = 0, delayFrameIdle = 0;

    public GerenciadorDeAnimacaoHeroi(int largura, int altura) {
        // Carrega a imagem da hitbox com suas dimensões corretas para evitar distorção
        imagemHitboxFoco = carregarImagem("hero/sprite_hitbox.png", 64, 61);

        // O construtor já carrega todas as imagens necessárias
        iImagesStrafingEsquerda = new ImageIcon[MAX_FRAMES_STRAFING];
        for (int i = 0; i < MAX_FRAMES_STRAFING; i++) {
            iImagesStrafingEsquerda[i] = carregarImagem("hero/hero_s" + (i + 1) + ".png", largura, altura);
        }
        iImagesIdle = carregarFramesDoSpriteSheet("hero/hero_standing_still.png", MAX_FRAMES_IDLE, largura, altura);
        iImagesStrafingMax = carregarFramesDoSpriteSheet("hero/hero_strafing_max.png", MAX_FRAMES_STRAFING_MAX, largura,
                altura);
    }

    // O Hero diz seu estado (direcaoHorizontal) e este método atualiza os
    // contadores
    public boolean atualizar(HeroState estado) {
        // Lógica de rotação da hitbox
        frameCounter++;
        if (frameCounter >= 2) {
            anguloRotacaoHitbox = (anguloRotacaoHitbox + 1) % 360;
            frameCounter = 0;
        }

        // Lógica de fade da hitbox
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
                // Não faz nada
                break;
        }

        boolean animacaoTerminou = false;

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
                    frameAtualStrafing--; // Animação ao contrário!

                    delayFrameStrafing = 0;
                    if (frameAtualStrafing < 0) {
                        frameAtualStrafing = 0;
                        animacaoTerminou = true; // Sinaliza que a animação acabou!
                    }
                }
                break;
        }

        return animacaoTerminou;
    }

    public ImageIcon getImagemAtual(HeroState estado) {
        switch (estado) {
            case STRAFING_LEFT:
            case STRAFING_RIGHT:
                // Se está se movendo para os lados
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

    // Métodos auxiliares para limpar os contadores
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


    // Métodos de carregamento (agora precisam de largura/altura como parâmetro)
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

    public void iniciarDeStrafing() {
        // Força a animação a começar do último frame da SEQUÊNCIA DE TRANSIÇÃO
        this.frameAtualStrafing = MAX_FRAMES_STRAFING - 1;
        this.delayFrameStrafing = 0; // Reseta o delay para começar imediatamente
    }

    public void iniciarFadeInHitbox() {
        hitboxFadeState = FadeState.FADE_IN;
    }

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