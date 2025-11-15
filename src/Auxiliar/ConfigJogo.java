package Auxiliar;

/**
 * @brief Centraliza as configurações gerais do jogo, como FPS, tempos e nomes de arquivos.
 */
public final class ConfigJogo {

    /**
     * @brief Construtor privado para previnir a instanciação da classe.
     */
    private ConfigJogo() {
    }

    // --- Constantes de Jogo ---
    public static final int GAME_FPS = 60;
    public static final int RESPAWN_TIME_FRAMES = 60;
    public static final int DEATHBOMB_WINDOW_FRAMES = 8;
    public static final String SAVE_FILE_NAME = "POO.dat";
}
