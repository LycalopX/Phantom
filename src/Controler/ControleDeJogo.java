package Controler;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Projeteis.BombaProjetil;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilBombaHoming;
import Auxiliar.Personagem.LootItem;
import Auxiliar.Personagem.Quadtree;
import Auxiliar.Projeteis.HitboxType;
import Auxiliar.Projeteis.TipoProjetil;
import Modelo.Items.ItemPool;
import Auxiliar.LootTable;
import Auxiliar.ConfigMapa;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.util.List;
import Modelo.Inimigos.Boss;

/**
 * @brief Orquestra a lógica de interações e colisões do jogo.
 * 
 *        Utiliza uma Quadtree para otimizar a detecção de colisão e gerencia
 *        as interações entre todos os personagens da fase.
 */
public class ControleDeJogo {
    private Quadtree quadtree;
    private ItemPool itemPool;

    // Listas pré-alocadas para evitar a criação de novos objetos a cada frame,
    // otimizando o desempenho e reduzindo a carga no Garbage Collector.
    private final ArrayList<Projetil> projeteisInimigosEspeciais;
    private final ArrayList<Personagem> novosObjetos;
    private final ArrayList<Personagem> alvosProximos;
    private final ArrayList<Personagem> vizinhosPotenciais;

    /**
     * @brief Construtor do ControleDeJogo.
     * 
     *        Inicializa a Quadtree, que abrange toda a área do jogo, e as listas
     *        de apoio usadas para otimizar o processamento de colisões.
     */
    public ControleDeJogo(ItemPool itemPool) {
        this.quadtree = new Quadtree(0, new Rectangle(0, 0, ConfigMapa.LARGURA_TELA, ConfigMapa.ALTURA_TELA));
        this.itemPool = itemPool;
        this.projeteisInimigosEspeciais = new ArrayList<>();
        this.novosObjetos = new ArrayList<>();
        this.alvosProximos = new ArrayList<>();
        this.vizinhosPotenciais = new ArrayList<>();
    }

    public void setItemPool(ItemPool itemPool) {
        this.itemPool = itemPool;
    }

    /**
     * @brief Desenha todos os personagens de uma lista na tela. (Atualmente não
     *        utilizado)
     */
    public void desenhaTudo(List<Personagem> e, Graphics g) {
        for (Personagem personagem : e) {
            personagem.autoDesenho(g);
        }
    }

    /**
     * @brief Processa todas as interações e colisões para um frame do jogo.
     * 
     *        Este é um método central que:
     *        1. Insere todos os personagens ativos em uma Quadtree.
     *        2. Verifica colisões da bomba e de projéteis retangulares.
     *        3. Itera sobre os personagens, usando a Quadtree para encontrar
     *        vizinhos
     *        próximos e testar colisões apenas contra eles.
     * 
     * @return true se o herói foi atingido, false caso contrário.
     */
    public boolean processaTudo(Hero hero, List<Inimigo> inimigos, List<Projetil> projeteis, List<Item> itens,
            List<BombaProjetil> bombas, boolean removeProjectiles) {
        if (hero == null)
            return false;

        BombaProjetil bombaAtiva = null;
        if (!bombas.isEmpty()) {
            bombaAtiva = bombas.get(0);
        }

        boolean bombsOnField = false;
        Boss boss = null;

        projeteisInimigosEspeciais.clear();
        quadtree.clear();

        // Insere todos os personagens ativos na Quadtree para otimizar a
        // detecção de colisões.
        quadtree.insert(hero);

        for (Inimigo i : inimigos) {
            if (i.isActive()) {
                quadtree.insert(i);
                if (i instanceof Boss) {
                    boss = (Boss) i;
                }
            }
        }

        for (Projetil p : projeteis) {
            if (p.isActive()) {
                quadtree.insert(p);
                if (p instanceof ProjetilBombaHoming) {
                    bombsOnField = true;
                }
                if (removeProjectiles) {
                    p.deactivate();
                    continue;
                }
                // Projéteis com hitbox retangular (como lasers) são tratados separadamente.
                if (p.getTipoHitbox() == HitboxType.RECTANGULAR) {
                    projeteisInimigosEspeciais.add(p);
                }
            }
        }

        for (Item i : itens) {
            if (i.getY() > ConfigMapa.MUNDO_ALTURA) {
                i.deactivate();
            }
            if (i.isActive()) {
                quadtree.insert(i);
            }
        }

        for (BombaProjetil b : bombas) {
            if (b.isActive()) {
                quadtree.insert(b);
            }
        }

        if (boss != null && !bombsOnField) {
            boss.setBombed(false);
        }

        novosObjetos.clear();
        boolean heroiFoiAtingido = false;

        // Checagem de colisão especial para projéteis retangulares.
        if (checarColisaoRetangular(hero, projeteisInimigosEspeciais)) {
            heroiFoiAtingido = true;
        }

        // Lógica de dano da bomba: verifica quais personagens estão dentro do raio
        // de efeito da bomba ativa.
        if (bombaAtiva != null) {
            int raioEmPixels = (int) (bombaAtiva.getRaioAtualGrid() * ConfigMapa.CELL_SIDE);
            int diametroEmPixels = raioEmPixels * 2;
            int bombaX = (int) (bombaAtiva.getX() * ConfigMapa.CELL_SIDE) - raioEmPixels;
            int bombaY = (int) (bombaAtiva.getY() * ConfigMapa.CELL_SIDE) - raioEmPixels;
            Rectangle areaDaBomba = new Rectangle(bombaX, bombaY, diametroEmPixels, diametroEmPixels);

            alvosProximos.clear();
            quadtree.retrieve(alvosProximos, areaDaBomba);

            for (Personagem alvo : alvosProximos) {
                if ((alvo instanceof Inimigo || alvo instanceof Projetil) && alvo.isActive()) {
                    double dx = bombaAtiva.getX() - alvo.getX();
                    double dy = bombaAtiva.getY() - alvo.getY();
                    double distanciaAoQuadrado = (dx * dx) + (dy * dy);
                    double raioBombaAoQuadrado = bombaAtiva.getHitboxRaio() * bombaAtiva.getHitboxRaio();

                    if (distanciaAoQuadrado < raioBombaAoQuadrado) {
                        if (alvo instanceof Projetil) {
                            alvo.deactivate();
                        } else if (alvo instanceof Inimigo) {
                            if (alvo instanceof Boss && !((Boss) alvo).isBombed()) {
                                ((Boss) alvo).takeDamage(500);
                                ((Boss) alvo).setBombed(true);
                            } else {
                                ((Inimigo) alvo).takeDamage(9999);
                            }
                        }
                    }
                }
            }
        }

        // Agrupa todos os personagens para a verificação principal de colisão.
        ArrayList<Personagem> todosOsPersonagens = new ArrayList<>();
        todosOsPersonagens.add(hero);
        todosOsPersonagens.addAll(inimigos);
        todosOsPersonagens.addAll(projeteis);
        todosOsPersonagens.addAll(itens);
        todosOsPersonagens.addAll(bombas);

        // Itera sobre cada personagem e verifica colisões apenas com seus vizinhos
        // potenciais, obtidos da Quadtree.
        for (Personagem p1 : todosOsPersonagens) {
            if (!p1.isActive())
                continue;

            vizinhosPotenciais.clear();
            quadtree.retrieve(vizinhosPotenciais, p1);

            for (Personagem p2 : vizinhosPotenciais) {
                // A condição p1.hashCode() >= p2.hashCode() evita a verificação duplicada
                // de pares (p1, p2) e (p2, p1).
                if (p1.hashCode() >= p2.hashCode() || !p2.isActive())
                    continue;

                double somaRaios;
                double dx = p1.getX() - p2.getX();
                double dy = p1.getY() - p2.getY();

                // Itens têm um raio de atração maior para facilitar a coleta.
                if ((p1 instanceof Hero && p2 instanceof Item) || (p1 instanceof Item && p2 instanceof Hero)) {
                    Item i = (p1 instanceof Item) ? (Item) p1 : (Item) p2;
                    somaRaios = hero.getGrabHitboxRaio() + i.getHitboxRaio();
                } else {
                    somaRaios = p1.getHitboxRaio() + p2.getHitboxRaio();
                }

                if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {
                    if (handleCollision(p1, p2, hero)) {
                        heroiFoiAtingido = true;
                    }
                }
            }
        }

        return heroiFoiAtingido;
    }

    /**
     * @brief Verifica se uma nova posição para um personagem é válida.
     * 
     *        Checa por colisões com outros personagens não transponíveis para
     *        impedir que o herói se sobreponha a eles.
     * 
     * @return true se a posição for válida, false caso contrário.
     */
    public boolean ehPosicaoValida(List<Inimigo> inimigos, Personagem personagem, double proximoX,
            double proximoY) {
        for (Personagem p : inimigos) {
            if (p == personagem || p.isTransponivel()) {
                continue;
            }

            double dx = proximoX - p.getX();
            double dy = proximoY - p.getY();
            double somaRaios = personagem.getHitboxRaio() + p.getHitboxRaio();

            if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @brief Aplica o efeito de um item coletado ao herói.
     */
    private void aplicarEfeitoDoItem(Hero heroi, Item item) {
        ItemType tipo = item.getTipo();
        switch (tipo) {
            case MINI_POWER_UP:
            case POWER_UP:
                heroi.addPower(tipo.getPowerValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item00", 0.5f);
                break;
            case FULL_POWER:
                heroi.addPower(tipo.getPowerValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item01", 0.5f);
                break;
            case BOMB:
                heroi.addBomb(tipo.getBombValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item01", 0.5f);
                break;
            case ONE_UP:
                heroi.addHP(1);
                Auxiliar.SoundManager.getInstance().playSfx("se_item01", 0.5f);
                break;
            case SCORE_POINT:
                heroi.addScore(tipo.getScoreValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item00", 0.5f);
                break;
            default:
                System.out.println("Item desconhecido: " + tipo);
        }
        item.deactivate();
    }

    /**
     * @brief Verifica a colisão entre o herói e projéteis com hitbox retangular.
     * @return true se houver colisão, false caso contrário.
     */
    private boolean checarColisaoRetangular(Hero hero, ArrayList<Projetil> projeteisRetangulares) {
        if (hero == null || projeteisRetangulares.isEmpty()) {
            return false;
        }

        Rectangle heroBounds = hero.getBounds();
        boolean heroiAtingido = false;

        for (Projetil p : projeteisRetangulares) {
            if (!p.isActive()) {
                continue;
            }

            Rectangle projetilBounds = p.getBounds();

            if (heroBounds.intersects(projetilBounds)) {
                if (colisaoHeroiProjetilInimigo(hero, p)) {
                    heroiAtingido = true;
                }
            }
        }
        return heroiAtingido;
    }

    /**
     * @brief Gerencia a lógica de interação para um par de personagens que
     *        colidiram.
     * 
     *        Determina o tipo de cada personagem e chama o método de tratamento de
     *        colisão apropriado (ex: Herói vs Inimigo, Projétil vs Inimigo).
     * 
     * @return true se a colisão resultou em dano ao herói, false caso contrário.
     */
    private boolean handleCollision(Personagem p1, Personagem p2, Hero hero) {

        if (p1 instanceof ProjetilBombaHoming && p2 instanceof Boss) {
            if (((Boss) p2).isBombed()) {
                return false;
            }

            ((Boss) p2).takeDamage(500);
            ((Boss) p2).setBombed(true);

            return false;
        } else if (p2 instanceof ProjetilBombaHoming && p1 instanceof Boss) {
            if (((Boss) p1).isBombed()) {
                return false;
            }

            ((Boss) p1).takeDamage(500);
            ((Boss) p1).setBombed(true);

            return false;
        }

        if (p1 instanceof Hero && p2 instanceof Inimigo) {
            return colisaoHeroiInimigo((Hero) p1, (Inimigo) p2);
        } else if (p2 instanceof Hero && p1 instanceof Inimigo) {
            return colisaoHeroiInimigo((Hero) p2, (Inimigo) p1);
        }

        if (p1 instanceof Hero && p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.INIMIGO) {
            return colisaoHeroiProjetilInimigo((Hero) p1, (Projetil) p2);
        } else if (p2 instanceof Hero && p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.INIMIGO) {
            return colisaoHeroiProjetilInimigo((Hero) p2, (Projetil) p1);
        }

        if (p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.JOGADOR && p2 instanceof Inimigo) {
            colisaoProjetilHeroiInimigo((Projetil) p1, (Inimigo) p2, hero);
        } else if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR
                && p1 instanceof Inimigo) {
            colisaoProjetilHeroiInimigo((Projetil) p2, (Inimigo) p1, hero);
        }

        if (p1 instanceof Hero && p2 instanceof Item) {
            aplicarEfeitoDoItem((Hero) p1, (Item) p2);
        } else if (p2 instanceof Hero && p1 instanceof Item) {
            aplicarEfeitoDoItem((Hero) p2, (Item) p1);
        }

        if (p1 instanceof ProjetilBombaHoming && p2 instanceof Projetil
                && ((Projetil) p2).getTipo() == TipoProjetil.INIMIGO) {
            p2.deactivate();
            return false;
        }

        if (p2 instanceof ProjetilBombaHoming && p1 instanceof Projetil
                && ((Projetil) p1).getTipo() == TipoProjetil.INIMIGO) {
            p1.deactivate();
            return false;
        }

        return false;
    }

    /**
     * @brief Lida com a colisão entre o herói e um inimigo.
     */
    private boolean colisaoHeroiInimigo(Hero h, Inimigo i) {
        if (h.takeDamage()) {
            return true;
        }
        return false;
    }

    /**
     * @brief Lida com a colisão entre o herói e um projétil inimigo.
     */
    private boolean colisaoHeroiProjetilInimigo(Hero h, Projetil p) {
        if (h.takeDamage()) {
            p.deactivate();
            return true;
        }
        return false;
    }

    /**
     * @brief Lida com a colisão entre um projétil do herói e um inimigo.
     * 
     *        Aplica dano ao inimigo e, se o inimigo for destruído, gera os itens
     *        de sua tabela de loot.
     */
    private void colisaoProjetilHeroiInimigo(Projetil p, Inimigo i, Hero hero) {

        i.takeDamage(Hero.DANO_BALA);

        if (!(p instanceof ProjetilBombaHoming)) {
            p.deactivate();
        }

        if (i.getVida() <= 0) {
            LootTable tabela = i.getLootTable();
            if (tabela != null) {
                ArrayList<LootItem> drops = tabela.gerarDrops();

                for (LootItem dropInfo : drops) {
                    Item itemCriado = itemPool.getItem(dropInfo.getTipo());
                    if (itemCriado != null) {
                        itemCriado.init(i.getX(), i.getY());
                    }
                }
            }
        }
    }
}