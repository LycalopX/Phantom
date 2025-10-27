package Modelo.Fases;

import Modelo.Items.ItemType;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Auxiliar.Cenario1.ArvoreParallax;
import Auxiliar.Personagem.LootItem;
import Modelo.Cenario.FundoInfinito;
import static Auxiliar.ConfigMapa.*;
import java.awt.image.BufferedImage;
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
    }

    /**
     * @brief Carrega os recursos visuais específicos da fase (imagens de fundo, etc).
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
     * @brief Restaura as referências de imagens transientes nos elementos de cenário após a desserialização.
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

    /**
     * @brief Controla o spawning de inimigos (FadaComum) em posições aleatórias na parte superior da tela.
     */
    @Override
    public void atualizarInimigos(Fase fase) {
        if (proximoSpawnInimigo <= 0) {

            double xInicial = random.nextDouble() * MUNDO_LARGURA;
            LootTable lootTable = new LootTable();

            lootTable.addItem(new LootItem(ItemType.MINI_POWER_UP, 1, 1, 0.5, true, false));
            lootTable.addItem(new LootItem(ItemType.SCORE_POINT, 1, 1, 0.5, false, false));
            lootTable.addItem(new LootItem(ItemType.POWER_UP, 1, 1, 0.02, true, false));
            fase.adicionarPersonagem(new Modelo.Inimigos.FadaComum(xInicial, -1.0, lootTable, 40, fase));

            proximoSpawnInimigo = intervaloSpawnInimigo;
        } else {
            proximoSpawnInimigo--;
        }
    }

    /**
     * @brief Atualiza a lógica de spawn de elementos de cenário (como árvores).
     * @param fase             A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        if (imagemArvore == null) return;

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
        if (imagemArvore == null) return;

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