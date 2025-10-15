package Controler;

import Modelo.Fase;
import Modelo.Hero;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class Engine implements Runnable {
    
    private static final int FPS = 60;
    private Tela tela;
    private Cenario cenario;
    private Thread gameThread;

    private Fase faseAtual;
    private Hero hero;
    private ControleDeJogo controleDeJogo; // Sua classe de regras
    
    private final Set<Integer> teclasPressionadas = new HashSet<>();

    public Engine() {
        controleDeJogo = new ControleDeJogo();
        
        // 1. Inicializa o Modelo
        faseAtual = new Fase();
        hero = new Hero("hero/hero_s0.png", 10, 10);
        faseAtual.adicionarPersonagem(hero);
        
        // 2. Inicializa a View
        cenario = new Cenario();
        cenario.setFase(faseAtual);
        
        // 3. Inicializa a Janela Principal
        tela = new Tela();
        tela.add(cenario);
        tela.pack();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true);

        configurarTeclado();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                atualizar();
                cenario.repaint();
                cenario.atualizarContadorFPS();
                delta--;
            }
        }
    }

    private void atualizar() {
        // Atualiza o herói com base no input
        hero.atualizar(teclasPressionadas, controleDeJogo, faseAtual.getPersonagens());

        // Atualiza a fase (scroll, inimigos, spawns)
        double velocidadeScroll = 2.0; // Definir a lógica de velocidade aqui
        faseAtual.atualizar(velocidadeScroll);
        
        // Processa colisões e outras regras
        controleDeJogo.processaTudo(faseAtual.getPersonagens());
    }

    private void configurarTeclado() {
        tela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclasPressionadas.add(e.getKeyCode());
            }
            @Override
            public void keyReleased(KeyEvent e) {
                teclasPressionadas.remove(e.getKeyCode());
            }
        });
    }
}