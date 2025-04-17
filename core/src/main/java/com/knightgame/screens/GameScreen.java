package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.knightgame.KnightGame;

public class GameScreen implements Screen {
    private final KnightGame game;
    private SpriteBatch batch;
    private Texture knightTexture;
    private Texture backgroundTexture;
    private float x, y;
    private float speed = 200f; // pixels per second

    public GameScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("background.png"); // додай фон в assets
        knightTexture = new Texture("knight.png");
        // Початкова позиція
        x = Gdx.graphics.getWidth() / 2f - knightTexture.getWidth() / 2f;
        y = Gdx.graphics.getHeight() / 2f - knightTexture.getHeight() / 2f;
    }

    @Override
    public void render(float delta) {
        // Очищення екрану
        ScreenUtils.clear(0, 0, 0, 1);

        // Обробка вводу для руху
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
        }

        // Малювання
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(knightTexture, x, y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Можна оновити камеру або viewport
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        knightTexture.dispose();
        backgroundTexture.dispose();
    }
}
