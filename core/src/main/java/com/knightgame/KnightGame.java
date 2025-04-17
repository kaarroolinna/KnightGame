package com.knightgame;

import com.badlogic.gdx.Game;
import com.knightgame.screens.MainMenuScreen;

public class KnightGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
