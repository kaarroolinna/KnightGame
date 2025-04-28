package com.knightgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.knightgame.screens.MainMenuScreen;

public class KnightGame extends Game {
    public Music menuMusic;
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
        playMenuMusic();
        setScreen(new com.knightgame.screens.MainMenuScreen(this));
    }

    public void playMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.dispose();
        }
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        menuMusic.setLooping(true);

        float volume = 0.5f;
        boolean mute = false;
        var prefs = Gdx.app.getPreferences("Settings");
        if (prefs.contains("volume")) {
            volume = prefs.getFloat("volume", 0.5f);
        }
        if (prefs.contains("mute")) {
            mute = prefs.getBoolean("mute", false);
        }

        menuMusic.setVolume(mute ? 0f : volume);
        menuMusic.play();
    }

    public void setMusicVolume(float volume) {
        if (menuMusic != null) {
            menuMusic.setVolume(volume);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (menuMusic != null) menuMusic.dispose();
    }
}
