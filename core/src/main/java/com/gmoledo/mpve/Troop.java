package com.gmoledo.mpve;

import java.util.ArrayList;
import java.util.List;


public class Troop {
    int q;
    int r;

    float radius;

    Shape.Type shape;
    List<Cell> cells;
    Cell.Type cell_type;

    Troop(Cell.Type cell_type, Shape.Type shape, int q, int r, float radius) {
        this.q = q;
        this.r = r;

        this.radius = radius;

        this.shape = shape;
        this.cell_type = cell_type;

        update_shape();
    }

    private void update_shape() {
        // Use shape_data offsets to place cells of troop
        int[] shape_data = Shape.SHAPE_MAP.get(this.shape);
        List<Cell> new_cells = new ArrayList<>();
        for (int v = 0; v < shape_data.length; v += 2) {
            new_cells.add(new Cell(this.cell_type, shape_data[v] + q, shape_data[v+1] + r, this.radius));
        }

        // Only update cells if new_cells is within bounds (not off-grid)
        boolean success = check_is_in_bounds(new_cells);
        if (success) {
            this.cells = new_cells;

            if (!check_can_place()) {
                for (Cell cell : this.cells) {
                    cell.change_enabled(false);
                }
            }
        }
    }

    public void move(int dq, int dr) {
        List<Cell> new_cells = new ArrayList<>();
        for (Cell cell : this.cells) {
            Cell new_cell = new Cell(this.cell_type, cell.q, cell.r, this.radius);
            new_cell.translate(dq, dr);
            new_cells.add(new_cell);
        }

        if (Board.get(this.q + dq, this.r + dr) != null) {
            this.cells = new_cells;
            this.q += dq;
            this.r += dr;

            if (!check_can_place() || !check_is_in_territory()) {
                set_enabled(false);
            }
        }
    }

    public boolean place() {
        boolean success = check_can_place();
        if (success) {
            for (Cell cell : this.cells) {
                cell.change_enabled(false);
                Board.get(cell.q, cell.r).change_type(cell.get_compliment());
            }
        }
        return success;
    }

    public void rotate(int direction) {
        List<Cell> new_cells = new ArrayList<>();
        for (Cell cell : this.cells) {
            Cell new_cell = new Cell(this.cell_type, cell.q, cell.r, this.radius);
            new_cell.rotate(this.q, this.r, direction);
            new_cells.add(new_cell);
        }

        if (check_is_in_bounds(new_cells)) {
            this.cells = new_cells;

            if (!check_can_place() || !check_is_in_territory()) {
                set_enabled(false);
            }
        }
    }

    // Restrict movement to board
    // Only fails if entire troop is off-grid
    private boolean check_is_in_bounds(List<Cell> cells) {
        boolean is_move_fail = true;
        for (Cell cell : cells) {
            Cell board_cell = Board.get(cell.q, cell.r);
            if (board_cell != null) {
                is_move_fail = false;
                break;
            }
        }

        return !is_move_fail;
    }

    // Fails if a cell is off-grid or if cell is occupied already
    private boolean check_can_place() {
        boolean is_place_fail = false;

        if (!check_is_in_territory()) return is_place_fail;

        for (Cell cell : this.cells) {
            Cell board_cell = Board.get(cell.q, cell.r);
            if (board_cell == null || board_cell.type == cell.get_compliment()) {
                is_place_fail = true;
            }
        }

        return !is_place_fail;
    }

    private boolean check_is_in_territory() {
        boolean is_out_of_territory = false;

        for (Cell cell : cells) {
            boolean is_bordering = check_surrounding_cells(cell);
            if (!is_bordering) {
                is_out_of_territory = true;
                break;
            }
        }

        return !is_out_of_territory;
    }

    private boolean check_surrounding_cells(Cell cell) {
        boolean is_neighboring_territory = false;

        for (int dq = -1; dq <= 1; ++dq) {
            for (int dr = -1; dr <= 1; ++dr) {
                if (Math.abs(dq + dr) <= 1) {
                    Cell neighbor = Board.get(cell.q + dq, cell.r + dr);
                    if (neighbor != null && cell.compare_territory(neighbor)) {
                        is_neighboring_territory = true;
                        break;
                    }
                }
            }

            if (is_neighboring_territory) break;
        }

        return is_neighboring_territory;
    }

    // For placing cells off of the board
    public void set_absolute_position(float x, float y) {
        int[] shape_data = Shape.SHAPE_MAP.get(this.shape);
        List<Cell> new_cells = new ArrayList<>();
        for (int v = 0; v < shape_data.length; v += 2) {
            Cell new_cell = new Cell(this.cell_type, 0, 0, this.radius);
            int dq = shape_data[v];
            int dr = shape_data[v + 1];
            float offset_dr = this.radius * (float) Math.sqrt(3) / 2;
            float dx = dq * this.radius * 3 / 2;
            float dy = -dr * this.radius * (float) Math.sqrt(3) + -dq * offset_dr;
            new_cell.set_absolute_position(x + dx, y + dy);
            new_cell.change_enabled(false);
            new_cells.add(new_cell);
        }

        this.cells = new_cells;
    }

    public void set_enabled(boolean is_enabled) {
        for (Cell cell : this.cells) {
            cell.change_enabled(is_enabled);
        }
    }

    public void draw() {
        for (Cell cell : this.cells) {
            cell.draw();
        }
    }
}
