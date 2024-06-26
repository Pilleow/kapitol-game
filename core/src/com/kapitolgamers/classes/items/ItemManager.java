package com.kapitolgamers.classes.items;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.kapitolgamers.classes.structures.MapManager;
import com.kapitolgamers.classes.structures.Room;
import com.kapitolgamers.classes.util.RandomHandler;

import java.io.Serializable;
import java.util.Vector;

public class ItemManager {

    public record ItemData(Rectangle rect, int weight, int value, String spritePath, boolean isHidden) implements Serializable {}
    private Item[] items;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ItemManager() {}

    // Public methods --------------------------------------------------------------------------------------------------

    public void generateItems(int itemCount, MapManager map) {
        Room r;
        Vector<int[]> roomLocations = map.getRoomLocations();
        items = new Item[itemCount];
        int[] itemPos = new int[2];
        String spritePath;
        Rectangle mainWalkableRect;
        int sideLength, weight, value;
        for (int i = 0; i < itemCount; ++i) {
            // todo - add generation of all these stuff, from a JSON preferably
            sideLength = 25;
            weight = RandomHandler.getRandomIntBothInclusive(5, 25);
            value = RandomHandler.getRandomIntBothInclusive(30, 120);
            spritePath = "sprites/items/temp.jpg";

            int[] roomLoc = roomLocations.get(RandomHandler.nextInt(roomLocations.size()));
            r = map.getMapRooms()[roomLoc[0]][roomLoc[1]];
            mainWalkableRect = r.walkableRects.get(0);
            itemPos[0] = RandomHandler.getRandomIntBothInclusive(
                    (int) mainWalkableRect.x,
                    (int) (mainWalkableRect.x + mainWalkableRect.width) - sideLength
            );
            itemPos[1] = RandomHandler.getRandomIntBothInclusive(
                    (int) mainWalkableRect.y,
                    (int) (mainWalkableRect.y + mainWalkableRect.height) - sideLength
            );
            items[i] = new Item(itemPos[0], itemPos[1], sideLength, weight, value, spritePath);
        }
    }

    public void render(SpriteBatch batch, Camera cam) {
        // todo - add chunk based rendering such that not all items are rendered at all times, only those visible.
        for (Item item : items) {
            if (!item.isHidden) item.draw(batch);
        }
    }

    public void dispose() {
        for (Item item : items) item.dispose();
    }

    // Getters, setters ------------------------------------------------------------------------------------------------

    public Item[] getItems() {
        return items;
    }

    public ItemData[] getItemsData() {
        ItemData[] out = new ItemData[items.length];
        for (int i = 0; i < items.length; ++i) {
            Item it = items[i];
            out[i] = new ItemData(it.rect, it.getWeight(), it.getValue(), it.getSpritePath(), it.isHidden);
        }
        return out;
    }

    public void createItemsFromData(ItemData[] newItems) {
        items = new Item[newItems.length];
        for (int i = 0; i < newItems.length; ++i) {
            ItemData itd = newItems[i];
            items[i] = new Item(
                    (int) itd.rect.x, (int) itd.rect.y, (int) itd.rect.width,
                    itd.weight, itd.value, itd.spritePath
            );
        }
    }
}
