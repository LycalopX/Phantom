package Modelo.Projeteis;

import Auxiliar.TipoProjetil;
import Modelo.Personagem;
import Modelo.Inimigos.Inimigo;

import java.util.ArrayList;

public class ProjetilHoming extends Projetil {

    // Enum para controlar o estado do míssil
    private enum HomingState {
        INERCIA,
        PERSEGUINDO
    }
    private HomingState estadoAtual = HomingState.INERCIA;

    private Inimigo alvo;
    private double taxaDeCurva = 0.1;

    // Timer para a fase de inércia (em frames)
    private int inertiaTimer = 20; // O míssil voará reto por 1/3 de segundo (20 frames)

    public ProjetilHoming(String sNomeImagePNG, double x, double y,
                          int larguraVisual, int alturaVisual, int hitboxTamanho,
                          double velocidadeGrid, double anguloInicial, TipoProjetil tipo) {
        
        super(sNomeImagePNG, x, y, larguraVisual, alturaVisual, hitboxTamanho, velocidadeGrid, anguloInicial, tipo);
        this.alvo = null;
    }

    /**
     * O método 'atualizar' agora é uma máquina de estados.
     */
    @Override
    public void atualizar(ArrayList<Personagem> personagens) {
        
        // --- LÓGICA DA MÁQUINA DE ESTADOS ---
        switch (estadoAtual) {
            case INERCIA:
                // Durante a inércia, o míssil apenas voa reto.
                inertiaTimer--;
                if (inertiaTimer <= 0) {
                    // Quando o tempo de inércia acaba, ele começa a perseguir.
                    estadoAtual = HomingState.PERSEGUINDO;
                }
                break;

            case PERSEGUINDO:
                // Lógica de perseguição que já tínhamos
                if (alvo == null || !personagens.contains(alvo)) {
                    encontrarAlvoMaisProximo(personagens);
                }
                if (alvo != null) {
                    ajustarAnguloParaOAlvo();
                }
                break;
        }

        // A chamada 'super.atualizar()' move o projétil na sua direção atual (anguloRad),
        // independentemente do estado.
        super.atualizar(personagens);
    }

    private void ajustarAnguloParaOAlvo() {
        double anguloParaOAlvo = Math.atan2(alvo.y - this.y, alvo.x - this.x);
        double diferencaAngulo = anguloParaOAlvo - this.anguloRad;

        while (diferencaAngulo > Math.PI) diferencaAngulo -= 2 * Math.PI;
        while (diferencaAngulo < -Math.PI) diferencaAngulo += 2 * Math.PI;

        if (Math.abs(diferencaAngulo) > taxaDeCurva) {
            this.anguloRad += Math.signum(diferencaAngulo) * taxaDeCurva;
        } else {
            this.anguloRad = anguloParaOAlvo;
        }
    }

    private void encontrarAlvoMaisProximo(ArrayList<Personagem> personagens) {
        double menorDistancia = Double.MAX_VALUE;
        Inimigo inimigoMaisProximo = null;

        for (Personagem p : personagens) {
            if (p instanceof Inimigo) {
                double distancia = Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.y - this.y, 2));
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    inimigoMaisProximo = (Inimigo) p;
                }
            }
        }
        this.alvo = inimigoMaisProximo;
    }
}