package Modelo.Fases;

import Auxiliar.ConfigMapa;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Controler.Engine;
import Modelo.Inimigos.*;
import Modelo.Personagem;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * @brief Classe abstrata que define o contrato para os scripts de cada fase.
 * 
 *        Cada fase do jogo deve ter uma implementação concreta desta classe
 *        para
 *        controlar os eventos, o spawning de inimigos e a aparência do cenário.
 *        A lógica é organizada em "Ondas" que são executadas sequencialmente.
 */
public abstract class ScriptDeFase implements Serializable {

    protected Random random = new Random();
    protected transient Engine engine;

    protected ArrayList<Onda> ondas;
    protected int ondaAtualIndex;
    private boolean faseIniciada = false;
    private boolean faseFinalizada = false;

    public ScriptDeFase(Engine engine) {
        this.engine = engine;
        this.ondas = new ArrayList<>();
        this.ondaAtualIndex = 0;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
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
     */
    public abstract void carregarRecursos(Fase fase);

    /**
     * @brief Restaura as referências de imagens transientes após a desserialização.
     */
    public abstract void relinkarRecursosDosElementos(Fase fase);

    /**
     * @brief Define a lista e a ordem das ondas de inimigos para a fase.
     * @return Uma lista de objetos `Onda`.
     */
    protected abstract ArrayList<Onda> inicializarOndas(Fase fase);

    /**
     * @brief Gerencia a execução sequencial das ondas de inimigos.
     * 
     *        A cada frame, incrementa o tempo da onda atual. Quando uma onda
     *        termina,
     *        avança para a próxima. Ao final de todas as ondas, sinaliza para a
     *        engine
     *        carregar a próxima fase.
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
     * @brief Atualiza a lógica de spawn de elementos de cenário.
     *        Pode ser sobrescrito por subclasses para criar cenários dinâmicos.
     */
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
    }

    /**
     * @brief Preenche o cenário com elementos iniciais.
     *        Pode ser sobrescrito por subclasses.
     */
    public void preencherCenarioInicial(Fase fase) {
    }

    /**
     * @brief Método principal chamado pela Fase, que orquestra as atualizações.
     */
    public final void atualizar(Fase fase, double velocidadeScroll) {
        atualizarInimigos(fase);
        atualizarCenario(fase, velocidadeScroll);
    }

    protected interface ITocarMusica {
        void tocarMusicaDeOnda(String musica);
    }

    /**
     * @brief Classe base para uma onda de eventos ou inimigos.
     * 
     *        Uma onda contém uma lista de `InimigoSpawn` que são acionados
     *        sequencialmente com base em um temporizador.
     */
    protected abstract class Onda implements Serializable {

        protected class InimigoSpawn implements Serializable {

            protected Personagem personagem;
            protected int tempoAposAcaoPrevia;

            public InimigoSpawn(Personagem personagem, int tempoAposAcaoPrevia) {
                this.personagem = personagem;
                this.tempoAposAcaoPrevia = tempoAposAcaoPrevia;
            }

            public void spawn(Fase fase) {
                if (personagem == null) {
                    return;
                }
                if (personagem instanceof Inimigo) {
                    ((Inimigo) personagem).initialize(fase);
                }
                fase.adicionarPersonagem(personagem);
            }
        }

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
         * @brief Avança a lógica da onda, spawnando inimigos conforme o tempo.
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

            tempoDeEspera = inimigo.tempoAposAcaoPrevia;
        }

        public void reiniciar() {
            tempoDeEspera = 0;
            indiceInimigoAtual = 0;
            todosSpawnados = false;
        }

        public boolean getFinalizado() {
            return todosSpawnados;
        }
    }

    /**
     * @brief Uma onda especial que serve apenas para criar uma pausa no script.
     */
    protected class OndaDeEspera extends Onda {

        public OndaDeEspera(Fase fase, int tempoDeEsperaInicial) {
            super();
            inimigos.add(new InimigoSpawn(null, tempoDeEsperaInicial));
        }
    }

    /**
     * @brief Uma onda especial para batalhas de chefe.
     * 
     *        A onda só é considerada finalizada quando todos os inimigos da lista
     *        foram spawnados E o chefe (`boss`) foi derrotado.
     */
    protected abstract class OndaDeBoss extends OndaComMusica {
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

    /**
     * @brief Uma onda que toca uma música específica ao ser iniciada.
     */
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
            if (musica != null) {
                SoundManager.getInstance().playMusic(musica, true);
            }
        }
    }

    /**
     * @brief Uma onda especial que aciona um efeito de aceleração global no jogo.
     */
    protected class OndaDeSpeedup extends Onda {
        public OndaDeSpeedup(int duration, double amplitude) {
            super();
            inimigos.add(new InimigoSpawn(null, 1) {
                @Override
                public void spawn(Fase fase) {
                    Fase.triggerGlobalSpeedup(duration, amplitude);
                }
            });
        }
    }
}
