package Auxiliar.Projeteis;

/**
 * @brief Enum para diferenciar a origem de um projétil.
 * 
 * Usado para determinar se um projétil foi disparado pelo jogador
 * ou por um inimigo, o que é crucial para a lógica de colisão.
 */
public enum TipoProjetil {
    JOGADOR, 
    INIMIGO    
}