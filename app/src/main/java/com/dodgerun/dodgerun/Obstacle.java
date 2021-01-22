package com.dodgerun.dodgerun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Obstacle extends BaseObject{
    private float StrokeWidth = 20.0f;
    Paint paint = new Paint ();

    private final int left_widthX;
    public Obstacle (int randomX1, int randomX2) {
        this.yPosition = 0;
        this.xPosition = randomX1;
        this.left_widthX = randomX2;
    }
    //このwidthとheightはcanvas.getWidth();にしたい
    public void draw1(Canvas canvas, Bitmap bitmap, int width, int height){
        // ペイントする色の設定
        paint.setColor(Color.argb(255, 75, 176, 208));
        // ペイントストロークの太さを設定
        paint.setStrokeWidth(StrokeWidth);
        // Styleのストロークを設定する
        paint.setStyle(Paint.Style.STROKE);
        // drawRectを使って矩形を描画する、引数に座標を設定
        // (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)
        canvas.drawLine(xPosition, yPosition, (xPosition + (width - bitmap.getWidth() * 2)), yPosition, paint);
    }
    public void draw2(Canvas canvas, Bitmap bitmap, int width, int height){
        // ペイントする色の設定
        paint.setColor(Color.argb(255, 75, 176, 208));
        // ペイントストロークの太さを設定
        paint.setStrokeWidth(StrokeWidth);
        // Styleのストロークを設定する
        paint.setStyle(Paint.Style.STROKE);
        // drawRectを使って矩形を描画する、引数に座標を設定
        // (x1,y1,x2,y2,paint) 左上の座標(x1,y1), 右下の座標(x2,y2)
        canvas.drawLine(0, yPosition, left_widthX, yPosition, paint);
        canvas.drawLine(left_widthX + bitmap.getWidth() * 2, yPosition, width, yPosition, paint);
    }
    public void move (Bitmap bitmap) {
        yPosition += bitmap.getHeight() * 0.1;
    }
    public int getLeft_widthX () {
        return this.left_widthX;
    }
}
