package Modelo;

import Controler.ControleDeJogo;
import Auxiliar.Consts;
import Auxiliar.DebugManager;
import Auxiliar.TipoProjetil;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import Auxiliar.Projetil;
import java.awt.Color;

public class Hero extends Personagem {

    // Para não acumular as animações em Hero.java
    private GerenciadorDeAnimacao animador;

    private double VELOCIDADE_HERO = 12.0;
    public double FPS = 60;

    private HeroState estado = HeroState.IDLE;
    private int cooldownTiro = 0;
    private final int tempoDeRecarga = 5;

    public double grabHitboxRaio;
    private int bombas = 3;
    private int invencibilidadeTimer = 0;
    private final int duracaoInvencibilidade = 1200;

    public Hero(String sNomeImagePNG, double x, double y) {
        super(sNomeImagePNG,
                x,
                y,
                Consts.CELL_SIDE * 2, // larguraVisual
                Consts.CELL_SIDE * 2, // alturaVisual
                (Consts.HITBOX_RAIO / Consts.CELL_SIDE) // raioEmGrid
        );

        this.grabHitboxRaio = (double) (Consts.CELL_SIDE) / Consts.CELL_SIDE; // Raio de 32px em grid units.

        this.animador = new GerenciadorDeAnimacao(this.largura, this.altura);
    }

    // --- Lógica Principal ---

    public HeroUpdateResult atualizar(Set<Integer> teclasPressionadas, ControleDeJogo cj,
            ArrayList<Personagem> personagens) {
        // Cria o objeto de resultado que será retornado
        ArrayList<Projetil> novosProjeteis = new ArrayList<>();
        boolean bombaFoiUsada = false;

        // Decrementa o timer de invencibilidade
        if (invencibilidadeTimer > 0) {
            invencibilidadeTimer--;
        }

        // --- LÓGICA DE MOVIMENTO (CALCULADA APENAS UMA VEZ) ---
        double delta = VELOCIDADE_HERO / FPS;
        double dx = 0, dy = 0;

        // Movimentação
        if (teclasPressionadas.contains(KeyEvent.VK_W))
            dy -= delta;
        if (teclasPressionadas.contains(KeyEvent.VK_S))
            dy += delta;
        if (teclasPressionadas.contains(KeyEvent.VK_A))
            dx -= delta;
        if (teclasPressionadas.contains(KeyEvent.VK_D))
            dx += delta;

        // Lógica de bomba
        if (teclasPressionadas.contains(KeyEvent.VK_L) && bombas > 0 && invencibilidadeTimer == 0) {
            bombaFoiUsada = true;
            bombas--;
            invencibilidadeTimer = duracaoInvencibilidade;
        }
        if (invencibilidadeTimer % 30 == 0 && invencibilidadeTimer > 0) {
            bombaFoiUsada = true; // Garantir que a bomba não seja usada novamente
        }

        // Lógica de Animação
        boolean isMovingLeft = teclasPressionadas.contains(KeyEvent.VK_A);
        boolean isMovingRight = teclasPressionadas.contains(KeyEvent.VK_D);

        switch (estado) {
            case IDLE:
                if (isMovingLeft)
                    estado = HeroState.STRAFING_LEFT;
                else if (isMovingRight)
                    estado = HeroState.STRAFING_RIGHT;
                break;
            case STRAFING_LEFT:
                if (!isMovingLeft) {
                    estado = HeroState.DE_STRAFING_LEFT;
                    animador.iniciarDeStrafing();
                } else if (isMovingRight) {
                    estado = HeroState.STRAFING_RIGHT;
                }
                break;
            case STRAFING_RIGHT:
                if (!isMovingRight) {
                    estado = HeroState.DE_STRAFING_RIGHT;
                    animador.iniciarDeStrafing();
                } else if (isMovingLeft) {
                    estado = HeroState.STRAFING_LEFT;
                }
                break;
            case DE_STRAFING_LEFT:
                if (isMovingRight)
                    estado = HeroState.STRAFING_RIGHT;
                else if (isMovingLeft)
                    estado = HeroState.STRAFING_LEFT;
                break;
            case DE_STRAFING_RIGHT:
                if (isMovingLeft)
                    estado = HeroState.STRAFING_LEFT;
                else if (isMovingRight)
                    estado = HeroState.STRAFING_RIGHT;
                break;
        }

        // Atualiza o animador e verifica se a animação de retorno terminou
        boolean animacaoDeRetornoTerminou = animador.atualizar(estado);

        if (animacaoDeRetornoTerminou) {
            estado = HeroState.IDLE; // Volta ao estado parado
        }

        // --- APLICAÇÃO DO MOVIMENTO E LÓGICA DO JOGO ---
        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        double proximoX = this.x + dx;
        double proximoY = this.y + dy;

        double limiteEsquerda = this.hitboxRaio;
        double limiteDireita = ((double) Consts.largura / Consts.CELL_SIDE) - this.hitboxRaio;
        double limiteTopo = this.hitboxRaio;
        double limiteBaixo = ((double) Consts.altura / Consts.CELL_SIDE) - this.hitboxRaio;

        if (proximoX < limiteEsquerda)
            proximoX = limiteEsquerda;
        if (proximoX > limiteDireita)
            proximoX = limiteDireita;
        if (proximoY < limiteTopo)
            proximoY = limiteTopo;
        if (proximoY > limiteBaixo)
            proximoY = limiteBaixo;

        // Tenta mover nos eixos separados
        if (cj.ehPosicaoValida(personagens, this, proximoX, this.y)) {
            this.x = proximoX;
        }
        if (cj.ehPosicaoValida(personagens, this, this.x, proximoY)) {
            this.y = proximoY;
        }
        // Lógica de tiro
        if (cooldownTiro > 0) {
            cooldownTiro--;
        }
        if (teclasPressionadas.contains(KeyEvent.VK_K) && cooldownTiro <= 0) {
            novosProjeteis.add(new Projetil((int) (this.x * Consts.CELL_SIDE), (int) (this.y * Consts.CELL_SIDE), -90,
                    20, 8, Color.CYAN, TipoProjetil.JOGADOR));
            cooldownTiro = tempoDeRecarga;
        }

        return new HeroUpdateResult(novosProjeteis, bombaFoiUsada);
    }

    @Override
    public void autoDesenho(Graphics g) {
        int telaX = (int) Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transformOriginal = g2d.getTransform();

        ImageIcon imagemParaDesenhar = animador.getImagemAtual(estado);

        if (imagemParaDesenhar != null) {
            // A lógica de espelhamento depende do estado
            if (estado == HeroState.STRAFING_RIGHT || estado == HeroState.DE_STRAFING_RIGHT) {
                g2d.translate(telaX + largura, telaY);
                g2d.scale(-1, 1);
                g2d.drawImage(imagemParaDesenhar.getImage(), 0, 0, largura, altura, null);
            } else {
                g2d.drawImage(imagemParaDesenhar.getImage(), telaX, telaY, largura, altura, null);
            }
        }

        g2d.setTransform(transformOriginal);

        if (DebugManager.isActive()) {
            // --- PASSO 4: DESENHAR AS HITBOXES POR CIMA (PARA DEPURAÇÃO) ---
            int centroX = (int) (this.x * Consts.CELL_SIDE);
            int centroY = (int) (this.y * Consts.CELL_SIDE);

            // Desenha a hitbox de DANO (pequena, vermelha)
            g2d.setColor(Color.RED);
            int danoRaioPixels = (int) (this.hitboxRaio * Consts.CELL_SIDE);
            g2d.fillOval(centroX - danoRaioPixels, centroY - danoRaioPixels, danoRaioPixels * 2, danoRaioPixels * 2);

            // Desenha a hitbox de COLETA (grande, azul)
            g2d.setColor(new Color(22, 100, 7, 100)); // Azul com transparência
            int coletaRaioPixels = (int) (this.grabHitboxRaio * Consts.CELL_SIDE);
            g2d.drawOval(centroX - coletaRaioPixels, centroY - coletaRaioPixels, coletaRaioPixels * 2,
                    coletaRaioPixels * 2);
        }

    }

    public int getBombas() {
        return this.bombas;
    }

    public boolean isInvencivel() {
        return this.invencibilidadeTimer > 0;
    }

    // Classe interna do resultado da atualização
    public static class HeroUpdateResult {
        public final ArrayList<Projetil> novosProjeteis;
        public final boolean usouBomba;

        public HeroUpdateResult(ArrayList<Projetil> projeteis, boolean bomba) {
            this.novosProjeteis = projeteis;
            this.usouBomba = bomba;
        }
    }
}