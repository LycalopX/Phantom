package Controler;

import Modelo.Personagem;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @brief Gerencia a renderização do menu de pausa do jogo.
 * 
 *        Esta classe é responsável por carregar e desenhar todas as imagens
 *        que compõem a tela de pausa, incluindo o fundo, título e opções.
 */
public class MenuPausa {

    private BufferedImage imagemResume;
    private BufferedImage imagemReturn;
    private BufferedImage imagemQuit;
    private BufferedImage imagemReally;

    public MenuPausa() {
        carregarImagens();
    }

    /**
     * @brief Carrega todas as imagens necessárias para o menu a partir dos assets.
     */
    private void carregarImagens() {
        try {
            imagemResume = ImageIO.read(getClass().getClassLoader().getResource("Assets/pause.png"));

            BufferedImage returnQuitImage = ImageIO
                    .read(getClass().getClassLoader().getResource("Assets/return_quit.png"));
            imagemReturn = returnQuitImage.getSubimage(0, 0, returnQuitImage.getWidth(),
                    returnQuitImage.getHeight() / 2);
            imagemQuit = returnQuitImage.getSubimage(0, returnQuitImage.getHeight() / 2, returnQuitImage.getWidth(),
                    returnQuitImage.getHeight() / 2);

            imagemReally = ImageIO.read(getClass().getClassLoader().getResource("Assets/really_question.png"));
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagens do menu de pausa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Desenha o menu de pausa na tela.
     * 
     * @param g2d                  O contexto gráfico para desenhar.
     * @param menuSelection        O índice da opção de menu atualmente selecionada
     *                             (0 para Return, 1 para Quit).
     * @param showQuitConfirmation Flag que indica se a tela de confirmação de saída
     *                             deve ser mostrada.
     * @param screenWidth          A largura da tela.
     * @param screenHeight         A altura da tela.
     */
    public void desenhar(Graphics2D g2d, int menuSelection, boolean showQuitConfirmation, int screenWidth,
            int screenHeight) {

        g2d.setColor(new Color(0, 0, 0, (int) (255 * 0.6)));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        double scale = Personagem.BODY_PROPORTION;

        if (imagemResume != null) {
            int scaledWidth = (int) (imagemResume.getWidth() * scale);
            int scaledHeight = (int) (imagemResume.getHeight() * scale);
            int x = (screenWidth - scaledWidth) / 2;
            int y = screenHeight / 4;
            g2d.drawImage(imagemResume, x, y, scaledWidth, scaledHeight, null);
        }

        // Exibe a tela de confirmação ou as opções do menu principal.
        if (showQuitConfirmation) {
            if (imagemReally != null) {
                int scaledWidth = (int) (imagemReally.getWidth() * scale);
                int scaledHeight = (int) (imagemReally.getHeight() * scale);
                int x = (screenWidth - scaledWidth) / 2;
                int y = screenHeight / 2;
                g2d.drawImage(imagemReally, x, y, scaledWidth, scaledHeight, null);
            }
        } else {

            if (imagemReturn != null && imagemQuit != null) {
                int scaledReturnWidth = (int) (imagemReturn.getWidth() * scale);
                int scaledReturnHeight = (int) (imagemReturn.getHeight() * scale);
                int xReturn = (screenWidth - scaledReturnWidth) / 2;
                int yReturn = screenHeight / 2;

                int scaledQuitWidth = (int) (imagemQuit.getWidth() * scale);
                int scaledQuitHeight = (int) (imagemQuit.getHeight() * scale);
                int xQuit = (screenWidth - scaledQuitWidth) / 2;
                int yQuit = yReturn + scaledReturnHeight + (int) (20);

                // Aplica um efeito de "glow" (brilho) na opção selecionada,
                // desenhando-a com opacidade total e a outra com opacidade reduzida.
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

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }
}
