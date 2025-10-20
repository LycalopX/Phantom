package Auxiliar.Personagem;

import java.io.Serializable;

import Modelo.Items.ItemType;

public class LootItem implements Serializable {

    public ItemType item;

    private double probabilidade;
    private final int quantidadeMin;
    private final int quantidadeMax;

    private boolean attractToHero = false;

    public LootItem(ItemType item, int quantidadeMin, int quantidadeMax, double probabilidade, boolean thrown, boolean attractToHero) {
        this.item = item;

        this.quantidadeMin = quantidadeMin;
        this.quantidadeMax = quantidadeMax;
        this.probabilidade = probabilidade;
        this.attractToHero = attractToHero;
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
    
    public boolean isAttractToHero() {
        return attractToHero;
    }

    public ItemType getTipo() {
        return item;
    }
}