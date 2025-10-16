package Controler;

import Modelo.Personagem;
import Modelo.Projetil;
import Modelo.Hero;
import Modelo.Inimigo;
import Auxiliar.TipoProjetil;
import Modelo.Item;

import java.awt.Graphics;
import java.util.ArrayList;

public class ControleDeJogo {

    public void desenhaTudo(ArrayList<Personagem> e, Graphics g) {
        for (Personagem personagem : e) {
            personagem.autoDesenho(g);
        }
    }

    public void processaTudo(ArrayList<Personagem> personagens) {
        if (personagens.isEmpty())
            return;

        Hero hero = null;
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                hero = (Hero) p;
                break;
            }
        }
        if (hero == null)
            return;

        // MUDANÇA 1: Criar "Listas de Remoção"
        // Vamos marcar quem deve ser removido, e remover no final.
        ArrayList<Personagem> objetosARemover = new ArrayList<>();

        // Loop principal de colisão (agora podemos iterar para frente sem medo)
        for (Personagem p1 : personagens) {

            // Lógica de colisão do Herói
            if (p1 instanceof Hero) {
                Hero h = (Hero) p1;
                if (h.isInvencivel())
                    continue; // Se o herói está invencível, pula suas colisões

                for (Personagem p2 : personagens) {
                    if (p1 == p2)
                        continue;

                    double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));

                    if (p2 instanceof Item) {
                        if (dist < h.grabHitboxRaio + p2.hitboxRaio) {
                            System.out.println("Pegou Item!");
                            objetosARemover.add(p2); // Marca o item para remoção
                        }
                    } else if (p2.isbMortal()) {
                        if (p2 instanceof Projetil && ((Projetil) p2).getTipo() == TipoProjetil.JOGADOR) {
                            continue;
                        }
                        if (dist < p1.hitboxRaio + p2.hitboxRaio) {
                            System.out.println("HERÓI ATINGIDO!");
                            objetosARemover.add(p2); // Marca o inimigo/projétil para remoção
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
                    if (p2 instanceof Inimigo) {
                        double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));

                        if (dist < p1.hitboxRaio + p2.hitboxRaio) {
                            System.out.println("INIMIGO ATINGIDO!");
                            objetosARemover.add(p1); // Marca o projétil
                            objetosARemover.add(p2); // Marca o inimigo
                            break; // Projétil foi destruído, não precisa checar mais inimigos
                        }
                    }
                }
            }
        }

        // MUDANÇA 2: Remover todos os objetos marcados de uma só vez
        personagens.removeAll(objetosARemover);
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
}
