package com.gmoledo.mpve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Cell {
    enum Type { field, player, opponent }
    Type type;

    float x;
    float y;

    PolygonSprite sprite;
    PolygonSpriteBatch batch;
    ShapeRenderer renderer;

    Cell(Type type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;

        renderer = new ShapeRenderer();

        create_hexagon_batch();
    }

    private void create_hexagon_batch() {
        batch = new PolygonSpriteBatch();

        // Create solid white texture
        Texture cell_texture;
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        cell_texture = new Texture(pix);
        TextureRegion texture_region = new TextureRegion(cell_texture);

        // Provide hexagon vertices/indices for sprite
        float[] vertices = new float[14];
        float radius = Board.CELL_RADIUS * 0.95f; // Shrink radius to add space between cells
        for (int v = 0; v < 14; v += 2) {
            vertices[v + 0] = radius * (float) Math.sin(2 * Math.PI * v / 12f + Math.PI / 6);
            vertices[v + 1] = radius * (float) Math.cos(2 * Math.PI * v / 12f + Math.PI / 6);
        }
        short[] indices = new short[] {
            0, 1, 2,  0, 2, 3,  0, 3, 4,
            0, 4, 5,  0, 5, 6,  0, 6, 1
        };

        PolygonRegion region = new PolygonRegion(texture_region, vertices, indices);

        sprite = new PolygonSprite(region);
        batch = new PolygonSpriteBatch();

        // Set rendering properties for cell
        Color color = Color.BLACK;
        switch (type) {
            case field:    color = Color.WHITE;  break;
            case player:   color = Color.CYAN; break;
            case opponent: color = Color.RED; break;
        }
        sprite.setColor(color);
        sprite.setPosition(this.x, this.y);
    }

    public void draw() {
        batch.begin();

        sprite.draw(batch);

        batch.end();
    }
}
