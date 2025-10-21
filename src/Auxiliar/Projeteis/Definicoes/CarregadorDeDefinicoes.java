package Auxiliar.Projeteis.Definicoes;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import Auxiliar.Projeteis.HitboxType;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @brief Classe Singleton responsável por carregar e fornecer as definições de projéteis
 *        a partir de um arquivo JSON.
 */
public class CarregadorDeDefinicoes {

    private static CarregadorDeDefinicoes instance;

    private final Map<String, String> spritesheets;
    private final Map<String, DefinicaoProjetil> projeteis;

    /**
     * @brief Construtor privado que inicializa os mapas e chama o método de carregamento.
     */
    private CarregadorDeDefinicoes() {
        spritesheets = new HashMap<>();
        projeteis = new HashMap<>();
        carregar();
    }

    /**
     * @brief Retorna a instância única (Singleton) do carregador de definições.
     * @return A instância de CarregadorDeDefinicoes.
     */
    public static CarregadorDeDefinicoes getInstance() {
        if (instance == null) {
            instance = new CarregadorDeDefinicoes();
        }
        return instance;
    }

    /**
     * @brief Carrega as definições de spritesheets e projéteis do arquivo JSON
     *        'definicoes_projeteis.json' e as armazena nos mapas internos.
     */
    private void carregar() {
        try {
            InputStream is = CarregadorDeDefinicoes.class.getResourceAsStream("/recursos/definicoes_projeteis.json");
            if (is == null) {
                throw new RuntimeException("Arquivo de definições de projéteis não encontrado.");
            }

            JSONTokener tokener = new JSONTokener(is);
            JSONObject root = new JSONObject(tokener);

            JSONObject spritesheetsJson = root.getJSONObject("spritesheets");
            for (String key : spritesheetsJson.keySet()) {
                spritesheets.put(key, spritesheetsJson.getString(key));
            }

            JSONArray projeteisJson = root.getJSONArray("projeteis");
            for (int i = 0; i < projeteisJson.length(); i++) {
                JSONObject projJson = projeteisJson.getJSONObject(i);

                String id = projJson.getString("id");
                String spritesheet = projJson.getString("spritesheet");
                int x = projJson.getInt("x");
                int y = projJson.getInt("y");
                int w = projJson.getInt("w");
                int h = projJson.getInt("h");

                JSONObject hitboxJson = projJson.getJSONObject("hitbox");
                HitboxType hitboxType = HitboxType.valueOf(hitboxJson.getString("type"));
                int hitboxW = hitboxJson.getInt("w");
                int hitboxH = hitboxJson.getInt("h");

                DefinicaoProjetil.HitboxDef hitboxDef = new DefinicaoProjetil.HitboxDef(hitboxType, hitboxW, hitboxH);
                DefinicaoProjetil definicao = new DefinicaoProjetil(id, spritesheet, x, y, w, h, hitboxDef);

                projeteis.put(id, definicao);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao carregar definições de projéteis.", e);
        }
    }

    /**
     * @brief Obtém a definição completa de um projétil com base em seu ID.
     * @param id O identificador único do projétil.
     * @return O objeto DefinicaoProjetil correspondente.
     */
    public DefinicaoProjetil getDefinicaoProjetil(String id) {
        DefinicaoProjetil def = projeteis.get(id);
        if (def == null) {
            throw new IllegalArgumentException("Nenhuma definição de projétil encontrada para o id: " + id);
        }
        return def;
    }

    /**
     * @brief Obtém o caminho do arquivo de imagem para um spritesheet com base em seu ID.
     * @param id O identificador único do spritesheet.
     * @return O caminho para o arquivo de imagem do spritesheet.
     */
    public String getSpritesheetPath(String id) {
        String path = spritesheets.get(id);
        if (path == null) {
            throw new IllegalArgumentException("Nenhum spritesheet encontrado para o id: " + id);
        }
        return path;
    }
}
