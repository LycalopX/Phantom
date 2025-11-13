package Modelo.Fases;

import Auxiliar.Cenario1.ArvoreParallax;
import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.Personagem.LootItem;
import Auxiliar.SoundManager;
import Modelo.Cenario.FundoInfinito;
import Modelo.Items.ItemType;
import Modelo.Personagem;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * @brief Script de eventos e spawns para a Fase 1 do jogo.
 */
public class ScriptFase1 extends ScriptDeFase {

    private transient BufferedImage imagemFundo;
    private transient BufferedImage imagemArvore;
    private long proximoSpawnY = 0;
    private int[] posicoesXDasDiagonais;
    private int direcaoDoGrupo = 1;
    private double distanciaTotalRolada = 0;

    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;
    private static final int OFFSET_DIAGONAL_X = 100;
    private static final int VARIACAO_ALEATORIA_PIXELS = 40;
    private static final int NUMERO_DE_DIAGONAIS = 3;
    private static final int ESPACO_ENTRE_DIAGONAIS_X = 500;

    // Onda
    private ArrayList<Onda> ondas;
    private int ondaAtualIndex;

    /**
     * @brief Construtor do script da Fase 1. Inicializa a música da fase.
     */
    public ScriptFase1() {
        super();
        SoundManager.getInstance().playMusic("Illusionary Night ~ Ghostly Eyes", true);

        posicoesXDasDiagonais = new int[NUMERO_DE_DIAGONAIS];
        for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
            posicoesXDasDiagonais[i] = 50 + (i * ESPACO_ENTRE_DIAGONAIS_X);
        }

        ondaAtualIndex = -1;
    }

    /**
     * @brief Carrega os recursos visuais específicos da fase (imagens de fundo,
     * etc).
     * @param fase A instância da fase para a qual os recursos serão carregados.
     */
    @Override
    public void carregarRecursos(Fase fase) {
        try {
            this.imagemFundo = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage1/stage_1_bg1.png"));
            this.imagemArvore = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage1/stage_1_bg2.png"));

            fase.adicionarElementoCenario(new FundoInfinito("fundo_principal", this.imagemFundo, 1.0, Modelo.Cenario.DrawLayer.BACKGROUND, 1.0f));

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 1: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Restaura as referências de imagens transientes nos elementos de
     * cenário após a desserialização.
     * @param fase A instância da fase cujos elementos precisam ser religados.
     */
    @Override
    public void relinkarRecursosDosElementos(Fase fase) {

        for (var elemento : fase.getElementosCenario()) {
            if (elemento instanceof FundoInfinito) {
                FundoInfinito fundo = (FundoInfinito) elemento;

                if ("fundo_principal".equals(fundo.getId())) {
                    fundo.setImagem(this.imagemFundo);
                }
            } else if (elemento instanceof ArvoreParallax) {
                ((ArvoreParallax) elemento).setImagem(this.imagemArvore);
            }
        }
    }

    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        ondas = new ArrayList<>();
        ondas.add(new Onda1(fase));
        ondas.add(new OndaFazNada(fase, 1000));
        ondas.add(new Onda1(fase));
        ondaAtualIndex = 0;
        return ondas;
    }

    /**
     * @brief Controla o spawning de inimigos (FadaComum) em posições aleatórias
     * na parte superior da tela.
     */
    @Override
    public void atualizarInimigos(Fase fase) {
        if(ondaAtualIndex == -1) {
            ondas = inicializarOndas(fase);
        }

        Onda ondaAtual = ondas.get(ondaAtualIndex);
        ondaAtual.incrementarTempo(1, fase);
        if (!ondaAtual.getFinalizado()) return;

        ondaAtualIndex++;
    }

    // Onda
    private class OndaFazNada extends Onda{
        public OndaFazNada(Fase fase, int tempoDeEsperaInicial) {
            super();
            inimigos.add(new InimigoSpawn(null, tempoDeEsperaInicial));
        }
    }

    private class Onda1 extends Onda{
        public Onda1(Fase fase) {
            super();

            // Adiciona inimigos à onda
            double xInicial = 0.5 * (MUNDO_LARGURA - 2) + 2;
            LootTable lootTable = new LootTable();

            // Loot table
            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, false, false));
            lootTable.addItem(new LootItem(ItemType.POWER_UP, 1, 1, 0.02, true, false));

            // Inimigos
            inimigos.add(new InimigoSpawn(new Modelo.Inimigos.FadaComum1(xInicial, -1.0, lootTable, 40, fase), 200));
            inimigos.add(new InimigoSpawn(new Modelo.Inimigos.FadaComum1(xInicial + 0.1, -1.0, lootTable, 40, fase), 200));
            inimigos.add(new InimigoSpawn(new Modelo.Inimigos.FadaComum1(xInicial - 0.1, -1.0, lootTable, 40, fase), 200));
        }
    }

    protected abstract class Onda {

        // Classes
        protected  class InimigoSpawn {

            protected Personagem personagem;
            protected int tempoAposInicioSpawn;

            public InimigoSpawn(Personagem personagem, int tempoAposInicioSpawn) {
                this.personagem = personagem;
                this.tempoAposInicioSpawn = tempoAposInicioSpawn;
            }

            public void spawn(Fase fase) {
                if(personagem == null) return;
                fase.adicionarPersonagem(personagem);
            }
        }

        // Variaveis
        protected ArrayList<InimigoSpawn> inimigos;

        protected int tempoDeEspera;
        protected int indiceInimigoAtual;
        protected boolean finalizado;

        public Onda() {
            this.tempoDeEspera = 0;
            this.indiceInimigoAtual = 0;
            this.finalizado = false;
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
         * @brief Incrementa o tempo de espera e spawna inimigos conforme o tempo
         * progride.
         * @param tempo O tempo a ser incrementado.
         * @param fase A instância da fase onde os inimigos serão spawnados.
         */
        public void incrementarTempo(int tempo, Fase fase) {
            if(finalizado) return;

            tempoDeEspera -= tempo;
            if(tempoDeEspera > 0) return;

            InimigoSpawn inimigo = proximoInimigo(fase);
            if(inimigo == null){
                finalizado = true;
                return;
            }

            tempoDeEspera = inimigo.tempoAposInicioSpawn;
        }

        /**
         * @brief Reinicia a onda para permitir que ela seja executada novamente.
         */
        public void reiniciar() {
            tempoDeEspera = 0;
            indiceInimigoAtual = 0;
            finalizado = false;
        }

        // Getters
        public boolean getFinalizado() {
            return finalizado;
        }
    }

    /**
     * @brief Atualiza a lógica de spawn de elementos de cenário (como árvores).
     * @param fase A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        if (imagemArvore == null) {
            return;
        }

        this.distanciaTotalRolada += velocidadeScroll;

        if (this.distanciaTotalRolada >= proximoSpawnY) {
            int tamanhoBase = (int) Math.round(LARGURA_TELA * 0.8);

            boolean vaiBaterNaDireita = direcaoDoGrupo == 1
                    && (posicoesXDasDiagonais[NUMERO_DE_DIAGONAIS - 1] + tamanhoBase) > ALTURA_TELA;
            boolean vaiBaterNaEsquerda = direcaoDoGrupo == -1 && posicoesXDasDiagonais[0] < 0;

            if (vaiBaterNaDireita || vaiBaterNaEsquerda) {
                direcaoDoGrupo *= -1;
            }

            for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
                int xBase = posicoesXDasDiagonais[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = random.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;
                int yInicial = -tamanhoBase;

                fase.adicionarElementoCenario(new ArvoreParallax(novoX + randomOffsetX, yInicial, tamanhoBase, velocidadeScroll,
                        imagemArvore));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    /**
     * @brief Preenche o cenário com elementos iniciais (como árvores).
     * @param fase A instância da fase que este script está controlando.
     */
    @Override
    public void preencherCenarioInicial(Fase fase) {
        if (imagemArvore == null) {
            return;
        }

        int tamanhoBase = (int) Math.round(ALTURA_TELA * 0.8);
        while (proximoSpawnY < ALTURA_TELA) {
            boolean vaiBaterNaDireita = direcaoDoGrupo == 1
                    && (posicoesXDasDiagonais[NUMERO_DE_DIAGONAIS - 1] + tamanhoBase) > ALTURA_TELA;
            boolean vaiBaterNaEsquerda = direcaoDoGrupo == -1 && posicoesXDasDiagonais[0] < 0;
            if (vaiBaterNaDireita || vaiBaterNaEsquerda) {
                direcaoDoGrupo *= -1;
            }

            for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
                int xBase = posicoesXDasDiagonais[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = random.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;
                int yInicial = (int) proximoSpawnY - tamanhoBase;

                fase.adicionarElementoCenario(new ArvoreParallax(novoX + randomOffsetX, yInicial, tamanhoBase, 2.0, imagemArvore));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }
}
