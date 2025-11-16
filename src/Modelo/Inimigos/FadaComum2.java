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

    public FadaComum2(double x, double y, LootTable lootTable, double vida, Fase fase, String skin, int behaviorType) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "imgs/inimigos/enemy2_spreadsheet" + skin + ".png",
                32, 32, 0, 4, 4,
                true,
                (int) (32.0 * BODY_PROPORTION),
                (int) (32.0 * BODY_PROPORTION),
                false);
        this.largura = (int) (32.0 * BODY_PROPORTION);
        this.altura = (int) (32.0 * BODY_PROPORTION);
        this.hitboxRaio = (this.largura / 2.0) / CELL_SIDE;

        switch (behaviorType) {
            case 2:
                double arcHeight = 3.0;
                Estado entrada2 = new IrPara(this, x, 6, 0.1);

                Estado ataqueArco1 = new MovimentoParabolicoAtirando(this, MUNDO_LARGURA * 0.8, 6, 0.2, arcHeight, 30);
                Estado ataqueArco2 = new MovimentoParabolicoAtirando(this, MUNDO_LARGURA * 0.2, 8, 0.2, arcHeight, 30);

                Estado pausa2 = new Esperar(this, 120);

                Estado ataqueArco3 = new MovimentoParabolicoAtirando(this, MUNDO_LARGURA * 0.8, 6, 0.2, arcHeight, 30);
                Estado ataqueArco4 = new MovimentoParabolicoAtirando(this, MUNDO_LARGURA * 0.2, 8, 0.2, arcHeight, 30);

                Estado saida2 = new IrPara(this, x, -2, 0.2);

                entrada2.setProximoEstado(ataqueArco1);
                ataqueArco1.setProximoEstado(ataqueArco2);
                ataqueArco2.setProximoEstado(pausa2);
                pausa2.setProximoEstado(ataqueArco3);
                ataqueArco3.setProximoEstado(ataqueArco4);
                ataqueArco4.setProximoEstado(saida2);

                this.estadoAtual = entrada2;
                break;
            case 1:
            default:
                Estado entrada = new IrPara(this, x, 6, 0.1);

                Estado ataqueMovendo1 = new MovimentoAtirando(this, MUNDO_LARGURA - 5, 8, 0.1, 30);
                Estado ataqueMovendo2 = new MovimentoAtirando(this, 5, 8, 0.2, 30);

                Estado pausa = new Esperar(this, 120);

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
        return estadoAtual instanceof IrPara || estadoAtual instanceof MovimentoParabolicoAtirando;
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
                false);
    }

    private void atirarEmCirculo(Fase fase, double centerX, double centerY, int numProjeteis, double velocidade) {
        double anguloEntreProjeteis = 360.0 / numProjeteis;
        for (int i = 0; i < numProjeteis; i++) {
            double angulo = i * anguloEntreProjeteis;
            Projetil p = fase.getProjetilPool().getProjetilInimigo();
            if (p != null) {
                p.reset(centerX, centerY, velocidade, angulo, TipoProjetil.INIMIGO,
                        TipoProjetilInimigo.ESFERA_VERMELHA);
            }
        }
    }

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
            super.incrementarTempo(fase, tempo);

            proximoAtaque -= tempo;
            if (proximoAtaque <= 0) {
                atirar(fase);
                proximoAtaque = intervaloAtaque;
            }
        }

        private void atirar(Fase fase) {
            if (fase == null)
                return;
            atirarEmCirculo(fase, getX(), getY(), 8, 0.08);
            Auxiliar.SoundManager.getInstance().playSfx("se_tan00", 0.8f);
        }
    }

    private class MovimentoParabolicoAtirando extends Estado {
        private double startX, startY;
        private double targetX, targetY;
        private double arcHeight;
        private double totalDistance;
        private double distanceTraveled;
        private double speed;
        private int intervaloAtaque;
        private int proximoAtaque;

        public MovimentoParabolicoAtirando(Inimigo inimigo, double targetX, double targetY, double speed,
                double arcHeight, int intervaloAtaque) {
            super(inimigo);
            this.targetX = targetX;
            this.targetY = targetY;
            this.speed = speed;
            this.arcHeight = arcHeight;
            this.intervaloAtaque = intervaloAtaque;
            this.proximoAtaque = 0;
        }

        @Override
        public void reset() {
            super.reset();
            this.startX = getX();
            this.startY = getY();
            this.totalDistance = Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2));
            this.distanceTraveled = 0;
        }

        @Override
        public void incrementarTempo(Fase fase, int tempo) {
            if (estadoCompleto)
                return;

            distanceTraveled += speed * tempo;
            double progress = totalDistance > 0 ? distanceTraveled / totalDistance : 1.0;

            if (progress >= 1.0) {
                progress = 1.0;
                estadoCompleto = true;
            }

            double newX = startX + (targetX - startX) * progress;
            double linearY = startY + (targetY - startY) * progress;

            double parabolicOffset = 4 * arcHeight * (progress - progress * progress);
            double newY = linearY + parabolicOffset;

            setPosition(newX, newY);

            proximoAtaque -= tempo;
            if (proximoAtaque <= 0) {
                atirar(fase);
                proximoAtaque = intervaloAtaque;
            }
        }

        private void atirar(Fase fase) {
            if (fase == null)
                return;
            atirarEmCirculo(fase, x, y, 8, 0.08);
            Auxiliar.SoundManager.getInstance().playSfx("se_tan00", 0.8f);
        }
    }
}