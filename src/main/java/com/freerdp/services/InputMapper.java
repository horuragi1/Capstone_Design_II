package com.freerdp.services;

import java.awt.event.KeyEvent;

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

    public static final int KEYSTATE_ON = 1;
    public static final int KEYSTATE_LOCKED = 2;
    public static final int KEYSTATE_OFF = 3;
    final static int VK_LBUTTON = 0x01;
    final static int VK_RBUTTON = 0x02;
    final static int VK_CANCEL = 0x03;
    final static int VK_MBUTTON = 0x04;
    final static int VK_XBUTTON1 = 0x05;
    final static int VK_XBUTTON2 = 0x06;
    final static int VK_BACK = 0x08;
    final static int VK_TAB = 0x09;
    final static int VK_CLEAR = 0x0C;
    final static int VK_RETURN = 0x0D;
    final static int VK_SHIFT = 0x10;
    final static int VK_CONTROL = 0x11;
    final static int VK_MENU = 0x12;
    final static int VK_PAUSE = 0x13;
    final static int VK_CAPITAL = 0x14;
    final static int VK_KANA = 0x15;
    final static int VK_HANGUEL = 0x15;
    final static int VK_HANGUL = 0x15;
    final static int VK_JUNJA = 0x17;
    final static int VK_FINAL = 0x18;
    final static int VK_HANJA = 0x19;
    final static int VK_KANJI = 0x19;
    final static int VK_ESCAPE = 0x1B;
    final static int VK_CONVERT = 0x1C;
    final static int VK_NONCONVERT = 0x1D;
    final static int VK_ACCEPT = 0x1E;
    final static int VK_MODECHANGE = 0x1F;
    final static int VK_SPACE = 0x20;
    final static int VK_PRIOR = 0x21;
    final static int VK_NEXT = 0x22;
    final static int VK_END = 0x23;
    final static int VK_HOME = 0x24;
    final static int VK_LEFT = 0x25;
    final static int VK_UP = 0x26;
    final static int VK_RIGHT = 0x27;
    final static int VK_DOWN = 0x28;
    final static int VK_SELECT = 0x29;
    final static int VK_PRINT = 0x2A;
    final static int VK_EXECUTE = 0x2B;
    final static int VK_SNAPSHOT = 0x2C;
    final static int VK_INSERT = 0x2D;
    final static int VK_DELETE = 0x2E;
    final static int VK_HELP = 0x2F;
    final static int VK_KEY_0 = 0x30;
    final static int VK_KEY_1 = 0x31;
    final static int VK_KEY_2 = 0x32;
    final static int VK_KEY_3 = 0x33;
    final static int VK_KEY_4 = 0x34;
    final static int VK_KEY_5 = 0x35;
    final static int VK_KEY_6 = 0x36;
    final static int VK_KEY_7 = 0x37;
    final static int VK_KEY_8 = 0x38;
    final static int VK_KEY_9 = 0x39;
    final static int VK_KEY_A = 0x41;
    final static int VK_KEY_B = 0x42;
    final static int VK_KEY_C = 0x43;
    final static int VK_KEY_D = 0x44;
    final static int VK_KEY_E = 0x45;
    final static int VK_KEY_F = 0x46;
    final static int VK_KEY_G = 0x47;
    final static int VK_KEY_H = 0x48;
    final static int VK_KEY_I = 0x49;
    final static int VK_KEY_J = 0x4A;
    final static int VK_KEY_K = 0x4B;
    final static int VK_KEY_L = 0x4C;
    final static int VK_KEY_M = 0x4D;
    final static int VK_KEY_N = 0x4E;
    final static int VK_KEY_O = 0x4F;
    final static int VK_KEY_P = 0x50;
    final static int VK_KEY_Q = 0x51;
    final static int VK_KEY_R = 0x52;
    final static int VK_KEY_S = 0x53;
    final static int VK_KEY_T = 0x54;
    final static int VK_KEY_U = 0x55;
    final static int VK_KEY_V = 0x56;
    final static int VK_KEY_W = 0x57;
    final static int VK_KEY_X = 0x58;
    final static int VK_KEY_Y = 0x59;
    final static int VK_KEY_Z = 0x5A;
    final static int VK_LWIN = 0x5B;
    final static int VK_RWIN = 0x5C;
    final static int VK_APPS = 0x5D;
    final static int VK_SLEEP = 0x5F;
    final static int VK_NUMPAD0 = 0x60;
    final static int VK_NUMPAD1 = 0x61;
    final static int VK_NUMPAD2 = 0x62;
    final static int VK_NUMPAD3 = 0x63;
    final static int VK_NUMPAD4 = 0x64;
    final static int VK_NUMPAD5 = 0x65;
    final static int VK_NUMPAD6 = 0x66;
    final static int VK_NUMPAD7 = 0x67;
    final static int VK_NUMPAD8 = 0x68;
    final static int VK_NUMPAD9 = 0x69;
    final static int VK_MULTIPLY = 0x6A;
    final static int VK_ADD = 0x6B;
    final static int VK_SEPARATOR = 0x6C;
    final static int VK_SUBTRACT = 0x6D;
    final static int VK_DECIMAL = 0x6E;
    final static int VK_DIVIDE = 0x6F;
    final static int VK_F1 = 0x70;
    final static int VK_F2 = 0x71;
    final static int VK_F3 = 0x72;
    final static int VK_F4 = 0x73;
    final static int VK_F5 = 0x74;
    final static int VK_F6 = 0x75;
    final static int VK_F7 = 0x76;
    final static int VK_F8 = 0x77;
    final static int VK_F9 = 0x78;
    final static int VK_F10 = 0x79;
    final static int VK_F11 = 0x7A;
    final static int VK_F12 = 0x7B;
    final static int VK_F13 = 0x7C;
    final static int VK_F14 = 0x7D;
    final static int VK_F15 = 0x7E;
    final static int VK_F16 = 0x7F;
    final static int VK_F17 = 0x80;
    final static int VK_F18 = 0x81;
    final static int VK_F19 = 0x82;
    final static int VK_F20 = 0x83;
    final static int VK_F21 = 0x84;
    final static int VK_F22 = 0x85;
    final static int VK_F23 = 0x86;
    final static int VK_F24 = 0x87;
    final static int VK_NUMLOCK = 0x90;
    final static int VK_SCROLL = 0x91;
    final static int VK_LSHIFT = 0xA0;
    final static int VK_RSHIFT = 0xA1;
    final static int VK_LCONTROL = 0xA2;
    final static int VK_RCONTROL = 0xA3;
    final static int VK_LMENU = 0xA4;
    final static int VK_RMENU = 0xA5;
    final static int VK_BROWSER_BACK = 0xA6;
    final static int VK_BROWSER_FORWARD = 0xA7;
    final static int VK_BROWSER_REFRESH = 0xA8;
    final static int VK_BROWSER_STOP = 0xA9;
    final static int VK_BROWSER_SEARCH = 0xAA;
    final static int VK_BROWSER_FAVORITES = 0xAB;
    final static int VK_BROWSER_HOME = 0xAC;
    final static int VK_VOLUME_MUTE = 0xAD;
    final static int VK_VOLUME_DOWN = 0xAE;
    final static int VK_VOLUME_UP = 0xAF;
    final static int VK_MEDIA_NEXT_TRACK = 0xB0;
    final static int VK_MEDIA_PREV_TRACK = 0xB1;
    final static int VK_MEDIA_STOP = 0xB2;
    final static int VK_MEDIA_PLAY_PAUSE = 0xB3;
    final static int VK_LAUNCH_MAIL = 0xB4;
    final static int VK_LAUNCH_MEDIA_SELECT = 0xB5;
    final static int VK_LAUNCH_APP1 = 0xB6;
    final static int VK_LAUNCH_APP2 = 0xB7;
    final static int VK_OEM_1 = 0xBA;
    final static int VK_OEM_PLUS = 0xBB;
    final static int VK_OEM_COMMA = 0xBC;
    final static int VK_OEM_MINUS = 0xBD;
    final static int VK_OEM_PERIOD = 0xBE;
    final static int VK_OEM_2 = 0xBF;
    final static int VK_OEM_3 = 0xC0;
    final static int VK_ABNT_C1 = 0xC1;
    final static int VK_ABNT_C2 = 0xC2;
    final static int VK_OEM_4 = 0xDB;
    final static int VK_OEM_5 = 0xDC;
    final static int VK_OEM_6 = 0xDD;
    final static int VK_OEM_7 = 0xDE;
    final static int VK_OEM_8 = 0xDF;
    final static int VK_OEM_102 = 0xE2;
    final static int VK_PROCESSKEY = 0xE5;
    final static int VK_PACKET = 0xE7;
    final static int VK_ATTN = 0xF6;
    final static int VK_CRSEL = 0xF7;
    final static int VK_EXSEL = 0xF8;
    final static int VK_EREOF = 0xF9;
    final static int VK_PLAY = 0xFA;
    final static int VK_ZOOM = 0xFB;
    final static int VK_NONAME = 0xFC;
    final static int VK_PA1 = 0xFD;
    final static int VK_OEM_CLEAR = 0xFE;
    final static int VK_UNICODE = 0x80000000;
    final static int VK_EXT_KEY = 0x00000100;
    // key codes to switch between custom keyboard
    private final static int EXTKEY_KBFUNCTIONKEYS = 0x1100;
    private final static int EXTKEY_KBNUMPAD = 0x1101;
    private final static int EXTKEY_KBCURSOR = 0x1102;
    // this flag indicates if we got a VK or a unicode character in our translation map
    private static final int KEY_FLAG_UNICODE = 0x80000000;
    // this flag indicates if the key is a toggle key (remains down when pressed and goes up if
    // pressed again)
    private static final int KEY_FLAG_TOGGLE = 0x40000000;

    public static int KeyboardEventToVirtualcode(String key) {
        int VKCode = 0;
        if(key.length() == 1) {
            int keyCode = (int)key.charAt(0);
            // [a-z] [0-9] ' '
            if('a' <= keyCode && keyCode <= 'z')
                VKCode = keyCode - 32;
            else if('*' <= keyCode && keyCode <= '/')
                VKCode = keyCode + 0x40;
            else
                VKCode = keyCode;
        }
        else {
            int firstChar = (int)key.charAt(0);
            switch (firstChar) {
                case 'A':
                    if(key.equals("Alt"))
                        VKCode = VK_MENU;
                    else if(key.equals("ArrowLeft"))
                        VKCode = VK_LEFT;
                    else if(key.equals("ArrowUp"))
                        VKCode = VK_UP;
                    else if(key.equals("ArrowRight"))
                        VKCode = VK_RIGHT;
                    else if(key.equals("ArrowDown"))
                        VKCode = VK_DOWN;
                    break;
                case 'B':
                    if(key.equals("Backspace"))
                        VKCode = VK_BACK;
                    break;
                case 'C':
                    if(key.equals("Control"))
                        VKCode = VK_CONTROL;
                    else if(key.equals("CapsLock"))
                        VKCode = VK_CAPITAL;
                    else if(key.equals("ContextMenu"))
                        VKCode = VK_APPS;
                    break;
                case 'D':
                    if(key.equals("Delete"))
                        VKCode = VK_DELETE;
                    break;
                case 'E':
                    if(key.equals("Escape"))
                        VKCode = VK_ESCAPE;
                    else if(key.equals("Enter"))
                        VKCode = VK_RETURN;
                    else if(key.equals("End"))
                        VKCode = VK_END;
                    break;
                case 'F':
                    if(key.length() <= 3) {
                        int num = Integer.parseInt(key.substring(1));
                        VKCode = VK_F1 + num - 1;
                    }
                    break;
                case 'H':
                    if(key.equals("VK_HOME"))
                        VKCode = VK_HOME;
                    else if(key.equals("HangulMode"))
                        VKCode = VK_HANGUEL;
                    else if(key.equals("HanjaMode"))
                        VKCode = VK_HANJA;
                    break;
                case 'I':
                    if(key.equals("Insert"))
                        VKCode = VK_INSERT;
                    break;
                case 'M':
                    if(key.equals("Meta"))
                        VKCode = VK_LWIN;
                    break;
                case 'N':
                    if(key.equals("NumLock"))
                        VKCode = VK_NUMLOCK;
                    break;
                case 'P':
                    if(key.equals("PageDown"))
                        VKCode = VK_NEXT;
                    else if(key.equals("PageUp"))
                        VKCode = VK_PRIOR;
                    else if(key.equals("Pause"))
                        VKCode = VK_PAUSE;
                    break;
                case 'S':
                    if(key.equals("Shift"))
                        VKCode = VK_SHIFT;
                    else if(key.equals("ScrollLock"))
                        VKCode = VK_SCROLL;
                    break;
                case 'T':
                    if(key.equals("Tab"))
                        VKCode = VK_TAB;
                    break;
            }
        }

        return VKCode;
    }

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