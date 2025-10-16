// Em Modelo/ProjetilHoming.java
package Modelo;

import Auxiliar.Consts;
import Auxiliar.TipoProjetil;
import java.util.ArrayList;

public class ProjetilHoming extends Projetil {

    private Personagem alvo = null;
    private double taxaDeCurva = 0.05; // Controla a "agressividade" da curva

    // MUDANÇA 1: O construtor foi atualizado para corresponder ao construtor de Projetil
    public ProjetilHoming(String sNomeImagePNG, double x, double y, 
                          int larguraVisual, int alturaVisual, int hitboxTamanho, 
                          double velocidadeGrid, double angulo, TipoProjetil tipo) {
        
        // Ele simplesmente passa todos os parâmetros para a classe-mãe
        super(sNomeImagePNG, x, y, 
              larguraVisual, alturaVisual, hitboxTamanho, 
              velocidadeGrid, angulo, tipo);
        
        // Podemos definir que projéteis teleguiados não são sólidos (passam por outros inimigos)
        this.bTransponivel = true; 
    }

    @Override
    public void atualizar(ArrayList<Personagem> personagens) {
        // 1. Lógica para encontrar um alvo
        if (alvo == null || !personagens.contains(alvo) || !alvo.isbMortal()) {
            alvo = encontrarInimigoMaisProximo(personagens);
        }

        // 2. Se tiver um alvo, ajustar o ângulo suavemente
        if (alvo != null) {
            double anguloParaAlvo = Math.atan2(alvo.y - this.y, alvo.x - this.x);
            
            // Interpolação suave de ângulo
            // Isso evita que o míssil vire "instantaneamente" e dá a ele um efeito de curva
            double deltaAngulo = anguloParaAlvo - this.anguloRad;
            // Normaliza o delta para o caminho mais curto (evita girar 359 graus)
            while (deltaAngulo > Math.PI) deltaAngulo -= 2 * Math.PI;
            while (deltaAngulo < -Math.PI) deltaAngulo += 2 * Math.PI;

            this.anguloRad += deltaAngulo * taxaDeCurva;
        }
        
        // 3. Mover-se na direção do novo ângulo
        // Chama o 'atualizar' da classe-mãe (Projetil), que contém a lógica de movimento
        super.atualizar(personagens);
    }

    private Personagem encontrarInimigoMaisProximo(ArrayList<Personagem> personagens) {
        Personagem alvoMaisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        // Procura por qualquer personagem que seja "mortal" (Inimigos)
        for(Personagem p : personagens) {
            if(p.isbMortal()) {
                double dist = Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
                if(dist < menorDistancia) {
                    menorDistancia = dist;
                    alvoMaisProximo = p;
                }
            }
        }
        return alvoMaisProximo;
    }
}