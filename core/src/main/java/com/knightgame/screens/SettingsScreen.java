package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

public class SettingsScreen implements Screen {
    private final KnightGame game;
    private final Screen returnScreen;

    private Stage stage;
    private Skin skin;
    private Texture grayTexture;
    private Slider volumeSlider;
    private Label percentLabel;
    private CheckBox muteCheckbox;
    private TextButton skipTutorialButton;
    private TextButton restartLevelButton;
    private TextButton saveButton;
    private TextButton backButton;

    private Preferences prefs;

    /** Викликається з MainMenuScreen */
    public SettingsScreen(KnightGame game) {
        this(game, new MainMenuScreen(game));
    }

    /** Загальний конструктор — можна вказати, куди повертатися */
    public SettingsScreen(KnightGame game, Screen returnScreen) {
        this.game = game;
        this.returnScreen = returnScreen;
    }

    @Override
    public void show() {
        // Ініціалізуємо Preferences, Stage і Skin
        prefs = Gdx.app.getPreferences("Settings");
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("uiskin.json"));

        // Підтягнемо збережені значення
        float savedVolume = prefs.getFloat("volume", 0.5f);
        boolean savedMute  = prefs.getBoolean("mute", false);

        // Фон
        grayTexture = new Texture(Gdx.files.internal("gray_background.png"));

        // Таблиця для UI
        Table table = new Table(skin);
        table.setFillParent(true);
        table.top().pad(20);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(grayTexture)));
        stage.addActor(table);

        // Заголовок
        table.add(new Label("Settings", skin))
            .colspan(2).padBottom(20).row();

        // Відсоток гучності над повзунком
        percentLabel = new Label((int)(savedVolume * 100) + "%", skin);
        table.add(percentLabel).colspan(2).padBottom(10).row();

        // Підпис "Гучність"
        table.add(new Label("Volume:", skin)).left().padRight(10);

        // Повзунок гучності
        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(savedVolume);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float v = volumeSlider.getValue();
                percentLabel.setText((int)(v * 100) + "%");
            }
        });
        table.add(volumeSlider).width(200).row();

        // Чекбокс «Вимкнути звук»
        muteCheckbox = new CheckBox("Turn off the sound", skin);
        muteCheckbox.setChecked(savedMute);
        table.add(muteCheckbox).colspan(2).padTop(10).row();

        // Пропустити туторіал
        skipTutorialButton = new TextButton("Skip Tutorial", skin);
        skipTutorialButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                prefs.putBoolean("skipTutorial", true);
                prefs.flush();
            }
        });
        table.add(skipTutorialButton).colspan(2).padTop(20).row();

        // Перезапустити рівень
        restartLevelButton = new TextButton("Restart Level", skin);
        restartLevelButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        table.add(restartLevelButton).colspan(2).padTop(10).row();

        // Кнопка збереження
        saveButton = new TextButton("Save Settings", skin);
        saveButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                prefs.putFloat("volume", volumeSlider.getValue());
                prefs.putBoolean("mute", muteCheckbox.isChecked());
                prefs.flush();
                game.setScreen(returnScreen);
            }
        });
        table.add(saveButton).colspan(2).padTop(20).row();

        // Кнопка повернення
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(returnScreen);
            }
        });
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
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   { dispose(); }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        grayTexture.dispose();
    }
}
