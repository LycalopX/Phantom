package Auxiliar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import Auxiliar.Personagem.LootItem;

/**
 * @brief Gerencia os itens que podem ser dropados por um inimigo.
 * 
 * Uma `LootTable` é associada a um inimigo e contém uma lista de `LootItem`,
 * cada um com sua própria probabilidade de drop e quantidade.
 */
public class LootTable implements Serializable {

    private final ArrayList<LootItem> itens;
    private static final Random random = new Random();

    /**
     * @brief Construtor da tabela de loot. Inicializa a lista de itens.
     */
    public LootTable() {
        this.itens = new ArrayList<>();
    }

    /**
     * @brief Adiciona um novo item possível à tabela de loot.
     */
    public void addItem(LootItem item) {
        this.itens.add(item);
    }

    /**
     * @brief Processa a tabela de loot e gera os drops.
     * 
     * Para cada item possível na tabela, um número aleatório é gerado para
     * determinar se o item será dropado, com base em sua probabilidade.
     * Se o drop ocorrer, uma quantidade aleatória do item (dentro do mínimo
     * e máximo definidos) é adicionada à lista de drops gerados.
     * 
     * @return Uma lista de `LootItem` contendo os drops que foram gerados.
     */
    public ArrayList<LootItem> gerarDrops() {
        ArrayList<LootItem> dropsGerados = new ArrayList<>();

        for (LootItem itemPossivel : this.itens) {
            if (random.nextDouble() <= itemPossivel.getProbabilidade()) {
                int quantidade = random.nextInt(itemPossivel.getQuantidadeMax() - itemPossivel.getQuantidadeMin() + 1)
                        + itemPossivel.getQuantidadeMin();
                for (int i = 0; i < quantidade; i++) {
                    dropsGerados.add(itemPossivel);
                }
            }
        }
        return dropsGerados;
    }

    public ArrayList<LootItem> getItems() {
        return this.itens;
    }
}