package Auxiliar;

import java.io.File;

public class Consts {
    // Caminho das imagens
    public static final String PATH = File.separator + "imgs" + File.separator;
    public static final int PERIOD = 80;   
    public static final double BODY_PROPORTION = 2.37; // Proporção do sprite para o tamanho da tela

    // Configurações player
    public static final double HITBOX_RAIO = 4;
    public static final int respawnX = 10;
    public static final int respawnY = 20;
    public static final int BOMB_EFFECT_PERIOD = 30; // Efeito da bomba a cada 10 frames

    // Mapa
    public static final int largura = 632, altura = 740; 
    public static final int MUNDO_LARGURA = 16; // total do mundo
    public static final int MUNDO_ALTURA = 40;
    public static final int TIMER = 10;
    public static final int RES = 20; 
    public static final int CELL_SIDE = 32;


}