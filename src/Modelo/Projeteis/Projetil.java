package Modelo.Projeteis;

import Auxiliar.Consts;
import Auxiliar.TipoProjetil;
import Modelo.Personagem;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Projetil extends Personagem {

    protected double velocidade;
    protected double anguloRad;
    protected TipoProjetil tipo;

    public Projetil(String sNomeImagePNG) {
        super(sNomeImagePNG, 0, 0); // Posição inicial não importa
        this.bMortal = true;
        this.bTransponivel = true; // Projéteis devem ser transponíveis para não bloquearem uns aos outros
        deactivate();
    }

    public void reset(double x, double y, int largura, int altura, double hitboxRaio,
            double velocidadeGrid, double angulo, TipoProjetil tipo) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.hitboxRaio = hitboxRaio;
        this.velocidade = velocidadeGrid;
        this.anguloRad = Math.toRadians(angulo);
        this.tipo = tipo;
        activate();
    }

    @Override
    public void atualizar() {
        if (!isActive())
            return; // Se estiver inativo, não faz nada

        this.x += Math.cos(this.anguloRad) * this.velocidade;
        this.y += Math.sin(this.anguloRad) * this.velocidade;
    }

    // MUDANÇA 2: O método de desenho agora aplica ROTAÇÃO
    @Override
    public void autoDesenho(Graphics g) {
        if (!isActive()) return;
        System.out.println("Desenhando projétil em " + x + ", " + y);

        Graphics2D g2d = (Graphics2D) g;

        // Salva a transformação original (posição, rotação) do mundo
        AffineTransform transformOriginal = g2d.getTransform();

        // Posição central em PIXELS
        int telaX = (int) Math.round(x * Consts.CELL_SIDE);
        int telaY = (int) Math.round(y * Consts.CELL_SIDE);

        // Aplica opacidade (se for do jogador)
        Composite compositeOriginal = g2d.getComposite();
        if (this.tipo == TipoProjetil.JOGADOR) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        // --- Lógica de Rotação ---
        // 1. Move o "ponto de origem" do canvas para o centro do projétil
        g2d.translate(telaX, telaY);

        // 2. Rotaciona o canvas.
        // Adicionamos Math.PI / 2 (90 graus) para "levantar" o sprite que está
        // "deitado"
        g2d.rotate(this.anguloRad);

        // 3. Desenha a imagem no novo ponto de origem (0,0), centralizada
        g2d.drawImage(iImage.getImage(), -this.largura / 2, -this.altura / 2, this.largura, this.altura, null);

        // Restaura a opacidade
        g2d.setComposite(compositeOriginal);

        // Restaura a transformação original do mundo
        g2d.setTransform(transformOriginal);

        // Chama o 'autoDesenho' do pai (Personagem) para desenhar a hitbox de debug
        // A hitbox (círculo) não será rotacionada e ficará na "ponta" (o centro real)
        super.autoDesenho(g);
    }

    // Método para a Fase checar se o projétil saiu da tela
    public boolean estaForaDaTela() {
        if (!isActive()) return false;
        double limiteX = (double) Consts.largura / Consts.CELL_SIDE;
        double limiteY = (double) Consts.altura / Consts.CELL_SIDE;
        return (x < 0 || x > limiteX || y < -1 || y > limiteY);
    }

    public void deactivate() {
        super.deactivate(); // Usa o método da classe pai
    }
    
    public void activate() {
        super.activate(); // Usa o método da classe pai
    }

    public TipoProjetil getTipo() {
        return this.tipo;
    }
}