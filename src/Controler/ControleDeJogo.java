package Controler;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.Item;
import Modelo.Items.ItemType;
import Modelo.Projeteis.Projetil;
import Auxiliar.LootItem;
import Auxiliar.LootTable;
import Auxiliar.TipoProjetil;
import Auxiliar.Consts;

import java.awt.Graphics;
import java.util.ArrayList;

public class ControleDeJogo {

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

        // MUDANÇA 1: Criar "Listas de Remoção"
        // Vamos marcar quem deve ser removido, e remover no final.
        ArrayList<Personagem> objetosARemover = new ArrayList<>();
        ArrayList<Personagem> novosObjetos = new ArrayList<>();

        // Se a bomba do herói estiver ativa, marque todos os inimigos e seus projéteis.
        if (hero.isBombaAtiva()) {
            for (Personagem p : personagens) {
                if (p instanceof Inimigo) {
                    ((Inimigo) p).takeDamage(9999); // Mata o inimigo instantaneamente
                    objetosARemover.add(p);
                }
                if (p instanceof Projetil && ((Projetil) p).getTipo() == TipoProjetil.INIMIGO) {
                    // Adiciona à lista de remoção (sem checagem de duplicata, removeAll lidará com
                    // isso)
                    objetosARemover.add(p);
                }
            }
        }

        // Loop principal de colisão (agora podemos iterar para frente sem medo)
        for (Personagem p1 : personagens) {

            // Lógica de colisão do Herói
            if (p1 instanceof Hero) {
                Hero h = (Hero) p1;
                if (h.isActive() == false)
                    continue; // Se o herói não está ativo, pula suas colisões

                for (Personagem p2 : personagens) {
                    if (p1 == p2)
                        continue;
                    double dx = p1.x - p2.x;
                    double dy = p1.y - p2.y;
                    double somaRaios = p1.hitboxRaio + p2.hitboxRaio;
                    // Compara o quadrado da distância com o quadrado da soma dos raios

                    if (p2 instanceof Item) {
                        if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {
                            aplicarEfeitoDoItem(h, (Item) p2);

                            objetosARemover.add(p2); // Marca o item para remoção
                        }

                    } else if (p2.isbMortal()) {
                        if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR) {
                            continue;
                        }

                        if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {

                            if (!h.isInvencivel()) {
                                h.takeDamage();
                            }

                            objetosARemover.add(p2);

                            return false;
                        }
                    }
                }
            }
            // Lógica de colisão dos Projéteis do Jogador
            else if (p1 instanceof Projetil && ((Projetil) p1).getTipo() == TipoProjetil.JOGADOR) {
                // Se o projétil já foi marcado para remoção, pula.
                if (objetosARemover.contains(p1))
                    continue;

                for (Personagem p2 : personagens) {

                    double dx = p1.x - p2.x;
                    double dy = p1.y - p2.y;
                    double somaRaios = p1.hitboxRaio + p2.hitboxRaio;
                    // Compara o quadrado da distância com o quadrado da

                    if (p2 instanceof Inimigo) {

                        if ((dx * dx) + (dy * dy) < (somaRaios * somaRaios)) {

                            LootTable tabela = ((Inimigo) p2).getLootTable();
                            if (tabela != null) {
                                ArrayList<LootItem> drops = tabela.gerarDrops();

                                for (LootItem dropInfo : drops) {
                                    // Simplesmente cria o item. A gravidade no método 'atualizar' do Item
                                    // fará com que ele comece a cair automaticamente.
                                    Item itemCriado = new Item(dropInfo.getTipo(), p2.x, p2.y);
                                    novosObjetos.add(itemCriado);
                                }
                            }
                            
                            ((Projetil) p1).deactivate();

                            ((Inimigo) p2).takeDamage(Consts.DANO_BALA);

                            if (((Inimigo) p2).getVida() <= 0) {
                                objetosARemover.add(p2);
                            }

                            break;
                        }
                    }
                }
            }
        }

        // MUDANÇA 2: Remover todos os objetos marcados de uma só vez
        personagens.removeAll(objetosARemover);
        personagens.addAll(novosObjetos);
        return true;
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
    }
}
