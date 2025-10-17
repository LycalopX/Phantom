// Em Controler/ControladorDoHeroi.java

package Controler;

import java.util.Set;
import java.util.ArrayList;

import Auxiliar.Consts;
import Modelo.Personagem;
import Modelo.Fases.Fase;
import Modelo.Hero.Hero;

public class ControladorDoHeroi {

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

        if (teclas.contains(Consts.KEY_UP))
            dy -= delta; // Usando KeyEvent diretamente para clareza
        if (teclas.contains(Consts.KEY_DOWN))
            dy += delta;
        if (teclas.contains(Consts.KEY_LEFT))
            dx -= delta;
        if (teclas.contains(Consts.KEY_RIGHT))
            dx += delta;

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
        // Movimento separado nos eixos para "deslizar" nas paredes
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
        heroi.atualizar(isMovingLeft, isMovingRight); // Passa o estado do movimento para o herói atualizar sua animação

        // --- LÓGICA DE AÇÕES ---
        if (teclas.contains(Consts.KEY_SHOOT)) {
            ArrayList<Personagem> novosProjeteis = heroi.atirar();
            for (Personagem p : novosProjeteis) {
                fase.adicionarPersonagem(p);
            }
        }

        if (teclas.contains(Consts.KEY_BOMB) && heroi.getBombas() > 0 && !heroi.isInvencivel()) {
            // O herói agora apenas "ativa" seu próprio estado de bomba.
            heroi.usarBomba();
        }
    }
}