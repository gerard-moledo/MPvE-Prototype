package com.gmoledo.mpve;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {
    Board board;
    Controller controller;

    GameScreen() {
        board = new Board(5);
        controller = Controllers.getCurrent();
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.RED);
    }
}
