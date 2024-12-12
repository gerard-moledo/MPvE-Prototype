package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;

import java.util.HashMap;
import java.util.Map;

final public class Input_System {
    // Class for gamepad button states
    static class Button {
        boolean pressed = false;
        boolean down = false;
        boolean released = false;
        boolean up = true;
    }

    // Maps button codes to button states
    static Map<Integer, Button> buttons = new HashMap<>();
    final static int L_TRIGGER = 4;
    final static int R_TRIGGER = 5;
    static Controller controller;
    static ControllerListener controller_listener = new Controller_Input();

    static public boolean Initialize_Controller(Controller controller) {
        Input_System.controller = controller;
        if (controller != null) {
            Input_System.controller.addListener(controller_listener);

            buttons.put(controller.getMapping().buttonL1, new Button());
            buttons.put(controller.getMapping().buttonR1, new Button());
            buttons.put(L_TRIGGER, new Button());
            buttons.put(R_TRIGGER, new Button());
            buttons.put(controller.getMapping().buttonA, new Button());
            buttons.put(controller.getMapping().buttonB, new Button());
            buttons.put(controller.getMapping().buttonDpadDown, new Button());
            buttons.put(controller.getMapping().buttonStart, new Button());
        }

        return controller != null;
    }
}

class Controller_Input implements ControllerListener {
    public void connected(Controller controller) {
        System.out.println("Controller connected.");

        Input_System.Initialize_Controller(controller);
    }

    @Override
    public void disconnected(Controller controller) {
        System.out.println("Controller disconnected.");
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        Input_System.Button button = Input_System.buttons.get(buttonCode);
        if (button == null) return false;

        if (button.up) {
            button.up = false;

            button.pressed = true;
            button.down = true;
        }

        Input_System.buttons.replace(buttonCode, button);
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        Input_System.Button button = Input_System.buttons.get(buttonCode);
        if (button == null) return false;

        if (button.down) {
            button.down = false;

            button.released = true;
            button.up = true;
        }

        Input_System.buttons.replace(buttonCode, button);
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        Input_System.Button button = null;
        if (axisIndex == 4 || axisIndex == 5)
            button = Input_System.buttons.get(axisIndex);
        if (button == null) return false;

        if (value > 0.6f) {
            if (!button.down) button.pressed = true;
            button.down = true;
        }
        if (value < 0.01f) {
            button.down = false;

            button.released = true;
            button.up = true;
        }

        Input_System.buttons.replace(axisIndex, button);
        return false;
    }
}
