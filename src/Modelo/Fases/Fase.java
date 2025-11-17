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
 * @brief Representa um contêiner para uma fase do jogo, guardando todos os
 *        personagens,
 *        elementos de cenário e o estado de rolagem. Delega a lógica de eventos
 *        e spawns para um ScriptDeFase.
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
        this.projetilPool = new ProjetilPool(20, 25, 16, 1000);
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

    public static void triggerGlobalSpeedup(int duration, double amplitude) {
        if (!isSpeedingUp) {
            isSpeedingUp = true;
            speedupTimer = 0;
            speedupDuration = duration;
            speedupAmplitude = amplitude;
        }
    }

    private static void updateGlobalSpeedup() {
        if (isSpeedingUp) {
            speedupTimer++;
            if (speedupTimer <= speedupDuration) {
                // sin(x) from 0 to PI
                double sinValue = Math.sin(Math.PI * speedupTimer / speedupDuration);
                globalSpeedMultiplier = 1.0 + speedupAmplitude * sinValue;
            } else {
                isSpeedingUp = false;
                globalSpeedMultiplier = 1.0;
            }
        }
    }

    /**
     * @brief Método para desserialização, recarrega as imagens e restaura
     *        referências.
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
     * @brief Atualiza o estado da fase a cada frame, incluindo rolagem do cenário,
     *        execução do script de fase e atualização de todos os personagens.
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

        inimigos.removeIf(i -> !i.isActive());
        bombas.removeIf(b -> !b.isActive());
    }

    public ScriptDeFase getScript() {
        return this.scriptDaFase;
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
     * @deprecated This method is deprecated due to performance issues.
     * Prefer using specific getters like getInimigos(), getProjeteis(), etc.
     */
    @Deprecated
    public java.util.List<Personagem> getPersonagens() {
        ArrayList<Personagem> todos = new ArrayList<>();
        if (hero != null) todos.add(hero);
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


    /**
     * @brief Retorna a lista de elementos do cenário na fase.
     */
    public java.util.List<ElementoCenario> getElementosCenario() {
        return this.elementosCenario;
    }

    /**
     * @brief Adiciona um novo personagem à lista da fase.
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

    /**
     * @brief Retorna uma referência ao objeto do herói na fase.
     */
    public Hero getHero() {
        return hero;
    }
}