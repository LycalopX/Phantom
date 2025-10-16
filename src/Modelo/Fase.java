package Modelo;

import Auxiliar.ArvoreParallax;
import Auxiliar.Consts; // Adicionado para o caminho das imagens
import Auxiliar.Projetil;
import Auxiliar.TipoProjetil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random; // Adicionado para o spawn
import javax.imageio.ImageIO;
import java.io.File;

public class Fase {

    private ArrayList<Personagem> personagens;
    private ArrayList<Projetil> projeteis;
    private ArrayList<ArvoreParallax> arvores;

    private BufferedImage imagemFundo1, imagemFundo2; // Adicionada imagemFundo2
    private double scrollY = 0;
    private double distanciaTotalRolada = 0;

    // --- LÓGICA DE SPAWN MOVIDA DE CENARIO.JAVA ---
    private final Random random = new Random();
    private long proximoSpawnY = 0;
    private final int[] posicoesXDasDiagonais;
    private int direcaoDoGrupo = 1;
    private static final int DISTANCIA_ENTRE_ONDAS_Y = 250;
    private static final int OFFSET_DIAGONAL_X = 100;
    private static final int VARIACAO_ALEATORIA_PIXELS = 40;
    private static final int NUMERO_DE_DIAGONAIS = 3;
    private static final int ESPACO_ENTRE_DIAGONAIS_X = 500;

    private long proximoSpawnInimigo = 0;
    private long intervaloSpawnInimigo = 120; // Spawn a cada 120 frames (2 segundos)
    // ---------------------------------------------

    public Fase() {
        this.personagens = new ArrayList<>();
        this.projeteis = new ArrayList<>();
        this.arvores = new ArrayList<>();

        carregarRecursos(); // Método para carregar imagens

        posicoesXDasDiagonais = new int[NUMERO_DE_DIAGONAIS];
        for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
            posicoesXDasDiagonais[i] = 50 + (i * ESPACO_ENTRE_DIAGONAIS_X);
        }

        preencherCenarioInicial();
    }

    private void carregarRecursos() {
        try {
            // Usa o caminho de Consts para ser mais robusto
            String basePath = new java.io.File(".").getCanonicalPath() + Consts.PATH;
            imagemFundo1 = ImageIO.read(new File(basePath + "stage1/stage_1_bg1.png"));
            imagemFundo2 = ImageIO.read(new File(basePath + "stage1/stage_1_bg2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // O método 'atualizar' da fase cuida da lógica INTERNA dela
    public void atualizar(double velocidadeScroll) {
        scrollY = (scrollY + velocidadeScroll) % (Consts.altura);
        distanciaTotalRolada += velocidadeScroll;

        for (Projetil p : projeteis) {
            p.mover();
        }
        projeteis.removeIf(p -> p.estaForaDaTela(Consts.largura, Consts.altura));

        atualizarArvores(velocidadeScroll);

        if (proximoSpawnInimigo <= 0) {
            // Cria um novo inimigo em uma posição X aleatória no topo da tela
            double xInicial = random.nextDouble() * Consts.MUNDO_LARGURA;
            adicionarPersonagem(new Inimigo("inimigo.png", xInicial, -1.0, 100)); // Começa um pouco acima da tela
            proximoSpawnInimigo = intervaloSpawnInimigo;
        } else {
            proximoSpawnInimigo--;
        }

        // Atualiza todos os personagens (incluindo inimigos)
        for (Personagem p : personagens) {
            if (p instanceof Inimigo) {
                ((Inimigo) p).atualizar();
            }
        }
    }

    public void adicionarProjetil(Projetil p) {
        if (p != null) { // Uma pequena verificação de segurança
            this.projeteis.add(p);
        }
    }

    private void atualizarArvores(double velocidadeScroll) {
        // --- LÓGICA DE SPAWN DE ÁRVORES (DE CENARIO.JAVA) ---
        if (imagemFundo2 != null && distanciaTotalRolada >= proximoSpawnY) {
            int tamanhoBase = (int) Math.round(Consts.largura * 0.8);
            boolean vaiBaterNaDireita = direcaoDoGrupo == 1
                    && (posicoesXDasDiagonais[NUMERO_DE_DIAGONAIS - 1] + tamanhoBase) > Consts.altura;
            boolean vaiBaterNaEsquerda = direcaoDoGrupo == -1 && posicoesXDasDiagonais[0] < 0;
            if (vaiBaterNaDireita || vaiBaterNaEsquerda) {
                direcaoDoGrupo *= -1;
            }
            for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {

                int xBase = posicoesXDasDiagonais[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = random.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;
                int yInicial = -tamanhoBase;

                arvores.add(new ArvoreParallax(novoX + randomOffsetX, yInicial, tamanhoBase, velocidadeScroll,
                        imagemFundo2));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
        // ---------------------------------------------------

        for (ArvoreParallax arvore : arvores) {
            arvore.mover(velocidadeScroll);
        }
        arvores.removeIf(arvore -> arvore.estaForaDaTela(Consts.altura));
    }

    // Getters para que a View (Cenario2) e o Controller (Engine) possam ler os
    // dados
    public ArrayList<Personagem> getPersonagens() {
        return this.personagens;
    }

    public ArrayList<Projetil> getProjeteis() {
        return this.projeteis;
    }

    public ArrayList<ArvoreParallax> getArvores() {
        return this.arvores;
    }

    public BufferedImage getImagemFundo1() {
        return this.imagemFundo1;
    }

    public double getScrollY() {
        return this.scrollY;
    }

    public void adicionarPersonagem(Personagem p) {
        this.personagens.add(p);
    }// Dentro da classe Modelo/Fase.java

    private void preencherCenarioInicial() {
        int tamanhoBase = (int) Math.round(Consts.altura * 0.8);

        // Continua gerando ondas de árvores até que a próxima posição de spawn
        // esteja abaixo da tela, garantindo que tudo esteja coberto.
        while (proximoSpawnY < Consts.altura) {
            
            // Lógica de spawn copiada de 'atualizarArvores'
            boolean vaiBaterNaDireita = direcaoDoGrupo == 1
                    && (posicoesXDasDiagonais[NUMERO_DE_DIAGONAIS - 1] + tamanhoBase) > Consts.altura;
            boolean vaiBaterNaEsquerda = direcaoDoGrupo == -1 && posicoesXDasDiagonais[0] < 0;

            if (vaiBaterNaDireita || vaiBaterNaEsquerda) {
                direcaoDoGrupo *= -1;
            }

            for (int i = 0; i < NUMERO_DE_DIAGONAIS; i++) {
                int xBase = posicoesXDasDiagonais[i];
                int novoX = xBase + (OFFSET_DIAGONAL_X * direcaoDoGrupo);
                int randomOffsetX = random.nextInt(VARIACAO_ALEATORIA_PIXELS * 2) - VARIACAO_ALEATORIA_PIXELS;

                // A posição Y inicial é relativa a onde a "onda" deveria estar (proximoSpawnY)
                int yInicial = (int) proximoSpawnY - tamanhoBase;

                arvores.add(new ArvoreParallax(novoX + randomOffsetX, yInicial, tamanhoBase, 2.0, imagemFundo2));
                posicoesXDasDiagonais[i] = novoX;
            }
            proximoSpawnY += DISTANCIA_ENTRE_ONDAS_Y;
        }
    }

    public void ativarBomba() {
        // Limpa todos os projéteis da tela
        this.projeteis.clear();

        this.projeteis.removeIf(p -> p.getTipo() == TipoProjetil.INIMIGO);

        // (Isso assume que você tem uma classe Inimigo que herda de Personagem)
        this.personagens.removeIf(p -> p instanceof Inimigo);

        System.out.println("BOMBA ATIVADA!");
    }

    public Personagem getHero() {
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                return p;
            }
        }
        return null;
    }

}