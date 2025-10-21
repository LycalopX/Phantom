package Auxiliar.Personagem;

import Modelo.Personagem;
import java.util.ArrayList;
import Auxiliar.ConfigMapa;
import java.awt.Rectangle;

/**
 * @brief Implementa uma estrutura de dados Quadtree para otimizar a detecção de
 *        colisão
 *        espacial, dividindo o espaço de jogo em quadrantes recursivamente.
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
     * @brief Limpa a Quadtree recursivamente, removendo todos os objetos e nós
     *        filhos.
     */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
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

        nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /**
     * @brief Determina em qual quadrante (sub-nó) um determinado personagem se
     *        encaixa.
     * @param p O personagem a ser analisado.
     * @return O índice do nó (0-3) ou -1 se o personagem não couber completamente
     *         em nenhum filho.
     */
    private int getIndex(Personagem p) {
        int pLargura = p.getLargura();
        int pAltura = p.getAltura();
        int pX = (int) (p.x * ConfigMapa.CELL_SIDE - pLargura / 2.0);
        int pY = (int) (p.y * ConfigMapa.CELL_SIDE - pAltura / 2.0);

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
     * @brief Insere um personagem na Quadtree. Se o nó exceder a capacidade,
     *        ele se subdivide e distribui seus objetos entre os filhos.
     * @param p O personagem a ser inserido.
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
     * @brief Retorna uma lista de personagens que podem colidir com um personagem
     *        específico.
     * @param returnObjects A lista onde os personagens próximos serão adicionados.
     * @param p             O personagem para o qual a verificação de colisão será
     *                      feita.
     * @return A lista `returnObjects` preenchida com os candidatos à colisão.
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
     * @brief Retorna todos os objetos que podem colidir com um dado retângulo
     *        (área).
     *        Útil para buscas em área, como explosões.
     * @param returnObjects A lista onde os personagens próximos serão adicionados.
     * @param pBounds       O retângulo que define a área de busca.
     * @return A lista `returnObjects` preenchida com os candidatos à colisão.
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
