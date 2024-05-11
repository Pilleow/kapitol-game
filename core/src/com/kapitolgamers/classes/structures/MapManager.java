package com.kapitolgamers.classes.structures;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kapitolgamers.classes.util.Direction;
import com.kapitolgamers.classes.util.TextureTools;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;


public class MapManager {

    private final int roomSideLength = 400;
    HashMap<String, Texture> sprites;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Room[][] mapRooms;
    private Vector<int[]> roomLocations = new Vector<>();;
    private boolean isGenerated = false;
    private int renderDistance;


    // Constructors ----------------------------------------------------------------------------------------------------
    public MapManager(String roomSpriteDir) {
        loadSpritesFromDir(roomSpriteDir);
        renderDistance = 4;
    }

    private void loadSpritesFromDir(String dir) {
        File directory = new File(dir);
        if (!directory.isDirectory()) throw new IllegalArgumentException("Not a directory: " + dir);
        sprites = new HashMap<>();
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (!file.isFile()) continue;
            Texture tex = new Texture(file.getAbsolutePath());
            if (tex.getHeight() != roomSideLength) {
                tex = TextureTools.resizeTexture(tex, roomSideLength, roomSideLength);
            }
            sprites.put(file.getName(), tex);
        }
    }


    // Public methods --------------------------------------------------------------------------------------------------
    public void generateMap(int roomCount) {
        isGenerated = false;
        generateRooms(roomCount);
        isGenerated = true;
    }

    private void generateRooms(int roomCount) {
        int originalRoomCount = roomCount;
        //roomCount = (int) Math.ceil(1.3 * Math.sqrt(roomCount)); // use this if checking only 1st degree neighbours
        roomCount = (int) Math.ceil(1.7 * Math.sqrt(roomCount));

        mapRooms = new Room[roomCount][roomCount];

        int posX = roomCount / 2;
        int posY = roomCount / 2;
        int newPosX = posX;
        int newPosY = posY;
        mapRooms[posX][posY] = new Room(
                Room.Type.ENTRANCE,
                (newPosX) * roomSideLength,
                (newPosY) * roomSideLength,
                roomSideLength
        );
        roomLocations.add(new int[]{posX, posY});

        int direction = 0;
        Room parentRoom = null;
        Random random = new Random();
        while (roomLocations.size() < originalRoomCount) {
            boolean validRoomGenerated = false;
            while (!validRoomGenerated) {
                direction = random.nextInt(4);
                int[] roomLoc = roomLocations.get(random.nextInt(roomLocations.size()));
                posX = roomLoc[0];
                posY = roomLoc[1];
                parentRoom = mapRooms[posX][posY];
                newPosX = posX;
                newPosY = posY;
                switch (direction) {
                    case 0:
                        if (newPosY + 1 >= roomCount) continue;
                        newPosY += 1;
                        break;
                    case 1:
                        if (newPosY - 1 < 0) continue;
                        newPosY -= 1;
                        break;
                    case 2:
                        if (newPosX + 1 >= roomCount) continue;
                        newPosX += 1;
                        break;
                    case 3:
                        if (newPosX - 1 < 0) continue;
                        newPosX -= 1;
                        break;
                }
                if (mapRooms[newPosX][newPosY] != null) continue;
                int neighbourCount = getNeighbourCount(roomCount, newPosX, newPosY);

                if (neighbourCount > 2) continue;

                validRoomGenerated = true;
            }
            Room newRoom = new Room(
                    Room.Type.NORMAL,
                    (newPosX) * roomSideLength,
                    (newPosY) * roomSideLength,
                    roomSideLength
            );
            switch (direction) {
                case 0:
                    newRoom.addPassage(Direction.DOWN);
                    parentRoom.addPassage(Direction.UP);
                    break;
                case 1:
                    newRoom.addPassage(Direction.UP);
                    parentRoom.addPassage(Direction.DOWN);
                    break;
                case 2:
                    newRoom.addPassage(Direction.LEFT);
                    parentRoom.addPassage(Direction.RIGHT);
                    break;
                case 3:
                    newRoom.addPassage(Direction.RIGHT);
                    parentRoom.addPassage(Direction.LEFT);
                    break;
            }
            mapRooms[newPosX][newPosY] = newRoom;
            roomLocations.add(new int[]{newPosX, newPosY});
        }
    }

    private int getNeighbourCount(int roomCount, int newPosX, int newPosY) {
        int neighbourCount = 0;
        if (newPosX + 1 < roomCount && mapRooms[newPosX + 1][newPosY] != null) neighbourCount++;
        if (newPosX - 1 > 0 && mapRooms[newPosX - 1][newPosY] != null) neighbourCount++;
        if (newPosY + 1 < roomCount && mapRooms[newPosX][newPosY + 1] != null) neighbourCount++;
        if (newPosY - 1 > 0 && mapRooms[newPosX][newPosY - 1] != null) neighbourCount++;

        if (newPosX + 2 < roomCount && mapRooms[newPosX + 2][newPosY] != null) neighbourCount++;
        if (newPosX - 2 > 0 && mapRooms[newPosX - 2][newPosY] != null) neighbourCount++;
        if (newPosY + 2 < roomCount && mapRooms[newPosX][newPosY + 2] != null) neighbourCount++;
        if (newPosY - 2 > 0 && mapRooms[newPosX][newPosY - 2] != null) neighbourCount++;
        return neighbourCount;
    }

    public void printMapToConsole() {
        for (int x = 0; x < mapRooms.length; ++x) System.out.print("--");
        System.out.println();
        for (Room[] mapRoom : mapRooms) {
            for (int y = 0; y < mapRooms[0].length; ++y) {
                if (mapRoom[y] != null) System.out.print("[]");
                else System.out.print("  ");
            }
            System.out.println();
        }
        for (int x = 0; x < mapRooms.length; ++x) System.out.print("--");
        System.out.println();
    }

    public void render(SpriteBatch batch, Camera cam) {
        Vector2 cameraCenterPos = new Vector2(
                cam.position.x + cam.viewportWidth / roomSideLength * 2,
                cam.position.y - cam.viewportHeight / roomSideLength * 2
        );
        int startX, endX, startY, endY;
        startX = (int) cameraCenterPos.x / roomSideLength - renderDistance;
        startY = (int) cameraCenterPos.y / roomSideLength - renderDistance;
        endX = (int) cameraCenterPos.x / roomSideLength + renderDistance;
        endY = (int) cameraCenterPos.y / roomSideLength + renderDistance;
        for (int x = Math.max(0, startX); x <= Math.min(mapRooms.length - 1, endX); ++x) {
            for (int y = Math.max(0, startY); y <= Math.min(mapRooms[0].length - 1, endY); ++y) {
                Room room = mapRooms[x][y];
                if (room == null) continue;
                for (int i = 0; i < 4; ++i) {
                    if (!room.passages[i]) continue;
                    String key;
                    if (i == Direction.UP.ordinal()) key = "roomconnectionnorth.png";
                    else if (i == Direction.DOWN.ordinal()) key = "roomconnectionsouth.png";
                    else if (i == Direction.RIGHT.ordinal()) key = "roomconnectionwest.png";
                    else if (i == Direction.LEFT.ordinal()) key = "roomconnectioneast.png";
                    else continue;
                    batch.draw(sprites.get(key), room.rect.x, room.rect.y);
                }
                batch.draw(sprites.get("roombase.png"), room.rect.x, room.rect.y);
            }
        }
    }

    public void dispose() {
        for (String key : sprites.keySet()) sprites.get(key).dispose();
    }

    public Vector<Vector<Rectangle>> getAllCurrentChunkWalkableRects(Camera cam, int customRenderDistance) {
        Vector2 cameraCenterPos = new Vector2(
                cam.position.x + cam.viewportWidth / roomSideLength * 2,
                cam.position.y - cam.viewportHeight / roomSideLength * 2
        );
        int startX, endX, startY, endY;
        startX = (int) cameraCenterPos.x / roomSideLength - customRenderDistance;
        startY = (int) cameraCenterPos.y / roomSideLength - customRenderDistance;
        endX = (int) cameraCenterPos.x / roomSideLength + customRenderDistance;
        endY = (int) cameraCenterPos.y / roomSideLength + customRenderDistance;

        Vector<Vector<Rectangle>> out = new Vector<>();
        for (int x = Math.max(0, startX); x <= Math.min(mapRooms.length - 1, endX); ++x) {
            for (int y = Math.max(0, startY); y <= Math.min(mapRooms[0].length - 1, endY); ++y) {
                if (mapRooms[x][y] == null) continue;
                out.add(mapRooms[x][y].walkableRects);
            }
        }
        return out;
    }

    public void loadNewMapRooms(Room[][] newMapRooms) {
        mapRooms = newMapRooms;
        roomLocations.clear();
        for (int x = 0; x < mapRooms.length; ++x) {
            for (int y = 0; y < mapRooms[0].length; ++y) {
                if (mapRooms[x][y] == null) continue;
                roomLocations.add(new int[]{x, y});
            }
        }
        isGenerated = true;
        System.out.println("newMapRooms loaded successfully.");
    }

    // Getters, setters ------------------------------------------------------------------------------------------------

    public void setRenderDistance(int newRenderDistance) {
        renderDistance = newRenderDistance;
    }

    public Room getEntrance() {
        Room centerRoom = mapRooms[mapRooms.length / 2][mapRooms.length / 2];
        int x = 0;
        int y = 0;
        while (centerRoom.type != Room.Type.ENTRANCE) {
            centerRoom = mapRooms[x][y];
            x += 1;
            if (x >= mapRooms.length) {
                x = 0;
                y += 1;
            }
            if (y >= mapRooms[0].length)
                throw new IndexOutOfBoundsException("Entrance not found on map.");
        }
        return centerRoom;
    }

    public Vector2 getEntranceCenter() {
        Room entrance = getEntrance();
        return new Vector2(
                entrance.rect.x + entrance.rect.width / 2,
                entrance.rect.y + entrance.rect.height / 2
        );
    }

    public Room[][] getMapRooms() {
        return mapRooms;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public Vector<int[]> getRoomLocations() {
        return roomLocations;
    }
}
