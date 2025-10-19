// Em Controler/ControladorDoHeroi.java

package Controler;

import java.util.Set;
import java.awt.event.KeyEvent;

import Auxiliar.Consts;
import Modelo.Fases.Fase;
import Modelo.Projeteis.BombaProjetil;
import Modelo.Hero.GerenciadorDeArmas;
import Modelo.Hero.Hero;

public class ControladorDoHeroi {

    private Engine engine;

    public ControladorDoHeroi(Engine engine) {
        this.engine = engine;
    }

    /**
     * Interpreta o input do teclado e comanda o objeto Hero.
     * 
     * @param teclas O conjunto de teclas pressionadas.
     * @param heroi  O objeto Hero a ser controlado.
     * @param fase   A fase atual do jogo.
     * @param cj     O objeto de controle de jogo para validações.
     */
    public void processarInput(Set<Integer> teclas, Hero heroi, Fase fase, ControleDeJogo cj) {
        // --- LÓGICA DE MOVIMENTO ---
        double delta = Consts.HERO_VELOCITY / 60.0;
        double dx = 0, dy = 0;

        Engine.GameState estadoAtual = this.engine.getEstadoAtual();

        // Lógica do Modo Foco (Shift)
        boolean isFocoAtivo = teclas.contains(KeyEvent.VK_SHIFT);
        double velocidadeAtual = isFocoAtivo ? delta / 2.0 : delta;

        if (teclas.contains(Consts.KEY_UP))
            dy -= velocidadeAtual;
        if (teclas.contains(Consts.KEY_DOWN))
            dy += velocidadeAtual;
        if (teclas.contains(Consts.KEY_LEFT))
            dx -= velocidadeAtual;
        if (teclas.contains(Consts.KEY_RIGHT))
            dx += velocidadeAtual;

        // Normaliza o movimento diagonal para não ser mais rápido
        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        double proximoX = heroi.x + dx;
        double proximoY = heroi.y + dy;

        // Validação de Limites da Tela
        double limiteEsquerda = heroi.hitboxRaio;
        double limiteDireita = ((double) Consts.largura / Consts.CELL_SIDE) - heroi.hitboxRaio;
        double limiteTopo = heroi.hitboxRaio;
        double limiteBaixo = ((double) Consts.altura / Consts.CELL_SIDE) - heroi.hitboxRaio;

        if (proximoX < limiteEsquerda)
            proximoX = limiteEsquerda;
        if (proximoX > limiteDireita)
            proximoX = limiteDireita;
        if (proximoY < limiteTopo)
            proximoY = limiteTopo;
        if (proximoY > limiteBaixo)
            proximoY = limiteBaixo;

        // Validação de Colisão com Personagens
        double xFinal = heroi.x, yFinal = heroi.y;
        if (cj.ehPosicaoValida(fase.getPersonagens(), heroi, proximoX, heroi.y)) {
            xFinal = proximoX;
        }
        if (cj.ehPosicaoValida(fase.getPersonagens(), heroi, heroi.x, proximoY)) {
            yFinal = proximoY;
        }
        heroi.mover(xFinal, yFinal);

        // --- LÓGICA DE ANIMAÇÃO ---
        boolean isMovingLeft = teclas.contains(Consts.KEY_LEFT);
        boolean isMovingRight = teclas.contains(Consts.KEY_RIGHT);
        heroi.atualizarAnimacao(isMovingLeft, isMovingRight);

        // --- LÓGICA DE TIRO ---
        if (teclas.contains(Consts.KEY_SHOOT)) {

            GerenciadorDeArmas armas = heroi.getSistemaDeArmas();
            armas.disparar(heroi.x, heroi.y, heroi.getPower(), fase);
        }

        // --- LÓGICA DE BOMBA ---
        if (teclas.contains(Consts.KEY_BOMB) && heroi.getBombas() > 0 && !heroi.isInvencivel()) {

            if (estadoAtual == Engine.GameState.JOGANDO || estadoAtual == Engine.GameState.DEATHBOMB_WINDOW) {

                BombaProjetil bomba = heroi.usarBomba(fase);
                if (bomba != null) {
                    fase.adicionarPersonagem(bomba);
                    Auxiliar.SoundManager.getInstance().playSfx("se_gun00");
                }
            }
        }
    }

}