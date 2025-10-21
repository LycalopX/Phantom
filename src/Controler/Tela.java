package Controler;

import javax.swing.JFrame;

/**
 * @brief Representa a janela principal (JFrame) do jogo, que serve como
 *        contêiner para os outros componentes da interface gráfica.
 */
public class Tela extends JFrame {

    /**
     * @brief Construtor da Tela. Configura o título, a operação de fechamento
     *        e a redimensionalização da janela.
     */
    public Tela() {
        this.setTitle("Phantom Project (POO)");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }
}