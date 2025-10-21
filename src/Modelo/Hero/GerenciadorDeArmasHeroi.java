// Em Modelo/GerenciadorDeArmas.java

package Modelo.Hero;

import java.io.Serializable;
import Auxiliar.Projeteis.TipoProjetil;
import static Auxiliar.ConfigMapa.*;

import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilHoming;
import Modelo.Projeteis.ProjetilPool;
import Modelo.Projeteis.TipoProjetilHeroi;

public class GerenciadorDeArmasHeroi implements Serializable {

    private final double tempoDeRecarga = 6; // Frames de recarga base
    private int cooldownTiroPrincipal = 0;
    private int cooldownMisseis = 0;

    // Constante para o cooldown dos mísseis (ex: 20 frames = 3 tiros por segundo a
    // 60 FPS)
    private static final int MISSILE_COOLDOWN_TIME = 20;

    /**
     * Atualiza os contadores internos de cooldown.
     * Deve ser chamado a cada frame.
     */
    public void atualizarTimers() {
        if (cooldownTiroPrincipal > 0)
            cooldownTiroPrincipal--;
        if (cooldownMisseis > 0)
            cooldownMisseis--;
    }

    /**
     * Lógica principal de tiro. Decide quais projéteis criar com base no poder e
     * nos cooldowns.
     * 
     * @param x     Posição X do Herói.
     * @param y     Posição Y do Herói.
     * @param power Quantidade de poder do Herói.
     * @return Uma lista de novos projéteis a serem adicionados à fase.
     */
    public void disparar(double x, double y, int power, Fase fase) {

        int nivelTiro = getNivelTiro(power);


        ProjetilPool pool = fase.getProjetilPool();
        if (pool == null)
            return;

        // Dispara os Mísseis
        if (nivelTiro >= 1 && cooldownMisseis <= 0) {
            adicionarMisseisTeleguiados(x, y, nivelTiro, fase);
            cooldownMisseis = MISSILE_COOLDOWN_TIME;
        }

        // 2. Tenta disparar o Tiro Principal
        if (cooldownTiroPrincipal <= 0) {
            Auxiliar.SoundManager.getInstance().playSfx("se_plst00", 1.0);
            double velocidadeProjetilEmGrid = 40.0 / CELL_SIDE;

            double velocidadeFinal = velocidadeProjetilEmGrid * (1 + (Math.min(nivelTiro, 3) - 1) * 0.03);

            // Tiro Central
            Projetil p1 = pool.getProjetilNormal();
            if (p1 != null) {
                p1.reset(x, y, velocidadeFinal, -90, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
            }

            // Tiros Laterais (se tiver nível 3)
            if (nivelTiro >= 3) {
                double offsetX = 0.5; // Distância lateral do centro do herói

                Projetil p2 = pool.getProjetilNormal();
                if (p2 != null) {
                    p2.reset(x - offsetX, y, velocidadeFinal, -100, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
                }

                Projetil p3 = pool.getProjetilNormal();
                if (p3 != null) {
                    p3.reset(x + offsetX, y, velocidadeFinal, -80, TipoProjetil.JOGADOR, TipoProjetilHeroi.NORMAL);
                }
            }

            // A cadência de tiro aumenta com o poder (cooldown diminui)
            //
            int cooldownFinal = (int) ((tempoDeRecarga) - (getNivelTiro(power) * 0.5));
            cooldownTiroPrincipal = Math.max(cooldownFinal, 2); // Garante um cooldown mínimo
        }

        return;
    }

    private void adicionarMisseisTeleguiados(double x, double y, int nivelDeMisseis, Fase fase) {

        ProjetilPool pool = fase.getProjetilPool();

        double velocidadeBase = 8.0 / CELL_SIDE;
        double velocidadeFinal = velocidadeBase * (1 + (nivelDeMisseis - 1) * 0.2);

        if (nivelDeMisseis == 2) {
            nivelDeMisseis -= 1;
        } else if (nivelDeMisseis > 2){
            nivelDeMisseis -= 2;
        }
 
        for (int i = 0; i < nivelDeMisseis; i++) {
            double anguloEsquerda = -90 - 30 - (i * 10);
            double anguloDireita = -90 + 30 + (i * 10);
            double offsetX = 0.3 * (i + 1);

            ProjetilHoming pEsquerdo = pool.getProjetilHoming();
            if (pEsquerdo != null) {
                pEsquerdo.resetHoming(x - offsetX, y, velocidadeFinal, anguloEsquerda, TipoProjetil.JOGADOR, TipoProjetilHeroi.HOMING);
            }

            // 2. Pega um projétil COMPLETAMENTE DIFERENTE e o armazena em 'pDireito'
            ProjetilHoming pDireito = pool.getProjetilHoming();
            if (pDireito != null) {
                pDireito.resetHoming(x + offsetX, y, velocidadeFinal, anguloDireita, TipoProjetil.JOGADOR, TipoProjetilHeroi.HOMING);
            }
        }
    }

    public int getNivelTiro(int power) {
        double next_term = (-1 + Math.sqrt(1+power))/2;

        return Math.min((int) next_term, 5);
    }
}