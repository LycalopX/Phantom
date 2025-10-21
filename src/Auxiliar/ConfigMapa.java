package Auxiliar;

/**
 * @brief Centraliza as configurações de mapa, tela e grid.
 */
public final class ConfigMapa {

    /**
     * @brief Construtor privado para previnir a instanciação da classe.
     */
    private ConfigMapa() {
    }

    // Dimensões da Tela (em pixels)
    public static final int LARGURA_TELA = 580;
    public static final int ALTURA_TELA = 680;

    // Dimensões do Mundo (em células de grid)
    public static final int MUNDO_LARGURA = 34;
    public static final int MUNDO_ALTURA = 40;

    // Tamanho da Célula do Grid (em pixels)
    public static final int CELL_SIDE = 17;
}
