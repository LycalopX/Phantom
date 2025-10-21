package Auxiliar.Debug;

/**
 * @brief Gerencia o estado do modo de depuração (debug) do jogo.
 */
public class DebugManager {
    private static boolean active = false;

    /**
     * @brief Alterna o estado do modo de depuração (ativo/inativo) e imprime o novo
     *        estado no console.
     */
    public static void toggle() {
        active = !active;
        System.out.println("Modo de Desenvolvedor: " + (active ? "ATIVADO" : "DESATIVADO"));
    }

    /**
     * @brief Verifica se o modo de depuração está atualmente ativo.
     * @return true se o modo de depuração estiver ativo, false caso contrário.
     */
    public static boolean isActive() {
        return active;
    }
}