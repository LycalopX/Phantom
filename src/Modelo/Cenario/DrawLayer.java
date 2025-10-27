package Modelo.Cenario;

/**
 * @brief Define as camadas de renderização para elementos do cenário,
 *        determinando se são desenhados antes ou depois de certos efeitos
 *        como gradientes.
 */
public enum DrawLayer {
    BACKGROUND, // Desenhado atrás de gradientes e overlays
    FOREGROUND; // Desenhado na frente de tudo
}
