package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;

public class GameScreen implements Screen {

    private final KnightGame game;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    private static final int FRAME_COLS = 3;
    private Texture knightRightSheet, knightLeftSheet;
    private Animation<TextureRegion> knightRightAnim, knightLeftAnim;
    private float stateTime;
    private boolean facingRight = true;

    // Фізика
    private float x, y, velocityY;
    private final float speed = 230f, gravity = 1250f, jumpVelocity = 700f;
    private float groundY;

    // UI / HUD
    private Stage uiStage;
    private Skin skin;
    private Table  pauseMenu;
    private Window inventoryWindow, shopWindow;
    private boolean paused, inventoryOpen, shopOpen;

    private final int MAX_HP = 100, MAX_MANA = 100;
    private int currentHp = MAX_HP, currentMana = MAX_MANA, gold = 100;

    private ProgressBar hpBar, manaBar;
    private Label hpValueLabel, manaValueLabel, goldDisplayLabel;

    public GameScreen(KnightGame game) { this.game = game; }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));

        knightRightSheet = new Texture(Gdx.files.internal("knightAnimationRight.png"));
        knightLeftSheet  = new Texture(Gdx.files.internal("knightAnimationLeft.png"));
        knightRightAnim  = buildAnimation(knightRightSheet);
        knightLeftAnim   = buildAnimation(knightLeftSheet);
        stateTime = 0f;

        groundY = 0;
        velocityY = 0;
        x = Gdx.graphics.getWidth() / 2f - knightRightSheet.getWidth() / FRAME_COLS / 2f;
        y = groundY;

        uiStage = new Stage(new ScreenViewport());
        skin    = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(uiStage);

        createPauseMenu();
        createInventory();
        createShop();
        createHUD();
    }

    private Animation<TextureRegion> buildAnimation(Texture sheet) {
        TextureRegion[][] tmp = TextureRegion.split(
            sheet,
            sheet.getWidth()  / FRAME_COLS,
            sheet.getHeight() / 1);

        TextureRegion[] frames = new TextureRegion[FRAME_COLS];
        for (int i = 0; i < FRAME_COLS; i++) frames[i] = tmp[0][i];

        Animation<TextureRegion> anim = new Animation<>(1f, frames);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

//             UI-білдери
    /** Пауза */
    private void createPauseMenu() {
        pauseMenu = new Table(skin);
        pauseMenu.setFillParent(true);
        pauseMenu.center();
        pauseMenu.setVisible(false);

        TextButton resume = new TextButton("Resume", skin);
        resume.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                paused = false;
                inventoryOpen = shopOpen = false;
            }
        });

        TextButton settings = new TextButton("Settings", skin);
        settings.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });

        TextButton exit = new TextButton("Exit to Menu", skin);
        exit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        pauseMenu.add(resume).pad(10).row();
        pauseMenu.add(settings).pad(10).row();
        pauseMenu.add(exit).pad(10);
        uiStage.addActor(pauseMenu);
    }

    /** Інвентар */
    private void createInventory() {
        inventoryWindow = new Window("Inventory", skin);
        inventoryWindow.setSize(300, 300);
        inventoryWindow.setPosition(
            (Gdx.graphics.getWidth()  - 300) / 2f,
            (Gdx.graphics.getHeight() - 300) / 2f);
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

    /** Крамниця */
    private void createShop() {
        shopWindow = new Window("Shop", skin);
        shopWindow.setSize(350, 380);
        shopWindow.setPosition(
            (Gdx.graphics.getWidth()  - 350) / 2f,
            (Gdx.graphics.getHeight() - 380) / 2f);
        shopWindow.setVisible(false);

        Table content = new Table(skin);
        Label goldLabel = new Label("Gold: " + gold, skin);
        content.add(goldLabel).colspan(2).padBottom(10).row();

        addShopItem(content, goldLabel, "+20 HP",   25, () -> {
            currentHp = Math.min(currentHp + 20, MAX_HP);
            hpBar.setValue(currentHp);
            hpValueLabel.setText(currentHp + "/100");
        });
        addShopItem(content, goldLabel, "+20 Mana", 25, () -> {
            currentMana = Math.min(currentMana + 20, MAX_MANA);
            manaBar.setValue(currentMana);
            manaValueLabel.setText(currentMana + "/100");
        });
        // ... інші товари ...

        TextButton close = new TextButton("Close", skin);
        close.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                shopOpen = false;
                paused   = false;
            }
        });
        content.add(close).colspan(2).padTop(15);

        shopWindow.add(content).pad(10);
        uiStage.addActor(shopWindow);
    }

    private void addShopItem(Table table, Label goldLabel,
                             String name,int cost,Runnable purchase) {
        TextButton buyBtn = new TextButton("Buy (" + cost + ")", skin);
        buyBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e,float x,float y) {
                if (gold >= cost) {
                    gold -= cost;
                    goldLabel.setText("Gold: " + gold);
                    goldDisplayLabel.setText("Gold: " + gold);
                    purchase.run();
                }
            }
        });
        table.add(new Label(name, skin)).left().pad(5);
        table.add(buyBtn).right().pad(5).row();
    }

    /** HUD (HP, Mana, Gold) */
    private void createHUD() {
        hpBar   = new ProgressBar(1, MAX_HP,   1, false, skin);
        manaBar = new ProgressBar(1, MAX_MANA, 1, false, skin);
        hpBar.setValue(currentHp);
        manaBar.setValue(currentMana);

        hpValueLabel   = new Label(currentHp   + "/100", skin);
        manaValueLabel = new Label(currentMana + "/100", skin);
        goldDisplayLabel = new Label("Gold: " + gold, skin);

        Table hud = new Table(skin);
        hud.setFillParent(true);
        hud.top().left();

        hud.add(new Label("HP:", skin)).pad(2);
        hud.add(hpBar).width(150).height(20).pad(2);
        hud.add(hpValueLabel).pad(2).row();

        hud.add(new Label("Mana:", skin)).pad(2);
        hud.add(manaBar).width(150).height(20).pad(2);
        hud.add(manaValueLabel).pad(2).row();

        hud.add(goldDisplayLabel).colspan(3).pad(2).row();

        uiStage.addActor(hud);
    }

    //  Render
    @Override
    public void render(float delta) {
        /* гарячі клавіші UI */
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if      (shopOpen)      { shopOpen      = false; paused = false; }
            else if (inventoryOpen) { inventoryOpen = false; paused = false; }
            else                    { paused = !paused;
                if (paused) inventoryOpen = shopOpen = false; }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen; shopOpen = false; paused = inventoryOpen;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            shopOpen = true; inventoryOpen = false; paused = true;
        }

        /* рух */
        if (!paused) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= speed * delta; facingRight = false; }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += speed * delta; facingRight = true;  }
            if ((Gdx.input.isKeyJustPressed(Input.Keys.W)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                && y <= groundY + 0.1f) {
                velocityY = jumpVelocity;
            }

            velocityY -= gravity * delta;
            y += velocityY * delta;
            if (y < groundY) { y = groundY; velocityY = 0; }

            stateTime += delta;
        }

        /* малювання */
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextureRegion frame = facingRight
            ? knightRightAnim.getKeyFrame(stateTime)
            : knightLeftAnim .getKeyFrame(stateTime);
        batch.draw(frame, x, y);
        batch.end();

        pauseMenu     .setVisible(paused && !inventoryOpen && !shopOpen);
        inventoryWindow.setVisible(inventoryOpen);
        shopWindow     .setVisible(shopOpen);

        uiStage.act(delta);
        uiStage.draw();
    }

    //  lifecycle‑stub

    @Override public void resize(int w,int h) { uiStage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   { dispose(); }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        knightRightSheet.dispose();
        knightLeftSheet.dispose();
        uiStage.dispose();
        skin.dispose();
    }
}


