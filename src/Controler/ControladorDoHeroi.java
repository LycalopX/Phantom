package Controler;

import Auxiliar.ConfigMapa;
import Auxiliar.ConfigTeclado;
import Modelo.Fases.Fase;
import Modelo.Hero.GerenciadorDeArmasHeroi;
import Modelo.Hero.Hero;
import Modelo.Projeteis.BombaProjetil;
import java.awt.event.KeyEvent;
import java.util.Set;

/**
 * @brief Classe responsável por traduzir os inputs do teclado em ações para o
 *        Herói.
 */
public class ControladorDoHeroi {

    private Engine engine;
    private boolean f2Pressionado = false; // Trava para o cheat de pular de fase

    /**
     * @brief Construtor do controlador do herói.
     * @param engine A instância da engine principal do jogo.
     */
    public ControladorDoHeroi(Engine engine) {
        this.engine = engine;
    }

    /**
     * @brief Processa o input do teclado para controlar o movimento, animação, tiro
     *        e bombas do herói.
     * @param teclas O conjunto de teclas atualmente pressionadas.
     * @param heroi  O objeto do herói a ser controlado.
     * @param fase   A fase atual do jogo.
     * @param cj     O controlador de jogo para validações de posição.
     */
    public void processarInput(Set<Integer> teclas, Hero heroi, Fase fase, ControleDeJogo cj) {
        double delta = Hero.HERO_VELOCITY / 60.0;
        double dx = 0, dy = 0;

        Engine.GameState estadoAtual = this.engine.getEstadoAtual();

        boolean isFocoAtivo = teclas.contains(KeyEvent.VK_SHIFT);
        double velocidadeAtual = isFocoAtivo ? delta / 2.0 : delta;
        heroi.setFocoAtivo(isFocoAtivo);

        // Cheat para pular de fase (Shift + F2)
        if (isFocoAtivo && teclas.contains(KeyEvent.VK_F2)) {
            if (!f2Pressionado) {
                engine.carregarProximaFase();
                f2Pressionado = true; // Ativa a trava para evitar pulos múltiplos
            }
        } else {
            f2Pressionado = false; // Reseta a trava quando a tecla F2 é solta
        }

        if (teclas.contains(ConfigTeclado.KEY_UP) || teclas.contains(ConfigTeclado.ARROW_UP))
            dy -= velocidadeAtual;
        if (teclas.contains(ConfigTeclado.KEY_DOWN) || teclas.contains(ConfigTeclado.ARROW_DOWN))
            dy += velocidadeAtual;
        if (teclas.contains(ConfigTeclado.KEY_LEFT) || teclas.contains(ConfigTeclado.ARROW_LEFT))
            dx -= velocidadeAtual;
        if (teclas.contains(ConfigTeclado.KEY_RIGHT) || teclas.contains(ConfigTeclado.ARROW_RIGHT))
            dx += velocidadeAtual;

        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        double proximoX = heroi.getX() + dx;
        double proximoY = heroi.getY() + dy;

        double limiteEsquerda = heroi.getHitboxRaio();
        double limiteDireita = ((double) ConfigMapa.LARGURA_TELA / ConfigMapa.CELL_SIDE) - heroi.getHitboxRaio();
        double limiteTopo = heroi.getHitboxRaio();
        double limiteBaixo = ((double) ConfigMapa.ALTURA_TELA / ConfigMapa.CELL_SIDE) - heroi.getHitboxRaio();

        if (proximoX < limiteEsquerda)
            proximoX = limiteEsquerda;
        if (proximoX > limiteDireita)
            proximoX = limiteDireita;
        if (proximoY < limiteTopo)
            proximoY = limiteTopo;
        if (proximoY > limiteBaixo)
            proximoY = limiteBaixo;

        double xFinal = heroi.getX(), yFinal = heroi.getY();
        if (cj.ehPosicaoValida(fase.getPersonagens(), heroi, proximoX, heroi.getY())) {
            xFinal = proximoX;
        }
        if (cj.ehPosicaoValida(fase.getPersonagens(), heroi, heroi.getX(), proximoY)) {
            yFinal = proximoY;
        }
        heroi.mover(xFinal, yFinal);

        boolean isMovingLeft = teclas.contains(ConfigTeclado.KEY_LEFT);
        boolean isMovingRight = teclas.contains(ConfigTeclado.KEY_RIGHT);
        heroi.atualizarAnimacao(isMovingLeft, isMovingRight);

        if (teclas.contains(ConfigTeclado.KEY_SHOOT) || teclas.contains(ConfigTeclado.KEY_SHOOT2)) {
            GerenciadorDeArmasHeroi armas = heroi.getSistemaDeArmas();
            armas.disparar(heroi.getX(), heroi.getY(), heroi.getPower(), fase);
        }

        if ((teclas.contains(ConfigTeclado.KEY_BOMB) || teclas.contains(ConfigTeclado.KEY_BOMB2)) && heroi.getBombas() > 0 && !heroi.isInvencivel()) {
            if (estadoAtual == Engine.GameState.JOGANDO || estadoAtual == Engine.GameState.DEATHBOMB_WINDOW) {
                BombaProjetil bomba = heroi.usarBomba(fase);
                if (bomba != null) {
                    fase.adicionarPersonagem(bomba);
                    Auxiliar.SoundManager.getInstance().playSfx("se_gun00", 2);
                }
            }
        }
    }
}