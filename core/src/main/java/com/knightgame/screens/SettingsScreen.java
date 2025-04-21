package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

/**
 * Екран налаштувань з підтримкою повернення на вказаний екран.
 */
public class SettingsScreen implements Screen {
    private final KnightGame game;
    private final Screen returnScreen;
    private Stage stage;
    private Skin skin;
    private Texture grayTexture;
    private Slider volumeSlider;
    private CheckBox muteCheckbox;
    private TextButton skipTutorialButton;
    private TextButton restartLevelButton;
    private TextButton saveButton;
    private TextButton backButton;

    /**
     * Використовується при вході з головного меню: повернення веде в MainMenuScreen.
     */
    public SettingsScreen(KnightGame game) {
        this(game, new MainMenuScreen(game));
    }

    /**
     * Загальний конструктор, де можна вказати, куди повернутись натисканням "Back".
     */
    public SettingsScreen(KnightGame game, Screen returnScreen) {
        this.game = game;
        this.returnScreen = returnScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table(skin);
        table.setFillParent(true);
        table.top();
        stage.addActor(table);

        // Фон
        grayTexture = new Texture(Gdx.files.internal("gray_background.png"));
        table.setBackground(new TextureRegionDrawable(new TextureRegion(grayTexture)));

        // Елементи налаштувань
        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        muteCheckbox = new CheckBox("Mute Sound", skin);
        skipTutorialButton = new TextButton("Skip Tutorial", skin);
        restartLevelButton = new TextButton("Restart Level", skin);

        saveButton = new TextButton("Save Settings", skin);
        saveButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                // логіка збереження...
            }
        });

        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(returnScreen);
            }
        });

        // Розміщення в таблиці
        table.pad(20);
        table.add("Settings").colspan(2).padBottom(20).row();
        table.add("Volume").left();
        table.add(volumeSlider).width(200).row();
        table.add(muteCheckbox).colspan(2).padTop(10).row();
        table.add(skipTutorialButton).colspan(2).padTop(20).row();
        table.add(restartLevelButton).colspan(2).padTop(10).row();
        table.add(saveButton).colspan(2).padTop(20).row();
        table.add(backButton).colspan(2).padTop(30);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        grayTexture.dispose();
    }
}
