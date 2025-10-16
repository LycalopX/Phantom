// Main.java
import Controler.Engine;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Engine().startGameThread();
        });
    }
}

/*
In this system, the game starts at 11:00 pm, and 1:00 is added after each stage. 
If enough time orbs are collected in the stage, 0:30 is added for that stage 
instead. 0:30 is also added in the case the player decides to continue. If the 
time reaches 5:00 am before the end of the last stage, the player receives a 
Game Over. 
*/