package Auxiliar;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @brief Centraliza as configurações de mapa, tela e grid.
 * 
 *        Esta classe calcula dinamicamente as dimensões da tela do jogo com
 *        base
 *        na resolução do monitor do usuário, garantindo que a janela se ajuste
 *        de forma proporcional.
 */
public final class ConfigMapa {

    /**
     * @brief Construtor privado para impedir a instanciação da classe.
     */
    private ConfigMapa() {
    }

    /**
     * @brief Proporção da altura do monitor que a janela do jogo deve tentar usar.
     *        O valor 0.9444... é (680.0 / 720.0), mantido da resolução de design
     *        original.
     */
    private static final double PROPORCAO_ALTURA_DESEJADA = 680.0 / 900.0;

    public static final int MUNDO_LARGURA = 34;
    public static final int MUNDO_ALTURA = 40;

    /**
     * @brief Largura final da tela (em pixels), calculada como `CELL_SIDE *
     *        MUNDO_LARGURA`.
     */
    public static final int LARGURA_TELA;

    /**
     * @brief Altura final da tela (em pixels), calculada como `CELL_SIDE *
     *        MUNDO_ALTURA`.
     */
    public static final int ALTURA_TELA;

    /**
     * @brief O tamanho de uma única célula do grid (em pixels), calculado
     *        dinamicamente.
     */
    public static final int CELL_SIDE;

    /**
     * @brief Bloco de inicialização estático.
     * 
     *        Este código é executado uma única vez quando a classe é carregada.
     *        Ele detecta a resolução do monitor do usuário e calcula as dimensões
     *        da tela e do grid para que o jogo se ajuste proporcionalmente,
     *        mantendo
     *        a proporção de aspecto do design original.
     */
    static {

        int tempLargura, tempAltura, tempCellSide;

        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenHeight = screenSize.height;

            int alturaJanelaDesejada = (int) (screenHeight * PROPORCAO_ALTURA_DESEJADA);

            tempCellSide = alturaJanelaDesejada / MUNDO_ALTURA;

            tempAltura = tempCellSide * MUNDO_ALTURA;
            tempLargura = tempCellSide * MUNDO_LARGURA;

        } catch (Exception e) {

            System.err.println("Nao foi possivel detectar a resolucao da tela. Usando valores padrao.");
            tempLargura = 580;
            tempAltura = 680;
            tempCellSide = 17;
        }

        LARGURA_TELA = tempLargura;
        ALTURA_TELA = tempAltura;
        CELL_SIDE = tempCellSide;
    }

    public static final double FATOR_ESCALA_ALTURA = (double) ALTURA_TELA / 680.0;

    public static final int HERO_RESPAWN_X = (LARGURA_TELA / CELL_SIDE) / 2;
    public static final int HERO_RESPAWN_Y = (int) ((ALTURA_TELA / CELL_SIDE) * 0.9);
}