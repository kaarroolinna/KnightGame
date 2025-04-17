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
    private Slider volumeSlider;
    private CheckBox muteCheckbox;
    private TextButton skipTutorialButton;
    private TextButton restartLevelButton;
    private TextButton backButton;
    private TextButton saveButton; // Кнопка збереження налаштувань

    public SettingsScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table(skin);
        table.setFillParent(true);
        table.top();  // Встановлюємо верхнє вирівнювання
        stage.addActor(table);

        // Додаємо сірий фон
        Texture grayTexture = new Texture(Gdx.files.internal("gray_background.png")); // Якщо є файл gray_background.png
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(grayTexture));
        table.setBackground(drawable);  // Встановлюємо фон для таблиці

        // Volume Slider
        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        // volumeSlider.setValue(game.getSettings().getVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                // game.getSettings().setVolume(volumeSlider.getValue());
            }
        });

        // Mute Checkbox
        muteCheckbox = new CheckBox("Mute Sound", skin);
        // muteCheckbox.setChecked(game.getSettings().isMuted());
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

        // Save Button to save the settings
        saveButton = new TextButton("Save Settings", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });

        // Back Button to Main Menu
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Переходимо до головного меню
                game.setScreen(new MainMenuScreen(game));

            }
        });

        // Layout
        table.pad(20);
        table.add("Settings").colspan(2).padBottom(20).row();
        table.add("Volume");
        table.add(volumeSlider).width(200).row();
        table.add(muteCheckbox).colspan(2).padTop(10).row();
        table.add(skipTutorialButton).colspan(2).padTop(20).row();
        table.add(restartLevelButton).colspan(2).padTop(10).row();
        table.add(saveButton).colspan(2).padTop(20).row();  // Додаємо кнопку збереження
        table.add(backButton).colspan(2).padTop(30);
    }

    // Метод для збереження налаштувань
    private void saveSettings() {
        // Ваш код для збереження налаштувань гри
        // Наприклад, збереження значень volumeSlider та muteCheckbox
        float volume = volumeSlider.getValue();
        boolean isMuted = muteCheckbox.isChecked();

        // Приклад збереження налаштувань:
        // game.getSettings().setVolume(volume);
        // game.getSettings().setMuted(isMuted);

        // Можна також зберігати налаштування у файл або через Preferences
        System.out.println("Settings saved: Volume = " + volume + ", Mute = " + isMuted);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  // Очищуємо екран
        stage.act(delta);  // Оновлюємо сцени
        stage.draw();  // Малюємо сцени
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);  // Оновлюємо розміри сцени
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
        stage.dispose();  // Очищаємо сцену
        skin.dispose();   // Очищаємо шкіру
    }
}
