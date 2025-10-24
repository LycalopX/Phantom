
package Modelo.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * @brief Gerencia uma piscina de objetos de itens para reutilização.
 */
public class ItemPool implements Serializable {
    private EnumMap<ItemType, ArrayList<Item>> pool;
    private int maxActiveItems = 0;

    /**
     * @brief Construtor da piscina de itens.
     */
    public ItemPool() {
        pool = new EnumMap<>(ItemType.class);
        for (ItemType type : ItemType.values()) {
            ArrayList<Item> itemList = new ArrayList<>(type.getPoolSize());

            for (int i = 0; i < type.getPoolSize(); i++) {
                itemList.add(new Item(type));
            }
            pool.put(type, itemList);
        }
    }

    /**
     * @brief Retorna um item inativo da piscina para o tipo especificado.
     */
    public Item getItem(ItemType type) {
        ArrayList<Item> itemList = pool.get(type);
        if (itemList != null) {
            for (Item item : itemList) {
                if (!item.isActive()) {
                    return item;
                }
            }
        }
        // Opcional: Aumentar a piscina dinamicamente se necessário
        System.err.println("PISCINA DE ITENS CHEIA PARA O TIPO: " + type);
        return null;
    }

    /**
     * @brief Retorna uma lista contendo todos os itens de todas as piscinas.
     */
    public ArrayList<Item> getTodosOsItens() {
        ArrayList<Item> todos = new ArrayList<>();
        for (ArrayList<Item> itemList : pool.values()) {
            todos.addAll(itemList);
        }
        return todos;
    }

    public void updateHighWatermark() {
        int currentActive = 0;
        for (ArrayList<Item> itemList : pool.values()) {
            for (Item item : itemList) {
                if (item.isActive()) {
                    currentActive++;
                }
            }
        }
        if (currentActive > maxActiveItems) {
            maxActiveItems = currentActive;
        }
    }

    public int getMaxActiveItems() {
        return maxActiveItems;
    }
}
