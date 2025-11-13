package Modelo;

/**
 * @brief Define as camadas de renderização para os personagens,
 * determinando a ordem em que são desenhados na tela (Z-ordering).
 * Enums com menor valor ordinal são desenhados primeiro (mais ao fundo).
 */
public enum RenderLayer {
    ENEMY_LAYER,         // Inimigos e seus projéteis
    PLAYER_LAYER,        // Herói, itens e efeito da bomba
    PLAYER_PROJECTILE_LAYER; // Projéteis do herói
}
