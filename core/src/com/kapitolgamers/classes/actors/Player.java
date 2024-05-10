package com.kapitolgamers.classes.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.kapitolgamers.classes.items.Inventory;
import com.kapitolgamers.classes.items.Item;
import com.kapitolgamers.classes.items.ItemManager;


public class Player extends GameActor {

    public Inventory inventory;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Player(String spriteRelativePath, float x, float y, float width, float height) {
        super(spriteRelativePath, x, y, width, height);
        inventory = new Inventory(4);
    }

    // Public methods --------------------------------------------------------------------------------------------------

    public void processNewMovementInput() {
        boolean xMoved = false;
        boolean yMoved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            yMoved = true;
            velocity.y = getMaxSpeed();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            yMoved = true;
            velocity.y = -getMaxSpeed();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            xMoved = true;
            velocity.x = -getMaxSpeed();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            xMoved = true;
            velocity.x = getMaxSpeed();
        }
        if (!xMoved && velocity.x != 0) velocity.x = 0;
        if (!yMoved && velocity.y != 0) velocity.y = 0;
        if (xMoved && yMoved) {
            velocity.x *= 0.707F;
            velocity.y *= 0.707F;
        }
        isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
    }

    public void setRotationTowardsPosition(Vector2 p) {
        Vector2 texCenter = rect.getCenter(new Vector2(0, 0));
        float deltaX = p.x - texCenter.x;
        float deltaY = p.y - texCenter.y;
        float angleRad = (float) Math.atan2(deltaY, deltaX);
        setAngleDeg((float) Math.toDegrees(angleRad));
    }

    public void handleItemPickup(ItemManager items) {
        inventory.handleItemPickup(items, new Vector2(rect.x, rect.y));
    }

    public void handleItemDrop() {
        inventory.handleItemDrop(rect);
    }

    // Getters, setters ------------------------------------------------------------------------------------------------

    public boolean clickedPickupButton() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E);
    }

    public boolean clickedDropButton() {
        return Gdx.input.isKeyJustPressed(Input.Keys.Q);
    }
}
