package com.knightgame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.knightgame.KnightGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("My Knight Game");
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        config.setDecorated(false);
        config.setResizable(false);
        new Lwjgl3Application(new KnightGame(), config);
    }
}
