package Modelo.Inimigos;

import Auxiliar.LootTable;
import Modelo.Fases.Fase;
import Modelo.Personagem;
import Modelo.Projeteis.Projetil;
import Auxiliar.Projeteis.TipoProjetil;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import static Auxiliar.ConfigMapa.*;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FadaComum3 extends Inimigo {

    private Estado estadoAtual;
    private transient GerenciadorDeAnimacaoInimigo animador;

    public FadaComum3(double x, double y, double targetX, LootTable lootTable, double vida, Fase fase, String skin, int behaviorType) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy3_spreadsheet" + skin + ".png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false);
        this.largura = (int) (32.0 * BODY_PROPORTION);
        this.altura = (int) (32.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;

        switch (behaviorType) {
            case 1:
            default:
                // Define a sequência de estados para o padrão "Pressão Dupla"
                Estado entrada = new IrPara(this, targetX, 5, 0.1);
                Estado ataqueLento = new AtaqueEmLequeMirado(this, 5, 45, 0.105, TipoProjetilInimigo.ESFERA_GRANDE_AMARELA); // 5 tiros, 30% mais lento
                Estado pausaAposAtaqueLento = new Esperar(this, 30); // Meio segundo de pausa
                Estado ataqueRapido = new AtaqueEmLequeMirado(this, 8, 60, 0.15, TipoProjetilInimigo.ESFERA_AMARELA); // 8 tiros
                Estado pausaAntesSaida = new Esperar(this, 120); // 2 segundos de pausa antes de sair
                Estado saida = new IrPara(this, targetX, -2, 0.15);

                entrada.setProximoEstado(ataqueLento);
                ataqueLento.setProximoEstado(pausaAposAtaqueLento);
                pausaAposAtaqueLento.setProximoEstado(ataqueRapido);
                ataqueRapido.setProximoEstado(pausaAntesSaida);
                pausaAntesSaida.setProximoEstado(saida);

                this.estadoAtual = entrada;
                break;
            case 2:
                // Novo comportamento a ser implementado
                break;
        }
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
                "imgs/inimigos/enemy3_spreadsheet.png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false);
    }

    // --- Novos Estados de Ataque ---

    private class AtaqueEmLequeMirado extends Estado {
        private int quantidadeTiros;
        private double amplitudeLeque;
        private double velocidadeProjetil;
        private TipoProjetilInimigo tipoProjetil;

        public AtaqueEmLequeMirado(Inimigo inimigo, int quantidade, double amplitude, double velocidade, TipoProjetilInimigo tipo) {
            super(inimigo);
            this.quantidadeTiros = quantidade;
            this.amplitudeLeque = amplitude;
            this.velocidadeProjetil = velocidade;
            this.tipoProjetil = tipo;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto) return;

            Personagem hero = fase.getHero();
            if (hero != null) {
                double anguloCentral = 90.0; // Atira sempre para baixo
                atirarEmLeque(fase, anguloCentral);
                Auxiliar.SoundManager.getInstance().playSfx("se_tan01", 0.9f);
            }
            this.estadoCompleto = true;
        }

        private void atirarEmLeque(Fase fase, double anguloInicial) {
            double espacamento = (quantidadeTiros > 1) ? amplitudeLeque / (quantidadeTiros - 1) : 0;
            for (int i = 0; i < quantidadeTiros; i++) {
                double angulo = anguloInicial - (amplitudeLeque / 2.0) + (espacamento * i);
                Projetil p = fase.getProjetilPool().getProjetilInimigo();
                if (p != null) {
                    p.reset(inimigo.getX(), inimigo.getY(), velocidadeProjetil, angulo, TipoProjetil.INIMIGO, tipoProjetil);
                }
            }
        }
    }
}
