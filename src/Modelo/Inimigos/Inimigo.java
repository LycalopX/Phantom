package Modelo.Inimigos;

import java.awt.Graphics;
import Auxiliar.LootTable;
import Modelo.Personagem;
import Modelo.Fases.Fase;
import static Auxiliar.ConfigMapa.*;

/**
 * @brief Classe abstrata base para todos os inimigos do jogo.
 */
public class Inimigo extends Personagem {

    private static final long serialVersionUID = 1L;

    public LootTable lootTable;
    public double vida;
    protected transient Fase faseReferencia;
    protected double initialX;

    /**
     * @brief Construtor automático que calcula o tamanho do inimigo com base na imagem.
     */
    public Inimigo(String sNomeImagePNG, double x, double y, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y);
        this.bMortal = true;
        this.lootTable = lootTable;
        this.vida = vida;
        this.initialX = x;
    }

    /**
     * @brief Construtor manual que permite definir um tamanho específico para o inimigo.
     */
    public Inimigo(String sNomeImagePNG, double x, double y, int tamanho, LootTable lootTable, double vida) {
        super(sNomeImagePNG, x, y, tamanho, tamanho);
        this.bMortal = true;
        this.lootTable = lootTable;
        this.vida = vida;
        this.initialX = x;
    }

    /**
     * @brief Atualiza a lógica de movimento padrão do inimigo.
     */
    @Override
    public void atualizar() {
        this.y += 0.02;
    }
    
    /**
     * @brief Inicializa a referência da fase para o inimigo, usado após a desserialização.
     */
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    public void setInitialX(double initialX) {
        this.initialX = initialX;
    }

    /**
     * @brief Desenha o sprite do inimigo na tela.
     */
    @Override
    public void autoDesenho(Graphics g) {
        int telaX = (int) Math.round(x * CELL_SIDE) - (this.largura / 2);
        int telaY = (int) Math.round(y * CELL_SIDE) - (this.altura / 2);
        g.drawImage(iImage.getImage(), telaX, telaY, largura, altura, null);
        super.autoDesenho(g);
    }

    /**
     * @brief Retorna a tabela de loot do inimigo.
     */
    public LootTable getLootTable() {
        return this.lootTable;
    }

    /**
     * @brief Retorna a vida atual do inimigo.
     */
    public double getVida() {
        return this.vida;
    }

    /**
     * @brief Aplica dano ao inimigo e o desativa se a vida chegar a zero.
     */
    public void takeDamage(double damage) {
        this.vida -= damage;
        Auxiliar.SoundManager.getInstance().playSfx("se_damage01", 0.5f);
        if (this.vida <= 0) {
            this.vida = 0;
            deactivate();
            Auxiliar.SoundManager.getInstance().playSfx("se_enep00", 0.5f);
        }
    }
}