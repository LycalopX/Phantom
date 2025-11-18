package Modelo.Projeteis;

import javax.swing.ImageIcon;

import Auxiliar.Projeteis.HitboxType;
import Auxiliar.Projeteis.ProjetilTipo;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * @brief Define os tipos de projéteis específicos do herói.
 * 
 *        Este enum implementa a interface `ProjetilTipo`, agindo como uma
 *        "fábrica"
 *        para as definições de cada tipo de projétil. Cada constante (NORMAL,
 *        HOMING, BOMBA)
 *        encapsula todas as propriedades necessárias para criar e configurar um
 *        projétil,
 *        como sua imagem, dimensões e tipo de hitbox.
 */
public enum TipoProjetilHeroi implements ProjetilTipo {
    NORMAL("projectiles/hero/projectile1_hero.png", 96, 24, HitboxType.CIRCULAR, 19),
    HOMING("projectiles/hero/projectile2_hero.png", 20, 20, HitboxType.CIRCULAR, 19),
    BOMBA("projectiles/hero/talisman_bomb.png", 48, 48, HitboxType.CIRCULAR, 48);

    private final ImageIcon imagem;
    private final int spriteWidth;
    private final int spriteHeight;
    private final HitboxType hitboxType;
    private final int hitboxWidth;
    private final int hitboxHeight;

    /**
     * @brief Construtor para cada tipo de projétil.
     * @param sNomeImagePNG Caminho para o arquivo de imagem do sprite.
     * @param spriteWidth   Largura do sprite.
     * @param spriteHeight  Altura do sprite.
     * @param hitboxType    O tipo de hitbox (CIRCULAR ou RECTANGULAR).
     * @param hitboxWidth   A largura (ou raio, para circular) da hitbox.
     */
    TipoProjetilHeroi(String sNomeImagePNG, int spriteWidth, int spriteHeight, HitboxType hitboxType, int hitboxWidth) {
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.imagem = carregarImagem("Assets/" + sNomeImagePNG, spriteWidth, spriteHeight);
        this.hitboxType = hitboxType;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxWidth;
    }

    @Override
    public ImageIcon getImagem() {
        return imagem;
    }

    @Override
    public int getSpriteWidth() {
        return spriteWidth;
    }

    @Override
    public int getSpriteHeight() {
        return spriteHeight;
    }

    @Override
    public HitboxType getHitboxType() {
        return hitboxType;
    }

    @Override
    public int getHitboxWidth() {
        return hitboxWidth;
    }

    @Override
    public int getHitboxHeight() {
        return hitboxHeight;
    }

    /**
     * @brief Carrega e redimensiona a imagem do projétil.
     */
    private ImageIcon carregarImagem(String nomeArquivo, int largura, int altura) {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(nomeArquivo);
            if (imgURL == null) {
                System.err.println("Recurso não encontrado: " + nomeArquivo);
                return null;
            }
            ImageIcon imagemIcon = new ImageIcon(imgURL);
            Image img = imagemIcon.getImage();
            BufferedImage bi = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();

            g.drawImage(img, 0, 0, largura, altura, null);
            g.dispose();
            return new ImageIcon(bi);
        } catch (Exception ex) {
            System.out.println("Erro ao carregar imagem: " + ex.getMessage());
            return null;
        }
    }
}
