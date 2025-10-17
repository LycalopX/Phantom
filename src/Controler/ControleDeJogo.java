package Controler;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Projeteis.BombaProjetil;
import Modelo.Projeteis.Projetil;
import Auxiliar.Quadtree;
import Auxiliar.LootItem;
import Auxiliar.LootTable;
import Auxiliar.TipoProjetil;
import Auxiliar.Consts;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class ControleDeJogo {
    private Quadtree quadtree;

    public ControleDeJogo() {
        // Cria a Quadtree uma vez, cobrindo a tela do jogo em PIXELS
        this.quadtree = new Quadtree(0, new Rectangle(0, 0, Consts.largura, Consts.altura));
    }

    public void desenhaTudo(ArrayList<Personagem> e, Graphics g) {
        for (Personagem personagem : e) {
            personagem.autoDesenho(g);
        }
    }

    public boolean processaTudo(ArrayList<Personagem> personagens) {
        if (personagens.isEmpty())
            return false;

        Hero hero = null;
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                hero = (Hero) p;
                break;
            }
        }
        if (hero == null)
            return false;

        quadtree.clear();
        for (Personagem p : personagens) {
            // Apenas insere personagens ativos na Quadtree
            if (p instanceof Projetil && !((Projetil) p).isActive())
                continue;
            if (p instanceof Hero && !((Hero) p).isActive())
                continue;
            if (p instanceof Item && !((Item) p).isActive())
                continue;

            quadtree.insert(p);
        }

        // Vamos marcar quem deve ser removido, e remover no final.
        ArrayList<Personagem> objetosARemover = new ArrayList<>();
        ArrayList<Personagem> novosObjetos = new ArrayList<>();

        // --- LÓGICA DE COLISÃO COM QUADTREE ---
        List<Personagem> vizinhosPotenciais = new ArrayList<>();
        for (Personagem p1 : personagens) {
            if (!isPersonagemAtivo(p1))
                continue;

            // Reinicia e olha de volta a lista de vizinhos potenciais
            vizinhosPotenciais.clear();
            quadtree.retrieve(vizinhosPotenciais, p1);

            for (Personagem p2 : vizinhosPotenciais) {
                if (p1.hashCode() >= p2.hashCode() || !isPersonagemAtivo(p2))
                    continue;

                double somaRaios;

                double dx = p1.x - p2.x;
                double dy = p1.y - p2.y;

                // 1. Verifica se a colisão é entre um Herói e um Item
                if ((p1 instanceof Hero && p2 instanceof Item) || (p1 instanceof Item && p2 instanceof Hero)) {

                    Hero h = (p1 instanceof Hero) ? (Hero) p1 : (Hero) p2;
                    Item i = (p1 instanceof Item) ? (Item) p1 : (Item) p2;

                    // 2. USA A HITBOX DE COLETA (grabHitboxRaio) para o Herói
                    somaRaios = h.grabHitboxRaio + i.hitboxRaio;

                } else {
                    // 3. Para TODAS as outras colisões (dano), usa a hitbox padrão
                    somaRaios = p1.hitboxRaio + p2.hitboxRaio;
                }

                if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {
                    handleCollision(p1, p2, novosObjetos);
                }
            }
        }

        // Adiciona novos objetos (loot) e remove os "mortos"
        personagens.addAll(novosObjetos);

        personagens.removeIf(p -> {
            // A regra principal: NUNCA remova um projétil da lista.
            // Eles são gerenciados pela ProjetilPool e apenas desativados/reativados.
            if (p instanceof Projetil) {
                return false;
            }
            // Para todos os outros personagens (Inimigos, Itens, etc.),
            // remova-os se não estiverem mais ativos.
            return !isPersonagemAtivo(p);
        });

        return hero.getHP() > 0;
    }

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
                return false; // Colisão detectada
            }
        }
        return true;
    }

    private void aplicarEfeitoDoItem(Hero heroi, Item item) {
        // 1. Pega o tipo do item a partir do enum
        ItemType tipo = item.getTipo();

        // 2. Usa um switch para determinar qual efeito aplicar.
        // Isso é muito mais limpo e rápido que vários if/else.
        switch (tipo) {
            case MINI_POWER_UP:
            case POWER_UP:
            case FULL_POWER:
                // Pega o valor do power diretamente do enum e passa para o herói
                heroi.addPower(tipo.getPowerValue());
                break;

            case BOMB:
                heroi.addBomb(tipo.getBombValue());
                break;

            case ONE_UP:
                heroi.addHP(1); // Itens de vida geralmente dão um valor fixo
                break;

            case SCORE_POINT:
                heroi.addScore(tipo.getScoreValue());
                break;

            default:
                System.out.println("Item desconhecido: " + tipo);
        }

        item.deactivate();
    }

    /**
     * Lida com a interação entre dois personagens que colidiram.
     */
    private void handleCollision(Personagem p1, Personagem p2, ArrayList<Personagem> novosObjetos) {
        // --- COLISÃO: HERÓI E INIMIGO ---
        if (p1 instanceof Hero && p2 instanceof Inimigo) {
            colisaoHeroiInimigo((Hero) p1, (Inimigo) p2);
        } else if (p2 instanceof Hero && p1 instanceof Inimigo) {
            colisaoHeroiInimigo((Hero) p2, (Inimigo) p1);
        }

        // --- COLISÃO: HERÓI E PROJÉTIL INIMIGO ---
        if (p1 instanceof Hero && p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.INIMIGO) {
            colisaoHeroiProjetilInimigo((Hero) p1, (Projetil) p2);
        } else if (p2 instanceof Hero && p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.INIMIGO) {
            colisaoHeroiProjetilInimigo((Hero) p2, (Projetil) p1);
        }

        // --- COLISÃO: PROJÉTIL DO HERÓI E INIMIGO ---
        if (p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.JOGADOR && p2 instanceof Inimigo) {
            colisaoProjetilHeroiInimigo((Projetil) p1, (Inimigo) p2, novosObjetos);
        } else if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR
                && p1 instanceof Inimigo) {
            colisaoProjetilHeroiInimigo((Projetil) p2, (Inimigo) p1, novosObjetos);
        }

        // --- COLISÃO: HERÓI E ITEM ---
        if (p1 instanceof Hero && p2 instanceof Item) {
            aplicarEfeitoDoItem((Hero) p1, (Item) p2);
        } else if (p2 instanceof Hero && p1 instanceof Item) {
            aplicarEfeitoDoItem((Hero) p2, (Item) p1);
        }

        // --- COLISÃO: BOMBA E INIMIGO ---
        if (p1 instanceof BombaProjetil && p2 instanceof Inimigo) {
            ((Inimigo) p2).takeDamage(9999);
        } else if (p2 instanceof BombaProjetil && p1 instanceof Inimigo) {
            ((Inimigo) p1).takeDamage(9999);
        }

        // --- COLISÃO: BOMBA E PROJÉTIL INIMIGO ---
        if (p1 instanceof BombaProjetil && p2 instanceof Projetil
                && ((Projetil) p2).getTipo() == TipoProjetil.INIMIGO) {
            ((Projetil) p2).deactivate();
        } else if (p2 instanceof BombaProjetil && p1 instanceof Projetil
                && ((Projetil) p1).getTipo() == TipoProjetil.INIMIGO) {
            ((Projetil) p1).deactivate();
        }
    }

    // --- MÉTODOS AUXILIARES DE COLISÃO ---
    private void colisaoHeroiInimigo(Hero h, Inimigo i) {
        if (!h.isInvencivel()) {
            h.takeDamage();
            i.takeDamage(9999); // Inimigo também morre na colisão
        }
    }

    private void colisaoHeroiProjetilInimigo(Hero h, Projetil p) {
        if (!h.isInvencivel()) {
            h.takeDamage();
            p.deactivate();
        }
    }

    private void colisaoProjetilHeroiInimigo(Projetil p, Inimigo i, ArrayList<Personagem> novosObjetos) {
        p.deactivate();
        i.takeDamage(Consts.DANO_BALA);

        if (i.getVida() <= 0) {
            // Lógica de drop de loot
            LootTable tabela = i.getLootTable();
            if (tabela != null) {
                ArrayList<LootItem> drops = tabela.gerarDrops();
                for (LootItem dropInfo : drops) {
                    Item itemCriado = new Item(dropInfo.getTipo(), i.x, i.y);
                    novosObjetos.add(itemCriado);
                }
            }
        }
    }

    // --- MÉTODOS DE UTILIDADE ---
    private boolean isPersonagemAtivo(Personagem p) {
        // Regras especiais para classes com a flag 'isActive'
        if (p instanceof Projetil)
            return ((Projetil) p).isActive();
        if (p instanceof Hero)
            return ((Hero) p).isActive();
        if (p instanceof Item)
            return ((Item) p).isActive();

        // Regra geral para todas as outras classes (Inimigo, BombaProjetil)
        return p.getVida() > 0;
    }

    private Hero findHero(ArrayList<Personagem> personagens) {
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                return (Hero) p;
            }
        }
        return null;
    }
}
