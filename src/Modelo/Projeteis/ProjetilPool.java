package Modelo.Projeteis;

import Modelo.Personagem;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Gerencia uma piscina de objetos de projéteis para reutilização,
 *        evitando a criação e destruição contínua de objetos.
 */
public class ProjetilPool implements Serializable {
    private ArrayList<Projetil> poolNormais;
    private ArrayList<ProjetilHoming> poolHoming;
    private ArrayList<ProjetilBombaHoming> poolBombaHoming;
    private ArrayList<Projetil> poolInimigos;

    /**
     * @brief Construtor da piscina de projéteis. Inicializa as piscinas para cada
     *        tipo de projétil.
     */
    public ProjetilPool(int tamanhoNormais, int tamanhoHoming, ArrayList<Personagem> personagens) {
        poolNormais = new ArrayList<>(tamanhoNormais);
        for (int i = 0; i < tamanhoNormais; i++) {
            poolNormais.add(new Projetil("projectiles/hero/projectile1_hero.png"));
        }

        poolHoming = new ArrayList<>(tamanhoHoming);
        for (int i = 0; i < tamanhoHoming; i++) {
            poolHoming.add(new ProjetilHoming("projectiles/hero/projectile2_hero.png", personagens));
        }

        int tamanhoBombaHoming = 64;
        poolBombaHoming = new ArrayList<>(tamanhoBombaHoming);
        for (int i = 0; i < tamanhoBombaHoming; i++) {
            poolBombaHoming.add(new ProjetilBombaHoming("projectiles/hero/talisman_bomb.png", personagens));
        }

        int tamanhoInimigos = 100;
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
}