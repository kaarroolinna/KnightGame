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

    public void showSequence(String... texts) {
        messages = new java.util.LinkedList<>();
        for (String t : texts) messages.add(t);
        showNext();
    }

    private void showNext() {
        if (messages == null || messages.isEmpty()) {
            dialogBox.setVisible(false);
            return;
        }
        String next = messages.poll();
        dialogLabel.setText(next);
        dialogBox.setVisible(true);
        addOneClickListener();
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
