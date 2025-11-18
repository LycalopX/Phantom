package Modelo.Items;

/**
 * @brief Define os diferentes tipos de itens coletáveis no jogo.
 * 
 *        Este enum funciona como uma "fábrica" de definições para cada tipo de
 *        item.
 *        Cada constante encapsula as propriedades do item, como sua posição no
 *        spritesheet, valor de pontuação, valor de poder, e o tamanho da sua
 *        piscina de objetos (pool).
 */
public enum ItemType {

    MINI_POWER_UP(0, 0, 10, 1, 0, 160),
    SCORE_POINT(16, 0, 50, 0, 0, 20),
    POWER_UP(32, 0, 100, 5, 0, 5),
    BOMB(48, 0, 0, 0, 1, 2),
    FULL_POWER(64, 0, 0, 300, 0, 2),
    ONE_UP(80, 0, 0, 0, 0, 2),
    BOMB_SCORE(96, 0, 20, 0, 1, 50);

    private final int spriteX;
    private final int spriteY;
    private final int scoreValue;
    private final int powerValue;
    private final int bombValue;
    private final int poolSize;

    private static final int LARGURA = 16;
    private static final int ALTURA = 16;

    /**
     * @brief Construtor para cada tipo de item.
     * @param spriteX    A coordenada X do sprite no spritesheet.
     * @param spriteY    A coordenada Y do sprite no spritesheet.
     * @param scoreValue O valor de pontuação que o item concede.
     * @param powerValue O valor de poder que o item concede.
     * @param bombValue  O número de bombas que o item concede.
     * @param poolSize   O tamanho da piscina de objetos para este tipo de item.
     */
    ItemType(int spriteX, int spriteY, int scoreValue, int powerValue, int bombValue, int poolSize) {
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.scoreValue = scoreValue;
        this.powerValue = powerValue;
        this.bombValue = bombValue;
        this.poolSize = poolSize;
    }

    public int getSpriteX() {
        return spriteX;
    }

    public int getSpriteY() {
        return spriteY;
    }

    public int getLargura() {
        return LARGURA;
    }

    public int getAltura() {
        return ALTURA;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getPowerValue() {
        return powerValue;
    }

    public int getBombValue() {
        return bombValue;
    }

    public int getPoolSize() {
        return poolSize;
    }
}