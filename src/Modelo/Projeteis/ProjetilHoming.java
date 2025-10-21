package Modelo.Projeteis;

import Auxiliar.Projeteis.ProjetilTipo;
import Auxiliar.Projeteis.TipoProjetil;
import Modelo.Personagem;
import Modelo.Inimigos.Inimigo;
import java.util.ArrayList;

/**
 * @brief Projétil que, após um período inicial de inércia, persegue o inimigo
 *        mais próximo.
 */
public class ProjetilHoming extends Projetil {

    private enum HomingState {
        INERCIA,
        PERSEGUINDO
    }

    private HomingState estadoAtual = HomingState.INERCIA;

    private Inimigo alvo;
    private ArrayList<Personagem> personagens;
    private double taxaDeCurva = 0.1;

    private int TEMPO_INERCIA = 20;
    private int inertiaTimer = TEMPO_INERCIA;

    /**
     * @brief Construtor do projétil teleguiado.
     */
    public ProjetilHoming(String sNomeImagePNG, ArrayList<Personagem> personagens) {
        super(sNomeImagePNG);
        this.personagens = personagens;
    }

    /**
     * @brief Configura o míssil para um novo disparo, reiniciando seu estado de
     *        perseguição.
     */
    public void resetHoming(double x, double y, double velocidadeGrid, double anguloInicial, TipoProjetil tipo,
            ProjetilTipo tipoDetalhado) {
        super.reset(
                x,
                y,
                velocidadeGrid,
                anguloInicial,
                tipo,
                tipoDetalhado);

        this.estadoAtual = HomingState.INERCIA;
        this.alvo = null;
        this.inertiaTimer = TEMPO_INERCIA;
    }

    /**
     * @brief Atualiza a lógica do míssil, que transita de um estado de inércia para
     *        perseguição.
     */
    @Override
    public void atualizar() {
        if (!isActive())
            return;

        switch (estadoAtual) {
            case INERCIA:
                inertiaTimer--;
                if (inertiaTimer <= 0) {
                    estadoAtual = HomingState.PERSEGUINDO;
                }
                break;

            case PERSEGUINDO:
                if (alvo == null || !personagens.contains(alvo)) {
                    encontrarAlvoMaisProximo(personagens);
                }
                if (alvo != null) {
                    ajustarAnguloParaOAlvo();
                }
                break;
        }

        super.atualizar();
    }

    /**
     * @brief Ajusta suavemente o ângulo do projétil para que ele se vire em direção
     *        ao alvo.
     */
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

    /**
     * @brief Encontra o inimigo mais próximo do projétil para definir como alvo.
     */
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

    /**
     * @brief Verifica se o projétil está ativo.
     */
    public boolean isActive() {
        return this.isActive;
    }
}