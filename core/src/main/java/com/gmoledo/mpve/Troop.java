package com.gmoledo.mpve;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


public class Troop {
    enum Shape { single, pair, triple, cluster }

    static final int[] SINGLE = new int[]{ 0, 0 };
    static final int[] PAIR = new int[]{ 0, 0, 0, 1 };
    static final int[] TRIPLE = new int[]{ 0, -1, 0, 0, 0, 1 };
    static final int[] CLUSTER = new int[]{ 0, 0, 1, -1, 1, 0 };

    static EnumMap<Shape, int[]> SHAPE_MAP;

    Vector2 position;
    Vector2 origin;
    Shape shape;
    List<Cell> cells;
    Cell.Type cell_type;

    Troop(Cell.Type cell_type, Shape shape, int q, int r) {
        // Must instantiate map manually
        SHAPE_MAP = new EnumMap<>(Shape.class);
        SHAPE_MAP.put(Shape.single, SINGLE);
        SHAPE_MAP.put(Shape.pair, PAIR);
        SHAPE_MAP.put(Shape.triple, TRIPLE);
        SHAPE_MAP.put(Shape.cluster, CLUSTER);

        origin = Vector2.Zero;
        position = new Vector2(q, r);
        this.shape = shape;
        this.cell_type = cell_type;

        // Use shape_data offsets to place cells of troop
        int[] shape_data = SHAPE_MAP.get(this.shape);
        cells = new ArrayList<>();
        for (int v = 0; v < shape_data.length; v += 2) {
            cells.add(new Cell(this.cell_type, shape_data[v] + (int) position.x, shape_data[v+1] + (int) position.y));
        }
    }

    public void move(int dq, int dr) {
        // Restrict movement to board
        boolean is_move_fail = false;
        for (Cell cell : this.cells) {
            if (!Board.get_index(cell.q + dq, cell.r + dr)) {
                is_move_fail = true;
                break;
            }
        }

        // Move individual cells
        if (!is_move_fail) {
            for (Cell cell : this.cells) {
                cell.translate(dq, dr);
            }
        }
    }

    public void draw() {
        for (Cell cell : this.cells) {
            cell.draw();
        }
    }
}
