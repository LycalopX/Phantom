
package Auxiliar.Projeteis.Definicoes;

import Auxiliar.Projeteis.HitboxType;

// Esta classe é um contêiner de dados imutável para as definições de um projétil.
public class DefinicaoProjetil {

    private final String id;
    private final String spritesheet;
    private final int x, y, w, h;
    private final HitboxDef hitbox;

    public static class HitboxDef {
        private final HitboxType type;
        private final int w, h;

        public HitboxDef(HitboxType type, int w, int h) {
            this.type = type;
            this.w = w;
            this.h = h;
        }

        public HitboxType getType() { return type; }
        public int getW() { return w; }
        public int getH() { return h; }
    }

    public DefinicaoProjetil(String id, String spritesheet, int x, int y, int w, int h, HitboxDef hitbox) {
        this.id = id;
        this.spritesheet = spritesheet;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.hitbox = hitbox;
    }

    // Getters
    public String getId() { return id; }
    public String getSpritesheet() { return spritesheet; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getW() { return w; }
    public int getH() { return h; }
    public HitboxDef getHitbox() { return hitbox; }
}
