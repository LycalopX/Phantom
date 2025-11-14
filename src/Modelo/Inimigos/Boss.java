package Modelo.Inimigos;

import Auxiliar.LootTable;

public abstract class Boss extends Inimigo {

    boolean isBombed = false;

    public Boss(String sNomeImagePNG, double x, double y, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y, lootTable, vida);
    }

    public void setBombed(boolean bombed) {
        isBombed = bombed;
    }

    public boolean isBombed() {
        return this.isBombed;
    }
    
}
