package Auxiliar.Projeteis;

import Auxiliar.Projeteis.Definicoes.CarregadorDeDefinicoes;
import Auxiliar.Projeteis.Definicoes.DefinicaoProjetil;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * @brief Define todos os tipos de projéteis de inimigos disponíveis no jogo.
 * 
 *        Este enum implementa a interface `ProjetilTipo` e funciona como um
 *        catálogo
 *        central para todos os projéteis inimigos. As propriedades de cada tipo
 *        (imagem, hitbox, etc.) são carregadas dinamicamente de um arquivo JSON
 *        através do método estático `iniciar()`.
 */
public enum TipoProjetilInimigo implements ProjetilTipo {

    ESFERA_PRETA,
    ESFERA_VERMELHA,
    ESFERA_SALMAO,
    ESFERA_ROXA,
    ESFERA_ROSA,
    ESFERA_AZUL,
    ESFERA_AZUL_CLARA,
    ESFERA_AZUL_MAIS_CLARA,
    ESFERA_AZUL_PISCINA,
    ESFERA_VERDE,
    ESFERA_VERDE_CLARA,
    ESFERA_VERDE_LIMAO,
    ESFERA_AMARELA,
    ESFERA_AMARELO_CLARO,
    ESFERA_LARANJA,
    ESFERA_CINZA,

    ESFERA_GRANDE_PRETA,
    ESFERA_GRANDE_VERMELHA,
    ESFERA_GRANDE_AZUL,
    ESFERA_GRANDE_VERDE,
    ESFERA_GRANDE_AMARELA,
    ESFERA_GRANDE_PRETA_OCA,
    ESFERA_GRANDE_VERMELHA_OCA,
    ESFERA_GRANDE_AZUL_OCA,
    ESFERA_GRANDE_VERDE_OCA,
    ESFERA_GRANDE_AMARELA_OCA,

    BEAM_PRETO,
    BEAM_VERMELHO_ESCURO,
    BEAM_VERMELHO_LILAS,
    BEAM_ROSA,
    BEAM_AZUL_ESCURO,
    BEAM_AZUL,
    BEAM_AZUL_PISCINA_CLARO,
    BEAM_AZUL_PISCINA,
    BEAM_VERDE_ESCURO,
    BEAM_VERDE,
    BEAM_VERDE_LIMAO,
    BEAM_AMARELO_ESCURO,
    BEAM_AMARELO,
    BEAM_LARANJA,
    BEAM_CINZA,

    FLECHA_PRETA,
    FLECHA_VERMELHO_ESCURO,
    FLECHA_VERMELHO_LILAS,
    FLECHA_ROSA,
    FLECHA_AZUL_ESCURO,
    FLECHA_AZUL,
    FLECHA_AZUL_PISCINA_CLARO,
    FLECHA_AZUL_PISCINA,
    FLECHA_VERDE_ESCURO,
    FLECHA_VERDE,
    FLECHA_VERDE_LIMAO,
    FLECHA_AMARELO_ESCURO,
    FLECHA_AMARELO,
    FLECHA_LARANJA,
    FLECHA_CINZA,

    ESFERA_INCOMPLETA_PRETA,
    ESFERA_INCOMPLETA_VERMELHO_ESCURO,
    ESFERA_INCOMPLETA_VERMELHO_LILAS,
    ESFERA_INCOMPLETA_ROSA,
    ESFERA_INCOMPLETA_AZUL_ESCURO,
    ESFERA_INCOMPLETA_AZUL,
    ESFERA_INCOMPLETA_AZUL_PISCINA_CLARO,
    ESFERA_INCOMPLETA_AZUL_PISCINA,
    ESFERA_INCOMPLETA_VERDE_ESCURO,
    ESFERA_INCOMPLETA_VERDE,
    ESFERA_INCOMPLETA_VERDE_LIMAO,
    ESFERA_INCOMPLETA_AMARELO_ESCURO,
    ESFERA_INCOMPLETA_AMARELO,
    ESFERA_INCOMPLETA_LARANJA,
    ESFERA_INCOMPLETA_CINZA,

    ESFERA_COMPLETA_PRETA,
    ESFERA_COMPLETA_VERMELHO_ESCURO,
    ESFERA_COMPLETA_VERMELHO_LILAS,
    ESFERA_COMPLETA_ROSA,
    ESFERA_COMPLETA_AZUL_ESCURO,
    ESFERA_COMPLETA_AZUL,
    ESFERA_COMPLETA_AZUL_PISCINA_CLARO,
    ESFERA_COMPLETA_AZUL_PISCINA,
    ESFERA_COMPLETA_VERDE_ESCURO,
    ESFERA_COMPLETA_VERDE,
    ESFERA_COMPLETA_VERDE_LIMAO,
    ESFERA_COMPLETA_AMARELO_ESCURO,
    ESFERA_COMPLETA_AMARELO,
    ESFERA_COMPLETA_LARANJA,
    ESFERA_COMPLETA_CINZA,

    OVAL_PRETO,
    OVAL_VERMELHO_ESCURO,
    OVAL_VERMELHO_LILAS,
    OVAL_ROSA,
    OVAL_AZUL_ESCURO,
    OVAL_AZUL,
    OVAL_AZUL_PISCINA_CLARO,
    OVAL_AZUL_PISCINA,
    OVAL_VERDE_ESCURO,
    OVAL_VERDE,
    OVAL_VERDE_LIMAO,
    OVAL_AMARELO_ESCURO,
    OVAL_AMARELO,
    OVAL_LARANJA,
    OVAL_CINZA,

    FLECHA_LONGA_PRETA,
    FLECHA_LONGA_VERMELHO_ESCURO,
    FLECHA_LONGA_VERMELHO_LILAS,
    FLECHA_LONGA_ROSA,
    FLECHA_LONGA_AZUL_ESCURO,
    FLECHA_LONGA_AZUL,
    FLECHA_LONGA_AZUL_PISCINA_CLARO,
    FLECHA_LONGA_AZUL_PISCINA,
    FLECHA_LONGA_VERDE_ESCURO,
    FLECHA_LONGA_VERDE,
    FLECHA_LONGA_VERDE_LIMAO,
    FLECHA_LONGA_AMARELO_ESCURO,
    FLECHA_LONGA_AMARELO,
    FLECHA_LONGA_LARANJA,
    FLECHA_LONGA_CINZA;

    private DefinicaoProjetil definicao;
    private ImageIcon imagem;

    /**
     * @brief Inicializa todos os tipos de projéteis.
     * 
     *        Este método deve ser chamado uma vez no início do jogo. Ele carrega as
     *        definições de projéteis do arquivo JSON, carrega os spritesheets
     *        correspondentes e, em seguida, associa cada constante do enum com sua
     *        definição e imagem de sprite recortada.
     */
    public static void iniciar() {
        CarregadorDeDefinicoes carregador = CarregadorDeDefinicoes.getInstance();
        Map<SpriteSheetSource, BufferedImage> spritesheetsCarregados = new EnumMap<>(SpriteSheetSource.class);

        for (SpriteSheetSource source : SpriteSheetSource.values()) {
            try {
                URL imgURL = TipoProjetilInimigo.class.getClassLoader().getResource(source.path);
                if (imgURL == null) {
                    System.err.println("Spritesheet de projéteis inimigos não encontrado: " + source.path);
                    continue;
                }
                spritesheetsCarregados.put(source, ImageIO.read(imgURL));
            } catch (IOException e) {
                System.err.println("Erro ao ler spritesheet de projéteis inimigos: " + source.path);
            }
        }

        for (TipoProjetilInimigo tipo : values()) {
            DefinicaoProjetil def = carregador.getDefinicaoProjetil(tipo.name());
            tipo.definicao = def;

            try {
                SpriteSheetSource sourceEnum = SpriteSheetSource.valueOf(def.getSpritesheet());
                BufferedImage sheet = spritesheetsCarregados.get(sourceEnum);
                if (sheet != null) {
                    BufferedImage sprite = sheet.getSubimage(def.getX(), def.getY(), def.getW(), def.getH());
                    tipo.imagem = new ImageIcon(sprite);
                } else {
                    tipo.imagem = criarImagemPlaceholder(def.getW(), def.getH());
                }
            } catch (IllegalArgumentException e) {
                System.err.println(
                        "Spritesheet ID '" + def.getSpritesheet() + "' do JSON não existe no enum SpriteSheetSource.");
                tipo.imagem = criarImagemPlaceholder(def.getW(), def.getH());
            }
        }
    }

    /**
     * @brief Enum auxiliar para mapear os identificadores de spritesheet do JSON
     *        para os caminhos de arquivo.
     */
    private enum SpriteSheetSource {
        ESFERAS_8x8("Assets/projectiles/inimigos/esferas.png"),
        ESFERAS_GRANDES_12x12("Assets/projectiles/inimigos/esferas_grandes.png"),
        FORMAS_DIFERENTES("Assets/projectiles/inimigos/sprites_com_formas_diferentes.png");

        final String path;

        SpriteSheetSource(String path) {
            this.path = path;
        }
    }

    @Override
    public ImageIcon getImagem() {
        return imagem;
    }

    @Override
    public int getSpriteWidth() {
        return definicao.getW();
    }

    @Override
    public int getSpriteHeight() {
        return definicao.getH();
    }

    @Override
    public HitboxType getHitboxType() {
        return definicao.getHitbox().getType();
    }

    @Override
    public int getHitboxWidth() {
        return definicao.getHitbox().getW();
    }

    @Override
    public int getHitboxHeight() {
        return definicao.getHitbox().getH();
    }

    /**
     * @brief Cria uma imagem de placeholder magenta para casos em que o sprite não
     *        pode ser carregado.
     */
    private static ImageIcon criarImagemPlaceholder(int largura, int altura) {
        BufferedImage placeholder = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics g = placeholder.createGraphics();
        g.setColor(java.awt.Color.MAGENTA);
        g.fillRect(0, 0, largura, altura);
        g.dispose();
        return new ImageIcon(placeholder);
    }
}