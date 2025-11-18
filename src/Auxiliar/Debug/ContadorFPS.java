package Auxiliar.Debug;

/**
 * @brief Uma classe utilitária para medir e exibir os quadros por segundo
 *        (FPS).
 */
public class ContadorFPS {

    private long ultimoTempo;
    private int frames;
    private int fpsExibido;

    /**
     * @brief Construtor do contador de FPS.
     * 
     *        Inicializa as variáveis de controle de tempo e contagem de quadros.
     */
    public ContadorFPS() {
        ultimoTempo = System.nanoTime();
        frames = 0;
        fpsExibido = 0;
    }

    /**
     * @brief Atualiza o contador de quadros.
     * 
     *        Este método deve ser chamado a cada quadro (frame) do jogo. Ele
     *        incrementa
     *        a contagem de quadros e, se um segundo tiver passado desde a última
     *        atualização, o valor de `fpsExibido` é atualizado com a contagem de
     *        quadros acumulada.
     */
    public void atualizar() {
        frames++;
        long tempoAtual = System.nanoTime();

        if (tempoAtual - ultimoTempo >= 1000000000) { // 1 bilhão de nanossegundos = 1 segundo
            fpsExibido = frames;
            frames = 0;
            ultimoTempo = tempoAtual;
        }
    }

    /**
     * @brief Retorna o último valor de FPS calculado em formato de String.
     * @return Uma String como "FPS: 60".
     */
    public String getFPSString() {
        return "FPS: " + fpsExibido;
    }
}