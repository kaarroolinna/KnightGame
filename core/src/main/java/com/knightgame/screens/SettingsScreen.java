package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
    private Texture backgroundTexture;

    private Slider volumeSlider;
    private Label percentLabel;
    private CheckBox muteCheckbox;

    private ImageButton restartButton;
    private ImageButton saveButton;
    private ImageButton backButton;

    private Preferences prefs;

    public SettingsScreen(KnightGame game) {
        this(game, new MainMenuScreen(game));
    }

    public SettingsScreen(KnightGame game, Screen returnScreen) {
        this.game = game;
        this.returnScreen = returnScreen;
    }

    @Override
    public void show() {
        prefs = Gdx.app.getPreferences("Settings");
        float savedVolume = prefs.getFloat("volume", 0.5f);
        boolean savedMute = prefs.getBoolean("mute", false);

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/custom_font.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = 32;
        param.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        BitmapFont customFont = generator.generateFont(param);
        generator.dispose();
        LabelStyle labelStyle = new LabelStyle(customFont, Color.WHITE);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("settings_background.png"));

        if (savedMute) {
            game.setMusicVolume(0f);
        } else {
            game.setMusicVolume(savedVolume);
        }

        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.setValue(savedVolume);

        percentLabel = new Label((int)(savedVolume * 100) + "%", labelStyle);

        muteCheckbox = new CheckBox("Mute Sound", skin);
        muteCheckbox.setChecked(savedMute);

        Texture restartTex = new Texture(Gdx.files.internal("button_restart.png"));
        restartButton = new ImageButton(new TextureRegionDrawable(
            new TextureRegion(restartTex)));
        restartButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        Texture saveTex = new Texture(Gdx.files.internal("button_save.png"));
        saveButton = new ImageButton(new TextureRegionDrawable(
            new TextureRegion(saveTex)));
        saveButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                prefs.putFloat("volume", volumeSlider.getValue());
                prefs.putBoolean("mute", muteCheckbox.isChecked());
                prefs.flush();

                if (muteCheckbox.isChecked()) {
                    game.setMusicVolume(0f);
                } else {
                    game.setMusicVolume(volumeSlider.getValue());
                }
                game.setScreen(returnScreen);
            }
        });

        Texture backTex = new Texture(Gdx.files.internal("button_back.png"));
        backButton = new ImageButton(new TextureRegionDrawable(
            new TextureRegion(backTex)));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(returnScreen);
            }
        });

        volumeSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                float v = volumeSlider.getValue();
                percentLabel.setText((int)(v * 100) + "%");
                if (!muteCheckbox.isChecked()) {
                    game.setMusicVolume(v);
                }
            }
        });
        muteCheckbox.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (muteCheckbox.isChecked()) {
                    game.setMusicVolume(0f);
                } else {
                    game.setMusicVolume(volumeSlider.getValue());
                }
            }
        });

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        table.setBackground(new TextureRegionDrawable(
            new TextureRegion(backgroundTexture)));

        table.add(new Label("Settings", labelStyle))
            .colspan(1).padBottom(20).row();
        table.add(percentLabel).row();
        table.add(volumeSlider).width(300).row();
        table.add(muteCheckbox).row();
        table.add(restartButton).row();
        table.add(saveButton).row();
        table.add(backButton).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
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
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }
}
