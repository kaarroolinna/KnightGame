package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

    private float x, y, velocityY;
    private final float speed = 200f, gravity = 1000f, jumpVelocity = 500f;
    private float groundY;

    private Stage uiStage;
    private Skin skin;
    private Table pauseMenu;
    private Window inventoryWindow;
    private Window shopWindow;
    private boolean paused, inventoryOpen, shopOpen;

    private int gold = 100;
    private int maxHp = 100, maxMana = 50;

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

        createPauseMenu();
        createInventory();
        createShop();
    }


    private void createPauseMenu() {
        pauseMenu = new Table(skin);
        pauseMenu.setFillParent(true);
        pauseMenu.center();
        pauseMenu.setVisible(false);

        TextButton resume = new TextButton("Resume", skin);
        resume.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
                inventoryOpen = shopOpen = false;
            }
        });

        TextButton settings = new TextButton("Settings", skin);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });

        TextButton exit = new TextButton("Exit to Menu", skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        pauseMenu.add(resume).pad(10).row();
        pauseMenu.add(settings).pad(10).row();
        pauseMenu.add(exit).pad(10);

        uiStage.addActor(pauseMenu);
    }

    private void createInventory() {
        inventoryWindow = new Window("Inventory", skin);
        inventoryWindow.setSize(300, 300);
        inventoryWindow.setPosition(
            (Gdx.graphics.getWidth() - 300) / 2f,
            (Gdx.graphics.getHeight() - 300) / 2f
        );
        inventoryWindow.setVisible(false);

        Table grid = new Table(skin);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                TextButton slot = new TextButton("", skin);
                slot.setDisabled(true);
                slot.setTouchable(Touchable.disabled);
                grid.add(slot).size(60).pad(5);
            }
            grid.row();
        }
        inventoryWindow.add(grid);

        uiStage.addActor(inventoryWindow);
    }

    private void createShop() {
        shopWindow = new Window("Shop", skin);
        shopWindow.setSize(350, 380);
        shopWindow.setPosition(
            (Gdx.graphics.getWidth() - 350) / 2f,
            (Gdx.graphics.getHeight() - 380) / 2f
        );
        shopWindow.setVisible(false);

        Table content = new Table(skin);
        Label goldLabel = new Label("Gold: " + gold, skin);
        content.add(goldLabel).colspan(2).padBottom(10).row();

        addShopItem(content, goldLabel, "+20 Max HP", 25, () -> maxHp += 20);
        addShopItem(content, goldLabel, "+20 Max Mana", 25, () -> maxMana += 20);
        addShopItem(content, goldLabel, "New Sword", 50, () -> { /* give sword */ });
        addShopItem(content, goldLabel, "Potion: Speed", 15, () -> { /* add speed pot */ });
        addShopItem(content, goldLabel, "Potion: Heal", 15, () -> { /* add heal pot */ });
        addShopItem(content, goldLabel, "Potion: Strength", 20, () -> { /* add dmg pot */ });

        TextButton close = new TextButton("Close", skin);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shopOpen = false;
                paused = false;
            }
        });
        content.add(close).colspan(2).padTop(15);

        shopWindow.add(content).pad(10);
        uiStage.addActor(shopWindow);
    }

    private void addShopItem(Table table, Label goldLabel, String name, int cost, Runnable purchase) {
        TextButton buyBtn = new TextButton("Buy (" + cost + ")", skin);
        buyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (gold >= cost) {
                    gold -= cost;
                    goldLabel.setText("Gold: " + gold);
                    purchase.run();
                }
            }
        });
        table.add(new Label(name, skin)).left().pad(5);
        table.add(buyBtn).right().pad(5).row();
    }


    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (shopOpen) {
                shopOpen = false;
                paused = false;
            } else if (inventoryOpen) {
                inventoryOpen = false;
                paused = false;
            } else {
                paused = !paused;
                if (paused) {
                    inventoryOpen = shopOpen = false;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;
            shopOpen = false;
            paused = inventoryOpen;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            shopOpen = true;
            inventoryOpen = false;
            paused = true;
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
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(knightTexture, x, y);
        batch.end();

        pauseMenu.setVisible(paused && !inventoryOpen && !shopOpen);
        inventoryWindow.setVisible(inventoryOpen);
        shopWindow.setVisible(shopOpen);

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
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
