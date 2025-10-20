// Em src/Modelo/Projeteis/ProjetilPool.java
package Modelo.Projeteis;
import Modelo.Personagem;

import java.io.Serializable;
import java.util.ArrayList;

public class ProjetilPool implements Serializable {
    private ArrayList<Projetil> poolNormais;
    private ArrayList<ProjetilHoming> poolHoming;
    private ArrayList<ProjetilBombaHoming> poolBombaHoming;
    private ArrayList<Projetil> poolInimigos;

    public ProjetilPool(int tamanhoNormais, int tamanhoHoming, ArrayList<Personagem> personagens) {
        // Inicializa a piscina de projéteis normais
        poolNormais = new ArrayList<>(tamanhoNormais);
        for (int i = 0; i < tamanhoNormais; i++) {
            poolNormais.add(new Projetil("projectiles/hero/projectile1_hero.png"));
        }
        
        // Inicializa a piscina de projéteis teleguiados
        poolHoming = new ArrayList<>(tamanhoHoming);
        for (int i = 0; i < tamanhoHoming; i++) {
            poolHoming.add(new ProjetilHoming("projectiles/hero/projectile2_hero.png", personagens));
        }

        // Inicializa a piscina de projéteis da bomba.
        int tamanhoBombaHoming = 64;
        poolBombaHoming = new ArrayList<>(tamanhoBombaHoming);
        for (int i = 0; i < tamanhoBombaHoming; i++) {
            // Usaremos o sprite do míssil teleguiado como placeholder. Você pode mudar para um sprite novo depois.
            poolBombaHoming.add(new ProjetilBombaHoming("projectiles/hero/talisman_bomb.png", personagens));
        }

        // Inicializa a piscina de projéteis de inimigos
        int tamanhoInimigos = 100;
        poolInimigos = new ArrayList<>(tamanhoInimigos);
        for (int i = 0; i < tamanhoInimigos; i++) {
            poolInimigos.add(new Projetil("projectiles/inimigos/esferas.png"));
        }
    }

    /**
     * Retorna TODOS os projéteis (de todas as piscinas) para que a Fase
     * possa adicioná-los à lista principal de renderização.
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
     * Pega um projétil SIMPLES e inativo da piscina.
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
     * Pega um projétil TELEGUIADO e inativo da piscina.
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
     * Pega um projétil de BOMBA e inativo da piscina.
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
     * Pega um projétil de INIMIGO e inativo da piscina.
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