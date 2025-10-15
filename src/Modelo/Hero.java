package Modelo;

import Controler.ControleDeJogo;
import Auxiliar.Consts;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;

public class Hero extends Personagem {

    // --- Sprites e Animações ---
    private ImageIcon[] iImagesStrafingEsquerda;  // Animação de transição para o strafe
    private ImageIcon[] iImagesIdle;              // Animação parado
    private ImageIcon[] iImagesStrafingMax;       // NOVO: Animação de loop no strafe máximo

    // --- Controle de Animação de Strafing (Transição) ---
    private int frameAtualStrafing = 0;
    private int delayFrameStrafing = 0;
    private static final int MAX_FRAMES_STRAFING = 4; // São 4 frames (s1 a s4)
    private static final int DELAY_STRAFING = 4;      // Velocidade da transição

    // --- NOVO: Controle de Animação de Strafing Máximo (Loop) ---
    private int frameAtualStrafingMax = 0;
    private int delayFrameStrafingMax = 0;
    private static final int MAX_FRAMES_STRAFING_MAX = 4; // Assumindo 4 frames no seu novo sheet
    private static final int DELAY_STRAFING_MAX = 8;      // Velocidade do loop

    // --- Controle de Animação Parada (Idle) ---
    private int frameAtualIdle = 0;
    private int delayFrameIdle = 0;
    private static final int MAX_FRAMES_IDLE = 4;
    private static final int DELAY_IDLE = 8;

    private int direcaoHorizontal = 0;

    public Hero(String sNomeImagePNG, double x, double y) {
        super(sNomeImagePNG, x, y, Consts.CELL_SIDE * 2, Consts.CELL_SIDE * 2);

        // Carrega os sprites de strafing de transição
        iImagesStrafingEsquerda = new ImageIcon[MAX_FRAMES_STRAFING];
        for (int i = 0; i < MAX_FRAMES_STRAFING; i++) {
            String nomeDoArquivo = "hero/hero_s" + (i + 1) + ".png";
            iImagesStrafingEsquerda[i] = carregarImagem(nomeDoArquivo);
        }

        // Carrega os sprites de idle
        iImagesIdle = carregarFramesDoSpriteSheet("hero/hero_standing_still.png", MAX_FRAMES_IDLE);
        
        iImagesStrafingMax = carregarFramesDoSpriteSheet("hero/hero_strafing_max.png", MAX_FRAMES_STRAFING_MAX);
    }
    
    private ImageIcon carregarImagem(String nomeArquivo) {
        // Este método está correto, carrega um arquivo de imagem único
        try {
            ImageIcon imagem = new ImageIcon(new java.io.File(".").getCanonicalPath() + Consts.PATH + nomeArquivo);
            Image img = imagem.getImage();
            BufferedImage bi = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            g.drawImage(img, 0, 0, this.largura, this.altura, null);
            g.dispose();
            return new ImageIcon(bi);
        } catch (IOException ex) {
            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
            return null;
        }
    }

    private ImageIcon[] carregarFramesDoSpriteSheet(String nomeArquivo, int numFrames) {
        // Este método carrega um spritesheet e o divide em frames
        try {
            String caminhoCompleto = new java.io.File(".").getCanonicalPath() + Consts.PATH + nomeArquivo;
            BufferedImage spriteSheet = ImageIO.read(new File(caminhoCompleto));
            
            ImageIcon[] frames = new ImageIcon[numFrames];
            
            final int gap = 2;        
            int spriteLargura = (spriteSheet.getWidth() - (gap * (numFrames - 1))) / numFrames;
            int spriteAltura = spriteSheet.getHeight();
        
            for (int i = 0; i < numFrames; i++) {
                int startX = i * (spriteLargura + gap);
                
                BufferedImage frameImg = spriteSheet.getSubimage(startX, 0, spriteLargura, spriteAltura);
                           
                BufferedImage frameRedimensionado = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_ARGB);
                Graphics g = frameRedimensionado.createGraphics();
                g.drawImage(frameImg, 0, 0, this.largura, this.altura, null);
                g.dispose();

                frames[i] = new ImageIcon(frameRedimensionado);
            }
            return frames;
        } catch (IOException ex) {
            System.out.println("Erro ao carregar spritesheet: " + ex.getMessage());
            return null;
        } catch (Exception ex) { // Captura outros possíveis erros, como o de 'getSubimage'
            System.out.println("Erro ao recortar o spritesheet: " + ex.getMessage());
            ex.printStackTrace(); // Imprime mais detalhes do erro
            return null;
        }
    }

    // --- Lógica Principal ---
    public void atualizar(Set<Integer> teclasPressionadas, ControleDeJogo cj, ArrayList<Personagem> personagens) {
        double VELOCIDADE_HERO = 9.0;
        double FPS = 60;
        double delta = VELOCIDADE_HERO / FPS;
        double dx = 0, dy = 0;

        if (teclasPressionadas.contains(KeyEvent.VK_UP)) dy -= delta;
        if (teclasPressionadas.contains(KeyEvent.VK_DOWN)) dy += delta;

        boolean isMovingHorizontally = teclasPressionadas.contains(KeyEvent.VK_LEFT) || teclasPressionadas.contains(KeyEvent.VK_RIGHT);

        if (isMovingHorizontally) {
            if (teclasPressionadas.contains(KeyEvent.VK_LEFT)) {
                dx -= delta;
                direcaoHorizontal = -1;
            } else {
                dx += delta;
                direcaoHorizontal = 1;
            }

            // Lógica da animação de duas fases
            if (frameAtualStrafing < MAX_FRAMES_STRAFING - 1) {
                // FASE 1: Animação de transição
                delayFrameStrafing++;
                if (delayFrameStrafing >= DELAY_STRAFING) {
                    frameAtualStrafing++;
                    delayFrameStrafing = 0;
                }
            } else {
                // FASE 2: Animação de loop no máximo
                delayFrameStrafingMax++;
                if (delayFrameStrafingMax >= DELAY_STRAFING_MAX) {
                    frameAtualStrafingMax = (frameAtualStrafingMax + 1) % MAX_FRAMES_STRAFING_MAX;
                    delayFrameStrafingMax = 0;
                }
            }
        } else {
            // Se não está se movendo para os lados, reseta TUDO
            direcaoHorizontal = 0;
            frameAtualStrafing = 0;
            delayFrameStrafing = 0;
            frameAtualStrafingMax = 0;
            delayFrameStrafingMax = 0;
            
            // Animação de Idle
            delayFrameIdle++;
            if (delayFrameIdle >= DELAY_IDLE) {
                frameAtualIdle = (frameAtualIdle + 1) % MAX_FRAMES_IDLE;
                delayFrameIdle = 0;
            }
        }

        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }
        if (cj.ehPosicaoValida(personagens, this, this.x + dx, this.y + dy)) {
            this.x += dx;
            this.y += dy;
        }
    }
    
    @Override
    public void autoDesenho(Graphics g) {
        int telaX = (int)Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int)Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);
        
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transformOriginal = g2d.getTransform();

        ImageIcon imagemParaDesenhar = null;

        if (direcaoHorizontal != 0) {
            // Decidir qual animação de strafe usar (transição ou loop máximo)
            if (frameAtualStrafing < MAX_FRAMES_STRAFING - 1) {
                imagemParaDesenhar = iImagesStrafingEsquerda[frameAtualStrafing];
            } else {
                imagemParaDesenhar = iImagesStrafingMax[frameAtualStrafingMax];
            }
        } else {
            // Usar animação de Idle
            imagemParaDesenhar = iImagesIdle[frameAtualIdle];
        }

        // Desenhar a imagem selecionada
        if (imagemParaDesenhar != null) {
            if (direcaoHorizontal == 1) { // Espelhar se for para a direita
                g2d.translate(telaX + largura, telaY);
                g2d.scale(-1, 1); 
                g2d.drawImage(imagemParaDesenhar.getImage(), 0, 0, largura, altura, null);
            } else { // Desenho normal (esquerda ou parado)
                g2d.drawImage(imagemParaDesenhar.getImage(), telaX, telaY, largura, altura, null);
            }
        }
        
        g2d.setTransform(transformOriginal);
    }
}