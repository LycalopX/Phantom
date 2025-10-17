// Crie este novo arquivo: Modelo/ItemType.java
package Modelo.Items;

public enum ItemType {
    // (spriteX, spriteY, scoreValue, powerValue, bombValue)
    MINI_POWER_UP(0, 0, 10, 1, 0),
    SCORE_POINT(16, 0, 50, 0, 0),
    POWER_UP(32, 0, 100, 30, 0),
    BOMB(48, 0, 0, 0, 1),
    FULL_POWER(64, 0, 0, 300, 0),
    ONE_UP(80, 0, 0, 0, 0);

    // Adicione outros itens aqui no futuro

    // Propriedades que cada tipo de item possui
    private final int spriteX;
    private final int spriteY;
    private final int scoreValue;
    private final int powerValue;
    private final int bombValue;

    // Dimensões do sprite são as mesmas para todos
    private static final int LARGURA = 16;
    private static final int ALTURA = 16;

    // Construtor do enum (sempre privado)
    ItemType(int spriteX, int spriteY, int scoreValue, int powerValue, int bombValue) {
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.scoreValue = scoreValue;
        this.powerValue = powerValue;
        this.bombValue = bombValue;
    }

    // Getters para que o resto do jogo possa ler essas propriedades
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
}