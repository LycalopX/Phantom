// Novo arquivo: Modelo/Item.java
package Modelo;

import Auxiliar.Consts;

public class Item extends Personagem {
    public Item(String sNomeImagePNG, double x, double y) {
        super(sNomeImagePNG, x, y, Consts.CELL_SIDE, Consts.CELL_SIDE); // Tamanho padrão
        this.bTransponivel = true;
        this.bMortal = false;
    }
}