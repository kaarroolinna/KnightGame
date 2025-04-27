package com.knightgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.knightgame.KnightGame;
import com.knightgame.ui.DialogManager;

public class GameScreen implements Screen {
    private final KnightGame game;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    private static final int ATTACK_FRAMES = 6;
    private Texture attackRightSheet, attackLeftSheet;
    private Animation<TextureRegion> attackRightAnim, attackLeftAnim;
    private boolean attacking;
    private float attackTime;

    private static final int WALK_FRAMES = 3;
    private Texture knightRightSheet, knightLeftSheet;
    private Animation<TextureRegion> knightRightAnim, knightLeftAnim;
    private float stateTime;
    private boolean facingRight;

    private Texture coinSheet;
    private Animation<TextureRegion> coinAnim;
    private float coinTime;
    private Image coinImage;

    private float x, y, velocityY;
    private final float speed = 230f, gravity = 1250f, jumpVelocity = 700f;
    private float groundY;

    private Stage uiStage;
    private Skin skin;
    private Table pauseMenu;
    private Window inventoryWindow, shopWindow;
    private boolean paused, inventoryOpen, shopOpen;

    private final int MAX_HP = 100, MAX_MANA = 100;
    private int currentHp = MAX_HP, currentMana = MAX_MANA, gold = 100;
    private ProgressBar hpBar, manaBar;
    private Label hpValueLabel, manaValueLabel, goldDisplayLabel;

    private DialogManager dialog;

    private Texture monsterTex;
    private float   monsterX, monsterY;
    private boolean monsterAlive;
    private int     monsterHp;
    private final float monsterSpeed = 250f;

    private final float attackRange = 50f;
    private float monsterDamageCooldown = 0f;
    private static final float DAMAGE_COOLDOWN_TIME = 1f;

    public GameScreen(KnightGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("background.png");

        knightRightSheet = new Texture("knightAnimationRight.png");
        knightLeftSheet  = new Texture("knightAnimationLeft.png");
        knightRightAnim  = splitAnimation(knightRightSheet, WALK_FRAMES, 0.2f, true);
        knightLeftAnim   = splitAnimation(knightLeftSheet,  WALK_FRAMES, 0.2f, true);
        stateTime = 0f;
        facingRight = true;

        attackRightSheet = new Texture("Attack1 R.png");
        attackLeftSheet  = new Texture("Attack1 L.png");
        attackRightAnim  = splitAnimation(attackRightSheet, ATTACK_FRAMES, 0.1f, false);
        attackLeftAnim   = splitAnimation(attackLeftSheet,  ATTACK_FRAMES, 0.1f, false);
        attacking = false;
        attackTime = 0f;

        coinSheet = new Texture("coin.png");
        int coinCols = coinSheet.getWidth() / coinSheet.getHeight();
        coinAnim = splitAnimation(coinSheet, coinCols, 0.15f, true);
        coinTime = 0f;
        coinImage = new Image(new TextureRegionDrawable(coinAnim.getKeyFrame(0)));
        coinImage.setSize(24,24);

        groundY = 0;
        velocityY = 0;
        x = Gdx.graphics.getWidth()/2f - knightRightSheet.getWidth()/WALK_FRAMES/2f;
        y = groundY;

        uiStage = new Stage(new ScreenViewport());
        skin    = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(uiStage);

        dialog = new DialogManager(skin, uiStage);
        dialog.showSequence(
            () -> spawnMonster(),
            "Hello, knight!",
            "Let's start with the basics",
            "Jump - Space",
            "Inventory - I",
            "Shop - Z",
            "Attack - E",
            "Try to kill the first monster!"
        );

        createPauseMenu();
        createInventory();
        createShop();
        createHUD();
    }

    private Animation<TextureRegion> splitAnimation(Texture sheet, int cols, float frameDuration, boolean loop) {
        TextureRegion[][] tmp = TextureRegion.split(
            sheet,
            sheet.getWidth()/cols,
            sheet.getHeight()
        );
        TextureRegion[] frames = new TextureRegion[cols];
        System.arraycopy(tmp[0], 0, frames, 0, cols);
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(loop
            ? Animation.PlayMode.LOOP
            : Animation.PlayMode.NORMAL
        );
        return anim;
    }

    private void spawnMonster() {
        monsterTex   = new Texture("monster.png");
        monsterX     = Gdx.graphics.getWidth();
        monsterY     = groundY;
        monsterAlive = true;
        monsterHp    = 50;
    }

    private void createPauseMenu() {
        pauseMenu = new Table(skin);
        pauseMenu.setFillParent(true);
        pauseMenu.center();
        pauseMenu.setVisible(false);

        TextButton resume = new TextButton("Resume", skin);
        resume.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
                paused = false;
                inventoryOpen = shopOpen = false;
            }
        });
        TextButton settings = new TextButton("Settings", skin);
        settings.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
                game.setScreen(new SettingsScreen(game, GameScreen.this));
            }
        });
        TextButton exit = new TextButton("Exit", skin);
        exit.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
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
        inventoryWindow.setSize(300,300);
        inventoryWindow.setPosition(
            (Gdx.graphics.getWidth()-300)/2f,
            (Gdx.graphics.getHeight()-300)/2f
        );
        inventoryWindow.setVisible(false);

        Table grid = new Table(skin);
        for(int r=0; r<4; r++){
            for(int c=0; c<4; c++){
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
        shopWindow.setSize(350,380);
        shopWindow.setPosition(
            (Gdx.graphics.getWidth()-350)/2f,
            (Gdx.graphics.getHeight()-380)/2f
        );
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

        TextButton close = new TextButton("Close", skin);
        close.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
                shopOpen = false;
                paused   = false;
            }
        });
        content.add(close).colspan(2).padTop(15);

        shopWindow.add(content).pad(10);
        uiStage.addActor(shopWindow);
    }

    private void addShopItem(Table table, Label goldLabel, String name, int cost, Runnable purchase) {
        TextButton buyBtn = new TextButton("Buy ("+cost+")", skin);
        buyBtn.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
                if (gold >= cost) {
                    gold -= cost;
                    goldLabel.setText("Gold: " + gold);
                    goldDisplayLabel.setText(""+gold);
                    purchase.run();
                }
            }
        });
        table.add(new Label(name, skin)).left().pad(5);
        table.add(buyBtn).right().pad(5).row();
    }

    private void createHUD() {
        hpBar   = new ProgressBar(1, MAX_HP,   1, false, skin);
        manaBar = new ProgressBar(1, MAX_MANA, 1, false, skin);
        hpBar.setValue(currentHp);
        manaBar.setValue(currentMana);

        hpValueLabel   = new Label(currentHp + "/100", skin);
        manaValueLabel = new Label(currentMana + "/100", skin);
        goldDisplayLabel = new Label(""+gold, skin);

        Table hud = new Table(skin);
        hud.setFillParent(true);
        hud.top().left();

        hud.add(new Label("HP:", skin)).pad(2);
        hud.add(hpBar).width(150).height(20).pad(2);
        hud.add(hpValueLabel).pad(2).row();

        hud.add(new Label("Mana:", skin)).pad(2);
        hud.add(manaBar).width(150).height(20).pad(2);
        hud.add(manaValueLabel).pad(2).row();

        hud.add(coinImage).size(24).pad(2);
        hud.add(new Label(""+gold, skin)).left().pad(2).row();

        uiStage.addActor(hud);
    }

    @Override
    public void render(float delta) {
        monsterDamageCooldown = Math.max(0f, monsterDamageCooldown - delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if      (shopOpen)      { shopOpen      = false; paused = false; }
            else if (inventoryOpen) { inventoryOpen = false; paused = false; }
            else                   { paused = !paused; if (paused) inventoryOpen = shopOpen = false; }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen; shopOpen = false; paused = inventoryOpen;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            shopOpen = true; inventoryOpen = false; paused = true;
        }

        if (!attacking && monsterAlive && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            attacking  = true;
            attackTime = 0f;

            TextureRegion curr = facingRight
                ? knightRightAnim.getKeyFrame(stateTime)
                : knightLeftAnim .getKeyFrame(stateTime);
            float fw = curr.getRegionWidth(), fh = curr.getRegionHeight();
            float hitX = facingRight ? x + fw : x - attackRange;
            Rectangle hitBox = new Rectangle(hitX, y, attackRange, fh);
            Rectangle mBox   = new Rectangle(monsterX, monsterY,
                monsterTex.getWidth(),
                monsterTex.getHeight());
            if (hitBox.overlaps(mBox)) {
                monsterHp -= 10;
                if (monsterHp <= 0) {
                    monsterAlive = false;
                }
            }
        }

        if (!paused) {
            if (attacking) {
                attackTime += delta;
                boolean done = facingRight
                    ? attackRightAnim.isAnimationFinished(attackTime)
                    : attackLeftAnim .isAnimationFinished(attackTime);
                if (done) {
                    attacking  = false;
                    attackTime = 0f;
                }
            } else {
                if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= speed*delta; facingRight = false; }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += speed*delta; facingRight = true;  }
                if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                    && y <= groundY + 0.1f) {
                    velocityY = jumpVelocity;
                }
                velocityY -= gravity*delta;
                y += velocityY*delta;
                if (y < groundY) { y = groundY; velocityY = 0; }

                stateTime += delta;
                coinTime  += delta;
            }
        }

        ScreenUtils.clear(0,0,0,1);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        TextureRegion heroFrame = attacking
            ? (facingRight
            ? attackRightAnim.getKeyFrame(attackTime)
            : attackLeftAnim .getKeyFrame(attackTime))
            : (facingRight
            ? knightRightAnim.getKeyFrame(stateTime)
            : knightLeftAnim .getKeyFrame(stateTime));
        batch.draw(heroFrame, x, y);

        if (monsterAlive) {
            if (monsterX > x) monsterX -= monsterSpeed * delta;
            else              monsterX += monsterSpeed * delta;

            batch.draw(monsterTex, monsterX, monsterY);

            Rectangle heroRect = new Rectangle(
                x, y,
                heroFrame.getRegionWidth(),
                heroFrame.getRegionHeight()
            );
            Rectangle mRect = new Rectangle(
                monsterX, monsterY,
                monsterTex.getWidth(),
                monsterTex.getHeight()
            );
            if (monsterAlive && mRect.overlaps(heroRect) && monsterDamageCooldown <= 0f) {
                currentHp = Math.max(0, currentHp - 1);
                hpBar.setValue(currentHp);
                hpValueLabel.setText(currentHp + "/100");

                monsterDamageCooldown = DAMAGE_COOLDOWN_TIME;
            }
        }

        batch.end();

        pauseMenu    .setVisible(paused && !inventoryOpen && !shopOpen);
        inventoryWindow.setVisible(inventoryOpen);
        shopWindow    .setVisible(shopOpen);

        uiStage.act(delta);
        uiStage.draw();
        dialog.updateAndDraw(delta);
    }


    @Override public void resize(int w, int h)  {
        uiStage.getViewport().update(w,h,true);
    }
    @Override public void pause()  { }
    @Override public void resume() { }
    @Override public void hide()   {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        knightRightSheet.dispose();
        knightLeftSheet.dispose();
        attackRightSheet.dispose();
        attackLeftSheet.dispose();
        coinSheet.dispose();
        uiStage.dispose();
        skin.dispose();
        dialog.dispose();
        if (monsterTex != null) monsterTex.dispose();
    }
}
