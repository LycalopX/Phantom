package Modelo.Hero;

import java.io.Serializable;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;
import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilHoming;
import Modelo.Projeteis.ProjetilPool;
import Modelo.Projeteis.TipoProjetilHeroi;

/**
 * @brief Gerencia os sistemas de armas do herói, controlando a lógica de tiro,
 *        tipos de projéteis e cooldowns com base no nível de poder.
 */
public class GerenciadorDeArmasHeroi implements Serializable {

    private final double tempoDeRecarga = 6;
    private int cooldownTiroPrincipal = 0;
    private int cooldownMisseis = 0;

    private static final int MISSILE_COOLDOWN_TIME = 20;

    /**
     * @brief Atualiza os contadores internos de cooldown para o tiro principal e
     *        mísseis.
     *        Deve ser chamado a cada frame.
     */
    public void atualizarTimers() {
        if (cooldownTiroPrincipal > 0)
            cooldownTiroPrincipal--;
        if (cooldownMisseis > 0)
            cooldownMisseis--;
    }

    /**
     * @brief Executa a lógica de disparo principal do herói, criando projéteis
     *        com base no nível de poder e nos cooldowns atuais.
     * @param x     Posição X do herói.
     * @param y     Posição Y do herói.
     * @param power Nível de poder atual do herói.
     * @param fase  A fase atual do jogo para adicionar os projéteis.
     */
    public void disparar(double x, double y, int power, Fase fase) {
        int nivelTiro = getNivelTiro(power);
        ProjetilPool pool = fase.getProjetilPool();
        if (pool == null)
            return;

        if (nivelTiro >= 1 && cooldownMisseis <= 0) {
            adicionarMisseisTeleguiados(x, y, nivelTiro, fase);
            cooldownMisseis = MISSILE_COOLDOWN_TIME;
        }

        if (cooldownTiroPrincipal <= 0) {
            Auxiliar.SoundManager.getInstance().playSfx("se_plst00", 1.0);
            double velocidadeProjetilEmGrid = 40.0 / CELL_SIDE;
            double velocidadeFinal = FATOR_ESCALA_ALTURA * (velocidadeProjetilEmGrid * (1 + (Math.min(nivelTiro, 3) - 1) * 0.03));

            Projetil p1 = pool.getProjetilNormal();
            if (p1 != null) {
                p1.reset(x, y, velocidadeFinal, -90, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
            }

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

            int cooldownFinal = (int) ((tempoDeRecarga) - (getNivelTiro(power) * 0.5));
            cooldownTiroPrincipal = Math.max(cooldownFinal, 2);
        }
    }

    /**
     * @brief Adiciona mísseis teleguiados à fase, com base no nível de poder do
     *        herói.
     */
    private void adicionarMisseisTeleguiados(double x, double y, int nivelDeMisseis, Fase fase) {
        ProjetilPool pool = fase.getProjetilPool();
        double velocidadeBase = 8.0 / CELL_SIDE;
        double velocidadeFinal = FATOR_ESCALA_ALTURA * (velocidadeBase * (1 + (nivelDeMisseis - 1) * 0.2));

        if (nivelDeMisseis == 2) {
            nivelDeMisseis -= 1;
        } else if (nivelDeMisseis > 2) {
            nivelDeMisseis -= 2;
        }

        for (int i = 0; i < nivelDeMisseis; i++) {
            double anguloEsquerda = -90 - 30 - (i * 10);
            double anguloDireita = -90 + 30 + (i * 10);
            double offsetX = 0.3 * (i + 1);

            ProjetilHoming pEsquerdo = pool.getProjetilHoming();
            if (pEsquerdo != null) {
                pEsquerdo.resetHoming(x - offsetX, y, velocidadeFinal, anguloEsquerda, TipoProjetil.JOGADOR,
                        TipoProjetilHeroi.HOMING);
            }

            ProjetilHoming pDireito = pool.getProjetilHoming();
            if (pDireito != null) {
                pDireito.resetHoming(x + offsetX, y, velocidadeFinal, anguloDireita, TipoProjetil.JOGADOR,
                        TipoProjetilHeroi.HOMING);
            }
        }
    }

    /**
     * @brief Calcula o nível de tiro do herói com base em seu poder.
     * @param power O poder atual do herói.
     * @return O nível de tiro calculado.
     */
    public int getNivelTiro(int power) {
        double next_term = (-1 + Math.sqrt(1 + power)) / 2;
        return Math.min((int) next_term, 5);
    }
}