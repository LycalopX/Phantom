package Modelo.Cenario;

import Modelo.Fases.Fase;
import java.awt.Graphics2D;
import java.io.Serializable;

public interface ElementoCenario extends Serializable {
    
    void desenhar(Graphics2D g2d, int alturaDaTela);

    void mover(double velocidadeAtualDoFundo);

    void relinkarImagens(Fase fase);

    boolean estaForaDaTela(int alturaDaTela);
}
