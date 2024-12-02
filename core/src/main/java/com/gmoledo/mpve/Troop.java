package com.gmoledo.mpve;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


public class Troop {
    enum Shape {
        single, pair, line_3, cluster_3, curve_3, line_4, J_4, U_4, hammer_4, cluster_4, Z_4, hazard_4;

        final private int value;
        final static private Shape[] values = Shape.values();

        Shape() {
            value = this.ordinal();
        }

        public Shape toggle_value(int direction) {
            return values[(value + direction + values.length) % values.length];
        }
    }

    static final int[] SINGLE =     new int[]{ 0, 0 };
    static final int[] PAIR =       new int[]{ 0, 0, 0, 1 };
    static final int[] LINE_3 =     new int[]{ 0,-1, 0, 0, 0, 1 };
    static final int[] CLUSTER_3 =  new int[]{ 0, 0, 1,-1, 1, 0 };
    static final int[] CURVE_3 =    new int[]{-1, 0, 0, 0, 1,-1 };
    static final int[] LINE_4 =     new int[]{ 0,-2, 0,-1, 0, 0, 0, 1 };
    static final int[] J_4 =        new int[]{ 0,-2, 0,-1, 0, 0,-1, 1 };
    static final int[] U_4 =        new int[]{-1, 0, 0, 0, 1,-1, 1,-2 };
    static final int[] hammer_4 =   new int[]{ 0,-1, 0, 0, 0, 1,-1, 1 };
    static final int[] cluster_4 =  new int[]{ 0, 0,-1, 1, 0, 1, 1, 0 };
    static final int[] Z_4 =        new int[]{-1, 0, 0, 0, 0, 1, 1, 1 };
    static final int[] hazard_4  =  new int[]{ 0, 0,-1, 1, 0,-1, 1, 0 };
    static EnumMap<Shape, int[]> SHAPE_MAP;

    int q;
    int r;
    Vector2 origin;
    Shape shape;
    List<Cell> cells;
    Cell.Type cell_type;

    Troop(Cell.Type cell_type, Shape shape, int q, int r) {
        // Must instantiate map manually
        SHAPE_MAP = new EnumMap<>(Shape.class);
        SHAPE_MAP.put(Shape.single, SINGLE);
        SHAPE_MAP.put(Shape.pair, PAIR);
        SHAPE_MAP.put(Shape.line_3, LINE_3);
        SHAPE_MAP.put(Shape.cluster_3, CLUSTER_3);
        SHAPE_MAP.put(Shape.curve_3, CURVE_3);
        SHAPE_MAP.put(Shape.line_4, LINE_4);
        SHAPE_MAP.put(Shape.J_4, J_4);
        SHAPE_MAP.put(Shape.U_4, U_4);
        SHAPE_MAP.put(Shape.hammer_4, hammer_4);
        SHAPE_MAP.put(Shape.cluster_4, cluster_4);
        SHAPE_MAP.put(Shape.Z_4, Z_4);
        SHAPE_MAP.put(Shape.hazard_4, hazard_4);

        origin = Vector2.Zero;
        this.q = q;
        this.r = r;

        this.shape = shape;
        this.cell_type = cell_type;
        update_shape();
    }

    public void toggle_shape(int direction) {
        boolean success;
        Shape original_shape = this.shape;
        do {
            this.shape = this.shape.toggle_value(direction);
            success = update_shape();

            if (this.shape == original_shape && !success) break;
        } while (!success);
        if (!success) System.out.println("success should not be false; investigate.");
    }

    public boolean update_shape() {
        // Use shape_data offsets to place cells of troop
        int[] shape_data = SHAPE_MAP.get(this.shape);
        List<Cell> new_cells = new ArrayList<>();
        for (int v = 0; v < shape_data.length; v += 2) {
            new_cells.add(new Cell(this.cell_type, shape_data[v] + q, shape_data[v+1] + r));
        }

        boolean success = check_is_in_bounds(new_cells);
        if (success) {
            this.cells = new_cells;
        }

        return success;
    }

    public void move(int dq, int dr) {
        List<Cell> new_cells = new ArrayList<>();
        for (Cell cell : this.cells) {
            Cell new_cell = new Cell(this.cell_type, cell.q, cell.r);
            new_cell.translate(dq, dr);
            new_cells.add(new_cell);
        }

        if (check_is_in_bounds(new_cells)) {
            this.cells = new_cells;
            this.q += dq;
            this.r += dr;
        }
    }

    // Restrict movement to board
    private boolean check_is_in_bounds(List<Cell> cells) {
        boolean is_move_fail = false;
        for (Cell cell : cells) {
            if (!Board.get_index(cell.q, cell.r)) {
                is_move_fail = true;
                break;
            }
        }

        return !is_move_fail;
    }

    public void draw() {
        for (Cell cell : this.cells) {
            cell.draw();
        }
    }
}
