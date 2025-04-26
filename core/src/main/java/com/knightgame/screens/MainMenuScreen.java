package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

public class MainMenuScreen implements Screen {
    private final KnightGame game;
    private Stage        stage;
    private Skin         skin;
    private Table        table;
    private Texture      background;
    private SpriteBatch  batch;
    private Music        menuMusic;
    private Preferences  prefs;

    public MainMenuScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 1. Preferences
        prefs = Gdx.app.getPreferences("Settings");
        float savedVolume = prefs.getFloat("volume", 0.5f);
        boolean savedMute = prefs.getBoolean("mute", false);

        // 2. Load and play music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(savedMute ? 0f : savedVolume);
        menuMusic.play();

        // 3. Load background and batch
        batch      = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background_menu.png"));

        // 4. Set up Stage & Skin
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("uiskin.json"));

        // 5. Build UI table
        table = new Table(skin);
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        // 6. Buttons
        TextButton btnStart = new TextButton("Continue", skin);
        btnStart.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                // Якщо є механізм збереження, можна викликати game.loadGame();
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton btnNewGame = new TextButton("New Game", skin);
        btnNewGame.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton btnSettings = new TextButton("Settings", skin);
        btnSettings.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        TextButton btnExit = new TextButton("Exit", skin);
        btnExit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // 7. Add to table with padding
        table.add(btnStart).width(200).pad(10).row();
        table.add(btnNewGame).width(200).pad(10).row();
        table.add(btnSettings).width(200).pad(10).row();
        table.add(btnExit).width(200).pad(10);
    }

    @Override
    public void render(float delta) {
        // clear screen
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // draw background
        batch.begin();
        batch.draw(background,
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight());
        batch.end();

        // update and draw UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        // stop music when leaving screen
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
    }

    @Override
    public void dispose() {
        // dispose all resources
        if (menuMusic != null) menuMusic.dispose();
        if (background != null) background.dispose();
        if (batch      != null) batch.dispose();
        if (stage      != null) stage.dispose();
        if (skin       != null) skin.dispose();
    }
}
