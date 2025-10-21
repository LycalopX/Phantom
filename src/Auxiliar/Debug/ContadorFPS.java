package Auxiliar.Debug;

public class ContadorFPS {

    private long ultimoTempo;
    private int frames;
    private int fpsExibido;

    /**
     * @brief Construtor do contador de FPS. Inicializa as variáveis de controle de
     *        tempo e frames.
     */
    public ContadorFPS() {
        ultimoTempo = System.nanoTime();
        frames = 0;
        fpsExibido = 0;
    }

    /**
     * @brief Atualiza o contador de frames a cada chamada. Se um segundo passou,
     *        atualiza o valor de FPS a ser exibido e reseta a contagem.
     */
    public void atualizar() {
        frames++;
        long tempoAtual = System.nanoTime();

        if (tempoAtual - ultimoTempo >= 1000000000) {
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