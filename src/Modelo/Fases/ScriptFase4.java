package Modelo.Fases;

public class ScriptFase4  extends ScriptDeFase {

    protected long proximoSpawnInimigo = 0;
    protected long intervaloSpawnInimigo = 60; // 1 segundo
    // ... outras variáveis que o script precisa para se controlar ...

    /**
     * Chamado pela Fase a cada frame.
     * @param fase A instância da fase que este script está controlando.
     */


    @Override
    public void atualizarInimigos(Fase fase) {
    }

    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
    }

    @Override
    public void preencherCenarioInicial(Fase fase) {
    }
}
