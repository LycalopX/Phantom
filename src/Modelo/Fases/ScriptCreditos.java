package Modelo.Fases;

import Controler.Engine;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ScriptCreditos extends ScriptDeFase {

    private transient BufferedImage creditsBackground;
    private transient BufferedImage creditsText1;
    private transient BufferedImage creditsText2;
    private transient BufferedImage finalImage;

    private float alpha = 0.0f;
    private boolean fadingIn = true;
    private boolean scrolling = false;
    private boolean fadingOut = false;
    private boolean finalFadeIn = false;
    private boolean finished = false;

    private long timer;
    private float scrollY1;
    private float scrollY2;

    public ScriptCreditos(Engine engine) {
        super(engine);
    }

    @Override
    public void carregarRecursos(Fase fase) {
        try {
            creditsBackground = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits2.png"));
            creditsText1 = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits_text1.png"));
            creditsText2 = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits_text2.png"));
            finalImage = ImageIO.read(getClass().getClassLoader().getResource("Assets/credits1.png"));
        } catch (IOException e) {
            Logger.getLogger(ScriptCreditos.class.getName()).log(Level.SEVERE, "Erro ao carregar imagens de creditos", e);
        }
        timer = System.currentTimeMillis();
        scrollY1 = 600; // Start off-screen
        scrollY2 = scrollY1 + creditsText1.getHeight();
    }

    @Override
    public void relinkarRecursosDosElementos(Fase fase) {
        // Nothing to relink
    }

    @Override
    protected ArrayList<Onda> inicializarOndas(Fase fase) {
        // No waves in credits
        return new ArrayList<>();
    }

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
            if (scrollY2 < -creditsText2.getHeight()) {
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
            // Transition back to the main menu or restart
            engine.reiniciarJogo();
        }
    }

    public void render(Graphics2D g) {
        // Clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);

        if (fadingIn || scrolling) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(creditsBackground, 0, 0, null);
            g.drawImage(creditsText1, (800 - creditsText1.getWidth()) / 2, (int) scrollY1, null);
            g.drawImage(creditsText2, (800 - creditsText2.getWidth()) / 2, (int) scrollY2, null);
        } else if (fadingOut) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - alpha));
            g.drawImage(creditsBackground, 0, 0, null);
        } else if (finalFadeIn) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(finalImage, 0, 0, null);
        } else if (finished) {
            g.drawImage(finalImage, 0, 0, null);
        }
    }
}