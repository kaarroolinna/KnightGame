package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

public class GameScreen implements Screen {
    private final KnightGame game;
    private SpriteBatch batch;
    private Texture knightTexture;
    private Texture backgroundTexture;


    private float x, y;
    private float velocityY;
    private final float speed = 200f;
    private final float gravity = 1000f;
    private final float jumpVelocity = 500f;
    private float groundY;


    private Stage uiStage;
    private Skin skin;
    private Table menuTable;
    private boolean paused;

    public GameScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        knightTexture = new Texture(Gdx.files.internal("knight.png"));

        groundY = 0;
        velocityY = 0;
        x = Gdx.graphics.getWidth() / 2f - knightTexture.getWidth() / 2f;
        y = groundY;


        uiStage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        menuTable = new Table(skin);
        menuTable.setFillParent(true);
        menuTable.center();

        TextButton resumeBtn = new TextButton("Resume", skin);
        TextButton settingsBtn = new TextButton("Settings", skin);
        TextButton exitBtn = new TextButton("Exit to Menu", skin);

        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
                Gdx.input.setInputProcessor(null);
            }
        });
        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        menuTable.add(resumeBtn).pad(10).row();
        menuTable.add(settingsBtn).pad(10).row();
        menuTable.add(exitBtn).pad(10);
        uiStage.addActor(menuTable);

        paused = false;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(uiStage);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);

        if (!paused) {

            if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) x += speed * delta;
            if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                && y <= groundY + 0.1f) {
                velocityY = jumpVelocity;
            }

            velocityY -= gravity * delta;
            y += velocityY * delta;
            if (y < groundY) {
                y = groundY;
                velocityY = 0;
            }
        }


        batch.begin();
        batch.draw(backgroundTexture, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(knightTexture, x, y);
        batch.end();


        if (paused) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        knightTexture.dispose();
        backgroundTexture.dispose();
        uiStage.dispose();
        skin.dispose();
    }
}
