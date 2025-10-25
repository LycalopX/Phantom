package Auxiliar;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @brief Centraliza as configurações de mapa, tela e grid.
 * Agora calcula a resolução da tela dinamicamente.
 */
public final class ConfigMapa {

    /**
     * @brief Construtor privado para previnir a instanciação da classe.
     */
    private ConfigMapa() {
    }

    // --- Constantes de Design ---

    /**
     * @brief Proporção da altura do monitor que a janela do jogo deve tentar usar.
     * O valor 0.9444... é (680.0 / 720.0).
     */
    private static final double PROPORCAO_ALTURA_DESEJADA = 680.0 / 720.0;

    // Dimensões do Mundo (em células de grid)
    public static final int MUNDO_LARGURA = 34;
    public static final int MUNDO_ALTURA = 40;


    // --- Dimensões Calculadas (definidas no bloco estático abaixo) ---
    
    /**
     * @brief Largura final da tela (em pixels), calculada para ser (CELL_SIDE * MUNDO_LARGURA)
     */
    public static final int LARGURA_TELA;
    
    /**
     * @brief Altura final da tela (em pixels), calculada para ser (CELL_SIDE * MUNDO_ALTURA)
     */
    public static final int ALTURA_TELA;
    
    /**
     * @brief Tamanho da Célula do Grid (em pixels), calculado dinamicamente.
     */
    public static final int CELL_SIDE;


    /**
     * @brief Bloco estático de inicialização.
     * Este código roda UMA VEZ quando a classe ConfigMapa é carregada.
     * Ele calcula as dimensões da tela com base no monitor do usuário.
     */
    static {
        // Variáveis temporárias para o cálculo
        int tempLargura, tempAltura, tempCellSide;

        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenHeight = screenSize.height;

            int alturaJanelaDesejada = (int) (screenHeight * PROPORCAO_ALTURA_DESEJADA);

            tempCellSide = alturaJanelaDesejada / MUNDO_ALTURA;

            tempAltura = tempCellSide * MUNDO_ALTURA;
            tempLargura = tempCellSide * MUNDO_LARGURA;

        } catch (Exception e) {
            // Se algo der errado (ex: rodando em um ambiente sem tela),
            // usa os valores fixos originais.
            System.err.println("Nao foi possivel detectar a resolucao da tela. Usando valores padrao.");
            tempLargura = 580;
            tempAltura = 680;
            tempCellSide = 17;
        }

        // 5. Atribuir os valores calculados às constantes finais
        LARGURA_TELA = tempLargura;
        ALTURA_TELA = tempAltura;
        CELL_SIDE = tempCellSide;
    }
}