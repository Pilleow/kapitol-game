package com.kapitolgamers.classes.util;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

public class TextureTools {
    public static Texture resizeTexture(Texture texture, int newWidth, int newHeight) {
        TextureData texData = texture.getTextureData();
        texData.prepare();
        Pixmap originalPixmap = texData.consumePixmap();
        Pixmap resizedPixmap = new Pixmap(newWidth, newHeight, originalPixmap.getFormat());
        resizedPixmap.drawPixmap(
                originalPixmap,
                0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
                0, 0, newWidth, newHeight
        );
        Texture resizedTexture = new Texture(resizedPixmap);
        originalPixmap.dispose();
        resizedPixmap.dispose();
        return resizedTexture;
    }
}
