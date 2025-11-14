package Controler;

import Modelo.Fases.Fase;
import Modelo.Fases.ScriptDeFase;
import Modelo.Fases.ScriptFase1;
import Modelo.Fases.ScriptFase2;
import Modelo.Fases.ScriptFase3;
import Modelo.Fases.ScriptFase4;
import Modelo.Fases.ScriptFase5;

/**
 * @brief Gerencia a progressão das fases do jogo.
 */
public class GerenciadorDeFases {
    private int nivelAtual = 1;
    private final int TOTAL_DE_FASES = 5;

    /**
     * @brief Carrega e retorna a fase correspondente ao nível atual.
     * @return Um objeto Fase pronto para ser jogado.
     */
    public Fase carregarFase(Engine engine) {
        ScriptDeFase script;

        switch (nivelAtual) {
            case 1:
                script = new ScriptFase1(engine);
                break;
            case 2:
                script = new ScriptFase2(engine);
                break;
            case 3:
                script = new ScriptFase3(engine);
                break;
            case 4:
                script = new ScriptFase4(engine);
                break;
            case 5:
                script = new ScriptFase5(engine);
                break;
            default:
                script = new ScriptFase1(engine);
        }

        return new Fase(script);
    }

    /**
     * @brief Avança para a próxima fase e a carrega. Se chegar ao final, reinicia
     *        do começo.
     * @return A nova fase carregada.
     */
    public Fase proximaFase(Engine engine) {
        if (nivelAtual < TOTAL_DE_FASES) {
            nivelAtual++;
        } else {
            nivelAtual = 1;
        }
        return carregarFase(engine);
    }

    /**
     * @brief Pula para um número de fase específico.
     * @param numeroDaFase O nível para o qual pular.
     * @return A nova fase carregada.
     */
    public Fase irParaFase(int numeroDaFase, Engine engine) {
        if (numeroDaFase > 0 && numeroDaFase <= TOTAL_DE_FASES) {
            this.nivelAtual = numeroDaFase;
        }
        return carregarFase(engine);
    }

    /**
     * @brief Reseta o gerenciador para o nível inicial.
     */
    public void resetar() {
        this.nivelAtual = 1;
    }
}