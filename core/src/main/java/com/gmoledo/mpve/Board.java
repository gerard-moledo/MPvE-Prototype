package com.gmoledo.mpve;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class Board {
    List<List<Cell>> board;
    int size;
    final float CELL_RADIUS = 40f;

    ShapeRenderer renderer;

    Board(int size) {
        this.size = size;

        this.renderer = new ShapeRenderer();

        create_board();
    }

    private void create_board() {
        // Allocate space for entire board and fill with empty contents
        board = new ArrayList<>();
        for (int i = 0; i < this.size * 2; ++i) {
            board.add(new ArrayList<>());
            for (int j = 0; j < this.size * 2; ++j) {
                board.get(i).add(null);
            }
        }

        // Create board (hex-shaped, hexagonal grid with side length this.size)
        int board_index = this.size - 1;
        for (int q = -board_index; q <= board_index; ++q) { // Generate grid based on qr-coordinates
            for (int r = Math.max(-board_index - q, -board_index); r <= Math.min(board_index - q, board_index); ++r) {
                System.out.format("(%d, %d), ", q, r);

                // q and r must be converted to positive indices for array access
                board.get(q + this.size).set(r + this.size, new Cell(Cell.Type.field));
            }
            System.out.println();
        }
    }

    public void draw() {
        renderer.setAutoShapeType(true);
        renderer.begin();

        // Calculate positions of all cells in board
        // TODO: Cache this
        for (int q_index = 0; q_index < board.size(); ++q_index) {
            int q = q_index - this.size;
            for (int r_index = 0; r_index < board.get(q_index).size(); ++r_index) {
                int r = r_index - this.size;

                if (board.get(q_index).get(r_index) != null) {
                    float offset = CELL_RADIUS * (float) Math.sqrt(3) / 2;
                    float x = q * CELL_RADIUS * 3 / 2 + Gdx.graphics.getWidth() / 2f;
                    float y = r * CELL_RADIUS * (float) Math.sqrt(3) + q * offset + Gdx.graphics.getHeight() / 2f;

                    board.get(q_index).get(r_index).draw(this.renderer, x, y, CELL_RADIUS);
                }
            }
        }
        renderer.end();
    }
}
