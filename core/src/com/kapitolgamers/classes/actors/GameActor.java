package com.kapitolgamers.classes.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class GameActor {
    private final Texture sprite;
    public Rectangle rect;
    public Vector2 velocity = new Vector2(0, 0);
    public boolean isRunning = false;
    private int maxSpeed = 200;
    private float angleDeg = 0F;

    // Constructors ----------------------------------------------------------------------------------------------------

    public GameActor(String spriteRelativePath, float x, float y, float width, float height) {
        this.sprite = new Texture(spriteRelativePath);
        this.rect = new Rectangle(x, y, width, height);
    }

    // Public methods --------------------------------------------------------------------------------------------------

    private static boolean isInside(Rectangle innerRect, Rectangle outerRect) {
        return outerRect.contains(innerRect.x, innerRect.y)
                && outerRect.contains(innerRect.x + innerRect.width, innerRect.y)
                && outerRect.contains(innerRect.x, innerRect.y + innerRect.height)
                && outerRect.contains(innerRect.x + innerRect.width, innerRect.y + innerRect.height);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(
                sprite, rect.x, rect.y, (float) sprite.getWidth() / 2, (float) sprite.getHeight() / 2,
                sprite.getWidth(), sprite.getHeight(), 1, 1, angleDeg, 0, 0,
                sprite.getWidth(), sprite.getHeight(), false, false
        );
    }

    public void dispose() {
        sprite.dispose();
    }

    public void limitVelocityByWalkableRects(Vector<Vector<Rectangle>> rects, float deltaTime) {
        float runningModifier = 2F;
        Rectangle newRect;
        boolean isInsideAny;

        newRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        newRect.x += isRunning ? velocity.x * deltaTime * runningModifier : velocity.x * deltaTime;
        isInsideAny = false;
        for (Vector<Rectangle> r2 : rects) {
            for (Rectangle r : r2) {
                if (isInside(newRect, r)) {
                    isInsideAny = true;
                    break;
                }
            }
        }
        if (!isInsideAny) velocity.x = 0;

        newRect = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        newRect.y += isRunning ? velocity.y * deltaTime * runningModifier : velocity.y * deltaTime;
        isInsideAny = false;
        for (Vector<Rectangle> r2 : rects) {
            for (Rectangle r : r2) {
                if (isInside(newRect, r)) {
                    isInsideAny = true;
                    break;
                }
            }
        }
        if (!isInsideAny) velocity.y = 0;
    }

    public void applyVelocity(float deltaTime) {
        float runningModifier = 2F;
        rect.x += isRunning ? velocity.x * deltaTime * runningModifier : velocity.x * deltaTime;
        rect.y += isRunning ? velocity.y * deltaTime * runningModifier : velocity.y * deltaTime;
    }

    // Getters, setters ------------------------------------------------------------------------------------------------

    public Vector2 getCenter() {
        return new Vector2(rect.x + (float) sprite.getWidth() / 2, rect.y + (float) sprite.getHeight() / 2);
    }

    public void setAngleDeg(float angleDeg) {
        this.angleDeg = angleDeg;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        if (maxSpeed > 0) this.maxSpeed = maxSpeed;
    }

    public Texture getSprite() {
        return sprite;
    }
}
