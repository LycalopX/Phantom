package Auxiliar.Personagem;

import Modelo.Personagem;
import java.util.ArrayList;
import Auxiliar.ConfigMapa;
import java.awt.Rectangle;

/**
 * @brief Implementa uma estrutura de dados Quadtree para otimizar a detecção de colisão.
 * 
 * A Quadtree particiona o espaço de jogo em quadrantes recursivamente. Em vez de
 * verificar a colisão de um objeto contra todos os outros, a verificação é
 * limitada aos objetos no mesmo quadrante, melhorando drasticamente o desempenho
 * em cenas com muitos objetos.
 */
public class Quadtree {

    private final int MAX_OBJECTS = 4;
    private final int MAX_LEVELS = 8;

    private int level;
    private ArrayList<Personagem> objects;
    private Rectangle bounds;
    private Quadtree[] nodes;

    /**
     * @brief Construtor da Quadtree.
     * @param pLevel  O nível de profundidade atual do nó.
     * @param pBounds Os limites (área) que este nó da árvore representa.
     */
    public Quadtree(int pLevel, Rectangle pBounds) {
        level = pLevel;
        objects = new ArrayList<>();
        bounds = pBounds;
        nodes = new Quadtree[4];
    }

    /**
     * @brief Limpa a Quadtree recursivamente, removendo todos os objetos e nós filhos.
     */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null; // Garante que os nós filhos sejam removidos para a próxima inserção.
            }
        }
    }

    /**
     * @brief Subdivide o nó atual em quatro sub-nós (quadrantes).
     */
    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight)); // Nordeste
        nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight));             // Noroeste
        nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight)); // Sudoeste
        nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight)); // Sudeste
    }

    /**
     * @brief Determina em qual quadrante um personagem se encaixa.
     * @return O índice do nó (0-3) ou -1 se o personagem se sobrepuser a múltiplos quadrantes.
     */
    private int getIndex(Personagem p) {
        int pLargura = p.getLargura();
        int pAltura = p.getAltura();
        int pX = (int) (p.getX() * ConfigMapa.CELL_SIDE - pLargura / 2.0);
        int pY = (int) (p.getY() * ConfigMapa.CELL_SIDE - pAltura / 2.0);

        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        boolean topQuadrant = (pY < horizontalMidpoint && pY + pAltura < horizontalMidpoint);
        boolean bottomQuadrant = (pY > horizontalMidpoint);

        if (pX < verticalMidpoint && pX + pLargura < verticalMidpoint) {
            if (topQuadrant)
                index = 1;
            else if (bottomQuadrant)
                index = 2;
        } else if (pX > verticalMidpoint) {
            if (topQuadrant)
                index = 0;
            else if (bottomQuadrant)
                index = 3;
        }
        return index;
    }

    /**
     * @brief Insere um personagem na Quadtree.
     * 
     * Se o nó já tiver nós filhos, o personagem é inserido no filho apropriado.
     * Caso contrário, é adicionado à lista de objetos deste nó. Se a capacidade
     * do nó for excedida, ele se subdivide e redistribui seus objetos.
     */
    public void insert(Personagem p) {
        if (nodes[0] != null) {
            int index = getIndex(p);
            if (index != -1) {
                nodes[index].insert(p);
                return;
            }
        }

        objects.add(p);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }
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
     * @brief Retorna uma lista de personagens que podem colidir com um dado personagem.
     * @param returnObjects A lista onde os candidatos à colisão serão adicionados.
     * @param p O personagem de referência.
     * @return A lista `returnObjects` preenchida com os candidatos.
     */
    public ArrayList<Personagem> retrieve(ArrayList<Personagem> returnObjects, Personagem p) {
        if (nodes[0] != null) {
            int index = getIndex(p);
            if (index != -1) {
                nodes[index].retrieve(returnObjects, p);
            } else {
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i].retrieve(returnObjects, p);
                }
            }
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }

    /**
     * @brief Retorna todos os objetos que podem colidir com uma dada área retangular.
     * @param returnObjects A lista onde os candidatos à colisão serão adicionados.
     * @param pBounds O retângulo que define a área de busca.
     * @return A lista `returnObjects` preenchida com os candidatos.
     */
    public ArrayList<Personagem> retrieve(ArrayList<Personagem> returnObjects, Rectangle pBounds) {
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].bounds.intersects(pBounds)) {
                    nodes[i].retrieve(returnObjects, pBounds);
                }
            }
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }
}
