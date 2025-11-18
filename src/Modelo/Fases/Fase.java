package Modelo.Fases;

import static Auxiliar.ConfigMapa.*;
import Modelo.Cenario.ElementoCenario;
import Modelo.Hero.Hero;
import Modelo.Inimigos.Inimigo;
import Modelo.Items.ItemPool;
import Modelo.Personagem;
import Modelo.Projeteis.BombaProjetil;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilBombaHoming;
import Modelo.Projeteis.ProjetilPool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Modelo.Items.Item;

/**
 * @brief Contêiner para todos os elementos de uma fase do jogo.
 * 
 *        Esta classe armazena todos os personagens, elementos de cenário e o
 *        estado
 *        da fase. A lógica de eventos e spawns é delegada a um `ScriptDeFase`.
 *        As coleções usam `CopyOnWriteArrayList` para evitar
 *        `ConcurrentModificationException`
 *        durante a iteração e modificação.
 */
public class Fase implements Serializable {

    private Hero hero;
    private List<Inimigo> inimigos;
    private List<Projetil> projeteis;
    private List<Item> itens;
    private List<BombaProjetil> bombas;

    private ScriptDeFase scriptDaFase;
    private ProjetilPool projetilPool;
    private ItemPool itemPool;
    private List<ElementoCenario> elementosCenario;

    private static boolean isSpeedingUp = false;
    private static int speedupTimer = 0;
    private static int speedupDuration = 0;
    private static double speedupAmplitude = 0;
    private static double globalSpeedMultiplier = 1.0;

    /**
     * @brief Construtor da Fase.
     * @param script O script que define os eventos e spawns desta fase.
     */
    public Fase(ScriptDeFase script) {
        this.inimigos = new CopyOnWriteArrayList<>();
        this.projetilPool = new ProjetilPool(20, 50, 16, 1000);
        this.itemPool = new ItemPool();
        this.elementosCenario = new CopyOnWriteArrayList<>();
        this.scriptDaFase = script;
        this.bombas = new CopyOnWriteArrayList<>();

        this.projeteis = new CopyOnWriteArrayList<>(projetilPool.getTodosOsProjeteis());
        this.itens = new CopyOnWriteArrayList<>(itemPool.getTodosOsItens());

        if (this.scriptDaFase != null) {
            this.scriptDaFase.carregarRecursos(this);
            this.scriptDaFase.preencherCenarioInicial(this);
        }
    }

    /**
     * @brief Ativa um efeito de aceleração global temporário.
     * @param duration  Duração do efeito em frames.
     * @param amplitude A intensidade máxima da aceleração.
     */
    public static void triggerGlobalSpeedup(int duration, double amplitude) {
        if (!isSpeedingUp) {
            isSpeedingUp = true;
            speedupTimer = 0;
            speedupDuration = duration;
            speedupAmplitude = amplitude;
        }
    }

    /**
     * @brief Atualiza o multiplicador de velocidade global com base em uma curva
     *        senoidal.
     */
    private static void updateGlobalSpeedup() {
        if (isSpeedingUp) {
            speedupTimer++;
            if (speedupTimer <= speedupDuration) {

                double sinValue = Math.sin(Math.PI * speedupTimer / speedupDuration);
                globalSpeedMultiplier = 1.0 + speedupAmplitude * sinValue;
            } else {
                isSpeedingUp = false;
                globalSpeedMultiplier = 1.0;
            }
        }
    }

    /**
     * @brief Método customizado para desserialização.
     * 
     *        Garante que recursos não serializáveis, como imagens e referências
     *        de script, sejam recarregados e reinicializados corretamente.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (this.scriptDaFase != null) {
            this.scriptDaFase.carregarRecursos(this);
            this.scriptDaFase.preencherCenarioInicial(this);
            this.scriptDaFase.relinkarRecursosDosElementos(this);
        }
    }

    /**
     * @brief Atualiza o estado da fase a cada frame.
     * 
     *        Executa o script da fase, atualiza a posição de todos os personagens
     *        e elementos de cenário, e remove os objetos inativos.
     */
    public void atualizar(double velocidadeScroll) {
        updateGlobalSpeedup();

        if (this.scriptDaFase != null) {
            this.scriptDaFase.atualizar(this, velocidadeScroll);
        }

        for (ElementoCenario elemento : elementosCenario) {
            elemento.setSpeedMultiplier(globalSpeedMultiplier);
            elemento.mover(velocidadeScroll);
        }
        elementosCenario.removeIf(elemento -> elemento.estaForaDaTela(ALTURA_TELA));

        hero.atualizar();

        for (Inimigo i : inimigos) {
            i.atualizar();
        }

        for (Projetil p : projeteis) {
            p.atualizar();
            if (!(p instanceof ProjetilBombaHoming)) {
                if (p.isActive() && p.estaForaDaTela()) {
                    p.deactivate();
                }
            }
        }

        for (Item i : itens) {
            i.atualizar();
        }

        for (BombaProjetil b : bombas) {
            b.atualizar();
        }

        // Remove personagens inativos das listas para otimizar o processamento.
        inimigos.removeIf(i -> !i.isActive());
        bombas.removeIf(b -> !b.isActive());
    }

    public ScriptDeFase getScript() {
        return this.scriptDaFase;
    }

    public ProjetilPool getProjetilPool() {
        return this.projetilPool;
    }

    public ItemPool getItemPool() {
        return this.itemPool;
    }

    /**
     * @brief Retorna uma lista combinada de todos os personagens na fase.
     * @deprecated Este método é ineficiente por criar uma nova lista a cada
     *             chamada.
     *             Prefira usar os getters específicos: getInimigos(),
     *             getProjeteis(), etc.
     */
    @Deprecated
    public java.util.List<Personagem> getPersonagens() {
        ArrayList<Personagem> todos = new ArrayList<>();
        if (hero != null)
            todos.add(hero);
        todos.addAll(inimigos);
        todos.addAll(projeteis);
        todos.addAll(itens);
        todos.addAll(bombas);
        return todos;
    }

    public List<Inimigo> getInimigos() {
        return inimigos;
    }

    public List<Projetil> getProjeteis() {
        return projeteis;
    }

    public List<Item> getItens() {
        return itens;
    }

    public List<BombaProjetil> getBombas() {
        return bombas;
    }

    public java.util.List<ElementoCenario> getElementosCenario() {
        return this.elementosCenario;
    }

    /**
     * @brief Adiciona um novo personagem à lista apropriada da fase.
     */
    public void adicionarPersonagem(Personagem p) {
        if (p instanceof Hero) {
            this.hero = (Hero) p;
        } else if (p instanceof Inimigo) {
            inimigos.add((Inimigo) p);
        } else if (p instanceof BombaProjetil) {
            bombas.add((BombaProjetil) p);
        } else if (p instanceof Projetil) {
            projeteis.add((Projetil) p);
        } else if (p instanceof Item) {
            itens.add((Item) p);
        }
    }

    /**
     * @brief Adiciona um novo elemento de cenário à lista da fase.
     */
    public void adicionarElementoCenario(ElementoCenario elemento) {
        this.elementosCenario.add(elemento);
    }

    public Hero getHero() {
        return hero;
    }
}