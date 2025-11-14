package Modelo.Fases;

import Auxiliar.Cenario1.ArvoreParallax;
import static Auxiliar.ConfigMapa.*;
import Auxiliar.SoundManager;
import Controler.Engine;
import Modelo.Cenario.DrawLayer;
import Modelo.Cenario.FundoInfinito;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @brief Script de eventos e spawns para a Fase 2 do jogo.
 */
public class ScriptFase2 extends ScriptDeFase {

    private transient BufferedImage imagemFundo;
    private transient BufferedImage imagemTrilha;
    private transient BufferedImage imagemArvoreNormal;

    private long proximoSpawnY = 0;
    private double distanciaTotalRolada = 0;

    private static final int DISTANCIA_ENTRE_ONDAS_Y = 180;

    /**
     * @brief Construtor do script da Fase 2.
     */
    public ScriptFase2(Engine engine) {
        super(engine);
        SoundManager.getInstance().playMusic("Song of the Night Sparrow ~ Night Bird", true);
    }

    /**
     * @brief Carrega os recursos visuais específicos da fase.
     * @param fase A instância da fase para a qual os recursos serão carregados.
     */
    @Override
    public void carregarRecursos(Fase fase) {
        try {
            this.imagemFundo = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage2/bg1.png"));
            this.imagemTrilha = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage2/bg3.png"));
            this.imagemArvoreNormal = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage2/bg2.png"));
            
            fase.adicionarElementoCenario(new FundoInfinito("fundo_principal", this.imagemFundo, 0.5, DrawLayer.BACKGROUND, 1.0f));
            fase.adicionarElementoCenario(new FundoInfinito("trilha", this.imagemTrilha, 1.0, DrawLayer.BACKGROUND, 0.5f));

        } catch (Exception e) {
            System.err.println("Erro ao carregar recursos da Fase 2: " + e.getMessage());
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
                } else if ("trilha".equals(fundo.getId())) {
                    fundo.setImagem(this.imagemTrilha);
                }
            } else if (elemento instanceof ArvoreParallax) {
                ((ArvoreParallax) elemento).setImagem(this.imagemArvoreNormal);
            }
        }
    }

    /**
     * @brief Controla o spawning de inimigos.
     */
    @Override
    public void atualizarInimigos(Fase fase) {
        // Lógica de inimigos da Fase 2 (a ser implementada)
    }

    /**
     * @brief Atualiza a lógica de spawn de elementos de cenário (árvores azuis).
     * @param fase A instância da fase que este script está controlando.
     * @param velocidadeScroll A velocidade de rolagem atual do cenário.
     */
    @Override
    public void atualizarCenario(Fase fase, double velocidadeScroll) {
        if (imagemArvoreNormal == null) return;

        this.distanciaTotalRolada += velocidadeScroll;

        if (this.distanciaTotalRolada >= proximoSpawnY) {
            int tamanhoBase = (int) Math.round(LARGURA_TELA * 0.825);
            int yInicial = -tamanhoBase;

            // Gera árvore na faixa [-1/2, -1/3] da tela
            double inicioFaixaEsquerda = -LARGURA_TELA / 2.0;
            double larguraFaixaEsquerda = LARGURA_TELA / 6.0; // (-1/3) - (-1/2) = 1/6
            double xEsquerda = inicioFaixaEsquerda + random.nextDouble() * larguraFaixaEsquerda;
            fase.adicionarElementoCenario(new ArvoreParallax((int)xEsquerda, yInicial, tamanhoBase, velocidadeScroll, this.imagemArvoreNormal));

            // Gera árvore da direita na faixa [7/12, 9/12]
            double inicioFaixaDireita = LARGURA_TELA * 7.0 / 12.0;
            double larguraFaixaDireita = LARGURA_TELA / 6.0; // 9/12 - 7/12 = 2/12 = 1/6
            double xDireita = inicioFaixaDireita + random.nextDouble() * larguraFaixaDireita;
            fase.adicionarElementoCenario(new ArvoreParallax((int)xDireita, yInicial, tamanhoBase, velocidadeScroll, this.imagemArvoreNormal));
            
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    /**
     * @brief Preenche o cenário com elementos iniciais.
     * @param fase A instância da fase que este script está controlando.
     */
    @Override
    public void preencherCenarioInicial(Fase fase) {
        if (imagemArvoreNormal == null) return;

        int tamanhoBase = (int) Math.round(LARGURA_TELA * 0.825);
        while (proximoSpawnY < ALTURA_TELA) {
            int yInicial = (int) proximoSpawnY - tamanhoBase;

            // Gera árvore na faixa [-1/2, -1/3] da tela
            double inicioFaixaEsquerda = -LARGURA_TELA / 2.0;
            double larguraFaixaEsquerda = LARGURA_TELA / 6.0; // (-1/3) - (-1/2) = 1/6
            double xEsquerda = inicioFaixaEsquerda + random.nextDouble() * larguraFaixaEsquerda;
            fase.adicionarElementoCenario(new ArvoreParallax((int)xEsquerda, yInicial, tamanhoBase, 2.0, this.imagemArvoreNormal));

            // Gera árvore da direita na faixa [7/12, 9/12]
            double inicioFaixaDireita = LARGURA_TELA * 7.0 / 12.0;
            double larguraFaixaDireita = LARGURA_TELA / 6.0; // 9/12 - 7/12 = 2/12 = 1/6
            double xDireita = inicioFaixaDireita + random.nextDouble() * larguraFaixaDireita;
            fase.adicionarElementoCenario(new ArvoreParallax((int)xDireita, yInicial, tamanhoBase, 2.0, this.imagemArvoreNormal));
            
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }
}