package Modelo.Inimigos;

import static Auxiliar.ConfigMapa.*;
import Auxiliar.LootTable;
import Auxiliar.Projeteis.TipoProjetilInimigo;
import Modelo.Fases.Fase;
import Modelo.Inimigos.GerenciadorDeAnimacaoInimigo.AnimationState;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @brief Implementação do chefe Nightbug.
 * 
 *        Este chefe segue uma máquina de estados sequencial, alternando entre
 *        se mover para posições específicas e executar padrões de ataque
 *        a partir dessas posições.
 */
public class Nightbug extends Boss {

    private transient GerenciadorDeAnimacaoInimigo animador;
    private transient Estado estado;

    public Nightbug(double x, double y, LootTable lootTable, double vida, Fase fase) {
        super("", x, y, lootTable, vida);
        this.faseReferencia = fase;

        int scaledWidth = (int) (35 * BODY_PROPORTION);
        int scaledHeight = (int) (60 * BODY_PROPORTION);

        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss1_spreadsheet.png",
                35, 60, 13, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true);
        this.largura = scaledWidth;
        this.altura = scaledHeight;
        this.hitboxRaio = (this.largura / 2.0) / Auxiliar.ConfigMapa.CELL_SIDE;

        setupEstados();
    }

    /**
     * @brief Configura a máquina de estados sequencial do chefe.
     * 
     *        Define a sequência de movimentos e ataques, encadeando cada estado
     *        ao próximo para criar um ciclo de comportamento.
     */
    private void setupEstados() {
        Estado irCentro = new IrParaOCentro(this, new Point2D.Double(0.2, 0.2));
        Estado irEsquerda = new IrParaEsquerda(this, new Point2D.Double(0.5, 0.2));
        Estado irDireita = new IrParaDireita(this, new Point2D.Double(0.5, 0.2));

        Estado ataqueParaBaixo = new AtaqueParaBaixo(this);
        Estado ataqueParaDireita = new AtaqueParaDireita(this);
        Estado ataqueParaEsquerda = new AtaqueParaEsquerda(this);

        estado = irCentro;
        irCentro.setProximoEstado(ataqueParaBaixo);

        ataqueParaBaixo.setProximoEstado(irEsquerda);
        irEsquerda.setProximoEstado(ataqueParaDireita);

        ataqueParaDireita.setProximoEstado(irDireita);
        irDireita.setProximoEstado(ataqueParaEsquerda);
        ataqueParaEsquerda.setProximoEstado(irCentro);
    }

    /**
     * @brief Restaura o animador e a máquina de estados após a desserialização.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int scaledWidth = (int) (35 * BODY_PROPORTION);
        int scaledHeight = (int) (60 * BODY_PROPORTION);
        this.animador = new GerenciadorDeAnimacaoInimigo(
                "Assets/inimigos/boss1_spreadsheet.png",
                35, 60, 13, 4, 4,
                true,
                scaledWidth,
                scaledHeight,
                true);

        setupEstados();
    }

    @Override
    public void initialize(Fase fase) {
        this.faseReferencia = fase;
    }

    @Override
    public boolean isStrafing() {
        if (estado instanceof IrPara irPara) {
            return irPara.getMovimento().proximoMovimento(this.x, this.y).x != 0;
        } else {
            return false;
        }
    }

    @Override
    public void atualizar() {
        estado = processarEstado(estado, 1);
        animador.atualizar(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
    }

    @Override
    public void autoDesenho(Graphics g) {
        this.iImage = animador.getImagemAtual(isStrafing() ? AnimationState.STRAFING : AnimationState.IDLE);
        super.autoDesenho(g);
    }

    private class IrParaOCentro extends IrPara {
        public IrParaOCentro(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.5 * (MUNDO_LARGURA - 2) + 2, 0.2 * MUNDO_ALTURA),
                    velocidade);
        }
    }

    private class IrParaEsquerda extends IrPara {
        public IrParaEsquerda(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.1 * (MUNDO_LARGURA - 2) + 2, 0.1 * MUNDO_ALTURA),
                    velocidade);
        }
    }

    private class IrParaDireita extends IrPara {
        public IrParaDireita(Boss boss, Point2D.Double velocidade) {
            super(boss,
                    new Point2D.Double(0.9 * (MUNDO_LARGURA - 2) + 2, 0.1 * MUNDO_ALTURA),
                    velocidade);
        }
    }

    /**
     * @brief Define o padrão de ataque executado quando o chefe está no centro.
     */
    private class AtaqueParaBaixo extends AtaqueEmLeque {
        public AtaqueParaBaixo(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.OVAL_AZUL_PISCINA_CLARO;

            padroes.add(new PadraoLeque(90, 140, 10));
            padroes.add(new PadraoLeque(90, 80, 10));
        }
    }

    /**
     * @brief Define o padrão de ataque executado quando o chefe está na direita.
     */
    private class AtaqueParaDireita extends AtaqueEmLeque {
        public AtaqueParaDireita(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AZUL;

            padroes.add(new PadraoLeque(50, 140, 10));
            padroes.add(new PadraoLeque(50, 80, 10));
        }
    }

    /**
     * @brief Define o padrão de ataque executado quando o chefe está na esquerda.
     */
    private class AtaqueParaEsquerda extends AtaqueEmLeque {
        public AtaqueParaEsquerda(Boss boss) {
            super(boss);

            this.intervaloAtaque = 60;
            this.velocidadeProjetil = 0.15;
            this.tipoProjetil = TipoProjetilInimigo.ESFERA_AZUL;

            padroes.add(new PadraoLeque(130, 140, 10));
            padroes.add(new PadraoLeque(130, 80, 10));
        }
    }
}
