package Modelo.Projeteis;
import Modelo.Personagem;

import java.io.Serializable;
import java.util.ArrayList;

public class ProjetilPool implements Serializable {
    // Duas piscinas internas, uma para cada tipo
    private ArrayList<Projetil> poolNormais;
    private ArrayList<ProjetilHoming> poolHoming;

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
     * Retorna TODOS os projéteis (de ambas as piscinas) para que a Fase
     * possa adicioná-los à lista principal de renderização.
     */
    public ArrayList<Projetil> getTodosOsProjeteis() {
        ArrayList<Projetil> todos = new ArrayList<>();
        todos.addAll(poolNormais);
        todos.addAll(poolHoming);
        return todos;
    }
}