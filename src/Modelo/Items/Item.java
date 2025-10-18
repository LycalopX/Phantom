package Modelo.Items;

import Auxiliar.Consts;
import Modelo.Personagem;
import Modelo.Hero.Hero;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.image.RasterFormatException;

public class Item extends Personagem {

    private ItemType tipo;
    private transient ImageIcon spriteRecortado;
    private Hero hero;

    // --- FÍSICA DO ITEM ---
    private double velX = 0;
    private double velY = 0;
    private static final double GRAVIDADE = 0.0015; // Pequena força para baixo. Ajuste conforme necessário.
    private static final double MAX_FALL_SPEED = 0.06;
    private static final double VELOCIDADE_ATRACAO = 1;

    private int launchTimer = 0; // Contagem de frames para o lançamento
    private static final double FATOR_DE_ESCALA = 1.8;

    // Duração do lançamento em frames (assumindo 60 FPS, 60 frames = 1 segundo)
    private static final int DURACAO_LANCAMENTO = 60;

    public Item(ItemType tipo, double x, double y, Hero hero) {

        super("items.png",
                x,
                y,
                (int) (tipo.getLargura() * FATOR_DE_ESCALA), // Cálculo da nova largura
                (int) (tipo.getAltura() * FATOR_DE_ESCALA) // Cálculo da nova altura
        );

        this.tipo = tipo;
        this.bTransponivel = true;
        this.bMortal = false;
        this.hero = hero;
        recortarSprite();
    }

    /**
     * Este método é chamado APENAS para os drops do JOGADOR.
     * Ele ativa o timer e dá o impulso inicial.
     */
    public void lancarItem(double anguloEmGraus, double forca) {
        this.launchTimer = DURACAO_LANCAMENTO; // Ativa o modo "lançamento"

        double anguloRad = Math.toRadians(anguloEmGraus);
        this.velX = Math.cos(anguloRad) * forca;
        this.velY = Math.sin(anguloRad) * forca;
    }

    @Override
    public void atualizar() {
        // Se o herói existe e a bomba está ativa, ativa a atração

        if (hero != null && hero.isBombing()) {
            double dx = hero.x - this.x;
            double dy = hero.y - this.y;

            // Normaliza o vetor (transforma em um vetor de comprimento 1)
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            if (magnitude > 0) {
                this.x += (dx / magnitude) * VELOCIDADE_ATRACAO;
                this.y += (dy / magnitude) * VELOCIDADE_ATRACAO;
            }
        }
        // Senão, aplica a física normal de queda
        else {
            if (this.launchTimer > 0) {
                this.x += velX;
                this.y += velY;
                this.launchTimer--;
                if (this.launchTimer <= 0) {
                    this.velX = 0;
                    this.velY = 0;
                }
            } else {
                this.velY += GRAVIDADE;
                if (this.velY > MAX_FALL_SPEED) {
                    this.velY = MAX_FALL_SPEED;
                }
                this.y += velY;
            }
        }
    }

    private void recortarSprite() {
        if (this.iImage != null) {
            BufferedImage sheet = new BufferedImage(iImage.getIconWidth(), iImage.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = sheet.createGraphics();
            iImage.paintIcon(null, g, 0, 0);
            g.dispose();

            try {
                BufferedImage subImagem = sheet.getSubimage(
                        tipo.getSpriteX(), tipo.getSpriteY(), tipo.getLargura(), tipo.getAltura());
                this.spriteRecortado = new ImageIcon(subImagem);
            } catch (RasterFormatException e) {
                System.err.println("ERRO ao recortar sprite para o item: " + tipo.name());
                System.err.println("Verifique as coordenadas em ItemType.java e o tamanho de items.png");
                e.printStackTrace();
                this.spriteRecortado = null;
            }
            // As linhas duplicadas foram removidas daqui.
        }
    }

    @Override
    public void autoDesenho(Graphics g) {
        if (spriteRecortado == null)
            recortarSprite();

        int telaX = (int) Math.round(x * Consts.CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * Consts.CELL_SIDE) - (this.altura / 2);

        if (spriteRecortado != null) {
            g.drawImage(spriteRecortado.getImage(), telaX, telaY, this.largura, this.altura, null);
        }
        super.autoDesenho(g);
    }

    public void deactivate() {
        super.deactivate(); // Usa o método da classe pai
    }
    
    public void activate() {
        super.activate(); // Usa o método da classe pai
    }

    public ItemType getTipo() {
        return this.tipo;
    }
}