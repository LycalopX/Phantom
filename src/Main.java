import Auxiliar.Projeteis.TipoProjetilInimigo;
import Auxiliar.SoundManager;
import Controler.Engine;
import javax.swing.SwingUtilities;

/**
 * @brief Classe principal que contém o ponto de entrada da aplicação.
 */
public class Main {
    /**
     * @brief Ponto de entrada do jogo.
     * 
     * Este método é responsável por:
     * 1. Inicializar os subsistemas principais, como o `SoundManager` e o
     *    carregador de definições de projéteis (`TipoProjetilInimigo`).
     * 2. Registrar um "shutdown hook" para garantir que o sistema de som seja
     *    encerrado corretamente quando o jogo fechar.
     * 3. Instanciar e iniciar a `Engine` do jogo na Event Dispatch Thread (EDT)
     *    do Swing, que é a prática recomendada para aplicações gráficas Swing.
     */
    public static void main(String[] args) {
        SoundManager.init();
        TipoProjetilInimigo.iniciar();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SoundManager.shutdown();
        }));
        SwingUtilities.invokeLater(() -> {
            new Engine().startGameThread();
        });
    }
}