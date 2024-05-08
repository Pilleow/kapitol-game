package com.kapitolgamers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kapitolgamers.classes.actors.Player;

public class KapitolGame extends ApplicationAdapter {
    final int[] INIT_RES = {1280, 720};
    SpriteBatch batch;
    Player mainPlayer;
    OrthographicCamera mainCamera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        mainPlayer = new Player("player.png", (float) INIT_RES[0] / 2, (float) INIT_RES[1] / 2);
        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, 1280, 720);
    }

    @Override
    public void render() {
        updateGameElements(Gdx.graphics.getDeltaTime());
        ScreenUtils.clear(0, 0, 0, 1);
        mainCamera.update();
        batch.setProjectionMatrix(mainCamera.combined);
        batch.begin();
        renderGameElements();
        batch.end();
    }

    public void updateGameElements(float deltaTime) {
        mainPlayer.processNewMovementInput();
        mainPlayer.applyVelocity(deltaTime);
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        mainCamera.unproject(mousePos);
        mainPlayer.setRotationTowardsPosition(new Vector2(
                mousePos.x,
                mousePos.y
        ));
        mainCamera.position.set(mainPlayer.getCenter(), 0);
    }

    private void renderGameElements() {
        mainPlayer.draw(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        mainPlayer.dispose();
    }
}
