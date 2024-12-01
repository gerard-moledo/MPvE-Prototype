package com.gmoledo.mpve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Cell {
    enum Type { field, player, opponent };
    Type type;

    ShapeRenderer renderer;

    Cell(Type type) {
        this.type = type;

        renderer = new ShapeRenderer();
    }

    public void draw(ShapeRenderer renderer, float x, float y, float radius) {
        Color color = Color.BLACK;
        switch (type) {
            case field:    color = Color.GRAY;  break;
            case player:   color = Color.CYAN; break;
            case opponent: color = Color.RED; break;
        }

        // Provide hexagon vertices for drawing
        // TODO: Cache this
        float[] vertices = new float[14];
        for (int v = 0; v < 14; v += 2) {
            vertices[v + 0] = x + radius * (float) Math.sin(2 * Math.PI * v / 12f + Math.PI / 6);
            vertices[v + 1] = y + radius * (float) Math.cos(2 * Math.PI * v / 12f + Math.PI / 6);
        }
        renderer.setColor(color);
        renderer.polygon(vertices);
    }
}
