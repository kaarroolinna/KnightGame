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

    // Позиції та швидкості
    private float x, y;
    private float velocityY = 0;
    private final float speed = 200f;          // горизонтальна швидкість
    private final float gravity = 1000f;       // прискорення вільного падіння
    private final float jumpVelocity = 500f;   // початкова швидкість стрибка
    private float groundY;                     // рівень "землі" по вісі Y

    public GameScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("background.png");
        knightTexture = new Texture("knight.png");

        // Земля
        groundY = 0;

        // Початкова позиція лицаря
        x = Gdx.graphics.getWidth() / 2f - knightTexture.getWidth() / 2f;
        y = groundY;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
        }

        // Стрибок: тільки коли на землі
        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            && y <= groundY + 0.1f) {
            velocityY = jumpVelocity;
        }

        // Фізика гравітації
        velocityY -= gravity * delta;    // прискорення вниз
        y += velocityY * delta;          // зміна висоти

        // Обмежуємо знизу землею
        if (y < groundY) {
            y = groundY;
            velocityY = 0;
        }

        // Малювання
        batch.begin();

        batch.draw(backgroundTexture, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.draw(knightTexture, x, y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

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
