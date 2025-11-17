package Modelo.Fases;

import Modelo.Personagem;
import Modelo.Hero.Hero;
import Modelo.Items.ItemPool;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilBombaHoming;
import Modelo.Projeteis.ProjetilPool;
import Modelo.Inimigos.Inimigo;
import Modelo.Cenario.ElementoCenario;
import static Auxiliar.ConfigMapa.*;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @brief Representa um contêiner para uma fase do jogo, guardando todos os
 *        personagens,
 *        elementos de cenário e o estado de rolagem. Delega a lógica de eventos
 *        e spawns para um ScriptDeFase.
 */
public class Fase implements Serializable {

    private CopyOnWriteArrayList<Personagem> personagens;
    private ScriptDeFase scriptDaFase;
    private ProjetilPool projetilPool;
    private ItemPool itemPool;
    private CopyOnWriteArrayList<ElementoCenario> elementosCenario;

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
        this.personagens = new CopyOnWriteArrayList<>();
        this.projetilPool = new ProjetilPool(20, 25, 16, 500, personagens);
        this.itemPool = new ItemPool();
        this.elementosCenario = new CopyOnWriteArrayList<>();
        this.scriptDaFase = script;

        this.personagens.addAll(projetilPool.getTodosOsProjeteis());
        this.personagens.addAll(itemPool.getTodosOsItens());

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

        if (!(this.personagens instanceof CopyOnWriteArrayList)) {
            this.personagens = new CopyOnWriteArrayList<>(this.personagens);
        }

        if (this.scriptDaFase != null) {
            this.scriptDaFase.carregarRecursos(this);
            this.scriptDaFase.preencherCenarioInicial(this);
            this.scriptDaFase.relinkarRecursosDosElementos(this);
        }

        // O código antigo de relink foi removido daqui
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
     */
    public java.util.List<Personagem> getPersonagens() {
        return this.personagens;
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
        this.personagens.add(p);
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
    public Personagem getHero() {
        for (Personagem p : personagens) {
            if (p instanceof Hero) {
                return p;
            }
        }
        return null;
    }
}