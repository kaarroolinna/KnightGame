package com.knightgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DialogManager {
    private final Stage stage;
    private final Table rootTable;
    private final Table dialogBox;
    private final Label dialogLabel;

    public DialogManager(Skin skin, Stage uiStage) {
        this.stage = uiStage;

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(20);
        stage.addActor(rootTable);

        dialogBox = new Table(skin);

        if (skin.has("dialog", NinePatch.class)) {
            dialogBox.background("dialog");
        } else {
            Texture bg = new Texture(Gdx.files.internal("gray_background.png"));
            dialogBox.background(
                new TextureRegionDrawable(new TextureRegion(bg))
            );
        }

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
        dialogLabel.setText(text);
        dialogBox.setVisible(true);

        ClickListener globalClick = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event,
                                     float x, float y,
                                     int pointer, int button) {
                dialogBox.setVisible(false);
                stage.removeListener(this);
                return true;
            }
        };
        stage.addListener(globalClick);
    }

    public void updateAndDraw(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose(){
        stage.dispose();
    }
}
