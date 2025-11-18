package Auxiliar.Debug;

/**
 * @brief Gerenciador estático para o modo de depuração (debug) do jogo.
 *
 * Esta classe utilitária fornece uma maneira centralizada de verificar e
 * alternar o estado do modo de depuração, que pode ser usado para exibir
 * informações como hitboxes, FPS, etc.
 */
public class DebugManager {
    private static boolean debugAtivo = false;

    /**
     * @brief Alterna o estado do modo de depuração (ativado/desativado).
     */
    public static void toggle() {
        debugAtivo = !debugAtivo;
        System.out.println("Modo de depuracao " + (debugAtivo ? "ativado" : "desativado") + ".");
    }

    /**
     * @brief Verifica se o modo de depuração está atualmente ativo.
     * @return true se o modo de depuração estiver ativo, false caso contrário.
     */
    public static boolean isActive() {
        return debugAtivo;
    }
}