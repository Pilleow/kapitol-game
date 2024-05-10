package com.kapitolgamers.classes.items;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Inventory {
    private int inventorySize = 4;
    private float pickupRangeSquared = (float) Math.pow(100, 2);
    private Item[] inventory;
    private int padding = 20;

    public Inventory(int invSize) {
        inventorySize = invSize;
        inventory = new Item[inventorySize];
    }

    public void handleItemPickup(ItemManager items, Vector2 playerPos) {
        // todo - add item chunk based pickup detection (similar in ItemManager.render())
        for (int i = 0; i < inventorySize; ++i) {
            if (inventory[i] == null) break;
            if (i == inventorySize - 1) return;
        }
        for (Item item : items.getItems()) {
            if (!item.isHidden && playerPos.dst2(item.rect.x, item.rect.y) < pickupRangeSquared) {
                for (int i = 0; i < inventorySize; ++i)
                    if (inventory[i] == null) {
                        inventory[i] = item;
                        item.isHidden = true;
                        break;
                    }
            }
        }
    }

    public void render(SpriteBatch batch, Camera cam) {
        Texture sprite;
        for (int i = 0; i < inventorySize; ++i) {
            if (inventory[i] == null) continue;
            sprite = inventory[i].getSprite();
            Vector3 pos = new Vector3(
                    padding,
                    (i + 1) * padding + (i + 1) * sprite.getHeight(),
                    0
            );
            cam.unproject(pos);
            batch.draw(
                    sprite,
                    pos.x,
                    pos.y
            );
        }
    }
}
