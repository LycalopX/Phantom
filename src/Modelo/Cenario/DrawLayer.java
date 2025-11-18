package Modelo.Cenario;

/**
 * @brief Define as camadas de renderização para elementos do cenário.
 * 
 * Este enum determina a ordem em que os elementos do cenário são desenhados,
 * permitindo que alguns apareçam atrás (BACKGROUND) e outros na frente
 * (FOREGROUND) de efeitos como gradientes e overlays de cor.
 */
public enum DrawLayer {
    BACKGROUND, 
    FOREGROUND; 
}
