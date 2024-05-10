package com.kapitolgamers.classes.structures;

import com.badlogic.gdx.math.Rectangle;
import com.kapitolgamers.classes.util.Direction;

import java.util.Vector;

public class Room {

    public enum Type {
        NORMAL,
        ENTRANCE;
    }

    public Type type;
    public Rectangle rect;
    public Vector<Rectangle> walkableRects = new Vector<>();
    public boolean[] passages = new boolean[4];
    private int originalSpriteSideLength = 300;
    private float spritePaddingMultiplier;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Room(Type type, int x, int y, int sideLength) {
        this.type = type;
        rect = new Rectangle(x, y, sideLength, sideLength);
        // 30 and -60 are directly taken from the sprite data, as 30 is the padding around the room space
        spritePaddingMultiplier = (float) sideLength / originalSpriteSideLength;
        walkableRects.add(new Rectangle(
                x + 30 * spritePaddingMultiplier,
                y + 30 * spritePaddingMultiplier,
                sideLength - 60 * spritePaddingMultiplier,
                sideLength - 60 * spritePaddingMultiplier
        ));
    }

    // Public methods --------------------------------------------------------------------------------------------------

    public void addPassage(Direction direction) {
        if (direction == Direction.UP) {
            passages[Direction.UP.ordinal()] = true;
            walkableRects.add(new Rectangle(
                    rect.x + 120 * spritePaddingMultiplier,
                    rect.y + rect.height / 2,
                    60 * spritePaddingMultiplier,
                    rect.height
            ));
        } else if (direction == Direction.DOWN) {
            passages[Direction.DOWN.ordinal()] = true;
            walkableRects.add(new Rectangle(
                    rect.x + 120 * spritePaddingMultiplier,
                    rect.y - rect.height / 2,
                    60 * spritePaddingMultiplier,
                    rect.height
            ));
        } else if (direction == Direction.LEFT) {
            passages[Direction.LEFT.ordinal()] = true;
            walkableRects.add(new Rectangle(
                    rect.x - rect.width / 2,
                    rect.y + 120 * spritePaddingMultiplier,
                    rect.width,
                    60 * spritePaddingMultiplier
            ));
        } else if (direction == Direction.RIGHT) {
            passages[Direction.RIGHT.ordinal()] = true;
            walkableRects.add(new Rectangle(
                    rect.x + rect.width / 2,
                    rect.y + 120 * spritePaddingMultiplier,
                    rect.width,
                    60 * spritePaddingMultiplier
            ));
        }
    }

    // Getters, setters ------------------------------------------------------------------------------------------------
}

