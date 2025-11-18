package Modelo.Projeteis;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Gerencia uma piscina de objetos de projéteis para reutilização.
 * 
 *        Este padrão (Object Pooling) evita a criação e destruição contínua de
 *        objetos, melhorando significativamente o desempenho e reduzindo a
 *        carga
 *        sobre o Garbage Collector, especialmente em um jogo com muitos
 *        projéteis.
 */
public class ProjetilPool implements Serializable {
    private ArrayList<Projetil> poolNormais;
    private ArrayList<ProjetilHoming> poolHoming;
    private ArrayList<ProjetilBombaHoming> poolBombaHoming;
    private ArrayList<Projetil> poolInimigos;

    // "High watermarks" para debug: registram o número máximo de projéteis
    // ativos de cada tipo durante a execução, ajudando a dimensionar
    // o tamanho ideal das piscinas.
    private volatile int maxActiveNormais = 0;
    private volatile int maxActiveHoming = 0;
    private volatile int maxActiveBombaHoming = 0;
    private volatile int maxActiveInimigos = 0;

    /**
     * @brief Construtor da piscina de projéteis.
     * 
     *        Pré-aloca um número definido de projéteis de cada tipo e os armazena
     *        em suas respectivas listas para uso futuro.
     */
    public ProjetilPool(int tamanhoNormais, int tamanhoHoming, int tamanhoBombaHoming, int tamanhoInimigos) {
        poolNormais = new ArrayList<>(tamanhoNormais);
        for (int i = 0; i < tamanhoNormais; i++) {
            poolNormais.add(new Projetil("projectiles/hero/projectile1_hero.png"));
        }

        poolHoming = new ArrayList<>(tamanhoHoming);
        for (int i = 0; i < tamanhoHoming; i++) {
            poolHoming.add(new ProjetilHoming("projectiles/hero/projectile2_hero.png"));
        }

        poolBombaHoming = new ArrayList<>(tamanhoBombaHoming);
        for (int i = 0; i < tamanhoBombaHoming; i++) {
            poolBombaHoming.add(new ProjetilBombaHoming("projectiles/hero/talisman_bomb.png"));
        }

        poolInimigos = new ArrayList<>(tamanhoInimigos);
        for (int i = 0; i < tamanhoInimigos; i++) {
            poolInimigos.add(new Projetil("projectiles/inimigos/esferas.png"));
        }
    }

    /**
     * @brief Retorna uma lista contendo todos os projéteis de todas as piscinas.
     * 
     *        Usado principalmente para inicializar a lista de projéteis na classe
     *        `Fase`.
     */
    public ArrayList<Projetil> getTodosOsProjeteis() {
        ArrayList<Projetil> todos = new ArrayList<>();
        todos.addAll(poolNormais);
        todos.addAll(poolHoming);
        todos.addAll(poolBombaHoming);
        todos.addAll(poolInimigos);
        return todos;
    }

    /**
     * @brief Retorna um projétil normal inativo da piscina.
     * 
     *        Se nenhum projétil inativo estiver disponível, um erro é logado.
     * @return Um projétil reutilizável ou `null` se a piscina estiver cheia.
     */
    public Projetil getProjetilNormal() {
        for (Projetil p : poolNormais) {
            if (!p.isActive()) {
                return p;
            }
        }
        System.err.println("PISCINA DE PROJÉTEIS NORMAIS CHEIA!");
        return null;
    }

    /**
     * @brief Retorna um projétil teleguiado inativo da piscina.
     * @return Um `ProjetilHoming` reutilizável ou `null` se a piscina estiver
     *         cheia.
     */
    public ProjetilHoming getProjetilHoming() {
        for (ProjetilHoming p : poolHoming) {
            if (!p.isActive()) {
                return p;
            }
        }
        System.err.println("PISCINA DE PROJÉTEIS HOMING CHEIA!");
        return null;
    }

    /**
     * @brief Retorna um projétil de bomba teleguiado inativo da piscina.
     * @return Um `ProjetilBombaHoming` reutilizável ou `null` se a piscina estiver
     *         cheia.
     */
    public ProjetilBombaHoming getProjetilBombaHoming() {
        for (ProjetilBombaHoming p : poolBombaHoming) {
            if (!p.isActive()) {
                return p;
            }
        }
        System.err.println("PISCINA DE TALISMÃS DE BOMBA CHEIA!");
        return null;
    }

    /**
     * @brief Retorna um projétil de inimigo inativo da piscina.
     * @return Um projétil reutilizável ou `null` se a piscina estiver cheia.
     */
    public Projetil getProjetilInimigo() {
        for (Projetil p : poolInimigos) {
            if (!p.isActive()) {
                return p;
            }
        }
        System.err.println("PISCINA DE PROJÉTEIS DE INIMIGOS CHEIA!");
        return null;
    }

    /**
     * @brief Atualiza as "high watermarks" para cada tipo de projétil.
     * 
     *        Este método é chamado periodicamente para registrar o pico de uso de
     *        projéteis, servindo como uma ferramenta de profiling para otimizar
     *        o tamanho das piscinas.
     */
    public void updateHighWatermark() {
        int currentActiveNormais = 0;
        for (Projetil p : poolNormais) {
            if (p.isActive()) {
                currentActiveNormais++;
            }
        }
        if (currentActiveNormais > maxActiveNormais) {
            maxActiveNormais = currentActiveNormais;
        }

        int currentActiveHoming = 0;
        for (ProjetilHoming p : poolHoming) {
            if (p.isActive()) {
                currentActiveHoming++;
            }
        }
        if (currentActiveHoming > maxActiveHoming) {
            maxActiveHoming = currentActiveHoming;
        }

        int currentActiveBombaHoming = 0;
        for (ProjetilBombaHoming p : poolBombaHoming) {
            if (p.isActive()) {
                currentActiveBombaHoming++;
            }
        }
        if (currentActiveBombaHoming > maxActiveBombaHoming) {
            maxActiveBombaHoming = currentActiveBombaHoming;
        }

        int currentActiveInimigos = 0;
        for (Projetil p : poolInimigos) {
            if (p.isActive()) {
                currentActiveInimigos++;
            }
        }
        if (currentActiveInimigos > maxActiveInimigos) {
            maxActiveInimigos = currentActiveInimigos;
        }
    }

    public int getMaxActiveNormais() {
        return maxActiveNormais;
    }

    public int getMaxActiveHoming() {
        return maxActiveHoming;
    }

    public int getMaxActiveBombaHoming() {
        return maxActiveBombaHoming;
    }

    public int getMaxActiveInimigos() {
        return maxActiveInimigos;
    }
}