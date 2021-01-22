package com.dodgerun.dodgerun;

public abstract class BaseObject {
    static final int STATE_NORMAL = 1;
    static final int STATE_DESTROYED = 0;
    protected int state = STATE_NORMAL;
    protected float xPosition;
    protected float yPosition;
}