package Modelo.Cenario;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * @brief Define um contrato para todos os objetos que compõem o cenário.
 * 
 * Qualquer classe que represente um elemento visual do cenário (como fundos,
 * árvores, etc.) deve implementar esta interface para garantir que possa ser
 * desenhada, movida e gerenciada corretamente pela engine do jogo.
 */
public interface ElementoCenario extends Serializable {

    /**
     * @brief Desenha o elemento do cenário na tela.
     * @param g2d O contexto gráfico para desenhar.
     * @param larguraTela A largura atual da tela.
     * @param alturaTela A altura atual da tela.
     */
    void desenhar(Graphics2D g2d, int larguraTela, int alturaTela);

    /**
     * @brief Atualiza a posição do elemento com base na velocidade de rolagem do cenário.
     * @param velocidadeAtualDoFundo A velocidade de rolagem principal.
     */
    void mover(double velocidadeAtualDoFundo);

    /**
     * @brief Define um multiplicador de velocidade para o elemento.
     * @param multiplier O fator de multiplicação.
     */
    void setSpeedMultiplier(double multiplier);

    /**
     * @brief Verifica se o elemento já saiu completamente da área visível da tela.
     * @param alturaTela A altura da tela para referência.
     * @return true se o elemento estiver fora da tela, false caso contrário.
     */
    boolean estaForaDaTela(int alturaTela);

    /**
     * @brief Retorna a camada de desenho do elemento (BACKGROUND ou FOREGROUND).
     * @return O `DrawLayer` do elemento.
     */
    DrawLayer getDrawLayer();

}

