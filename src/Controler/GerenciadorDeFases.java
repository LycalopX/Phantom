// Em Controler/GerenciadorDeFases.java
package Controler;

import Modelo.Fases.Fase;
import Modelo.Fases.ScriptDeFase;
import Modelo.Fases.ScriptFase1;
import Modelo.Fases.ScriptFase2;
import Modelo.Fases.ScriptFase3;
import Modelo.Fases.ScriptFase4;
import Modelo.Fases.ScriptFase5;

public class GerenciadorDeFases {
    private int nivelAtual = 1;
    private final int TOTAL_DE_FASES = 5; // Do PDF [cite: 15]

    public Fase carregarFase() {
        ScriptDeFase script;

        switch (nivelAtual) {
            case 1:
                script = new ScriptFase1();
                break;
            case 2:
                script = new ScriptFase2();
                break;
            case 3:
                script = new ScriptFase3();
                break;
            case 4:
                script = new ScriptFase4();
                break;  
            case 5:
                script = new ScriptFase5();
                break;
                
            default:
                // Se não tiver, repete a fase 1
                script = new ScriptFase1();
        }

        return new Fase(script);
    }

    public Fase proximaFase() {
        if (nivelAtual < TOTAL_DE_FASES) {
            nivelAtual++;
        } else {
            // Loop de volta para a primeira fase ou vai para tela de "Fim de Jogo"
            nivelAtual = 1; 
        }
        return carregarFase();
    }

    public void resetar() {
        this.nivelAtual = 1;
    }
}