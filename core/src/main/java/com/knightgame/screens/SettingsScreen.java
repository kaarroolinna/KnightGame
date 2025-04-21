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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

public class SettingsScreen implements Screen {
    private final KnightGame game;
    private Stage stage;
    private Skin skin;
    private Texture grayTexture;

    private Slider volumeSlider;
    private CheckBox muteCheckbox;
    private TextButton skipTutorialButton;
    private TextButton restartLevelButton;
    private TextButton saveButton;
    private TextButton backButton;

    public SettingsScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Створюємо сцену і обробник вводу
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Завантажуємо скіни
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Створюємо таблицю для розмітки
        Table table = new Table(skin);
        table.setFillParent(true);
        table.top();
        stage.addActor(table);

        // Додаємо сірий фон
        grayTexture = new Texture(Gdx.files.internal("gray_background.png"));
        TextureRegionDrawable bg = new TextureRegionDrawable(new TextureRegion(grayTexture));
        table.setBackground(bg);

        // Volume Slider
        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // тут можна оновити налаштування звуку
                // game.getSettings().setVolume(volumeSlider.getValue());
            }
        });

        // Mute Checkbox
        muteCheckbox = new CheckBox("Mute Sound", skin);
        muteCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // game.getSettings().setMuted(muteCheckbox.isChecked());
            }
        });

        // Skip Tutorial Button
        skipTutorialButton = new TextButton("Skip Tutorial", skin);
        skipTutorialButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.getSettings().setTutorialSkipped(true);
            }
        });

        // Restart Level Button
        restartLevelButton = new TextButton("Restart Level", skin);
        restartLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.restartLevel();
            }
        });

        // Save Settings Button
        saveButton = new TextButton("Save Settings", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });

        // Back Button
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Розміщуємо всі елементи в таблиці
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
        // Очищуємо екран перед малюванням
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        grayTexture.dispose();
    }

    // Метод для збереження налаштувань гри
    private void saveSettings() {
        float volume = volumeSlider.getValue();
        boolean isMuted = muteCheckbox.isChecked();
        // Приклад: зберігаємо в налаштуваннях гри або Preferences
        // game.getSettings().setVolume(volume);
        // game.getSettings().setMuted(isMuted);
        System.out.println("Settings saved: Volume = " + volume + ", Mute = " + isMuted);
    }
}
