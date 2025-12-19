package Controler;

import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Taskbar;

/**
 * @brief Representa a janela principal do jogo.
 * 
 * Esta classe estende `JFrame` e serve como o contêiner de nível superior
 * para todos os outros componentes da interface gráfica, como o painel
 * do cenário (`Cenario`).
 */
public class Tela extends JFrame {

    /**
     * @brief Construtor da Tela.
     * 
     * Configura as propriedades básicas da janela, como o título,
     * a operação padrão de fechamento (encerrar a aplicação) e
     * impede que a janela seja redimensionada pelo usuário.
     */
    public Tela() {
        this.setTitle("Phantom Project (POO)");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        // Load and set the icon
        try {
            java.net.URL iconURL = getClass().getClassLoader().getResource("Assets/icon.png");
            if (iconURL != null) {
                BufferedImage icon = ImageIO.read(iconURL);
                
                // Set icon for window frame (works on most OS)
                this.setIconImage(icon);

                // Set icon for macOS Dock (requires Java 9+)
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar taskbar = Taskbar.getTaskbar();
                    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(icon);
                    }
                }
            } else {
                System.err.println("Icon file not found: Assets/icon.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }
}