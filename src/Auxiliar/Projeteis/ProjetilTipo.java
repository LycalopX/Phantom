package Auxiliar.Projeteis;

import javax.swing.ImageIcon;

/**
 * @brief Define um contrato para tipos de projéteis.
 * 
 *        Qualquer enum que represente um conjunto de projéteis (como
 *        `TipoProjetilHeroi` ou `TipoProjetilInimigo`) deve implementar esta
 *        interface. Isso garante que cada tipo de projétil possa fornecer
 *        todas as informações necessárias para sua criação e configuração,
 *        como imagem, dimensões do sprite e propriedades da hitbox.
 */
public interface ProjetilTipo {
    /**
     * @return A imagem (sprite) do projétil.
     */
    ImageIcon getImagem();

    /**
     * @return A largura do sprite do projétil.
     */
    int getSpriteWidth();

    /**
     * @return A altura do sprite do projétil.
     */
    int getSpriteHeight();

    /**
     * @return O tipo de hitbox (CIRCULAR ou RECTANGULAR).
     */
    HitboxType getHitboxType();

    /**
     * @return A largura da hitbox.
     */
    int getHitboxWidth();

    /**
     * @return A altura da hitbox.
     */
    int getHitboxHeight();
}
