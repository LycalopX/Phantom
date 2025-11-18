package Modelo.Hero;

/**
 * @brief Define os possíveis estados de animação para o personagem do herói.
 * 
 * Este enum é usado pela classe `Hero` e `GerenciadorDeAnimacaoHeroi` para
 * controlar qual animação deve ser reproduzida (parado, movendo para a esquerda/direita,
 * e as transições de volta para o estado parado).
 */
public enum HeroState {
    IDLE,
    STRAFING_LEFT,
    STRAFING_RIGHT,
    DE_STRAFING_LEFT,
    DE_STRAFING_RIGHT
}