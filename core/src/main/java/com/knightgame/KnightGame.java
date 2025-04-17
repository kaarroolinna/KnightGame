package com.knightgame;

import com.badlogic.gdx.Game;
import com.knightgame.screens.MainMenuScreen;

public class KnightGame extends Game {
    @Override
    public void create() {
        // Встановлюємо екран головного меню
        setScreen(new MainMenuScreen(this));
    }
}
