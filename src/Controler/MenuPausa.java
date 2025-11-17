package Controler;

import Auxiliar.ConfigMapa;
import Modelo.Personagem;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MenuPausa {

    private BufferedImage imagemResume;
    private BufferedImage imagemReturn;
    private BufferedImage imagemQuit;
    private BufferedImage imagemReally;

    public MenuPausa() {
        carregarImagens();
    }

    private void carregarImagens() {
        try {
            imagemResume = ImageIO.read(getClass().getClassLoader().getResource("imgs/pause.png"));
            
            // Load return and quit from the same image
            BufferedImage returnQuitImage = ImageIO
                    .read(getClass().getClassLoader().getResource("imgs/return_quit.png"));
            imagemReturn = returnQuitImage.getSubimage(0, 0, returnQuitImage.getWidth(),
                    returnQuitImage.getHeight() / 2);
            imagemQuit = returnQuitImage.getSubimage(0, returnQuitImage.getHeight() / 2, returnQuitImage.getWidth(),
                    returnQuitImage.getHeight() / 2);
            
            imagemReally = ImageIO.read(getClass().getClassLoader().getResource("imgs/really_question.png"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagens do menu de pausa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void desenhar(Graphics2D g2d, int menuSelection, boolean showQuitConfirmation, int screenWidth,
            int screenHeight) {
        // Fundo escurecido
        g2d.setColor(new Color(0, 0, 0, (int) (255 * 0.6)));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        double scale = Personagem.BODY_PROPORTION;

        // Título "Resume"
        if (imagemResume != null) {
            int scaledWidth = (int) (imagemResume.getWidth() * scale);
            int scaledHeight = (int) (imagemResume.getHeight() * scale);
            int x = (screenWidth - scaledWidth) / 2;
            int y = screenHeight / 4; 
            g2d.drawImage(imagemResume, x, y, scaledWidth, scaledHeight, null);
        }

        if (showQuitConfirmation) {
            if (imagemReally != null) {
                int scaledWidth = (int) (imagemReally.getWidth() * scale);
                int scaledHeight = (int) (imagemReally.getHeight() * scale);
                int x = (screenWidth - scaledWidth) / 2;
                int y = screenHeight / 2; 
                g2d.drawImage(imagemReally, x, y, scaledWidth, scaledHeight, null);
            }
        } else {
            // Opções "Return" e "Quit"
            if (imagemReturn != null && imagemQuit != null) {
                int scaledReturnWidth = (int) (imagemReturn.getWidth() * scale);
                int scaledReturnHeight = (int) (imagemReturn.getHeight() * scale);
                int xReturn = (screenWidth - scaledReturnWidth) / 2;
                int yReturn = screenHeight / 2; 

                int scaledQuitWidth = (int) (imagemQuit.getWidth() * scale);
                int scaledQuitHeight = (int) (imagemQuit.getHeight() * scale);
                int xQuit = (screenWidth - scaledQuitWidth) / 2;
                int yQuit = yReturn + scaledReturnHeight + (int)(20); // Only spacing scaled

                // Efeito de "glow" (brilho)
                if (menuSelection == 0) { // Return selecionado
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    g2d.drawImage(imagemReturn, xReturn, yReturn, scaledReturnWidth, scaledReturnHeight, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.drawImage(imagemQuit, xQuit, yQuit, scaledQuitWidth, scaledQuitHeight, null);
                } else { // Quit selecionado
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.drawImage(imagemReturn, xReturn, yReturn, scaledReturnWidth, scaledReturnHeight, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    g2d.drawImage(imagemQuit, xQuit, yQuit, scaledQuitWidth, scaledQuitHeight, null);
                }
                // Restaura o composite padrão
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }
}

