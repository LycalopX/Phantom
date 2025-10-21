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
import Auxiliar.LootTable;
import Auxiliar.ConfigMapa;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Rectangle;

/**
 * @brief Orquestra a lógica principal do jogo, incluindo detecção de colisão,
 *        interações entre personagens e gerenciamento de estado dos objetos da
 *        fase.
 */
public class ControleDeJogo {
    private Quadtree quadtree;

    private final ArrayList<Projetil> projeteisInimigosEspeciais;
    private final ArrayList<Personagem> novosObjetos;
    private final ArrayList<Personagem> alvosProximos;
    private final ArrayList<Personagem> vizinhosPotenciais;

    /**
     * @brief Construtor do ControleDeJogo. Inicializa a Quadtree e as listas de
     *        apoio.
     */
    public ControleDeJogo() {
        this.quadtree = new Quadtree(0, new Rectangle(0, 0, ConfigMapa.LARGURA_TELA, ConfigMapa.ALTURA_TELA));
        this.projeteisInimigosEspeciais = new ArrayList<>();
        this.novosObjetos = new ArrayList<>();
        this.alvosProximos = new ArrayList<>();
        this.vizinhosPotenciais = new ArrayList<>();
    }

    /**
     * @brief Desenha todos os personagens de uma lista na tela.
     */
    public void desenhaTudo(ArrayList<Personagem> e, Graphics g) {
        for (Personagem personagem : e) {
            personagem.autoDesenho(g);
        }
    }

    /**
     * @brief Processa todas as interações e colisões para um frame do jogo.
     *        Utiliza uma Quadtree para otimizar a detecção de colisão.
     * @param personagens       A lista de todos os personagens na fase.
     * @param removeProjectiles Flag para remover todos os projéteis da tela.
     * @return true se o herói foi atingido, false caso contrário.
     */
    public boolean processaTudo(ArrayList<Personagem> personagens, boolean removeProjectiles) {
        if (personagens.isEmpty())
            return false;

        Hero hero = null;
        BombaProjetil bombaAtiva = null;

        projeteisInimigosEspeciais.clear();
        quadtree.clear();

        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                hero = (Hero) p;
            }

            if (p.isActive()) {
                quadtree.insert(p);

                if (p instanceof BombaProjetil) {
                    bombaAtiva = (BombaProjetil) p;
                }

                if (p instanceof Projetil) {
                    if (removeProjectiles) {
                        p.deactivate();
                        continue;
                    }
                    Projetil proj = (Projetil) p;

                    if (proj.getTipoHitbox() == HitboxType.RECTANGULAR) {
                        projeteisInimigosEspeciais.add(proj);
                    }
                }
            }
        }

        if (hero == null)
            return false;

        novosObjetos.clear();
        boolean heroiFoiAtingido = false;

        if (checarColisaoRetangular(hero, projeteisInimigosEspeciais)) {
            heroiFoiAtingido = true;
        }

        if (bombaAtiva != null) {

            int raioEmPixels = (int) (bombaAtiva.getRaioAtualGrid() * ConfigMapa.CELL_SIDE);
            int diametroEmPixels = raioEmPixels * 2;
            
            int bombaX = (int) (bombaAtiva.x * ConfigMapa.CELL_SIDE) - raioEmPixels;
            int bombaY = (int) (bombaAtiva.y * ConfigMapa.CELL_SIDE) - raioEmPixels;
            Rectangle areaDaBomba = new Rectangle(bombaX, bombaY, diametroEmPixels, diametroEmPixels);

            alvosProximos.clear();
            quadtree.retrieve(alvosProximos, areaDaBomba);

            for (Personagem alvo : alvosProximos) {
                if ((alvo instanceof Inimigo || alvo instanceof Projetil) && alvo.isActive()) {

                    double dx = bombaAtiva.x - alvo.x;
                    double dy = bombaAtiva.y - alvo.y;
                    double distanciaAoQuadrado = (dx * dx) + (dy * dy);
                    double raioBombaAoQuadrado = bombaAtiva.getRaioAtualGrid() * bombaAtiva.getRaioAtualGrid();

                    if (distanciaAoQuadrado < raioBombaAoQuadrado) {
                        if (alvo instanceof Projetil) {
                            alvo.deactivate();
                        } else if (alvo instanceof Inimigo) {
                            ((Inimigo) alvo).takeDamage(9999);
                        }
                    }
                }
            }
        }

        for (Personagem p1 : personagens) {
            if (!p1.isActive())
                continue;

            vizinhosPotenciais.clear();
            quadtree.retrieve(vizinhosPotenciais, p1);

            for (Personagem p2 : vizinhosPotenciais) {
                if (p1.hashCode() >= p2.hashCode() || !p2.isActive())
                    continue;

                double somaRaios;
                double dx = p1.x - p2.x;
                double dy = p1.y - p2.y;

                if ((p1 instanceof Hero && p2 instanceof Item) || (p1 instanceof Item && p2 instanceof Hero)) {
                    Item i = (p1 instanceof Item) ? (Item) p1 : (Item) p2;
                    somaRaios = hero.grabHitboxRaio + i.hitboxRaio;
                } else {
                    somaRaios = p1.hitboxRaio + p2.hitboxRaio;
                }

                if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {
                    if (handleCollision(p1, p2, novosObjetos, hero)) {
                        heroiFoiAtingido = true;
                    }
                }
            }
        }

        personagens.addAll(novosObjetos);

        personagens.removeIf(p -> {
            if (p instanceof Projetil || p instanceof Hero) {
                return false;
            }
            return !p.isActive();
        });

        return heroiFoiAtingido;
    }

    /**
     * @brief Verifica se uma nova posição para um personagem é válida,
     *        checando por colisões com outros personagens não transponíveis.
     * @return true se a posição for válida, false caso contrário.
     */
    public boolean ehPosicaoValida(ArrayList<Personagem> umaFase, Personagem personagem, double proximoX,
            double proximoY) {
        for (Personagem p : umaFase) {
            if (p == personagem || p.isbTransponivel()) {
                continue;
            }

            double dx = proximoX - p.x;
            double dy = proximoY - p.y;
            double somaRaios = personagem.hitboxRaio + p.hitboxRaio;

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
     * @brief Verifica a colisão entre o herói e uma lista de projéteis com hitbox
     *        retangular.
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
     * @return true se a colisão resultou em dano ao herói, false caso contrário.
     */
    private boolean handleCollision(Personagem p1, Personagem p2, ArrayList<Personagem> novosObjetos, Hero hero) {
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
            colisaoProjetilHeroiInimigo((Projetil) p1, (Inimigo) p2, novosObjetos, hero);
        } else if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR
                && p1 instanceof Inimigo) {
            colisaoProjetilHeroiInimigo((Projetil) p2, (Inimigo) p1, novosObjetos, hero);
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
     * @brief Lida com a colisão entre um projétil do herói e um inimigo,
     *        aplicando dano e gerando loot se o inimigo for destruído.
     */
    private void colisaoProjetilHeroiInimigo(Projetil p, Inimigo i, ArrayList<Personagem> novosObjetos, Hero hero) {
        i.takeDamage(Hero.DANO_BALA);

        if (!(p instanceof ProjetilBombaHoming)) {
            p.deactivate();
        }

        if (i.getVida() <= 0) {
            LootTable tabela = i.getLootTable();
            if (tabela != null) {
                ArrayList<LootItem> drops = tabela.gerarDrops();
                for (LootItem dropInfo : drops) {
                    Item itemCriado = new Item(dropInfo.getTipo(), i.x, i.y, hero);
                    novosObjetos.add(itemCriado);
                }
            }
        }
    }
}