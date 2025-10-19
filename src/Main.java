import Auxiliar.SoundManager;
import Controler.Engine;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SoundManager.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SoundManager.shutdown();
        }));
        SwingUtilities.invokeLater(() -> {
            new Engine().startGameThread();
        });
    }
}