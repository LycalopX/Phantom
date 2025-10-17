// Em Auxiliar/LootTable.java (agora sem a classe interna)
package Auxiliar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class LootTable implements Serializable {
    
    private final ArrayList<LootItem> itens;
    private static final Random random = new Random();

    public LootTable() {
        this.itens = new ArrayList<>();
    }

    public void addItem(LootItem item) {
        this.itens.add(item);
    }

    /**
     * Rola os dados para cada item na tabela e retorna uma lista
     * dos itens que foram sorteados com sucesso.
     */
    public ArrayList<LootItem> gerarDrops() {
        ArrayList<LootItem> dropsGerados = new ArrayList<>();

        for (LootItem itemPossivel : this.itens) {

            // Verifica se o item foi sorteado baseado na probabilidade
            if (random.nextDouble() <= itemPossivel.getProbabilidade()) {

                // Sorteia a quantidade de itens a dropar
                int quantidade = random.nextInt(itemPossivel.getQuantidadeMax() - itemPossivel.getQuantidadeMin() + 1) + itemPossivel.getQuantidadeMin();

                // Adiciona o item na lista de drops a quantidade de vezes sorteada
                for (int i = 0; i < quantidade; i++) {
                    dropsGerados.add(itemPossivel);
                }
            }
        }
        return dropsGerados;
    }
}