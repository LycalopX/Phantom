package Auxiliar.Personagem;

import java.io.Serializable;

import Auxiliar.ConfigMapa;

public class Posicao implements Serializable {
    private int linha;
    private int coluna;
    
    private int linhaAnterior;
    private int colunaAnterior;

    public Posicao(int linha, int coluna) {
        this.setPosicao(linha, coluna);
    }

    public boolean setPosicao(int linha, int coluna) {
        if (linha < 0 || linha >= ConfigMapa.MUNDO_ALTURA)
            return false;
        linhaAnterior = this.linha;
        this.linha = linha;

        if (coluna < 0 || coluna >= ConfigMapa.MUNDO_LARGURA)
            return false;
        colunaAnterior = this.coluna;
        this.coluna = coluna;

        return true;
    }
    /**
     * Cria e retorna uma nova instância de Posição com os mesmos
     * valores de linha e coluna.
     * @return Uma cópia do objeto Posição.
     */
    public Posicao copia() {
        return new Posicao(this.linha, this.coluna);
    }

    public int getLinha() {
        return linha;
    }

    public boolean volta() {
        return this.setPosicao(linhaAnterior, colunaAnterior);
    }

    public int getColuna() {
        return coluna;
    }

    public boolean igual(Posicao posicao) {
        return (linha == posicao.getLinha() && coluna == posicao.getColuna());
    }

    public boolean copia(Posicao posicao) {
        return this.setPosicao(posicao.getLinha(), posicao.getColuna());
    }

    public boolean moveUp() {
        return this.setPosicao(this.getLinha() - 1, this.getColuna());
    }

    public boolean moveDown() {
        return this.setPosicao(this.getLinha() + 1, this.getColuna());
    }

    public boolean moveRight() {
        return this.setPosicao(this.getLinha(), this.getColuna() + 1);
    }

    public boolean moveLeft() {
        return this.setPosicao(this.getLinha(), this.getColuna() - 1);
    }
}
