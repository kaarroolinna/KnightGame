package com.knightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DialogManager {
    private final Stage stage;
    private final Table rootTable;
    private final Table dialogBox;
    private final Label dialogLabel;
    private java.util.Queue<String> messages;
    private Runnable onComplete;

    public DialogManager(Skin skin, Stage uiStage) {
        this.stage = uiStage;

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(20);
        stage.addActor(rootTable);

        dialogBox = new Table(skin);
        Texture bg = new Texture(Gdx.files.internal("dialog_bg.png"));
        dialogBox.background(new TextureRegionDrawable(new TextureRegion(bg)));

        dialogLabel = new Label("", skin);
        dialogLabel.setWrap(true);
        dialogLabel.setAlignment(Align.center);

        dialogBox.add(dialogLabel)
            .width(300f)
            .pad(10);
        dialogBox.setVisible(false);
        rootTable.add(dialogBox);
    }

    public void showOnClick(String text) {
        messages = null;
        dialogLabel.setText(text);
        dialogBox.setVisible(true);
        addOneClickListener();
    }

    public void showSequence(Runnable onComplete, String... messages) {
        this.onComplete = onComplete;
        this.messages = new java.util.LinkedList<>(java.util.Arrays.asList(messages));
        showNext();
    }

    private void showNext() {
        if (messages.isEmpty()) {
            dialogBox.setVisible(false);
            if (onComplete != null) onComplete.run();
            return;
        }
        String text = messages.poll();
        dialogLabel.setText(text);
        dialogBox.setVisible(true);

        ClickListener listener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int p, int b) {
                dialogBox.setVisible(false);
                stage.removeListener(this);
                showNext();
                return true;
            }
        };
        stage.addListener(listener);
    }

    private void addOneClickListener() {
        stage.getRoot().clearListeners();
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                showNext();
                stage.removeListener(this);
                return true;
            }
        });
    }

    public void updateAndDraw(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
    }
}
