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
    private ArrayList<Personagem> personagens;
    private double taxaDeCurva = 0.1;

    // Timer para a fase de inércia (em frames)
    private int TEMPO_INERCIA = 20;
    private int inertiaTimer = TEMPO_INERCIA;

    public ProjetilHoming(String sNomeImagePNG, ArrayList<Personagem> personagens) {
        super(sNomeImagePNG); // Chama o construtor de Projetil(String)
        this.personagens = personagens;
    }
    
    /**
     * MÉTODO RESET:
     * Configura o míssil para um novo disparo a partir da piscina de objetos.
     * Ele primeiro chama o reset do pai (Projetil) e depois reinicia seu
     * próprio estado de "homing".
     */
    public void resetHoming(double x, double y, int largura, int altura, double hitboxRaio,
                            double velocidadeGrid, double anguloInicial, TipoProjetil tipo) {
        
        // 1. Chama o reset da classe pai (Projetil) para configurar os atributos básicos
        super.reset(x, y, largura, altura, hitboxRaio, velocidadeGrid, anguloInicial, tipo);

        // 2. Reinicia o estado específico do Projétil Homing
        this.estadoAtual = HomingState.INERCIA;
        this.alvo = null; // Esquece o alvo anterior
        this.inertiaTimer = TEMPO_INERCIA; // Reinicia o contador de inércia
    }

    /**
     * O método 'atualizar' agora é uma máquina de estados.
     */
    @Override
    public void atualizar() {
        if (!isActive()) return;

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

        // A chamada 'super.atualizar()' move o projétil na sua direção atual
        super.atualizar();
    }

    private void ajustarAnguloParaOAlvo() {
        double anguloParaOAlvo = Math.atan2(alvo.y - this.y, alvo.x - this.x);
        double diferencaAngulo = anguloParaOAlvo - this.anguloRad;

        while (diferencaAngulo > Math.PI)
            diferencaAngulo -= 2 * Math.PI;
        while (diferencaAngulo < -Math.PI)
            diferencaAngulo += 2 * Math.PI;

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

    public boolean isActive() {
        return this.isActive;
    }
}