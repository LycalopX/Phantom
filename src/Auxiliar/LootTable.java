package Auxiliar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import Auxiliar.Personagem.LootItem;

/**
 * @brief Representa uma tabela de loot que pode ser associada a um inimigo,
 *        contendo os possíveis itens que podem ser dropados.
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
     * @brief Processa a tabela de loot, rolando a probabilidade para cada item
     *        e gerando uma lista de itens que foram efetivamente dropados.
     * @return Uma lista de `LootItem` contendo os drops gerados.
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