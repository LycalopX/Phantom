package Modelo.Inimigos;

import Auxiliar.LootTable;

public abstract class Boss extends Inimigo {

    public Boss(String sNomeImagePNG, double x, double y, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y, lootTable, vida);
    }
    
}
