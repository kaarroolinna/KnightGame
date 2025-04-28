package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

public class MainMenuScreen implements Screen {
    private final KnightGame game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture background;
    private Music menuMusic;

    private Texture titleTex;

    private Texture continueTex, newGameTex, settingsTex, exitTex;

    public MainMenuScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("background_menu.png"));

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5f);
        menuMusic.play();

        titleTex     = new Texture(Gdx.files.internal("title.png"));
        continueTex  = new Texture(Gdx.files.internal("button_continue.png"));
        newGameTex   = new Texture(Gdx.files.internal("button_new_game.png"));
        settingsTex  = new Texture(Gdx.files.internal("button_settings.png"));
        exitTex      = new Texture(Gdx.files.internal("button_exit.png"));

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table(skin);
        table.setFillParent(true);

        table.top().padTop(1f);
        table.defaults().space(1f);
        stage.addActor(table);

        Image titleImage = new Image(new TextureRegionDrawable(new TextureRegion(titleTex)));
        table.add(titleImage).padBottom(20f).row();

        ImageButton btnContinue = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(continueTex))
        );
        btnContinue.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
             //   game.loadGame();
            }
        });
        table.add(btnContinue).row();

        ImageButton btnNewGame = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(newGameTex))
        );
        btnNewGame.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        table.add(btnNewGame).row();

        ImageButton btnSettings = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(settingsTex))
        );
        btnSettings.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });
        table.add(btnSettings).row();

        ImageButton btnExit = new ImageButton(
            new TextureRegionDrawable(new TextureRegion(exitTex))
        );
        btnExit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(btnExit);
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(background, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        background.dispose();
        if (menuMusic != null) menuMusic.dispose();
        titleTex.dispose();
        continueTex.dispose();
        newGameTex.dispose();
        settingsTex.dispose();
        exitTex.dispose();
        stage.dispose();
    }
}
