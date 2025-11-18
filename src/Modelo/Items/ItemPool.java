package Modelo.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * @brief Gerencia piscinas de objetos de itens para reutilização.
 * 
 *        Utiliza o padrão Object Pooling para evitar a criação e destruição
 *        contínua
 *        de itens, melhorando o desempenho. Uma `EnumMap` é usada para manter
 *        uma
 *        piscina separada para cada `ItemType`.
 */
public class ItemPool implements Serializable {
    private EnumMap<ItemType, ArrayList<Item>> pool;
    private volatile int maxActiveItems = 0;

    /**
     * @brief Construtor da piscina de itens.
     * 
     *        Itera sobre todos os valores de `ItemType`, criando uma lista
     *        (piscina)
     *        para cada um e pré-alocando um número de instâncias de `Item` conforme
     *        definido em `ItemType.getPoolSize()`.
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
     * @param type O tipo de item a ser recuperado.
     * @return Um `Item` reutilizável ou `null` se a piscina para esse tipo estiver
     *         cheia.
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

        System.err.println("PISCINA DE ITENS CHEIA PARA O TIPO: " + type);
        return null;
    }

    /**
     * @brief Retorna uma lista contendo todos os itens de todas as piscinas.
     * 
     *        Usado principalmente para inicializar a lista de itens na classe
     *        `Fase`.
     */
    public ArrayList<Item> getTodosOsItens() {
        ArrayList<Item> todos = new ArrayList<>();
        for (ArrayList<Item> itemList : pool.values()) {
            todos.addAll(itemList);
        }
        return todos;
    }

    /**
     * @brief Atualiza a "high watermark" para o número máximo de itens ativos.
     * 
     *        Este método é uma ferramenta de profiling para ajudar a dimensionar
     *        o tamanho ideal das piscinas de itens.
     */
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
