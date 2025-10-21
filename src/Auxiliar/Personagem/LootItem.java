package Auxiliar.Personagem;

import java.io.Serializable;

import Modelo.Items.ItemType;

/**
 * @brief Representa um item dentro de uma LootTable, definindo o tipo de item,
 *        a quantidade que pode ser dropada e a probabilidade de drop.
 */
public class LootItem implements Serializable {

    public ItemType item;

    private double probabilidade;
    private final int quantidadeMin;
    private final int quantidadeMax;

    private boolean attractToHero = false;

    /**
     * @brief Construtor do LootItem.
     */
    public LootItem(ItemType item, int quantidadeMin, int quantidadeMax, double probabilidade, boolean thrown,
            boolean attractToHero) {
        this.item = item;

        this.quantidadeMin = quantidadeMin;
        this.quantidadeMax = quantidadeMax;
        this.probabilidade = probabilidade;
        this.attractToHero = attractToHero;
    }

    /**
     * @brief Retorna a probabilidade de drop do item.
     */
    public double getProbabilidade() {
        return probabilidade;
    }

    /**
     * @brief Retorna a quantidade mínima do item que pode ser dropada.
     */
    public int getQuantidadeMin() {
        return quantidadeMin;
    }

    /**
     * @brief Retorna a quantidade máxima do item que pode ser dropada.
     */
    public int getQuantidadeMax() {
        return quantidadeMax;
    }

    /**
     * @brief Verifica se o item deve ser atraído para o herói.
     */
    public boolean isAttractToHero() {
        return attractToHero;
    }

    /**
     * @brief Retorna o tipo do item.
     */
    public ItemType getTipo() {
        return item;
    }
}