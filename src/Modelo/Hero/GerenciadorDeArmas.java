// Em Modelo/GerenciadorDeArmas.java

package Modelo.Hero;

import java.io.Serializable;
import java.util.ArrayList;
import Auxiliar.Consts;
import Auxiliar.TipoProjetil;
import Modelo.Personagem;
import Modelo.Projeteis.Projetil;
import Modelo.Projeteis.ProjetilHoming;

public class GerenciadorDeArmas implements Serializable {

    private final int tempoDeRecarga = 8; // Frames de recarga base
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
    public ArrayList<Personagem> disparar(double x, double y, int power) {
        ArrayList<Personagem> novosProjeteis = new ArrayList<>();

        // 1. Tenta disparar os Mísseis Teleguiados
        int nivelDeMisseis = getNivelDeMisseis(power);

        if (nivelDeMisseis >= 1 && cooldownMisseis <= 0) {
            adicionarMisseisTeleguiados(novosProjeteis, x, y, nivelDeMisseis);
            cooldownMisseis = MISSILE_COOLDOWN_TIME; // Reinicia o cooldown dos mísseis
        }

        // 2. Tenta disparar o Tiro Principal
        if (cooldownTiroPrincipal <= 0) {
            String spriteTiro = "projectiles/hero/projectile1_hero.png";
            double velocidadeProjetilEmGrid = 10.0 / Consts.CELL_SIDE;
            int nivelDeTiroBase = getNivelTiroBase(power);

            double velocidadeFinal = velocidadeProjetilEmGrid * (1 + (nivelDeTiroBase - 1) * 0.2);

            int larguraVisual = 80;
            int alturaVisual = 20;
            int tamanhoHitbox = 8;

            // Tiro Central
            novosProjeteis.add(new Projetil(spriteTiro, x, y, larguraVisual, alturaVisual, tamanhoHitbox,
                    velocidadeFinal, -90, TipoProjetil.JOGADOR));

            // Tiros Laterais (se tiver nível 3)
            if (nivelDeTiroBase >= 3) {
                double offsetX = 0.5; // Distância lateral do centro do herói

                novosProjeteis.add(new Projetil(spriteTiro, x - offsetX, y, larguraVisual, alturaVisual, tamanhoHitbox,
                        velocidadeFinal, -105, TipoProjetil.JOGADOR));
                novosProjeteis.add(new Projetil(spriteTiro, x + offsetX, y, larguraVisual, alturaVisual, tamanhoHitbox,
                        velocidadeFinal, -75, TipoProjetil.JOGADOR));
            }

            // A cadência de tiro aumenta com o poder (cooldown diminui)
            int cooldownFinal = tempoDeRecarga - (int) (Math.min((double) power / 100, 2));
            cooldownTiroPrincipal = Math.max(cooldownFinal, 2); // Garante um cooldown mínimo
        }

        return novosProjeteis;
    }

    private void adicionarMisseisTeleguiados(ArrayList<Personagem> listaDeProjeteis, double x, double y,
            int nivelDeMisseis) {

        double velocidadeBase = 4.0 / Consts.CELL_SIDE;
        double velocidadeFinal = velocidadeBase * (1 + (nivelDeMisseis - 1) * 0.2);
        int tamanhoDoMissil = 20;

        for (int i = 0; i < nivelDeMisseis; i++) {
            double anguloEsquerda = -90 - 45 - (i * 10);
            double anguloDireita = -90 + 45 + (i * 10);
            double offsetX = 0.3 * (i + 1);

            // Míssil da Esquerda
            listaDeProjeteis.add(new ProjetilHoming("projectiles/hero/projectile2_hero.png", x - offsetX, y,
                    tamanhoDoMissil, tamanhoDoMissil, 16, velocidadeFinal, anguloEsquerda, TipoProjetil.JOGADOR));
            // Míssil da Direita
            listaDeProjeteis.add(new ProjetilHoming("projectiles/hero/projectile2_hero.png", x + offsetX, y,
                    tamanhoDoMissil, tamanhoDoMissil, 16, velocidadeFinal, anguloDireita, TipoProjetil.JOGADOR));
        }
    }

    // MÉTODOS DE UTILIDADE (privados e estáticos, pois não dependem do estado do
    // objeto)
    public int getNivelDeMisseis(int power) {
        return Math.min(power / Consts.REQ_MISSIL_POWER, 4);
    }
    
    public int getNivelTiroBase(int power) {
        return Math.min(power / Consts.REQ_TIROS_POWER, 3);
    }
}