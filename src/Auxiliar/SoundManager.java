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

    private float GLOBAL_VOLUME = 0.3f;

    private float globalSfxVolume = (0.05f * GLOBAL_VOLUME); // Drastically reduced for testing
    private float globalMusicVolume = (0.5f * GLOBAL_VOLUME); // Default music volume

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
            "Cinderella Cage ~ Kagome-Kagome.mp3",
            "Deaf to All but the Song.mp3",
            "Eastern Youkai Beauty.mp3",
            "Eternal Dream ~ Mystical Maple.mp3",
            "Eternal Night Vignette ~ Eastern Night.mp3",
            "Evening Primrose.mp3",
            "Extend Ash ~ Person of Hourai.mp3",
            "Flight of the Bamboo Cutter ~ Lunatic Princess.mp3",
            "Gensokyo Millennium ~ History of the Moon.mp3",
            "Illusionary Night ~ Ghostly Eyes.mp3",
            "Love-Colored Master Spark.mp3",
            "Lunatic Eyes ~ Invisible Full Moon.mp3",
            "Maiden's Capriccio ~ Dream Battle.mp3",
            "Nostalgic Blood of the East ~ Old World.mp3",
            "Plain Asia.mp3",
            "Reach for the Moon, Immortal Smoke.mp3",
            "Retribution for the Eternal Night ~ Imperishable Night.mp3",
            "Song of the Night Sparrow ~ Night Bird.mp3",
            "Voyage 1969.mp3",
            "Voyage 1970.mp3",
            "Wriggling Autumn Moon ~ Mooned Insect.mp3"
    };

    private SoundManager() {
        this.sfxMap = new ConcurrentHashMap<>();
        this.musicMap = new ConcurrentHashMap<>();
    }

    public static void init() {
        if (instance == null) {
            TinySound.init();
            System.out.println("TinySound initialized: " + TinySound.isInitialized());
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
        for (String musicFileName : MUSIC_FILES) {
            String musicName = musicFileName.substring(0, musicFileName.lastIndexOf('.'));

            // NO MORE "if (.mp3)" CHECK
            URL resourceUrl = SoundManager.class
                    .getResource("/sounds/Touhou Eiyashou - Imperishable Night/" + musicFileName);

            if (resourceUrl == null) {
                //System.err.println("Music file not found: " + musicFileName);
                continue;
            }

            // This line will NOW work for .mp3 files thanks to the MP3SPI plugin
            Music music = TinySound.loadMusic(resourceUrl);

            if (music != null) {
                musicMap.put(musicName, music); // <-- SUCCESS! All music is in the main map.
            } else {
                System.err.println("Failed to load music object for: " + musicFileName);
            }
        }
    }

    public void playSfx(String name, double volume) {
        Sound sound = sfxMap.get(name);
        if (sound != null) {
            sound.play(globalSfxVolume * volume);
        } else {
            System.err.println("SFX not found: " + name);
        }
    }

    public void playMusic(String name, boolean loop) {
        stopAllMusic();

        if (musicMap.containsKey(name)) {
            Music music = musicMap.get(name);
            System.out.println("Playing music: " + name + " | Music object: " + music);
            // This now works for your MP3s
            music.play(loop, globalMusicVolume);
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
        if (volume < 0.0f)
            volume = 0.0f;
        if (volume > 1.0f)
            volume = 1.0f;
        this.globalSfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        if (volume < 0.0f)
            volume = 0.0f;
        if (volume > 1.0f)
            volume = 1.0f;
        this.globalMusicVolume = volume;

        // Adjust the volume of currently playing music
        for (Music music : musicMap.values()) {
            if (music.playing()) {
                music.setVolume(volume);
            }
        }
    }
}
