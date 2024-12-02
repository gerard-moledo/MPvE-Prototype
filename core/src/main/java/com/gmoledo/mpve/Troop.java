package com.gmoledo.mpve;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;


public class Troop {
    int q;
    int r;
    Vector2 origin;
    Shape.Type shape;
    List<Cell> cells;
    Cell.Type cell_type;

    Troop(Cell.Type cell_type, Shape.Type shape, int q, int r) {
        origin = Vector2.Zero;
        this.q = q;
        this.r = r;

        this.shape = shape;
        this.cell_type = cell_type;
        update_shape();
    }

    public void toggle_shape(int direction) {
        boolean success;
        Shape.Type original_shape = this.shape;
        do {
            this.shape = this.shape.toggle_value(direction);
            success = update_shape();

            if (this.shape == original_shape && !success) break;
        } while (!success);
        if (!success) System.out.println("success should not be false; investigate.");
    }

    public boolean update_shape() {
        // Use shape_data offsets to place cells of troop
        int[] shape_data = Shape.SHAPE_MAP.get(this.shape);
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
