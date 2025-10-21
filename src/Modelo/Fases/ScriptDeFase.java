package Modelo.Fases;

import java.io.Serializable;
import java.util.Random;

/**
 * @brief Classe abstrata que define o contrato para scripts de fase.
 *        Cada fase do jogo terá uma implementação concreta desta classe
 *        para controlar os eventos e o spawning de inimigos e cenário.
 */
public abstract class ScriptDeFase implements Serializable {
    protected long proximoSpawnInimigo = 0;
    protected long intervaloSpawnInimigo = 60;

    protected Random random = new Random();

    /**
     * @brief Atualiza a lógica de spawn de inimigos. Deve ser implementado por
     *        subclasses.
     * @param fase A instância da fase que este script está controlando.
     */
    public abstract void atualizarInimigos(Fase fase);

    /**
     * @brief Atualiza a lógica de spawn de elementos de cenário (como árvores).
     *        Subclasses podem sobrepor isso. Por padrão, não faz nada.
     * @param fase             A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
    }

    /**
     * @brief Preenche o cenário com elementos iniciais (como árvores).
     *        Subclasses podem sobrepor isso. Por padrão, não faz nada.
     * @param fase A instância da fase que este script está controlando.
     */
    public void preencherCenarioInicial(Fase fase) {
    }

    /**
     * @brief Método principal chamado pela Fase, que orquestra os spawns.
     *        Este método final não pode ser sobreposto.
     * @param fase             A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    public final void atualizar(Fase fase, double velocidadeScroll) {
        atualizarInimigos(fase);
        atualizarCenario(fase, velocidadeScroll);
    }
}
