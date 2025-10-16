package Modelo;

import Controler.ControleDeJogo;
import Auxiliar.Consts;
import Auxiliar.DebugManager;
import Auxiliar.TipoProjetil;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Color;

public class Hero extends Personagem {
    private int HP = 3;
    private int bombas = 3;
    private int invencibilidadeTimer = 0; // Frames restantes de invencibilidade
    private int power = 0;
    private int score = 0;

    // Caso esteja respawnando ou morto
    private boolean isActive = true;

    // Para não acumular as animações em Hero
    private transient GerenciadorDeAnimacao animador;

    private double VELOCIDADE_HERO = 14.0;
    public double FPS = 60;

    private HeroState estado = HeroState.IDLE;
    private int cooldownTiro = 0;
    private final int tempoDeRecarga = 5;

    public double grabHitboxRaio;
    // Invincibilidade da bomba
    private final int duracaoInvencibilidadeBomba = 600;

    // Invencibilidade ao levar dano
    private final int duracaoInvencibilidadeRespawn = 180;

    public Hero(String sNomeImagePNG, double x, double y) {
        // Chama o construtor principal de Personagem
        super(sNomeImagePNG,
                x,
                y,
                (int) (Consts.CELL_SIDE * 0.64 * Consts.BODY_PROPORTION), // larguraVisual (com sua proporção)
                (int) (Consts.CELL_SIDE * Consts.BODY_PROPORTION), // alturaVisual
                (Consts.HITBOX_RAIO / Consts.CELL_SIDE) // hitboxRaio em Grid
        );

        // Define a grabHitbox (dividida por 2, como você fez)
        this.grabHitboxRaio = ((double) (Consts.CELL_SIDE) / Consts.CELL_SIDE) / 2.0;

        this.animador = new GerenciadorDeAnimacao(this.largura, this.altura);
    }

    // --- Lógica Principal ---

    public HeroUpdateResult atualizar(Set<Integer> teclasPressionadas, ControleDeJogo cj,
            ArrayList<Personagem> personagens) {
        // Cria o objeto de resultado que será retornado
        ArrayList<Personagem> novosProjeteis = new ArrayList<>();
        int inimigosMorremTimer = 0;

        // Decrementa o timer de invencibilidade
        if (invencibilidadeTimer > 0) {
            invencibilidadeTimer--;
        }
        if (inimigosMorremTimer > 0) {
            inimigosMorremTimer--;
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
            bombas--;
            invencibilidadeTimer = duracaoInvencibilidadeBomba;
            inimigosMorremTimer = duracaoInvencibilidadeBomba; // Temporizador para fazer inimigos morrerem por 30
                                                               // frames
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
        if (cooldownTiro > 0)
            cooldownTiro--;

        if (teclasPressionadas.contains(KeyEvent.VK_K) && cooldownTiro <= 0) {
            String spriteTiro = "projectiles/hero/projectile1_hero.png";
            double velocidadeProjetilEmGrid = 20.0 / Consts.CELL_SIDE;

            // Tamanhos visuais do cometa (em pixels)
            int larguraVisual = 80; // O lado "longo" do sprite do cometa
            int alturaVisual = 20; // O lado "curto" do sprite do cometa

            int tamanhoHitbox = 8;

            novosProjeteis.add(new Projetil(
                    spriteTiro, this.x, this.y,
                    larguraVisual, alturaVisual, tamanhoHitbox,
                    velocidadeProjetilEmGrid, -90,
                    TipoProjetil.JOGADOR));

            cooldownTiro = tempoDeRecarga;
        }

        // Lógica do tiro teleguiado (desativada por enquanto)
        if (teclasPressionadas.contains(KeyEvent.VK_J) && cooldownTiro <= 0) {
            double velocidadeProjetilEmGrid = 15.0 / Consts.CELL_SIDE;
            /*
             * novosProjeteis.add(new ProjetilHoming(
             * "projectiles/hero/projectile2_hero.png.png", // Imagem do tiro roxo
             * this.x, this.y,
             * 16,
             * velocidadeProjetilEmGrid,
             * -90,
             * TipoProjetil.JOGADOR
             * ));
             * cooldownTiro = tempoDeRecarga;
             */
        }

        return new HeroUpdateResult(novosProjeteis, inimigosMorremTimer);
    }

    @Override
    public void autoDesenho(Graphics g) {

        if (!this.isActive) {
            return; // Não desenha se não estiver ativo
        }

        // Lógica de animação e desenho do herói
        int telaX = (int) Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform transformOriginal = g2d.getTransform();
        ImageIcon imagemParaDesenhar = animador.getImagemAtual(estado);

        if (imagemParaDesenhar != null) {
            if (estado == HeroState.STRAFING_RIGHT || estado == HeroState.DE_STRAFING_RIGHT) {
                g2d.translate(telaX + largura, telaY);
                g2d.scale(-1, 1);
                g2d.drawImage(imagemParaDesenhar.getImage(), 0, 0, largura, altura, null);
            } else {
                g2d.drawImage(imagemParaDesenhar.getImage(), telaX, telaY, largura, altura, null);
            }
        }
        g2d.setTransform(transformOriginal);

        // Chama o autoDesenho da classe pai para desenhar o DEBUG
        super.autoDesenho(g);

        // Desenha a hitbox de COLETA (verde) se o Debug estiver ativo
        if (DebugManager.isActive()) {
            g2d.setColor(new Color(22, 100, 7, 100)); // Verde transparente
            int centroX = (int) (this.x * Consts.CELL_SIDE);
            int centroY = (int) (this.y * Consts.CELL_SIDE);
            int coletaRaioPixels = (int) (this.grabHitboxRaio * Consts.CELL_SIDE);
            g2d.drawOval(centroX - coletaRaioPixels, centroY - coletaRaioPixels, coletaRaioPixels * 2,
                    coletaRaioPixels * 2);
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Getter para o ControleDeJogo saber se ignora o herói
    public boolean isActive() {
        return this.isActive;
    }

    public void respawn() {
        this.x = Consts.respawnX;
        this.y = Consts.respawnY;
        this.bombas = 3;
        this.power = 0;
        this.invencibilidadeTimer = duracaoInvencibilidadeRespawn;
        this.estado = HeroState.IDLE;
        this.isActive = true;
    }

    public int getBombas() {
        return this.bombas;
    }

    public int getHP() {
        return this.HP;
    }

    public int getPower() {
        return this.power;
    }

    public int getScore() {
        return this.score;
    }

    public boolean isInvencivel() {
        return this.invencibilidadeTimer > 0;
    }

    public void takeDamage() {
        if (invencibilidadeTimer == 0) {
            HP--;
            invencibilidadeTimer = duracaoInvencibilidadeRespawn;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Recria o animador após o carregamento
        this.animador = new GerenciadorDeAnimacao(this.largura, this.altura);
    }

    // Classe interna do resultado da atualização
    public static class HeroUpdateResult {
        public final ArrayList<Personagem> novosProjeteis;
        public final int inimigosMorremTimer;

        public HeroUpdateResult(ArrayList<Personagem> projeteis, int inimigosMorremTimer) {
            this.novosProjeteis = projeteis;
            this.inimigosMorremTimer = inimigosMorremTimer;
        }
    }

    public void addPower(int quantidade) {
        this.power += quantidade;
    }

    public void addBomb(int quantidade) {
        this.bombas += quantidade;
    }

    public void addHP(int quantidade) {
        this.HP += quantidade;
    }

    public void addScore(int quantidade) {
        this.score += quantidade;
    }

}