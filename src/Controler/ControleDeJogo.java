package Controler;

import Modelo.Personagem;
import Modelo.Hero;
import java.awt.Graphics;
import java.util.ArrayList;

public class ControleDeJogo {

    public void desenhaTudo(ArrayList<Personagem> e, Graphics g) {
        for (Personagem personagem : e) {
            personagem.autoDesenho(g);
        }
    }

    public void processaTudo(ArrayList<Personagem> umaFase) {
        if (umaFase.isEmpty()) return;
        
        Hero hero = null;
        for (Personagem p : umaFase) {
            if (p instanceof Hero) {
                hero = (Hero) p;
                break;
            }
        }
        if (hero == null) return;

        // MUDANÇA: Lógica de colisão baseada em distância (esferas)
        for (int i = 0; i < umaFase.size(); i++) {
            Personagem p = umaFase.get(i);
            if (p == hero) continue;

            // Calcula a distância entre o centro do herói e o centro do personagem
            double dist = Math.sqrt(Math.pow(hero.x - p.x, 2) + Math.pow(hero.y - p.y, 2));
            
            // Se a distância for menor que a soma dos raios, há colisão
            if (dist < hero.raio + p.raio) {
                if (p.isbTransponivel()) {
                    if (p.isbMortal()) {
                        umaFase.remove(p);
                        i--; // Ajusta o índice após a remoção
                    }
                }
            }
        }
    }
    
    // MUDANÇA: Lógica de validação de posição para movimento suave
    public boolean ehPosicaoValida(ArrayList<Personagem> umaFase, Personagem personagem, double proximoX, double proximoY) {
        for (Personagem p : umaFase) {
            if (p == personagem || p.isbTransponivel()) {
                continue;
            }

            double dist = Math.sqrt(Math.pow(proximoX - p.x, 2) + Math.pow(proximoY - p.y, 2));

            if (dist < personagem.raio + p.raio) {
                return false; // Colisão detectada
            }
        }
        return true;
    }
}
