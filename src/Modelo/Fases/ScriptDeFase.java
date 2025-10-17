// Em Modelo/ScriptDeFase.java
package Modelo.Fases;

import java.io.Serializable;
import java.util.Random;

public abstract class ScriptDeFase implements Serializable {
    protected long proximoSpawnInimigo = 0;
    protected long intervaloSpawnInimigo = 60; // 1 segundo
    
    protected Random random = new Random();

    /**
     * Atualiza a lógica de spawn de inimigos.
     * Deve ser implementado por subclasses.
     */
    public abstract void atualizarInimigos(Fase fase);

    /**
     * Atualiza a lógica de spawn de elementos de cenário (como árvores).
     * Subclasses podem sobrepor isso. Por padrão, não faz nada.
     */
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        // Vazio por padrão. ScriptFase1 irá sobrepor isso.
    }

    /**
     * Preenche o cenário com elementos iniciais (como árvores).
     * Subclasses podem sobrepor isso. Por padrão, não faz nada.
     */
    public void preencherCenarioInicial(Fase fase) {
        // Vazio por padrão. ScriptFase1 irá sobrepor isso.
    }

    /**
     * Método principal chamado pela Fase, que orquestra os spawns.
     * Este método final NÃO PODE ser sobreposto.
     */
    public final void atualizar(Fase fase, double velocidadeScroll) {
        atualizarInimigos(fase);
        atualizarCenario(fase, velocidadeScroll);
    }
}

