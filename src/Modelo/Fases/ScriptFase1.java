// Em Modelo/Fases/ScriptFase1.java
package Modelo.Fases;

import Modelo.Items.ItemType;
import Auxiliar.LootTable;
import Auxiliar.SoundManager;
import Auxiliar.Cenario1.ArvoreParallax;
import Auxiliar.Personagem.LootItem;
import static Auxiliar.ConfigMapa.*;

import java.awt.image.BufferedImage;

public class ScriptFase1 extends ScriptDeFase {

    // --- LÓGICA DE INIMIGOS (ESPECÍFICA DA FASE 1) ---
    private long proximoSpawnInimigo = 0;
    private long intervaloSpawnInimigo = 30; // Spawn a cada 0.5 seg

    // --- LÓGICA DAS ÁRVORES (ESPECÍFICA DA FASE 1) ---
    private long proximoSpawnY = 0;
    private int[] posicoesXDasDiagonais;
    private int direcaoDoGrupo = 1;

    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;
    private static final int OFFSET_DIAGONAL_X = 100;
    private static final int VARIACAO_ALEATORIA_PIXELS = 40;
    private static final int NUMERO_DE_DIAGONAIS = 3;
    private static final int ESPACO_ENTRE_DIAGONAIS_X = 500;
    // -------------------------------------------------

    /**
     * Construtor: Inicializa as posições das árvores
     */
    public ScriptFase1() {
        super(); // Chama o construtor de ScriptDeFase
        posicoesXDasDiagonais = new int[NUMERO_DE_DIAGONAIS];
        
        for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
            posicoesXDasDiagonais[i] = 50 + (i * ESPACO_ENTRE_DIAGONAIS_X);
        }
        SoundManager.getInstance().playMusic("Illusionary Night ~ Ghostly Eyes", true);
    }

    /**
     * Implementa o spawn de inimigos da Fase 1.
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
     * Implementa o spawn de ÁRVORES (cenário) da Fase 1.
     */
    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        BufferedImage imagemArvore = fase.getImagemFundo2();
        if (imagemArvore == null)
            return;

        if (fase.getDistanciaTotalRolada() >= proximoSpawnY) {

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

                fase.getArvores().add(new ArvoreParallax(novoX + randomOffsetX, yInicial, tamanhoBase, velocidadeScroll,
                        imagemArvore));

                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    /**
     * Implementa o preenchimento INICIAL de árvores da Fase 1.
     */
    @Override
    public void preencherCenarioInicial(Fase fase) {
        BufferedImage imagemArvore = fase.getImagemFundo2();
        if (imagemArvore == null)
            return;

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

                fase.getArvores()
                        .add(new ArvoreParallax(novoX + randomOffsetX, yInicial, tamanhoBase, 2.0, imagemArvore));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }
}