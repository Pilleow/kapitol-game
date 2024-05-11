package com.kapitolgamers.classes.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.io.Serializable;

public class Item {
    public Rectangle rect;
    private int weight;
    private int value;
    private Texture sprite;
    private String spritePath;
    public boolean isHidden = false;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Item(int x, int y, int sideLength, int weight, int value, String spritePath) {
        rect = new Rectangle(x, y, sideLength, sideLength);
        this.weight = weight;
        this.value = value;
        this.sprite = new Texture(spritePath);
        this.spritePath = spritePath;
    }

    // Public methods --------------------------------------------------------------------------------------------------

    public void draw(SpriteBatch batch) {
        batch.draw(sprite, rect.x, rect.y);
    }

    public void dispose() {
        sprite.dispose();
    }

    public Texture getSprite() {
        return sprite;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    // Getters, setters ------------------------------------------------------------------------------------------------

}
