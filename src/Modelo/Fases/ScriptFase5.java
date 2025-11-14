package Modelo.Fases;

import Auxiliar.SoundManager;
import Controler.Engine;

public class ScriptFase5 extends ScriptDeFase {

    public ScriptFase5(Engine engine) {
        super(engine);
        SoundManager.getInstance().playMusic("Love-Colored Master Spark", true);
    }

    @Override
    public void carregarRecursos(Fase fase) {
        // nada por enquanto
    }
    
    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        // nada por enquanto
    }

    @Override
    public void atualizarInimigos(Fase fase) {
        // Lógica de inimigos da Fase 4
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        // nada por enquanto
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
        // Preenche o cenário inicial com bambus
        // A velocidade de scroll inicial pode ser 0 ou um valor padrão, já que eles
        // serão movidos no primeiro frame de atualização
    }
}