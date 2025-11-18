package Modelo;

/**
 * @brief Define as camadas de renderização para os personagens.
 * 
 * Este enum determina a ordem em que os diferentes tipos de `Personagem`
 * são desenhados na tela (Z-ordering). Enums com menor valor ordinal
 * (declarados primeiro) são desenhados mais ao fundo.
 */
public enum RenderLayer {
    /** Camada para inimigos. */
    ENEMY_LAYER,
    
    /** Camada para o herói, itens e efeitos como a explosão da bomba. */
    PLAYER_LAYER,
    
    /** Camada para os projéteis do herói. */
    PROJETILE_LAYER;
}
