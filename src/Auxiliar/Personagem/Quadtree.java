package Auxiliar.Personagem;

import Modelo.Personagem;
import java.util.ArrayList;

import Auxiliar.ConfigMapa;

import java.awt.Rectangle; // Gerenciar os limites

public class Quadtree {

    private final int MAX_OBJECTS = 4; // Objetos por nó antes de subdividir
    private final int MAX_LEVELS = 8; // Profundidade máxima da árvore

    private int level;
    private ArrayList<Personagem> objects;
    private Rectangle bounds;
    private Quadtree[] nodes; // Os 4 filhos (quadrantes)

    /** Construtor */
    public Quadtree(int pLevel, Rectangle pBounds) {
        level = pLevel;
        objects = new ArrayList<>();
        bounds = pBounds;
        nodes = new Quadtree[4];
    }

    /** Limpa a Quadtree recursivamente */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /** Subdivide o nó em 4 sub-nós */
    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight)); // Nordeste
        nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight)); // Noroeste
        nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight)); // Sudoeste
        nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight)); // Sudeste
    }

    /** Determina em qual quadrante um objeto pertence */
    private int getIndex(Personagem p) {

        // 1. Converte a posição de grid do personagem para um retângulo de pixels
        int pLargura = p.getLargura();
        int pAltura = p.getAltura();
        int pX = (int) (p.x * ConfigMapa.CELL_SIDE - pLargura / 2.0); // Canto superior esquerdo em pixels
        int pY = (int) (p.y * ConfigMapa.CELL_SIDE - pAltura / 2.0); // Canto superior esquerdo em pixels

        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        // 2. Realiza todos os cálculos usando as coordenadas de PIXEL convertidas
        boolean topQuadrant = (pY < horizontalMidpoint && pY + pAltura < horizontalMidpoint);
        boolean bottomQuadrant = (pY > horizontalMidpoint);

        if (pX < verticalMidpoint && pX + pLargura < verticalMidpoint) {
            if (topQuadrant)
                index = 1; // Noroeste
            else if (bottomQuadrant)
                index = 2; // Sudoeste
        } else if (pX > verticalMidpoint) {
            if (topQuadrant)
                index = 0; // Nordeste
            else if (bottomQuadrant)
                index = 3; // Sudeste
        }
        return index;
    }

    /** Insere um objeto na Quadtree */
    public void insert(Personagem p) {
        // Se este nó já tem filhos, passa o objeto para o filho apropriado
        if (nodes[0] != null) {
            int index = getIndex(p);
            if (index != -1) {
                nodes[index].insert(p);
                return;
            }
        }

        // Se não tem filhos (ou o objeto não coube em nenhum), adiciona à lista deste
        // nó
        objects.add(p);

        // Se o nó excedeu a capacidade e ainda pode se subdividir
        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }
            // Move os objetos do pai para os filhos
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    /**
     * Retorna todos os objetos que podem colidir com o objeto dado.
     * VERSÃO CORRIGIDA que lida com objetos em fronteiras.
     */
    public ArrayList<Personagem> retrieve(ArrayList<Personagem> returnObjects, Personagem p) {
        // Se este nó tem filhos, descubra onde o objeto p se encaixa
        if (nodes[0] != null) {
            int index = getIndex(p);

            // Se o objeto 'p' cabe completamente em um quadrante filho,
            // apenas busque nesse quadrante.
            if (index != -1) {
                nodes[index].retrieve(returnObjects, p);
            }
            // SENÃO (se 'p' está em uma fronteira e não coube em nenhum filho)
            else {
                // O objeto pode estar colidindo com qualquer um dos quadrantes filhos.
                // Busque em TODOS eles.
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i].retrieve(returnObjects, p);
                }
            }
        }

        // Adiciona todos os objetos que pertencem a ESTE nó (nó pai)
        returnObjects.addAll(objects);

        return returnObjects;
    }

    /**
     * Retorna todos os objetos que podem colidir com um dado retângulo.
     * Perfeito para buscas em área, como explosões.
     */
    public ArrayList<Personagem> retrieve(ArrayList<Personagem> returnObjects, Rectangle pBounds) {
        // Se este nó tem filhos, verifique em quais deles o retângulo se sobrepõe
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].bounds.intersects(pBounds)) {
                    nodes[i].retrieve(returnObjects, pBounds);
                }
            }
        }

        // Adiciona todos os objetos que pertencem a ESTE nó
        returnObjects.addAll(objects);

        return returnObjects;
    }
}
