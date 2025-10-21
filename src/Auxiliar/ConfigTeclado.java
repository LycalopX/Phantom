package Auxiliar;

import java.awt.event.KeyEvent;

/**
 * @brief Centraliza as configurações de teclado do jogo.
 */
public final class ConfigTeclado {

    /**
     * @brief Construtor privado para previnir a instanciação da classe.
     */
    private ConfigTeclado() {
    }

    // Movimentação Player
    public static final int KEY_UP = KeyEvent.VK_W;
    public static final int KEY_DOWN = KeyEvent.VK_S;
    public static final int KEY_LEFT = KeyEvent.VK_A;
    public static final int KEY_RIGHT = KeyEvent.VK_D;

    // Ações Player
    public static final int KEY_SHOOT = KeyEvent.VK_K;
    public static final int KEY_BOMB = KeyEvent.VK_L;

    // Ações do Jogo
    public static final int KEY_SAVE = KeyEvent.VK_P;
    public static final int KEY_LOAD = KeyEvent.VK_R;
    public static final int KEY_RESTART = KeyEvent.VK_R;
}
