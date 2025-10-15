import Controler.Cenario;
import Controler.Tela;

public class Main {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // 1. Cria a janela (Tela), que agora é só uma moldura.
                Tela tTela = new Tela();

                // 2. Cria o painel de jogo (Cenario), que contém toda a lógica.
                Cenario cenario = new Cenario();
                
                // 3. Adiciona o painel de jogo à janela.
                tTela.add(cenario);
                
                // 4. Ajusta o tamanho da janela para caber perfeitamente no painel.
                tTela.pack();

                // 5. Centraliza a janela no ecrã e torna-a visível.
                tTela.setLocationRelativeTo(null);
                tTela.setVisible(true);
                
                // 6. Inicia o loop do jogo e pede o foco para o teclado.
                cenario.startGameThread();
            }
        });
    }
}