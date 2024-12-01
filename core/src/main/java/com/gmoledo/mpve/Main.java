package com.gmoledo.mpve;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class Main extends Game {
    FPSLogger fps_logger;

    @Override
    public void create() {
        fps_logger = new FPSLogger();

        this.setScreen(new GameScreen());
    }

    public void render() {
        super.render();

        fps_logger.log();
    }
}
