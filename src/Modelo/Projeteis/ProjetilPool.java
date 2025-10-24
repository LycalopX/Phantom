package Modelo.Projeteis;

import Modelo.Personagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Gerencia uma piscina de objetos de projéteis para reutilização,
 *        evitando a criação e destruição contínua de objetos.
 */
public class ProjetilPool implements Serializable {
    private ArrayList<Projetil> poolNormais;
    private ArrayList<ProjetilHoming> poolHoming;
    private ArrayList<ProjetilBombaHoming> poolBombaHoming;
    private ArrayList<Projetil> poolInimigos;

    private int maxActiveNormais = 0;
    private int maxActiveHoming = 0;
    private int maxActiveBombaHoming = 0;
    private int maxActiveInimigos = 0;

    /**
     * @brief Construtor da piscina de projéteis. Inicializa as piscinas para cada
     *        tipo de projétil.
     */
    public ProjetilPool(int tamanhoNormais, int tamanhoHoming, int tamanhoBombaHoming, int tamanhoInimigos, List<Personagem> personagens) {
        poolNormais = new ArrayList<>(tamanhoNormais);
        for (int i = 0; i < tamanhoNormais; i++) {
            poolNormais.add(new Projetil("projectiles/hero/projectile1_hero.png"));
        }

        poolHoming = new ArrayList<>(tamanhoHoming);
        for (int i = 0; i < tamanhoHoming; i++) {
            poolHoming.add(new ProjetilHoming("projectiles/hero/projectile2_hero.png", personagens));
        }

        poolBombaHoming = new ArrayList<>(tamanhoBombaHoming);
        for (int i = 0; i < tamanhoBombaHoming; i++) {
            poolBombaHoming.add(new ProjetilBombaHoming("projectiles/hero/talisman_bomb.png", personagens));
        }

        poolInimigos = new ArrayList<>(tamanhoInimigos);
        for (int i = 0; i < tamanhoInimigos; i++) {
            poolInimigos.add(new Projetil("projectiles/inimigos/esferas.png"));
        }
    }

    /**
     * @brief Retorna uma lista contendo todos os projéteis de todas as piscinas.
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