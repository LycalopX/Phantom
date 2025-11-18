package Controler;

import Auxiliar.ConfigMapa;
import Auxiliar.ConfigTeclado;
import Modelo.Fases.Fase;
import Modelo.Hero.GerenciadorDeArmasHeroi;
import Modelo.Hero.Hero;
import Modelo.Projeteis.BombaProjetil;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.List;
import Modelo.Inimigos.Inimigo;

/**
 * @brief Gerencia e traduz os inputs do teclado em ações para o Herói.
 * 
 *        Esta classe é responsável por capturar as teclas pressionadas e
 *        aplicar as
 *        lógicas de movimento, foco, disparo, uso de bombas e cheats
 *        relacionados
 *        ao personagem do jogador.
 */
public class ControladorDoHeroi {

    private Engine engine;
    private boolean f2Pressionado = false;

    /**
     * @brief Construtor que associa o controlador à engine principal.
     * @param engine A instância da engine do jogo.
     */
    public ControladorDoHeroi(Engine engine) {
        this.engine = engine;
    }

    /**
     * @brief Processa os inputs do teclado para controlar o herói.
     * 
     *        Este método é chamado a cada frame para atualizar o estado do herói
     *        com
     *        base nas teclas pressionadas, gerenciando movimento, animações,
     *        disparos
     *        e o uso de bombas.
     * 
     * @param teclas O conjunto de teclas atualmente pressionadas.
     * @param heroi  O objeto do herói a ser controlado.
     * @param fase   A fase atual, para interações de disparo.
     * @param cj     O controlador de jogo, para validação de posição.
     */
    public void processarInput(Set<Integer> teclas, Hero heroi, Fase fase, ControleDeJogo cj) {
        double delta = Hero.HERO_VELOCITY / 60.0;
        double dx = 0, dy = 0;

        Engine.GameState estadoAtual = this.engine.getEstadoAtual();

        // Verifica se o modo de foco (movimento lento) está ativo
        boolean isFocoAtivo = teclas.contains(KeyEvent.VK_SHIFT);
        double velocidadeAtual = isFocoAtivo ? delta / 2.0 : delta;
        heroi.setFocoAtivo(isFocoAtivo);

        // Implementação do cheat para pular de fase (Shift + F2)
        // A trava 'f2Pressionado' evita que o cheat seja acionado múltiplas vezes
        // se a tecla for mantida pressionada.
        if (isFocoAtivo && teclas.contains(KeyEvent.VK_F2)) {
            if (!f2Pressionado) {
                engine.carregarProximaFase();
                f2Pressionado = true;
            }
        } else {
            f2Pressionado = false;
        }

        // Calcula o vetor de movimento com base nas teclas pressionadas.
        if (teclas.contains(ConfigTeclado.KEY_UP) || teclas.contains(ConfigTeclado.ARROW_UP))
            dy -= velocidadeAtual;
        if (teclas.contains(ConfigTeclado.KEY_DOWN) || teclas.contains(ConfigTeclado.ARROW_DOWN))
            dy += velocidadeAtual;
        if (teclas.contains(ConfigTeclado.KEY_LEFT) || teclas.contains(ConfigTeclado.ARROW_LEFT))
            dx -= velocidadeAtual;
        if (teclas.contains(ConfigTeclado.KEY_RIGHT) || teclas.contains(ConfigTeclado.ARROW_RIGHT))
            dx += velocidadeAtual;

        // Normaliza o vetor de movimento para evitar que o movimento diagonal
        // seja mais rápido que o movimento nos eixos.
        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        double proximoX = heroi.getX() + dx;
        double proximoY = heroi.getY() + dy;

        // Impede que o herói saia dos limites da tela de jogo.
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

        // Valida a posição final do herói, verificando colisões com inimigos
        // para evitar que o herói se sobreponha a eles.
        double xFinal = heroi.getX(), yFinal = heroi.getY();
        if (cj.ehPosicaoValida((List<Inimigo>) fase.getInimigos(), heroi, proximoX, heroi.getY())) {
            xFinal = proximoX;
        }
        if (cj.ehPosicaoValida((List<Inimigo>) fase.getInimigos(), heroi, heroi.getX(), proximoY)) {
            yFinal = proximoY;
        }
        heroi.mover(xFinal, yFinal);

        // Atualiza a animação de strafing do herói com base na direção do movimento.
        boolean isMovingLeft = teclas.contains(ConfigTeclado.KEY_LEFT);
        boolean isMovingRight = teclas.contains(ConfigTeclado.KEY_RIGHT);
        heroi.atualizarAnimacao(isMovingLeft, isMovingRight);

        // Aciona o sistema de armas do herói se a tecla de tiro estiver pressionada.
        if (teclas.contains(ConfigTeclado.KEY_SHOOT) || teclas.contains(ConfigTeclado.KEY_SHOOT2)) {
            GerenciadorDeArmasHeroi armas = heroi.getSistemaDeArmas();
            armas.disparar(heroi.getX(), heroi.getY(), heroi.getPower(), fase);
        }

        // Aciona a bomba se a tecla correspondente for pressionada, o herói tiver
        // bombas disponíveis e não estiver invencível. A bomba pode ser usada
        // durante o jogo normal ou na janela de 'deathbomb'.
        if ((teclas.contains(ConfigTeclado.KEY_BOMB) || teclas.contains(ConfigTeclado.KEY_BOMB2))
                && heroi.getBombas() > 0 && !heroi.isInvencivel()) {
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