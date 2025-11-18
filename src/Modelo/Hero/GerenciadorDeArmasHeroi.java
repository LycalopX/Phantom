package Modelo.Hero;

import java.io.Serializable;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;
import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilHoming;
import Modelo.Projeteis.ProjetilPool;
import Modelo.Projeteis.TipoProjetilHeroi;
import java.util.List;
import Modelo.Inimigos.Inimigo;

/**
 * @brief Gerencia os sistemas de armas do herói.
 * 
 *        Esta classe controla a lógica de tiro, os tipos de projéteis e os
 *        cooldowns com base no nível de poder do jogador.
 */
public class GerenciadorDeArmasHeroi implements Serializable {

    private final double tempoDeRecarga = 6;
    private int cooldownTiroPrincipal = 0;
    private int cooldownMisseis = 0;

    private static final int MISSILE_COOLDOWN_TIME = 20;

    /**
     * @brief Atualiza os contadores de cooldown para todas as armas.
     * 
     *        Este método deve ser chamado a cada frame para decrementar os timers
     *        de recarga do tiro principal e dos mísseis.
     */
    public void atualizarTimers() {
        if (cooldownTiroPrincipal > 0)
            cooldownTiroPrincipal--;
        if (cooldownMisseis > 0)
            cooldownMisseis--;
    }

    /**
     * @brief Executa a lógica de disparo do herói.
     * 
     *        Cria projéteis (normais e teleguiados) com base no nível de poder
     *        e nos cooldowns atuais, utilizando um pool de objetos para otimização.
     * 
     * @param x     Posição X de origem do disparo.
     * @param y     Posição Y de origem do disparo.
     * @param power Nível de poder atual do herói.
     * @param fase  A fase atual, para adicionar os projéteis.
     */
    public void disparar(double x, double y, int power, Fase fase) {
        int nivelTiro = getNivelTiro(power);
        ProjetilPool pool = fase.getProjetilPool();
        if (pool == null)
            return;

        // Dispara mísseis teleguiados se o nível de tiro for suficiente
        // e o cooldown específico dos mísseis tiver terminado.
        if (nivelTiro >= 1 && cooldownMisseis <= 0) {
            adicionarMisseisTeleguiados(x, y, nivelTiro, fase);
            cooldownMisseis = MISSILE_COOLDOWN_TIME;
        }

        // Dispara o tiro principal se o cooldown tiver terminado.
        if (cooldownTiroPrincipal <= 0) {
            Auxiliar.SoundManager.getInstance().playSfx("se_plst00", 1.0);

            // A velocidade do projétil aumenta ligeiramente com o nível de tiro.
            double velocidadeProjetilEmGrid = 40.0 / CELL_SIDE;
            double velocidadeFinal = FATOR_ESCALA_ALTURA
                    * (velocidadeProjetilEmGrid * (1 + (Math.min(nivelTiro, 3) - 1) * 0.1));

            // Tiro principal, sempre presente.
            Projetil p1 = pool.getProjetilNormal();
            if (p1 != null) {
                p1.reset(x, y, velocidadeFinal, -90, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
            }

            // A partir do nível 3, adiciona tiros laterais em ângulo.
            if (nivelTiro >= 3) {
                double offsetX = 0.5;
                Projetil p2 = pool.getProjetilNormal();

                if (p2 != null) {
                    p2.reset(x - offsetX, y, velocidadeFinal, -100, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
                }
                Projetil p3 = pool.getProjetilNormal();
                if (p3 != null) {
                    p3.reset(x + offsetX, y, velocidadeFinal, -80, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
                }
            }

            // O cooldown diminui com o nível de poder, aumentando a cadência de tiro,
            // com um limite mínimo para não se tornar rápido demais.
            int cooldownFinal = (int) ((tempoDeRecarga) - (getNivelTiro(power) * 0.2));
            cooldownTiroPrincipal = Math.max(cooldownFinal, 2);
        }
    }

    /**
     * @brief Adiciona mísseis teleguiados à fase.
     * 
     *        O número e a velocidade dos mísseis são baseados no nível de poder do
     *        herói.
     */
    private void adicionarMisseisTeleguiados(double x, double y, int nivelDeMisseis, Fase fase) {
        ProjetilPool pool = fase.getProjetilPool();
        double velocidadeBase = 8.0 / CELL_SIDE;
        double velocidadeFinal = FATOR_ESCALA_ALTURA * (velocidadeBase * (1 + (nivelDeMisseis - 1) * 0.2));

        // Ajusta o número de mísseis com base em patamares de nível.
        if (nivelDeMisseis == 2) {
            nivelDeMisseis -= 1;
        } else if (nivelDeMisseis > 2) {
            nivelDeMisseis -= 2;
        }

        // Cria pares de mísseis que são disparados em ângulos abertos
        // e depois buscam os inimigos.
        for (int i = 0; i < nivelDeMisseis; i++) {
            double anguloEsquerda = -90 - 30 - (i * 10);
            double anguloDireita = -90 + 30 + (i * 10);
            double offsetX = 0.3 * (i + 1);

            ProjetilHoming pEsquerdo = pool.getProjetilHoming();
            if (pEsquerdo != null) {
                pEsquerdo.resetHoming(x - offsetX, y, velocidadeFinal, anguloEsquerda, TipoProjetil.JOGADOR,
                        TipoProjetilHeroi.HOMING, (List<Inimigo>) fase.getInimigos());
            }

            ProjetilHoming pDireito = pool.getProjetilHoming();
            if (pDireito != null) {
                pDireito.resetHoming(x + offsetX, y, velocidadeFinal, anguloDireita, TipoProjetil.JOGADOR,
                        TipoProjetilHeroi.HOMING, (List<Inimigo>) fase.getInimigos());
            }
        }
    }

    /**
     * @brief Calcula o nível de tiro do herói com base em seu poder.
     * 
     *        A fórmula resulta em uma progressão onde são necessários cada vez mais
     *        pontos de poder para atingir o próximo nível.
     * 
     * @param power O poder atual do herói.
     * @return O nível de tiro (limitado a 5).
     */
    public int getNivelTiro(int power) {
        double next_term = (-1 + Math.sqrt(1 + power)) / 2;
        return Math.min((int) next_term, 5);
    }
}