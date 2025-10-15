package Auxiliar;

import Controler.Cenario;
import java.awt.Graphics;
import javax.swing.ImageIcon;

public class Desenho {
    private static Cenario cenario; 
    
    public static void setCenario(Cenario umCenario){
        cenario = umCenario;
    }
    
    /**
     * MUDANÇA: Este método agora recebe coordenadas de PIXEL (x, y)
     * e desenha a imagem diretamente nessas coordenadas.
     * @param g O contexto gráfico ("pincel").
     * @param i A imagem a ser desenhada.
     * @param x A coordenada X em pixels.
     * @param y A coordenada Y em pixels.
     */
    public static void desenhar(Graphics g, ImageIcon i, int x, int y) {
        i.paintIcon(cenario, g, x, y);
    }
}