
package Auxiliar.Projeteis.Definicoes;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import Auxiliar.Projeteis.HitboxType;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CarregadorDeDefinicoes {

    private static CarregadorDeDefinicoes instance;

    private final Map<String, String> spritesheets;
    private final Map<String, DefinicaoProjetil> projeteis;

    private CarregadorDeDefinicoes() {
        spritesheets = new HashMap<>();
        projeteis = new HashMap<>();
        carregar();
    }

    public static CarregadorDeDefinicoes getInstance() {
        if (instance == null) {
            instance = new CarregadorDeDefinicoes();
        }
        return instance;
    }

    private void carregar() {
        try {
            InputStream is = CarregadorDeDefinicoes.class.getResourceAsStream("/recursos/definicoes_projeteis.json");
            if (is == null) {
                throw new RuntimeException("Arquivo de definições de projéteis não encontrado.");
            }

            JSONTokener tokener = new JSONTokener(is);
            JSONObject root = new JSONObject(tokener);

            // Carregar Spritesheets
            JSONObject spritesheetsJson = root.getJSONObject("spritesheets");
            for (String key : spritesheetsJson.keySet()) {
                spritesheets.put(key, spritesheetsJson.getString(key));
            }

            // Carregar Projéteis
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

    public DefinicaoProjetil getDefinicaoProjetil(String id) {
        DefinicaoProjetil def = projeteis.get(id);
        if (def == null) {
            throw new IllegalArgumentException("Nenhuma definição de projétil encontrada para o id: " + id);
        }
        return def;
    }

    public String getSpritesheetPath(String id) {
        String path = spritesheets.get(id);
        if (path == null) {
            throw new IllegalArgumentException("Nenhum spritesheet encontrado para o id: " + id);
        }
        return path;
    }
}
