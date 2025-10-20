package Auxiliar.Projeteis;

import javax.swing.ImageIcon;

public interface ProjetilTipo {
    ImageIcon getImagem();
    int getSpriteWidth();
    int getSpriteHeight();
    HitboxType getHitboxType();
    int getHitboxWidth();
    int getHitboxHeight();
}
