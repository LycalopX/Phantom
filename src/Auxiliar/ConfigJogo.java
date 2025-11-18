package Auxiliar;

/**
 * @brief Centraliza as configurações gerais e constantes do jogo.
 * 
 *        Esta classe final contém constantes estáticas para parâmetros globais
 *        como a taxa de quadros por segundo (FPS), tempos de eventos e nomes de
 *        arquivos.
 */
public final class ConfigJogo {

    /**
     * @brief Construtor privado para impedir a instanciação da classe.
     */
    private ConfigJogo() {
    }

    /**
     * @brief A taxa de quadros por segundo (Frames Per Second) alvo para o jogo.
     */
    public static final int GAME_FPS = 60;

    /**
     * @brief O tempo de invencibilidade do jogador após reaparecer (em frames).
     */
    public static final int RESPAWN_TIME_FRAMES = 60;

    /**
     * @brief A janela de tempo (em frames) que o jogador tem para usar uma bomba
     *        após ser atingido para evitar a morte (deathbombing).
     */
    public static final int DEATHBOMB_WINDOW_FRAMES = 8;

    /**
     * @brief O nome do arquivo usado para salvar e carregar o estado do jogo.
     */
    public static final String SAVE_FILE_NAME = "POO.dat";
}
