package Auxiliar;

public class ContadorFPS {

    private long ultimoTempo;
    private int frames;
    private int fpsExibido;

    public ContadorFPS() {
        // Inicializa as variáveis no construtor
        ultimoTempo = System.nanoTime();
        frames = 0;
        fpsExibido = 0;
    }

    /**
     * Deve ser chamado a cada frame do loop principal do jogo.
     */
    public void atualizar() {
        frames++;
        long tempoAtual = System.nanoTime();

        // Verifica se já passou 1 segundo (1 bilhão de nanossegundos)
        if (tempoAtual - ultimoTempo >= 1000000000) {
            fpsExibido = frames; // Atualiza o FPS a ser exibido
            frames = 0;          // Reseta a contagem de frames
            ultimoTempo = tempoAtual; // Marca o novo tempo de início
        }
    }

    /**
     * Retorna o último valor de FPS calculado em formato de String.
     * @return Uma String como "FPS: 60".
     */
    public String getFPSString() {
        return "FPS: " + fpsExibido;
    }
}