package Modelo.Fases;

import Controler.Engine;

import static Auxiliar.ConfigMapa.ALTURA_TELA;
import static Auxiliar.ConfigMapa.LARGURA_TELA;
import static Modelo.Personagem.BODY_PROPORTION;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import Auxiliar.SoundManager;

/**
 * @brief Script que controla a cena de créditos do jogo.
 * 
 *        Gerencia uma sequência de animações, incluindo fade-in, rolagem de
 *        texto
 *        e fade-out, utilizando uma máquina de estados interna simples.
 */
public class ScriptCreditos extends ScriptDeFase {

    private transient BufferedImage creditsBackground;
    private transient BufferedImage creditsText1;
    private transient BufferedImage creditsText2;
    private transient BufferedImage creditsText3;
    private transient BufferedImage finalImage;

    // Variáveis da máquina de estados que controla a animação
    private float alpha = 0.0f;
    private boolean fadingIn = true;
    private boolean scrolling = false;
    private boolean fadingOut = false;
    private boolean finalFadeIn = false;
    private boolean finished = false;

    private long timer;
    private double scrollY1;
    private double scrollY2;
    private double scrollY3;

    public ScriptCreditos(Engine engine) {
        super(engine);
        SoundManager.getInstance().playMusic("Credits", false);
    }

    /**
     * @brief Carrega as imagens necessárias para a cena de créditos.
     */
    @Override
    public void carregarRecursos(Fase fase) {
        try {
            creditsBackground = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits2.png"));
            creditsText1 = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits_text1.png"));
            creditsText2 = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits_text2.png"));
            creditsText3 = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits_text3.png"));
            finalImage = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits1.png"));
        } catch (IOException e) {
            Logger.getLogger(ScriptCreditos.class.getName()).log(Level.SEVERE, "Erro ao carregar imagens de creditos",
                    e);
        }
        timer = System.currentTimeMillis();
        scrollY1 = ALTURA_TELA;
        scrollY2 = scrollY1 + (creditsText1.getHeight() * BODY_PROPORTION);
        scrollY3 = scrollY2 + (creditsText2.getHeight() * BODY_PROPORTION);
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {

    }

    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        // A cena de créditos não possui ondas de inimigos.
        return new ArrayList<>();
    }

    /**
     * @brief Atualiza a lógica da animação dos créditos.
     * 
     *        Este método funciona como o "coração" da cena, avançando a máquina de
     *        estados da animação (fade-in -> scroll -> fade-out -> fade-in final).
     */
    @Override
    public void atualizarInimigos(Fase fase) {
        long elapsed = System.currentTimeMillis() - timer;
        timer = System.currentTimeMillis();

        if (fadingIn) {
            alpha += 0.005f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadingIn = false;
                scrolling = true;
            }
        } else if (scrolling) {
            scrollY1 -= 0.5f * elapsed / 10.0f;
            scrollY2 -= 0.5f * elapsed / 10.0f;
            scrollY3 -= 0.5f * elapsed / 10.0f;
            if (scrollY3 < -creditsText3.getHeight()) {
                scrolling = false;
                fadingOut = true;
            }
        } else if (fadingOut) {
            alpha -= 0.005f;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                fadingOut = false;
                finalFadeIn = true;
            }
        } else if (finalFadeIn) {
            alpha += 0.005f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                finalFadeIn = false;
                finished = true;
            }
        }

        if (finished) {
            // Após um tempo, encerra o jogo.
            ondas.add(new OndaDeEspera(fase, 1000));
            ondas.add(new OndaFinalizarJogo(fase));
        }
    }

    /**
     * @brief Uma onda especial que serve apenas para criar uma pausa no script.
     */
    protected class OndaFinalizarJogo extends Onda {

        public OndaFinalizarJogo(Fase fase) {
            super();
            System.exit(0);
        }
    }

    /**
     * @brief Renderiza a cena de créditos com base no estado atual da animação.
     * @param g O contexto gráfico para desenhar.
     */

    public void render(Graphics2D g) {

        g.setColor(Color.BLACK);
        // Mudei de 800, 600 hardcoded para as constantes
        g.fillRect(0, 0, LARGURA_TELA, ALTURA_TELA);

        if (fadingIn || scrolling) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            g.drawImage(creditsBackground, 0, 0, LARGURA_TELA, ALTURA_TELA, null);

            int w1 = (int) (creditsText1.getWidth() * BODY_PROPORTION);
            int h1 = (int) (creditsText1.getHeight() * BODY_PROPORTION);

            int w2 = (int) (creditsText2.getWidth() * BODY_PROPORTION);
            int h2 = (int) (creditsText2.getHeight() * BODY_PROPORTION);

            int w3 = (int) (creditsText3.getWidth() * BODY_PROPORTION);
            int h3 = (int) (creditsText3.getHeight() * BODY_PROPORTION);

            // O texto mantivemos igual
            g.drawImage(creditsText1, (LARGURA_TELA - w1) / 2, (int) scrollY1, w1, h1, null);
            g.drawImage(creditsText2, (LARGURA_TELA - w2) / 2, (int) scrollY2, w2, h2, null);
            g.drawImage(creditsText3, (LARGURA_TELA - w3) / 2, (int) scrollY3, w3, h3, null);

        } else if (fadingOut) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(creditsBackground, 0, 0, LARGURA_TELA, ALTURA_TELA, null);

        } else if (finalFadeIn) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(finalImage, 0, 0, LARGURA_TELA, ALTURA_TELA, null);

        } else if (finished) {
            g.drawImage(finalImage, 0, 0, LARGURA_TELA, ALTURA_TELA, null);
        }
    }
}