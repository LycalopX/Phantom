// Em Modelo/Fases/Fase.java
package Modelo.Fases;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilPool;
import Auxiliar.Cenario1.ArvoreParallax;
import static Auxiliar.ConfigMapa.*;

import java.io.Serializable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * A classe Fase é um "contêiner" ou "palco".
 * Ela guarda os personagens e o estado do cenário (scroll),
 * mas DELEGA toda a lógica de spawn (eventos) para o seu ScriptDeFase.
 */
public class Fase implements Serializable {

    private ArrayList<Personagem> personagens;
    private ArrayList<ArvoreParallax> arvores;
    private ScriptDeFase scriptDaFase;
    private ProjetilPool projetilPool;

    private transient BufferedImage imagemFundo1, imagemFundo2;
    private double scrollY = 0;
    private double distanciaTotalRolada = 0;

    // --- TODAS AS VARIÁVEIS DE SPAWN (random, proximoSpawnY, etc.) FORAM REMOVIDAS
    // ---
    // A responsabilidade agora é 100% do Script.

    public Fase(ScriptDeFase script) {
        this.personagens = new ArrayList<>();
        this.projetilPool = new ProjetilPool(300, 100, personagens);
        this.arvores = new ArrayList<>();
        this.scriptDaFase = script;

        this.personagens.addAll(projetilPool.getTodosOsProjeteis());
        carregarRecursos(); // Carrega as imagens transient

        // O Script agora é responsável por preencher o cenário inicial
        if (this.scriptDaFase != null) {
            this.scriptDaFase.preencherCenarioInicial(this);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        carregarRecursos();

        // Relinka as imagens nas árvores (a lógica do script já foi carregada)
        if (this.arvores != null && this.imagemFundo2 != null) {
            for (ArvoreParallax arvore : this.arvores) {
                arvore.relinkarImagens(this.imagemFundo2);
            }
        }
    }

    private void carregarRecursos() {
    try {
        imagemFundo1 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage1/stage_1_bg1.png"));
        imagemFundo2 = ImageIO.read(getClass().getClassLoader().getResource("imgs/stage1/stage_1_bg2.png"));
    } catch (Exception e) {
        System.out.println("Erro ao carregar imagens de fundo da fase.");
        e.printStackTrace();
    }
}

    /**
     * O método 'atualizar' agora delega os spawns e gerencia os objetos existentes.
     */
    public void atualizar(double velocidadeScroll) {
        // 1. Atualiza o estado interno do cenário
        scrollY = (scrollY + velocidadeScroll) % (ALTURA_TELA);
        distanciaTotalRolada += velocidadeScroll;

        // 2. DELEGA toda a lógica de spawn (inimigos e árvores) para o script
        if (this.scriptDaFase != null) {
            this.scriptDaFase.atualizar(this, velocidadeScroll);
        }

        // 3. Atualiza os objetos que JÁ EXISTEM na fase

        // Move e remove as árvores
        for (ArvoreParallax arvore : arvores) {
            arvore.mover(velocidadeScroll);
        }
        arvores.removeIf(arvore -> arvore.estaForaDaTela(ALTURA_TELA));

        // Atualiza todos os personagens (herói, inimigos, projéteis)
        for (Personagem p : personagens) {
            p.atualizar(); // Atualiza Inimigos, Projéteis, etc.

            if (p instanceof Projetil) {
                Projetil proj = (Projetil) p;
                if (proj.isActive() && proj.estaForaDaTela()) {

                    proj.deactivate(); // Apenas desativa
                }
            }
        }
    }
    // --- OS MÉTODOS preencherCenarioInicial() e atualizarArvores() FORAM REMOVIDOS
    // ---
    // A lógica deles agora vive em ScriptFase1.java.

    // --- GETTERS E SETTERS ---

    public ProjetilPool getProjetilPool() {
        return this.projetilPool;
    }

    public ArrayList<Personagem> getPersonagens() {
        return this.personagens;
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
    }

    // --- NOVOS GETTERS NECESSÁRIOS PARA O SCRIPT FUNCIONAR ---

    /**
     * O Script precisa desta imagem para criar novas árvores.
     * 
     * @return A imagem de textura das árvores.
     */
    public BufferedImage getImagemFundo2() {
        return this.imagemFundo2;
    }

    /**
     * O Script precisa saber a distância rolada para decidir quando spawnar.
     * 
     * @return A distância total que o cenário já rolou.
     */
    public double getDistanciaTotalRolada() {
        return this.distanciaTotalRolada;
    }

    // --- Métodos de utilidade ---

    public Personagem getHero() {
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                return p;
            }
        }
        return null;
    }
}