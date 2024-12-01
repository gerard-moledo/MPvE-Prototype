package com.gmoledo.mpve;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


public class Troop {
    enum Shape { single, pair, triple, cluster }

    static final int[] SINGLE = new int[]{ 0, 0 };
    static final int[] PAIR = new int[]{ 0, 0, 0, 1 };
    static final int[] TRIPLE = new int[]{ 0, -1, 0, 0, 0, 1 };
    static final int[] CLUSTER = new int[]{-1, 1, 0, 0, 0, 1 };

    static EnumMap<Shape, int[]> SHAPE_MAP;

    Vector2 position;
    Vector2 origin;
    Shape shape;
    List<Cell> cells;
    Cell.Type type;

    Troop(Cell.Type type, int x, int y) {
        SHAPE_MAP = new EnumMap<>(Shape.class);
        SHAPE_MAP.put(Shape.single, SINGLE);
        SHAPE_MAP.put(Shape.pair, PAIR);
        SHAPE_MAP.put(Shape.triple, TRIPLE);
        SHAPE_MAP.put(Shape.cluster, CLUSTER);

        origin = Vector2.Zero;
        position = new Vector2(x, y);
        shape = Shape.pair;
        this.type = type;

        int[] shape_data = SHAPE_MAP.get(shape);
        cells = new ArrayList<>();
        for (int v = 0; v < shape_data.length; v += 2) {
            cells.add(new Cell(this.type, shape_data[v] + (int) position.x, shape_data[v+1] + (int) position.y));
        }
    }

    public void move(int dx, int dy) {
        for (Cell cell : this.cells) {
            cell.translate(dx, dy);
        }
    }

    public void draw(ShapeRenderer renderer) {
        renderer.setAutoShapeType(true);
        renderer.begin();

        for (Cell cell : this.cells) {
            cell.draw();
        }

        renderer.end();
    }
}
