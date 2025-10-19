package Controler;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Projeteis.BombaProjetil;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilBombaHoming;
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
        BombaProjetil bombaAtiva = null;

        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                hero = (Hero) p;
            }
            if (p instanceof BombaProjetil && p.isActive()) {
                bombaAtiva = (BombaProjetil) p;
            }
        }

        if (hero == null)
            return false;

        quadtree.clear();
        for (Personagem p : personagens) {
            if (p.isActive()) {
                quadtree.insert(p);
            }
        }

        // Vamos marcar quem deve ser removido, e remover no final.
        ArrayList<Personagem> novosObjetos = new ArrayList<>();
        boolean heroiFoiAtingido = false;

        // --- LÓGICA DE DANO EM ÁREA DA BOMBA ---
        if (bombaAtiva != null) {

            // 1. Converte o raio da bomba (que está em GRID) para PIXELS
            int raioEmPixels = (int) (bombaAtiva.getRaioAtualGrid() * Consts.CELL_SIDE);
            int diametroEmPixels = raioEmPixels * 2;

            // 2. Cria um retângulo em PIXELS que representa a área de efeito da bomba
            int bombaX = (int) (bombaAtiva.x * Consts.CELL_SIDE) - raioEmPixels;
            int bombaY = (int) (bombaAtiva.y * Consts.CELL_SIDE) - raioEmPixels;
            Rectangle areaDaBomba = new Rectangle(bombaX, bombaY, diametroEmPixels, diametroEmPixels);

            List<Personagem> alvosProximos = new ArrayList<>();
            // 3. Usa o método retrieve que aceita um RECTANGLE (em PIXELS)
            quadtree.retrieve(alvosProximos, areaDaBomba);

            // 4. Itera APENAS sobre os alvos próximos para a checagem final
            for (Personagem alvo : alvosProximos) {
                if (alvo instanceof Inimigo && alvo.isActive()) {
                    double dx = bombaAtiva.x - alvo.x; // Distância em GRID
                    double dy = bombaAtiva.y - alvo.y; // Distância em GRID
                    double distanciaAoQuadrado = (dx * dx) + (dy * dy);
                    double raioBombaAoQuadrado = bombaAtiva.getRaioAtualGrid() * bombaAtiva.getRaioAtualGrid();

                    // 5. Checagem final (círculo vs círculo) em GRID para precisão
                    if (distanciaAoQuadrado < raioBombaAoQuadrado) {
                        ((Inimigo) alvo).takeDamage(9999);
                    }
                }
            }
        }

        // --- LÓGICA DE COLISÃO COM QUADTREE (DISCRETA) ---
        List<Personagem> vizinhosPotenciais = new ArrayList<>();
        for (Personagem p1 : personagens) {
            if (!p1.isActive())
                continue;

            // Reinicia e olha de volta a lista de vizinhos potenciais
            vizinhosPotenciais.clear();
            quadtree.retrieve(vizinhosPotenciais, p1);

            for (Personagem p2 : vizinhosPotenciais) {
                if (p1.hashCode() >= p2.hashCode() || !p2.isActive())
                    continue;

                double somaRaios;

                double dx = p1.x - p2.x;
                double dy = p1.y - p2.y;

                // 1. Verifica se a colisão é entre um Herói e um Item
                if ((p1 instanceof Hero && p2 instanceof Item) || (p1 instanceof Item && p2 instanceof Hero)) {

                    Item i = (p1 instanceof Item) ? (Item) p1 : (Item) p2;

                    // 2. USA A HITBOX DE COLETA (grabHitboxRaio) para o Herói
                    somaRaios = hero.grabHitboxRaio + i.hitboxRaio;

                } else {
                    // 3. Para TODAS as outras colisões (dano), usa a hitbox padrão
                    somaRaios = p1.hitboxRaio + p2.hitboxRaio;
                }

                if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {

                    if (handleCollision(p1, p2, novosObjetos, hero)) {
                        heroiFoiAtingido = true;
                    }
                }
            }
        }

        // Adiciona novos objetos (loot) e remove os "mortos"
        personagens.addAll(novosObjetos);

        personagens.removeIf(p -> {
            // A regra principal: NUNCA remova um projétil OU O HERÓI da lista.
            if (p instanceof Projetil || p instanceof Hero) {
                return false;
            }
            // Para todos os outros personagens (Inimigos, Itens, etc.),
            // remova-os se não estiverem mais ativos.
            return !p.isActive();
        });

        return heroiFoiAtingido;
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
                // Pega o valor do power diretamente do enum e passa para o herói
                heroi.addPower(tipo.getPowerValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item00");
                break;

            case FULL_POWER:
                heroi.addPower(tipo.getPowerValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item01");
                break;

            case BOMB:
                heroi.addBomb(tipo.getBombValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item01");
                break;

            case ONE_UP:
                heroi.addHP(1); // Itens de vida geralmente dão um valor fixo
                Auxiliar.SoundManager.getInstance().playSfx("se_item01");
                break;

            case SCORE_POINT:
                heroi.addScore(tipo.getScoreValue());
                Auxiliar.SoundManager.getInstance().playSfx("se_item00");
                break;

            default:
                System.out.println("Item desconhecido: " + tipo);
        }

        item.deactivate();
    }

    /**
     * Lida com a interação entre dois personagens que colidiram.
     */
    private boolean handleCollision(Personagem p1, Personagem p2, ArrayList<Personagem> novosObjetos, Hero hero) {

        // --- COLISÃO: HERÓI E INIMIGO ---
        if (p1 instanceof Hero && p2 instanceof Inimigo) {
            return colisaoHeroiInimigo((Hero) p1, (Inimigo) p2);
        } else if (p2 instanceof Hero && p1 instanceof Inimigo) {
            return colisaoHeroiInimigo((Hero) p2, (Inimigo) p1);
        }

        // --- COLISÃO: HERÓI E PROJÉTIL INIMIGO ---
        if (p1 instanceof Hero && p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.INIMIGO) {
            return colisaoHeroiProjetilInimigo((Hero) p1, (Projetil) p2);
        } else if (p2 instanceof Hero && p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.INIMIGO) {
            return colisaoHeroiProjetilInimigo((Hero) p2, (Projetil) p1);
        }

        // --- COLISÃO: PROJÉTIL DO HERÓI E INIMIGO ---
        if (p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.JOGADOR && p2 instanceof Inimigo) {
            colisaoProjetilHeroiInimigo((Projetil) p1, (Inimigo) p2, novosObjetos, hero);

        } else if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR
                && p1 instanceof Inimigo) {

            colisaoProjetilHeroiInimigo((Projetil) p2, (Inimigo) p1, novosObjetos, hero);
        }

        // --- COLISÃO: HERÓI E ITEM ---
        if (p1 instanceof Hero && p2 instanceof Item) {
            aplicarEfeitoDoItem((Hero) p1, (Item) p2);
        } else if (p2 instanceof Hero && p1 instanceof Item) {
            aplicarEfeitoDoItem((Hero) p2, (Item) p1);
        }

        if (p1 instanceof ProjetilBombaHoming && p2 instanceof Projetil
                && ((Projetil) p2).getTipo() == TipoProjetil.INIMIGO) {
            p2.deactivate(); // Destroi o projétil inimigo
            return false; // Não conta como um "hit" no jogador, então retorna false
        }
        
        if (p2 instanceof ProjetilBombaHoming && p1 instanceof Projetil
                && ((Projetil) p1).getTipo() == TipoProjetil.INIMIGO) {
            p1.deactivate(); // Destroi o projétil inimigo
            return false; // Não conta como um "hit" no jogador
        }

        return false;
    }

    // --- MÉTODOS AUXILIARES DE COLISÃO ---

    private boolean colisaoHeroiInimigo(Hero h, Inimigo i) {
        if (h.takeDamage()) { // takeDamage() agora retorna true se o dano for válido
            return true; // Informa que o herói foi atingido
        }
        return false;
    }

    private boolean colisaoHeroiProjetilInimigo(Hero h, Projetil p) {
        if (h.takeDamage()) {
            p.deactivate();
            return true; // Informa que o herói foi atingido
        }
        return false;
    }

    private void colisaoProjetilHeroiInimigo(Projetil p, Inimigo i, ArrayList<Personagem> novosObjetos, Hero hero) {

        i.takeDamage(Consts.DANO_BALA);

        if (!(p instanceof ProjetilBombaHoming)) {
            p.deactivate();
        }

        if (i.getVida() <= 0) {
            // Lógica de drop de loot
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