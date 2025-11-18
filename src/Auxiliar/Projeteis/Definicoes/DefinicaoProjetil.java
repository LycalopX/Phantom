package Auxiliar.Projeteis.Definicoes;

import Auxiliar.Projeteis.HitboxType;

/**
 * @brief Contêiner de dados imutável para a definição de um tipo de projétil.
 * 
 * Esta classe armazena todas as informações de um projétil que são carregadas
 * a partir do arquivo JSON, como seu ID, spritesheet, coordenadas e
 * dimensões do sprite, e a definição de sua hitbox.
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

        
        public HitboxType getType() { return type; }
        
        public int getW() { return w; }
        
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

    
    public String getId() { return id; }
    
    public String getSpritesheet() { return spritesheet; }
    
    public int getX() { return x; }
    
    public int getY() { return y; }
    
    public int getW() { return w; }
    
    public int getH() { return h; }
    
    public HitboxDef getHitbox() { return hitbox; }
}
