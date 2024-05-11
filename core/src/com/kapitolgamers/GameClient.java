package com.kapitolgamers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kapitolgamers.classes.actors.Player;
import com.kapitolgamers.classes.items.Item;
import com.kapitolgamers.classes.items.ItemManager;
import com.kapitolgamers.classes.structures.MapManager;
import com.kapitolgamers.classes.structures.Room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class GameClient extends ApplicationAdapter {
    private final int[] INIT_RES = {640, 480};
    private SpriteBatch batch;
    private MapManager map;
    private ItemManager items;
    private Player mainPlayer;
    private Vector<Player> otherPlayers = new Vector<>();
    private OrthographicCamera mainCamera;
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

        boolean isHost = false;
        String ADDRESS = "localhost";
        int PORT = 12345;

        try {
            clientSocket = new Socket(ADDRESS, PORT);
            System.out.println("Running as CLIENT");
        } catch (IOException e) {
            isHost = true;
        }
        if (isHost) {
            gameServer = new GameServer();
            gameServer.startAcceptingNewPlayerConnections();
            gameServer.startProcessingExistingPlayerRequests();
            System.out.println("Running as SERVER");

            try {
                clientSocket = new Socket(ADDRESS, PORT);
                System.out.println("CLIENT Started");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            sendStringToServer("REQUEST MAPROOMS");
            map.loadNewMapRooms((Room[][]) getRequestedObjectFromServer());
            mainPlayer.rect.setPosition(map.getEntranceCenter());

            sendStringToServer("REQUEST ITEMS");
            items.createItemsFromData((ItemManager.ItemData[]) getRequestedObjectFromServer());
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

    private Object getRequestedObjectFromServer() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStringToServer(String message) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
