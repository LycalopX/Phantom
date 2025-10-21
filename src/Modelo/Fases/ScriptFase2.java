package Modelo.Fases;
 
import Auxiliar.LootTable;
import Auxiliar.Personagem.LootItem;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.ItemType;

/**
 * @brief Script de eventos e spawns para a Fase 2 (placeholder).
 */
public class ScriptFase2 extends ScriptDeFase {

    protected long proximoSpawnInimigo = 0;
    protected long intervaloSpawnInimigo = 60;

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
