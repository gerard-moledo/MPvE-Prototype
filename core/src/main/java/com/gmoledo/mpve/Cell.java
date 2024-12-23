package com.gmoledo.mpve;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cell {
    public enum Type { field, cursor, player, player_territory, player_troop, opponent, opponent_territory, opponent_troop }
    Type type;
    Type compliment;

    static final float CELL_RADIUS = 30f;

    int q;
    int r;
    float x;
    float y;
    float radius;

    boolean placeable = true;

    PolygonSprite sprite;
    PolygonSpriteBatch batch;

    Cell(Type type, int q, int r, float radius) {
        this.type = type;
        this.compliment = get_compliment();

        this.radius = radius;
        if (this.radius == 0.0f)
            this.radius = CELL_RADIUS;

        this.q = 0;
        this.r = 0;

        create_hexagon_batch();
        change_type(this.type);

        translate(q, r);
    }

    public Type get_compliment() {
        Type compliment = Type.field;

        if (this.type == Type.player)   compliment = Type.player_troop;
        if (this.type == Type.opponent) compliment = Type.opponent_troop;

        return compliment;
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
        float radius = this.radius * 0.95f; // Shrink radius to add space between cells
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
    }

    public void set_absolute_position(float x, float y) {
        this.x = x;
        this.y = y;
        sprite.setPosition(this.x, this.y);
    }

    public void translate(int dq, int dr) {
        this.q += dq;
        this.r += dr;

        Vector2 position = calculate_position(this.q, this.r);
        this.x = position.x;
        this.y = position.y;
        sprite.setPosition(this.x, this.y);
    }

    public void rotate(int origin_q, int origin_r, int direction) {
        int dq_start = this.q - origin_q;
        int dr_start = this.r - origin_r;
        int dq = dq_start;
        int dr = dr_start;
        int ds = -(dq + dr);
        dq += direction == -1 ? dr_start : ds;
        dr += direction == -1 ? ds : dq_start;
        this.q = origin_q + dq;
        this.r = origin_r + dr;

        Vector2 position = calculate_position(this.q, this.r);
        this.x = position.x;
        this.y = position.y;
        sprite.setPosition(this.x, this.y);
    }

    private Vector2 calculate_position(int q, int r) {
        float offset = CELL_RADIUS * (float) Math.sqrt(3) / 2;
        float x = q * CELL_RADIUS * 3 / 2 + Gdx.graphics.getWidth() / 2f;
        float y = -r * CELL_RADIUS * (float) Math.sqrt(3) + -q * offset + Gdx.graphics.getHeight() / 2f;

        return new Vector2(x, y);
    }

    public boolean compare_territory(Cell other) {
        return this.type == Type.player && (other.type == Type.player_troop || other.type == Type.player_territory) ||
            this.type == Type.opponent && (other.type == Type.opponent_troop || other.type == Type.opponent_territory);
    }

    public void change_type(Type new_type) {
        this.type = new_type;
        change_color();
    }

    public void change_enabled(boolean is_enabled) {
        this.placeable = is_enabled;
        change_color();
    }

    public void change_color() {
        Color color = Color.PINK;

        switch (this.type) {
            case field:                 color = Color.WHITE; break;
            case cursor:                color = Color.BLACK; break;
            case player:                color = new Color(Integer.reverseBytes(Color.BLUE.toIntBits() & 0xccffffff)); break;
            case player_troop:          color = new Color(0x0077ffff); break;
            case player_territory:      color = Color.CYAN; break;
            case opponent:              color = new Color(Integer.reverseBytes(Color.ORANGE.toIntBits() & 0xccffffff)); break;
            case opponent_troop:        color = Color.RED; break;
            case opponent_territory:    color = new Color(0xff7777ff); break;
        }

        if (this.type == Type.player || this.type == Type.opponent) {
            if (!this.placeable) {
                color.set(0x585858bd);
            }
        }

        sprite.setColor(color);
    }

    public void draw() {
        batch.begin();

        sprite.draw(batch);

        batch.end();
    }
}
