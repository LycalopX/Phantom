package Auxiliar;

import Modelo.ItemType;
import java.io.Serializable;

public class LootItem implements Serializable {

    public ItemType item;

    private double probabilidade;
    private final int quantidadeMin;
    private final int quantidadeMax;

    private boolean thrown;

    public LootItem(ItemType item, int quantidadeMin, int quantidadeMax, double probabilidade, boolean thrown) {
        this.item = item;

        this.quantidadeMin = quantidadeMin;
        this.quantidadeMax = quantidadeMax;
        this.probabilidade = probabilidade;
        this.thrown = thrown;
    }

    public double getProbabilidade() {
        return probabilidade;
    }

    public int getQuantidadeMin() {
        return quantidadeMin;
    }

    public int getQuantidadeMax() {
        return quantidadeMax;
    }
    
    public boolean isToBeThrown() {
        return thrown;
    }

    public ItemType getTipo() {
        return item;
    }
}