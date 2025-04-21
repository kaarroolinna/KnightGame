package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
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
    private Window inventoryWindow;
    private boolean paused;
    private boolean inventoryOpen;

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
        Gdx.input.setInputProcessor(uiStage);

        // Pause menu
        menuTable = new Table(skin);
        menuTable.setFillParent(true);
        menuTable.center();
        menuTable.setVisible(false);
        TextButton resumeBtn = new TextButton("Resume", skin);
        TextButton settingsBtn = new TextButton("Settings", skin);
        TextButton exitBtn = new TextButton("Exit to Menu", skin);
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
                inventoryOpen = false;
            }
        });

        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        menuTable.add(resumeBtn).pad(10).row();
        menuTable.add(settingsBtn).pad(10).row();
        menuTable.add(exitBtn).pad(10);
        uiStage.addActor(menuTable);

        // Inventory window
        inventoryWindow = new Window("Inventory", skin);
        inventoryWindow.setSize(300, 300);
        inventoryWindow.setPosition(
            (Gdx.graphics.getWidth() - 300) / 2f,
            (Gdx.graphics.getHeight() - 300) / 2f
        );
        // Create 4x4 grid of slots
        Table invTable = new Table(skin);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                TextButton slot = new TextButton("", skin);
                slot.setDisabled(true);
                slot.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
                invTable.add(slot).size(60).pad(5);
            }
            invTable.row();
        }
        inventoryWindow.add(invTable);
        inventoryWindow.setVisible(false);
        uiStage.addActor(inventoryWindow);

        paused = false;
        inventoryOpen = false;
    }

    @Override
    public void render(float delta) {
        // pause menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) inventoryOpen = false;
        }
        // inventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;

            paused = inventoryOpen;
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
            if (y < groundY) { y = groundY; velocityY = 0; }
        }

        batch.begin();
        batch.draw(backgroundTexture, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(knightTexture, x, y);
        batch.end();

        uiStage.act(delta);
        menuTable.setVisible(paused && !inventoryOpen);
        inventoryWindow.setVisible(inventoryOpen);
        uiStage.act(delta);
        uiStage.draw();
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
