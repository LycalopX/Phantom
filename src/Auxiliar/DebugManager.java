// Novo arquivo: src/Auxiliar/DebugManager.java

package Auxiliar;

public class DebugManager {
    // A variável 'static' garante que haverá apenas UMA instância dela para todo o jogo.
    private static boolean active = false;

    // Método para ligar/desligar o modo de depuração.
    public static void toggle() {
        active = !active;
        System.out.println("Modo de Desenvolvedor: " + (active ? "ATIVADO" : "DESATIVADO"));
    }
    
    // Método que as outras classes usarão para perguntar se o modo está ativo.
    public static boolean isActive() {
        return active;
    }
}