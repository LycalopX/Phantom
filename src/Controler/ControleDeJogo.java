package Controler;

import Modelo.Personagem;
import Modelo.Projetil;
import Modelo.Hero;
import Modelo.Inimigo;
import Auxiliar.LootItem;
import Auxiliar.LootTable;
import Auxiliar.TipoProjetil;
import Modelo.Item;
import Modelo.ItemType;

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

                    double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));

                    if (p2 instanceof Item) {
                        if (dist < h.grabHitboxRaio + p2.hitboxRaio) {
                            aplicarEfeitoDoItem(h, (Item) p2);

                            objetosARemover.add(p2); // Marca o item para remoção
                        }
                    } else if (p2.isbMortal()) {
                        if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR) {
                            continue;
                        }

                        if (dist < p1.hitboxRaio + p2.hitboxRaio) {

                            if (!h.isInvencivel()) {
                                h.takeDamage();
                            }

                            // Independentemente de tomar dano ou não, o inimigo/projétil é removido
                            if (p2 instanceof Projetil) {
                                objetosARemover.add(p2);
                            } else if (p2 instanceof Inimigo) {
                                // Se quiser que o inimigo seja destruído ao tocar no herói invencível
                                objetosARemover.add(p2);
                            }
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
                    double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
                    if (p2 instanceof Inimigo) {

                        if (dist < p1.hitboxRaio + p2.hitboxRaio) {

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

                            objetosARemover.add(p1);
                            objetosARemover.add(p2);
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

            double dist = Math.sqrt(Math.pow(proximoX - p.x, 2) + Math.pow(proximoY - p.y, 2));

            // CORREÇÃO: Usa 'hitboxRaio' em vez de 'raio'
            if (dist < personagem.hitboxRaio + p.hitboxRaio) {
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
