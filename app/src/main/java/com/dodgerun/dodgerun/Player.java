package com.dodgerun.dodgerun;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Canvas;

public class Player extends BaseObject{
    private Bitmap bitmap;
    private Rect rect;

    private Paint paint = new Paint ();
    private int top;
    private int right;
    private int bottom;
    private int left_change;

    public Player (Bitmap bitmap, int width) {
        this.bitmap = bitmap;
        this.left_change = (width - bitmap.getWidth()) / 2;
    }

    public void draw(Canvas canvas,int height) {
        this.top = height - bitmap.getHeight();
        this.right = this.left_change + bitmap.getWidth();
        this.bottom = top + bitmap.getHeight();
        rect = new Rect(this.left_change, this.top, this.right, this.bottom);
        //ここを!=にするとなぜか動く
        if (this.state == STATE_DESTROYED) {
            return;
        }
        canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
    }
    public void setPosition () {
        this.xPosition = rect.centerX();
        this.yPosition = rect.centerY();
    }


    public int getLeft_change () {
        return this.left_change;
    }
    public void setLeft_change (int left_change) {
        this.left_change = left_change;
    }
    public boolean isHit1 (Obstacle obstacle, Bitmap bitmap, int width, int height) {
        //プレイヤーが棒とぶつかる場合
        if (height - bitmap.getHeight() <= obstacle.yPosition && obstacle.yPosition <= height ) {
            return !(0 < this.left_change + bitmap.getWidth() && this.left_change + bitmap.getWidth() < obstacle.xPosition || (obstacle.xPosition + (width - bitmap.getWidth() * 2)) < this.left_change && this.left_change < width);
        } else {
            return false;
        }
    }
    public boolean isHit2 (Obstacle obstacle, Bitmap bitmap, int width, int height){
        if (height - bitmap.getHeight() <= obstacle.yPosition && obstacle.yPosition <= height ) {
            return !(obstacle.getLeft_widthX() < this.left_change && (this.left_change + bitmap.getWidth()) < obstacle.getLeft_widthX() + bitmap.getWidth() * 2);
        } else {
            return false;
        }
     }
    public void Hit () {
        this.state = STATE_DESTROYED;
    }
}