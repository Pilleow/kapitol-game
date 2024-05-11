package com.kapitolgamers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kapitolgamers.classes.actors.Player;
import com.kapitolgamers.classes.items.ItemManager;
import com.kapitolgamers.classes.structures.MapManager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Vector;

public class GameClient extends ApplicationAdapter {
    final int[] INIT_RES = {1280, 720};
    SpriteBatch batch;
    MapManager map;
    ItemManager items;
    Player mainPlayer;
    Vector<Player> otherPlayers = new Vector<>();
    OrthographicCamera mainCamera;
    private GameServer gameServer;
    private Socket clientSocket;

    @Override
    public void create() {
        batch = new SpriteBatch();
        items = new ItemManager();
        map = new MapManager("sprites/rooms");
        mainPlayer = new Player(
                "sprites/characters/player.png",
                (float) INIT_RES[0] / 2,
                (float) INIT_RES[1] / 2,
                50,
                50
        );
        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, 1280, 720);

        // todo- what the hell, do something with this abomination
        boolean SERVER = false;
        String ADDRESS = "localhost";
        int PORT = 12345;

        if (!ADDRESS.isEmpty()) {
            try {
                clientSocket = new Socket(ADDRESS, PORT);
                System.out.println("Running as CLIENT");
            } catch (IOException e) {
                SERVER = true;
                ADDRESS = "";
            }
        }
        if (ADDRESS.isEmpty()) {
            gameServer = new GameServer();
            gameServer.acceptNewPlayerConnections();
            System.out.println("Running as SERVER");
        }
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
        if (!map.isGenerated()) {
            map.generateMap(12);
            items.generateItems(10, map);
            // map.printMapToConsole();
            mainPlayer.rect.setPosition(map.getEntranceCenter());
        }

        mainPlayer.processNewMovementInput();
        mainPlayer.limitVelocityByWalkableRects(
                map.getAllCurrentChunkWalkableRects(mainCamera, 2),
                deltaTime
        );
        mainPlayer.applyVelocity(deltaTime);

        if (mainPlayer.clickedPickupButton()) mainPlayer.handleItemPickup(items);
        if (mainPlayer.clickedDropButton()) mainPlayer.handleItemDrop();

        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        mainCamera.unproject(mousePos);
        mainPlayer.setRotationTowardsPosition(new Vector2(mousePos.x, mousePos.y));
        mainCamera.position.set(mainPlayer.getCenter(), 0);

    }

    private void renderGameElements() {
        map.render(batch, mainCamera);
        items.render(batch, mainCamera);
        for (Player p : otherPlayers) p.draw(batch);
        mainPlayer.draw(batch);
        mainPlayer.inventory.render(batch, mainCamera);
    }

    @Override
    public void dispose() {
        batch.dispose();
        mainPlayer.dispose();
        items.dispose();
        map.dispose();

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (gameServer != null) {
            gameServer.dispose();
        }
    }
}
