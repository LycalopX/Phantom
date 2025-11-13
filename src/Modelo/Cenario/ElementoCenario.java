package Modelo.Cenario;

import java.awt.Graphics2D;
import java.io.Serializable;

public interface ElementoCenario extends Serializable {

    void desenhar(Graphics2D g2d, int larguraTela, int alturaTela);

    void mover(double velocidadeAtualDoFundo);

    void setSpeedMultiplier(double multiplier);

    boolean estaForaDaTela(int alturaTela);

    DrawLayer getDrawLayer();

}

