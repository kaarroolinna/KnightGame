package com.knightgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
    public class KnightGame extends ApplicationAdapter {
        SpriteBatch batch;
        Texture img;

        @Override
        public void create () {
            batch = new SpriteBatch();
            img = new Texture("knight.png"); // поклади файл в assets/
        }

        @Override
        public void render () {
            ScreenUtils.clear(0, 0, 0.2f, 1);
            batch.begin();
            batch.draw(img, 100, 100);
            batch.end();
        }

        @Override
        public void dispose () {
            batch.dispose();
            img.dispose();
        }
    }
