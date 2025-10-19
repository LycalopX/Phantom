package Auxiliar;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class SoundManager {

    private static SoundManager instance;
    private final Map<String, Sound> sfxMap;
    private final Map<String, Music> musicMap;
    
    private float globalSfxVolume = 0.1f; // Drastically reduced for testing
    private float globalMusicVolume = 0.5f; // Default music volume

    private static final String[] SFX_FILES = {
        "se_bonus.wav", "se_bonus2.wav", "se_border.wav", "se_cancel00.wav", "se_cardget.wav",
        "se_cat00.wav", "se_damage00.wav", "se_damage01.wav", "se_enep00.wav", "se_enep01.wav",
        "se_extend.wav", "se_graze.wav", "se_gun00.wav", "se_invalid.wav", "se_item00.wav",
        "se_item01.wav", "se_kira00.wav", "se_kira01.wav", "se_kira02.wav", "se_lazer00.wav",
        "se_lazer01.wav", "se_nep00.wav", "se_ok00.wav", "se_ophide.wav", "se_opshow.wav",
        "se_option.wav", "se_pause.wav", "se_pldead00.wav", "se_plst00.wav", "se_power0.wav",
        "se_power1.wav", "se_powerup.wav", "se_select00.wav", "se_slash.wav", "se_tan00.wav",
        "se_tan01.wav", "se_tan02.wav", "se_timeout.wav", "se_timeout2.wav"
    };

    private static final String[] MUSIC_FILES = {
        "Illusionary Night Ghostly Eyes.wav"
    };

    private SoundManager() {
        this.sfxMap = new ConcurrentHashMap<>();
        this.musicMap = new ConcurrentHashMap<>();
    }

    public static void init() {
        if (instance == null) {
            TinySound.init();
            instance = new SoundManager();
            instance.loadSounds();
        }
    }

    public static void shutdown() {
        if (instance != null) {
            TinySound.shutdown();
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SoundManager has not been initialized. Call SoundManager.init() first.");
        }
        return instance;
    }

    private void loadSounds() {
        // Load SFX
        for (String sfxFileName : SFX_FILES) {
            String soundName = sfxFileName.substring(0, sfxFileName.lastIndexOf('.'));
            URL resourceUrl = SoundManager.class.getResource("/sounds/" + sfxFileName);
            if (resourceUrl == null) {
                System.err.println("SFX file not found: " + sfxFileName);
                continue;
            }
            Sound sound = TinySound.loadSound(resourceUrl);
            if (sound != null) {
                sfxMap.put(soundName, sound);
            }
        }

        // Load Music
        System.out.println("DEBUG: Loading music...");
        for (String musicFileName : MUSIC_FILES) {
            String musicName = musicFileName.substring(0, musicFileName.lastIndexOf('.'));
            URL resourceUrl = SoundManager.class.getResource("/sounds/Touhou Eiyashou - Imperishable Night/" + musicFileName);
            System.out.println("DEBUG: Attempting to load music: " + musicFileName);
            System.out.println("DEBUG: Resource URL: " + resourceUrl);
            if (resourceUrl == null) {
                System.err.println("Music file not found: " + musicFileName);
                continue;
            }
            Music music = TinySound.loadMusic(resourceUrl);
            if (music != null) {
                musicMap.put(musicName, music);
                System.out.println("DEBUG: Successfully loaded and mapped music: " + musicName);
            } else {
                System.err.println("DEBUG: Failed to load music object for: " + musicFileName);
            }
        }
    }

    public void playSfx(String name) {
        Sound sound = sfxMap.get(name);
        if (sound != null) {
            sound.play(globalSfxVolume);
        } else {
            System.err.println("SFX not found: " + name);
        }
    }

    public void playMusic(String name, boolean loop) {
        System.out.println("DEBUG: playMusic called for: " + name);
        Music music = musicMap.get(name);
        if (music != null) {
            System.out.println("DEBUG: Music found in map. Playing...");
            if (loop) {
                music.play(loop, globalMusicVolume);
            } else {
                music.play(false, globalMusicVolume);
            }
        } else {
            System.err.println("Music not found in map: " + name);
        }
    }

    public void stopAllMusic() {
        for (Music music : musicMap.values()) {
            music.stop();
        }
    }

    public void setSfxVolume(float volume) {
        if (volume < 0.0f) volume = 0.0f;
        if (volume > 1.0f) volume = 1.0f;
        this.globalSfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        if (volume < 0.0f) volume = 0.0f;
        if (volume > 1.0f) volume = 1.0f;
        this.globalMusicVolume = volume;

        // Adjust the volume of currently playing music
        for (Music music : musicMap.values()) {
            if (music.playing()) {
                music.setVolume(volume);
            }
        }
    }
}