package Auxiliar;

import java.awt.event.KeyEvent;

/**
 * @brief Centraliza as configurações de mapeamento de teclado do jogo.
 * 
 *        Esta classe final contém constantes estáticas para todas as ações do
 *        jogo,
 *        permitindo que as teclas sejam facilmente reconfiguradas em um único
 *        lugar.
 */
public final class ConfigTeclado {

    /**
     * @brief Construtor privado para impedir a instanciação da classe.
     */
    private ConfigTeclado() {
    }

    // --- Movimentação do Jogador ---
    public static final int KEY_UP = KeyEvent.VK_W;
    public static final int ARROW_UP = KeyEvent.VK_UP;
    public static final int KEY_DOWN = KeyEvent.VK_S;
    public static final int ARROW_DOWN = KeyEvent.VK_DOWN;
    public static final int KEY_LEFT = KeyEvent.VK_A;
    public static final int ARROW_LEFT = KeyEvent.VK_LEFT;
    public static final int KEY_RIGHT = KeyEvent.VK_D;
    public static final int ARROW_RIGHT = KeyEvent.VK_RIGHT;

    // --- Ações do Jogador ---
    public static final int KEY_SHOOT = KeyEvent.VK_K;
    public static final int KEY_BOMB = KeyEvent.VK_L;
    public static final int KEY_SHOOT2 = KeyEvent.VK_Z; // Tecla alternativa
    public static final int KEY_BOMB2 = KeyEvent.VK_X; // Tecla alternativa

    // --- Ações do Jogo e Menus ---
    public static final int KEY_SAVE = KeyEvent.VK_P;
    public static final int KEY_LOAD = KeyEvent.VK_R;
    public static final int KEY_RESTART = KeyEvent.VK_R;

    public static final int KEY_PAUSE = KeyEvent.VK_ESCAPE;
    public static final int KEY_SELECT = KeyEvent.VK_ENTER;
    public static final int KEY_CANCEL = KeyEvent.VK_ESCAPE;
}
