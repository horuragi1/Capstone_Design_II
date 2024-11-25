package com.freerdp.services;

public class InputMapper {
    static final int PTR_FLAGS_HWHEEL = 0x0400;
    static final int PTR_FLAGS_WHEEL = 0x0200;
    static final int PTR_FLAGS_WHEEL_NEGATIVE = 0x0100;
    static final int PTR_FLAGS_MOVE = 0x0800;
    static final int PTR_FLAGS_DOWN = 0x8000;
    static final int PTR_FLAGS_BUTTON1 = 0x1000; /* left */
    static final int PTR_FLAGS_BUTTON2 = 0x2000; /* right */
    static final int PTR_FLAGS_BUTTON3 = 0x4000; /* middle */
    static final int WheelRotationMask = 0x01FF;

    public static final int MOUSE_HOLD = -1;
    public static final int MOUSE_MOVE = 0;
    public static final int MOUSE_DOWN = 1;
    public static final int MOUSE_UP = 2;
    public static final int MOUSE_NONE_BUTTON = -1;
    public static final int MOUSE_LEFT_BUTTON = 0;
    public static final int MOUSE_MIDDLE_BUTTON = 1;
    public static final int MOUSE_RIGHT_BUTTON = 2;


    public static int MouseEventToFlags(int buttonNum, int mouseState) {
        int flags = 0;

        //mouse state
        switch (mouseState) {
            case MOUSE_MOVE:
                flags |= PTR_FLAGS_MOVE;
                break;
            case MOUSE_DOWN:
                flags |= PTR_FLAGS_DOWN;
                break;
            case MOUSE_UP:
                break;
        }

        //mouse button
        switch (buttonNum) {
            case MOUSE_LEFT_BUTTON:
                flags |= PTR_FLAGS_BUTTON1;
                break;
            case MOUSE_MIDDLE_BUTTON:
                flags |= PTR_FLAGS_BUTTON3;
                break;
            case MOUSE_RIGHT_BUTTON:
                flags |= PTR_FLAGS_BUTTON2;
                break;
        }

        return flags;
    }
}
