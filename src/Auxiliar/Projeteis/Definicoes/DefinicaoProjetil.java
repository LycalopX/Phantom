package Auxiliar.Projeteis.Definicoes;

import Auxiliar.Projeteis.HitboxType;

/**
 * @brief Contêiner de dados imutável que armazena todas as informações de definição
 *        de um tipo de projétil, carregadas a partir do JSON.
 */
public class DefinicaoProjetil {

    private final String id;
    private final String spritesheet;
    private final int x, y, w, h;
    private final HitboxDef hitbox;

    /**
     * @brief Define as propriedades da hitbox de um projétil.
     */
    public static class HitboxDef {
        private final HitboxType type;
        private final int w, h;

        /**
         * @brief Construtor da definição da hitbox.
         */
        public HitboxDef(HitboxType type, int w, int h) {
            this.type = type;
            this.w = w;
            this.h = h;
        }

        /**
         * @brief Retorna o tipo da hitbox (CIRCULAR ou RECTANGULAR).
         */
        public HitboxType getType() { return type; }
        /**
         * @brief Retorna a largura da hitbox.
         */
        public int getW() { return w; }
        /**
         * @brief Retorna a altura da hitbox.
         */
        public int getH() { return h; }
    }

    /**
     * @brief Construtor da definição do projétil.
     */
    public DefinicaoProjetil(String id, String spritesheet, int x, int y, int w, int h, HitboxDef hitbox) {
        this.id = id;
        this.spritesheet = spritesheet;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.hitbox = hitbox;
    }

    /**
     * @brief Retorna o ID único do projétil.
     */
    public String getId() { return id; }
    /**
     * @brief Retorna o ID do spritesheet usado pelo projétil.
     */
    public String getSpritesheet() { return spritesheet; }
    /**
     * @brief Retorna a coordenada X do sprite no spritesheet.
     */
    public int getX() { return x; }
    /**
     * @brief Retorna a coordenada Y do sprite no spritesheet.
     */
    public int getY() { return y; }
    /**
     * @brief Retorna a largura do sprite.
     */
    public int getW() { return w; }
    /**
     * @brief Retorna a altura do sprite.
     */
    public int getH() { return h; }
    /**
     * @brief Retorna a definição da hitbox do projétil.
     */
    public HitboxDef getHitbox() { return hitbox; }
}
