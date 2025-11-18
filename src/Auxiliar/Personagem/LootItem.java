package Auxiliar.Personagem;

import java.io.Serializable;

import Modelo.Items.ItemType;

/**
 * @brief Define uma entrada na tabela de loot (`LootTable`).
 * 
 * Cada `LootItem` representa um tipo de item que pode ser dropado,
 * especificando a quantidade, a probabilidade de drop e outros
 * comportamentos, como ser atraído pelo herói.
 */
public class LootItem implements Serializable {

    public ItemType item;

    private double probabilidade;
    private final int quantidadeMin;
    private final int quantidadeMax;

    private boolean attractToHero = false;

    /**
     * @brief Construtor do LootItem.
     * 
     * @param item O tipo de item a ser dropado (de `ItemType`).
     * @param quantidadeMin A quantidade mínima que pode ser dropada.
     * @param quantidadeMax A quantidade máxima que pode ser dropada.
     * @param probabilidade A chance de drop, de 0.0 (0%) a 1.0 (100%).
     * @param thrown Se o item deve ser "jogado" (não utilizado atualmente).
     * @param attractToHero Se o item deve ser atraído automaticamente pelo herói.
     */
    public LootItem(ItemType item, int quantidadeMin, int quantidadeMax, double probabilidade, boolean thrown,
            boolean attractToHero) {
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