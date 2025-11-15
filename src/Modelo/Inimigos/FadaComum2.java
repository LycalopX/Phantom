package Modelo.Inimigos;

import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Projeteis.Projetil;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import static Auxiliar.ConfigMapa.*;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FadaComum2 extends Inimigo {

    private Estado estadoAtual;
    private transient GerenciadorDeAnimacaoInimigo animador;

    public FadaComum2(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, 40);
        this.faseReferencia = fase;

        // 32x32
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy2_spreadsheet.png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false
        );
        this.largura = (int) (32.0 * BODY_PROPORTION);
        this.altura = (int) (32.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;
        
        // Define a nova sequência de estados
        Estado entrada = new IrPara(this, x, 6, 0.1);
        
        Estado ataqueMovendo1 = new MovimentoAtirando(this, MUNDO_LARGURA - 5, 8, 0.1, 30);
        Estado ataqueMovendo2 = new MovimentoAtirando(this, 5, 8, 0.2, 30);
        
        Estado pausa = new Esperar(this, 120); // Pausa de 2 segundos
        
        Estado ataqueMovendo3 = new MovimentoAtirando(this, MUNDO_LARGURA - 5, 8, 0.1, 30);
        Estado ataqueMovendo4 = new MovimentoAtirando(this, 5, 8, 0.2, 30);

        Estado saida = new IrPara(this, x, -2, 0.2);

        entrada.setProximoEstado(ataqueMovendo1);
        ataqueMovendo1.setProximoEstado(ataqueMovendo2);
        ataqueMovendo2.setProximoEstado(pausa);
        pausa.setProximoEstado(ataqueMovendo3);
        ataqueMovendo3.setProximoEstado(ataqueMovendo4);
        ataqueMovendo4.setProximoEstado(saida);
        
        this.estadoAtual = entrada;
    }

    @Override
    public void atualizar() {
        this.estadoAtual = processarEstado(this.estadoAtual, 1);
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public boolean isStrafing() {
        return estadoAtual instanceof IrPara;
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy2_spreadsheet.png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false
        );
    }

    // --- Novo Estado que combina Movimento e Ataque ---

    private class MovimentoAtirando extends IrPara {
        private int intervaloAtaque;
        private int proximoAtaque;

        public MovimentoAtirando(Inimigo inimigo, double alvoX, double alvoY, double velocidade, int intervaloAtaque) {
            super(inimigo, alvoX, alvoY, velocidade);
            this.intervaloAtaque = intervaloAtaque;
            this.proximoAtaque = intervaloAtaque;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            super.incrementarTempo(fase, tempo); // Executa a lógica de movimento do IrPara

            proximoAtaque -= tempo;
            if (proximoAtaque <= 0) {
                atirar(fase);
                proximoAtaque = intervaloAtaque;
            }
        }

        private void atirar(Fase fase) {
            if (fase == null) return;
            
            atirarEmCirculo(fase, inimigo.getX(), inimigo.getY(), 8, 0.08);
            Auxiliar.SoundManager.getInstance().playSfx("se_tan00", 0.8f);
        }

        private void atirarEmCirculo(Fase fase, double centerX, double centerY, int numProjeteis, double velocidade) {
            double anguloEntreProjeteis = 360.0 / numProjeteis;
            for (int i = 0; i < numProjeteis; i++) {
                double angulo = i * anguloEntreProjeteis;
                Projetil p = fase.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(centerX, centerY, velocidade, angulo, TipoProjetil.INIMIGO, TipoProjetilInimigo.ESFERA_VERMELHA);
                }
            }
        }
    }
}