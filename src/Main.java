// Main.java
import Controler.Engine;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Engine().startGameThread();
        });
    }
}