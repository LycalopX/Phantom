package Controler;

import Modelo.Fase;
import Modelo.Hero;
import Modelo.Personagem;
import Modelo.Projetil;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import Auxiliar.DebugManager;

import java.util.ArrayList;

public class Engine implements Runnable {

    private static final int FPS = 60;
    private Tela tela;
    private Cenario cenario;
    private Thread gameThread;

    private Fase faseAtual;
    private Hero hero;
    private ControleDeJogo controleDeJogo; // Sua classe de regras
    double velocidadeScroll = 2.0; // Definir a lógica de velocidade aqui

    private final Set<Integer> teclasPressionadas = new HashSet<>();

    public Engine() {
        controleDeJogo = new ControleDeJogo();

        // 1. Inicializa o Modelo
        faseAtual = new Fase();
        hero = new Hero("hero/hero_s0.png", 10, 20);
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

        // 1. Chama o 'atualizar' do herói e recebe o "pacote de resultados"
        Hero.HeroUpdateResult resultadoHeroi = hero.atualizar(teclasPressionadas, controleDeJogo,
                faseAtual.getPersonagens());

        for (Personagem p : resultadoHeroi.novosProjeteis) {
            faseAtual.adicionarPersonagem(p);
        }
        if (resultadoHeroi.usouBomba) {
            faseAtual.ativarBomba();
        }

        // Atualiza a fase (scroll, inimigos, spawns)
        faseAtual.atualizar(velocidadeScroll);

        controleDeJogo.processaTudo(faseAtual.getPersonagens());

    }

    private void configurarTeclado() {
        tela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                // --- LÓGICA DE SAVE/LOAD (COM NOVAS TECLAS) ---
                if (e.getKeyCode() == KeyEvent.VK_P) { // 'S' foi mudado para 'P'
                    salvarJogo();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_R) { // 'L' foi mudado para 'R'
                    carregarJogo();
                    return;
                }

                teclasPressionadas.add(e.getKeyCode());

                // Verifica se a combinação F+3 foi completada
                if (teclasPressionadas.contains(KeyEvent.VK_F) && teclasPressionadas.contains(KeyEvent.VK_3)) {

                    DebugManager.toggle();
                    teclasPressionadas.remove(KeyEvent.VK_F);
                    teclasPressionadas.remove(KeyEvent.VK_3);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // A lógica de remover a tecla quando ela é solta continua a mesma
                teclasPressionadas.remove(e.getKeyCode());
            }
        });
    }

    private void salvarJogo() {
        try {
            // Cria um arquivo para salvar o jogo
            FileOutputStream fos = new FileOutputStream("POO.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Salva o objeto 'faseAtual' inteiro no arquivo
            oos.writeObject(this.faseAtual);

            oos.close();
            fos.close();
            System.out.println(">>> JOGO SALVO!");
        } catch (Exception e) {
            System.out.println("ERRO AO SALVAR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarJogo() {
        try {
            // Lê o arquivo salvo
            FileInputStream fis = new FileInputStream("POO.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Carrega o objeto 'faseAtual' do arquivo
            this.faseAtual = (Fase) ois.readObject();

            ois.close();
            fis.close();

            // ETAPA CRUCIAL: Atualizar as referências do Engine!
            // O Cenario precisa saber sobre a nova fase
            cenario.setFase(this.faseAtual);

            // O Engine precisa de uma referência direta para o novo Herói
            this.hero = (Hero) this.faseAtual.getHero();

            System.out.println(">>> JOGO CARREGADO!");
        } catch (Exception e) {
            System.out.println("ERRO AO CARREGAR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}