package Controler;

import javax.swing.JFrame;

/**
 * A classe Tela agora é apenas uma janela (JFrame) simples.
 * A sua única responsabilidade é servir como uma moldura para o jogo.
 */
public class Tela extends JFrame {

    public Tela() {
        this.setTitle("Phantom Project (POO)");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }
}