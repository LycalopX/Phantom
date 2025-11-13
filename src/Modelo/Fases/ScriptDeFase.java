package Modelo.Fases;

import java.io.Serializable;
import java.util.Random;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import Auxiliar.ConfigMapa;

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
     * @brief Retorna a cor de sobreposição do fundo para esta fase.
     * @return Um objeto Color.
     */
    public Color getBackgroundOverlayColor() {
        return new Color(0, 0, 50, 150);
    }

    /**
     * @brief Retorna o gradiente de fundo para esta fase.
     * @return Um objeto LinearGradientPaint.
     */
    public LinearGradientPaint getBackgroundGradient() {
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, ConfigMapa.ALTURA_TELA);

        float[] fractions = { 0.0f, 0.5f, 1.0f };
        Color[] colors = { new Color(0, 0, 50, 255), new Color(0, 0, 50, 100), new Color(0, 0, 50, 0) };

        return new LinearGradientPaint(start, end, fractions, colors);
    }

    /**
     * @brief Carrega os recursos visuais específicos da fase (imagens de fundo,
     *        etc).
     * @param fase A instância da fase para a qual os recursos serão carregados.
     */
    public abstract void carregarRecursos(Fase fase);

    /**
     * @brief Restaura as referências de imagens transientes nos elementos de
     *        cenário após a desserialização.
     * @param fase A instância da fase cujos elementos precisam ser religados.
     */
    public abstract void relinkarRecursosDosElementos(Fase fase);

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
