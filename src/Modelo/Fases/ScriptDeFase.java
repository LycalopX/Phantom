package Modelo.Fases;

import Auxiliar.ConfigMapa;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Controler.Engine;
import Modelo.Inimigos.Boss;
import Modelo.Personagem;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * @brief Classe abstrata que define o contrato para scripts de fase. Cada fase
 * do jogo terá uma implementação concreta desta classe para controlar os
 * eventos e o spawning de inimigos e cenário.
 */
public abstract class ScriptDeFase implements Serializable {

    protected Random random = new Random();
    protected Engine engine;

    // Onda
    protected ArrayList<Onda> ondas;
    protected int ondaAtualIndex;
    private boolean faseIniciada = false;
    private boolean faseFinalizada = false;

    public ScriptDeFase(Engine engine) {
        this.engine = engine;
        this.ondas = new ArrayList<>();
        this.ondaAtualIndex = 0;
    }

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
     * cenário após a desserialização.
     * @param fase A instância da fase cujos elementos precisam ser religados.
     */
    public abstract void relinkarRecursosDosElementos(Fase fase);

    protected abstract ArrayList<Onda> inicializarOndas(Fase fase);

    /**
     * @brief Chama as ondas da fase definidas em inicializarOndas.
     * @param fase A instância da fase que este script está controlando.
     */
    public void atualizarInimigos(Fase fase) {
        if (faseFinalizada) {
            return;
        }

        if (!faseIniciada) {
            ondas = inicializarOndas(fase);
            faseIniciada = true;
            if (ondas.isEmpty()) {
                faseFinalizada = true;
                engine.carregarProximaFase();
                return;
            }
        }

        if (ondaAtualIndex < ondas.size()) {
            Onda ondaAtual = ondas.get(ondaAtualIndex);
            ondaAtual.incrementarTempo(1, fase);
            if (ondaAtual.getFinalizado()) {
                ondaAtualIndex++;
            }
        } else {
            faseFinalizada = true;
            engine.carregarProximaFase();
        }
    }

    /**
     * @brief Atualiza a lógica de spawn de elementos de cenário (como árvores).
     * Subclasses podem sobrepor isso. Por padrão, não faz nada.
     * @param fase A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
    }

    /**
     * @brief Preenche o cenário com elementos iniciais (como árvores).
     * Subclasses podem sobrepor isso. Por padrão, não faz nada.
     * @param fase A instância da fase que este script está controlando.
     */
    public void preencherCenarioInicial(Fase fase) {
    }

    /**
     * @brief Método principal chamado pela Fase, que orquestra os spawns. Este
     * método final não pode ser sobreposto.
     * @param fase A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    public final void atualizar(Fase fase, double velocidadeScroll) {
        atualizarInimigos(fase);
        atualizarCenario(fase, velocidadeScroll);
    }

    // Onda
    protected interface ITocarMusica {
        void tocarMusicaDeOnda(String musica);
    }

    protected abstract class Onda {

        // Classes
        protected class InimigoSpawn {

            protected Personagem personagem;
            protected int tempoAposInicioSpawn;

            public InimigoSpawn(Personagem personagem, int tempoAposInicioSpawn) {
                this.personagem = personagem;
                this.tempoAposInicioSpawn = tempoAposInicioSpawn;
            }

            public void spawn(Fase fase) {
                if (personagem == null) {
                    return;
                }
                fase.adicionarPersonagem(personagem);
            }
        }

        // Variaveis
        protected ArrayList<InimigoSpawn> inimigos;

        protected int tempoDeEspera;
        protected int indiceInimigoAtual;
        protected boolean todosSpawnados;

        public Onda() {
            this.tempoDeEspera = 0;
            this.indiceInimigoAtual = 0;
            this.todosSpawnados = false;
            this.inimigos = new ArrayList<>();
        }

        /**
         * @brief Spawna o próximo inimigo na fase, se houver.
         * @param fase A instância da fase onde o inimigo será spawnado.
         * @return O inimigo spawnado ou null se não houver mais inimigos.
         */
        private InimigoSpawn proximoInimigo(Fase fase) {
            if (indiceInimigoAtual < inimigos.size()) {
                InimigoSpawn inimigo = inimigos.get(indiceInimigoAtual);
                inimigo.spawn(fase);

                indiceInimigoAtual++;
                return inimigo;
            }
            return null;
        }

        /**
         * @brief Incrementa o tempo de espera e spawna inimigos conforme o
         * tempo progride.
         * @param tempo O tempo a ser incrementado.
         * @param fase A instância da fase onde os inimigos serão spawnados.
         */
        public void incrementarTempo(int tempo, Fase fase) {
            if (todosSpawnados) {
                return;
            }

            tempoDeEspera -= tempo;
            if (tempoDeEspera > 0) {
                return;
            }

            InimigoSpawn inimigo = proximoInimigo(fase);
            if (inimigo == null) {
                todosSpawnados = true;
                return;
            }

            tempoDeEspera = inimigo.tempoAposInicioSpawn;
        }

        /**
         * @brief Reinicia a onda para permitir que ela seja executada
         * novamente.
         */
        public void reiniciar() {
            tempoDeEspera = 0;
            indiceInimigoAtual = 0;
            todosSpawnados = false;
        }

        // Getters
        public boolean getFinalizado() {
            return todosSpawnados;
        }
    }

    protected class OndaDeEspera extends Onda {

        public OndaDeEspera(Fase fase, int tempoDeEsperaInicial) {
            super();
            inimigos.add(new InimigoSpawn(null, tempoDeEsperaInicial));
        }
    }

    protected abstract class OndaDeBoss extends OndaComMusica{
        protected Boss boss;
        protected LootTable lootTable;
        public OndaDeBoss(String musica) {
            super(musica);
            this.lootTable = new LootTable();
        }

        @Override
        public boolean getFinalizado() {
            return super.getFinalizado() && !boss.isActive();
        }
    }

    protected class OndaComMusica extends Onda implements ITocarMusica {
        private final String musica;

        public OndaComMusica(String musica) {
            super();
            this.musica = musica;
        }

        @Override
        public void incrementarTempo(int tempo, Fase fase) {
            super.incrementarTempo(tempo, fase);
            if (tempoDeEspera == 0) {
                tocarMusicaDeOnda(musica);
            }
        }

        @Override
        public void tocarMusicaDeOnda(String musica) {
            SoundManager.getInstance().playMusic(musica, true);
        }
    }
}
