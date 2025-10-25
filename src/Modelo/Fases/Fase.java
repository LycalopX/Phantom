package Modelo.Fases;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Items.ItemPool;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilBombaHoming;
import Modelo.Projeteis.ProjetilPool;
import Modelo.Inimigos.Inimigo;
import Auxiliar.Cenario1.ArvoreParallax;
import static Auxiliar.ConfigMapa.*;
import java.io.Serializable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;

/**
 * @brief Representa um contêiner para uma fase do jogo, guardando todos os
 *        personagens,
 *        elementos de cenário e o estado de rolagem. Delega a lógica de eventos
 *        e spawns para um ScriptDeFase.
 */
public class Fase implements Serializable {

    private CopyOnWriteArrayList<Personagem> personagens;
    private ArrayList<ArvoreParallax> arvores;
    private ScriptDeFase scriptDaFase;
    private ProjetilPool projetilPool;
    private ItemPool itemPool;

    private transient BufferedImage imagemFundo1, imagemFundo2;
    private double scrollY = 0;
    private double distanciaTotalRolada = 0;

    /**
     * @brief Construtor da Fase.
     * @param script O script que define os eventos e spawns desta fase.
     */
    public Fase(ScriptDeFase script) {
        this.personagens = new CopyOnWriteArrayList<>();
        this.projetilPool = new ProjetilPool(20, 25, 16, 150, personagens);
        this.itemPool = new ItemPool();
        this.arvores = new ArrayList<>();

        this.scriptDaFase = script;

        this.personagens.addAll(projetilPool.getTodosOsProjeteis());
        this.personagens.addAll(itemPool.getTodosOsItens());
        carregarRecursos();
        
        if (this.scriptDaFase != null) {
            this.scriptDaFase.preencherCenarioInicial(this);
        }
    }

    /**
     * @brief Método para desserialização, recarrega as imagens e restaura
     *        referências.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Garante a segurança de thread ao carregar saves antigos
        if (!(this.personagens instanceof CopyOnWriteArrayList)) {
            this.personagens = new CopyOnWriteArrayList<>(this.personagens);
        }

        carregarRecursos();
        if (this.arvores != null && this.imagemFundo2 != null) {
            for (ArvoreParallax arvore : this.arvores) {
                arvore.relinkarImagens(this.imagemFundo2);
            }
        }
    }

    /**
     * @brief Carrega os recursos de imagem (transient) para a fase.
     */
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
     * @brief Atualiza o estado da fase a cada frame, incluindo rolagem do cenário,
     *        execução do script de fase e atualização de todos os personagens.
     */
    public void atualizar(double velocidadeScroll) {
        scrollY = (scrollY + velocidadeScroll) % (ALTURA_TELA);
        distanciaTotalRolada += velocidadeScroll;

        if (this.scriptDaFase != null) {
            this.scriptDaFase.atualizar(this, velocidadeScroll);
        }

        for (ArvoreParallax arvore : arvores) {
            arvore.mover(velocidadeScroll);
        }
        arvores.removeIf(arvore -> arvore.estaForaDaTela(ALTURA_TELA));

        for (Personagem p : personagens) {
            p.atualizar();
            if (p instanceof Projetil && !(p instanceof ProjetilBombaHoming)) {
                Projetil proj = (Projetil) p;
                if (proj.isActive() && proj.estaForaDaTela()) {
                    proj.deactivate();
                }
            }
        }

        personagens.removeIf(p -> (p instanceof Inimigo) && !p.isActive());
    }

    /**
     * @brief Retorna a piscina de projéteis da fase.
     */
    public ProjetilPool getProjetilPool() {
        return this.projetilPool;
    }

    /**
     * @brief Retorna a piscina de itens da fase.
     */
    public ItemPool getItemPool() {
        return this.itemPool;
    }

    /**
     * @brief Retorna a lista de todos os personagens na fase.
     */
    public java.util.List<Personagem> getPersonagens() {
        return this.personagens;
    }

    /**
     * @brief Retorna a lista de árvores de parallax na fase.
     */
    public ArrayList<ArvoreParallax> getArvores() {
        return this.arvores;
    }

    /**
     * @brief Retorna a imagem de fundo principal da fase.
     */
    public BufferedImage getImagemFundo1() {
        return this.imagemFundo1;
    }

    /**
     * @brief Retorna a posição Y atual da rolagem do fundo.
     */
    public double getScrollY() {
        return this.scrollY;
    }

    /**
     * @brief Adiciona um novo personagem à lista da fase.
     */
    public void adicionarPersonagem(Personagem p) {
        this.personagens.add(p);
    }

    /**
     * @brief Retorna a imagem de textura das árvores.
     */
    public BufferedImage getImagemFundo2() {
        return this.imagemFundo2;
    }

    /**
     * @brief Retorna a distância total que o cenário já rolou.
     */
    public double getDistanciaTotalRolada() {
        return this.distanciaTotalRolada;
    }

    /**
     * @brief Retorna uma referência ao objeto do herói na fase.
     */
    public Personagem getHero() {
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                return p;
            }
        }
        return null;
    }
}