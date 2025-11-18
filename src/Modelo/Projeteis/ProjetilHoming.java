package Modelo.Projeteis;

import Auxiliar.Projeteis.ProjetilTipo;
import Auxiliar.Projeteis.TipoProjetil;
import Modelo.Inimigos.Inimigo;
import java.util.List;

/**
 * @brief Projétil que persegue o inimigo mais próximo após um período inicial.
 * 
 *        Este projétil começa em um estado de inércia, movendo-se em linha
 *        reta,
 *        e depois transita para um estado de perseguição, ajustando sua
 *        trajetória
 *        para interceptar um alvo.
 */
public class ProjetilHoming extends Projetil {

    private enum HomingState {
        INERCIA,
        PERSEGUINDO
    }

    private HomingState estadoAtual = HomingState.INERCIA;

    private Inimigo alvo;
    private List<Inimigo> inimigosAlvo;
    private double taxaDeCurva = 0.1;

    private int TEMPO_INERCIA = 20;
    private int inertiaTimer = TEMPO_INERCIA;

    /**
     * @brief Construtor do projétil teleguiado.
     */
    public ProjetilHoming(String sNomeImagePNG) {
        super(sNomeImagePNG);
    }

    /**
     * @brief Configura o míssil para um novo disparo.
     * 
     *        Reinicia seu estado para `INERCIA` e define a lista de alvos
     *        potenciais.
     */
    public void resetHoming(double x, double y, double velocidadeGrid, double anguloInicial, TipoProjetil tipo,
            ProjetilTipo tipoDetalhado, List<Inimigo> inimigos) {
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
        this.inimigosAlvo = inimigos;
    }

    /**
     * @brief Atualiza a lógica do míssil a cada frame.
     * 
     *        Gerencia a transição do estado de inércia para o de perseguição e,
     *        neste último, busca e ajusta a rota em direção ao alvo.
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
                // Se não houver um alvo válido, encontra um novo.
                if (inimigosAlvo != null && (alvo == null || !alvo.isActive() || !inimigosAlvo.contains(alvo))) {
                    encontrarAlvoMaisProximo(inimigosAlvo);
                }
                // Se um alvo foi encontrado, ajusta o ângulo em sua direção.
                if (alvo != null) {
                    ajustarAnguloParaOAlvo();
                }
                break;
        }

        super.atualizar();
    }

    /**
     * @brief Ajusta suavemente o ângulo do projétil em direção ao alvo.
     * 
     *        Em vez de virar instantaneamente, o projétil interpola seu ângulo
     *        atual
     *        em direção ao ângulo do alvo a uma `taxaDeCurva`, criando um movimento
     *        de perseguição mais natural.
     */
    private void ajustarAnguloParaOAlvo() {
        double anguloParaOAlvo = Math.atan2(alvo.getY() - this.y, alvo.getX() - this.x);
        double diferencaAngulo = anguloParaOAlvo - this.anguloRad;

        // Normaliza a diferença de ângulo para o intervalo [-PI, PI]
        // para garantir que o projétil sempre tome o caminho mais curto.
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
     * @brief Encontra o inimigo ativo mais próximo do projétil.
     * 
     *        Itera sobre a lista de inimigos fornecida e define o mais próximo
     *        como o novo alvo.
     */
    private void encontrarAlvoMaisProximo(List<Inimigo> inimigos) {
        double menorDistancia = Double.MAX_VALUE;
        Inimigo inimigoMaisProximo = null;

        for (Inimigo i : inimigos) {
            if (i.isActive()) {
                double distancia = Math.sqrt(Math.pow(i.getX() - this.x, 2) + Math.pow(i.getY() - this.y, 2));
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    inimigoMaisProximo = i;
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