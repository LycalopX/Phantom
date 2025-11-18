package Controler;

import javax.swing.JFrame;

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
    }
}