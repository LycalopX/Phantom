package Modelo;

public class Hero extends Personagem {

    public Hero(String sNomeImagePNG, double x, double y) {
        super(sNomeImagePNG, x, y);
    }
    
    // A lógica de movimento (moveUp, moveDown, etc.) foi removida daqui
    // porque o Cenario agora controla o movimento suave frame a frame.
}
