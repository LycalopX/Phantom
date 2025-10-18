package Auxiliar;

import java.awt.event.KeyEvent;

public class Consts {

    // Mapa
    public static final int largura = 837, altura = 740;
    public static final int MUNDO_LARGURA = 45; // total do mundo
    public static final int MUNDO_ALTURA = 40;
    public static final int TIMER = 10;
    public static final int RES = 20;
    public static final int CELL_SIDE = 32;
    public static final int FPS = 60;

    // Caminho das imagens
    public static final String PATH = "imgs/";
    public static final int PERIOD = 80;
    public static final double BODY_PROPORTION = 1.675; // Proporção do sprite para o tamanho da tela

    // Configurações player
    public static final double HITBOX_RAIO = 4;
    public static final int respawnX = (largura / CELL_SIDE) / 2;
    public static final int respawnY = (int) ((altura / CELL_SIDE) * 0.9);
    public static final double HERO_VELOCITY = 15.4;
    public static final int DANO_BALA = 40;
    public static final int DANO_BALA_TELEGUIADA = 3;
    public static final int REQ_MISSIL_POWER = 120;
    public static final int REQ_TIROS_POWER = 150;
    public static final double TAMANHO_PROJETEIS = 1.2;
    public static final int SLOW_MOTION_FRAMES = 4; // Atualiza a cada 4 frames para efeito de slow motion

    // Movimentação Player
    public static final int KEY_UP = KeyEvent.VK_W;
    public static final int KEY_DOWN = KeyEvent.VK_S;
    public static final int KEY_LEFT = KeyEvent.VK_A;
    public static final int KEY_RIGHT = KeyEvent.VK_D;
    public static final int KEY_SHOOT = KeyEvent.VK_K;
    public static final int KEY_BOMB = KeyEvent.VK_L;
    public static final int KEY_SAVE = KeyEvent.VK_P;
    public static final int KEY_LOAD = KeyEvent.VK_R;
    public static final int KEY_RESTART = KeyEvent.VK_R;

}