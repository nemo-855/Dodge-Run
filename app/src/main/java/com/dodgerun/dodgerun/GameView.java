package com.dodgerun.dodgerun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    //簡単に技術的に言えば、描画用のスレッドを持った View を作るときは、SurfaceView を使います。
    private static final long VIBRATION_HIT = 200;
    public interface EventCallBack {
        void onGameOver(long score);
    }
    //インターフェースの使い方
    //インターフェースは下のように呼び出す
    private EventCallBack eventCallBack;
    public void setEventCallBack (EventCallBack eventCallBack) {
        this.eventCallBack = eventCallBack;
    }
    private Handler handler = new Handler();
    //https://re-engines.com/2019/12/19/%E3%80%90java%E3%80%91handler%E3%82%AF%E3%83%A9%E3%82%B9%E3%81%AB%E3%81%A4%E3%81%84%E3%81%A6%E3%81%BE%E3%81%A8%E3%82%81%E3%81%A6%E3%81%BF%E3%81%BE%E3%81%97%E3%81%9F/
    //handlerについて
    //Surfaceholder.callbackについて
    // http://256products.blogspot.com/2014/07/androidlistenercallback.html

    private static final float SCORE_TEXT_SIZE = 60.0f;
    private long score;
    private final Paint paintScore = new Paint();
    private final Vibrator vibrator;
    public GameView(Context context) {
        super(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        paintScore.setColor(Color.BLACK);
        paintScore.setTextSize(SCORE_TEXT_SIZE);
        paintScore.setAntiAlias(true);
        getHolder().addCallback(this);
    }

    private static long DRAW_INTERVAL = 25;
    //threadクラスで使うからstatic
    private Random ran = new Random();
    private Player player;
    private List<Obstacle> obstacleList = new ArrayList<>();

    public static final int LEFT_FLICK= 0;
    public static final int RIGHT_FLICK = 1;
    public static final int UP_FLICK = 2;
    public static final int DOWN_FLICK = 3;

    private float adjustX = 150.0f;
    private float adjustY = 150.0f;
    private float touchX;
    private float touchY;
    private float nowTouchX;
    private float nowTouchY;

    private final Bitmap playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);

    private DrawThread drawThread;

    private class DrawThread extends Thread {
        private AtomicBoolean isFinished = new AtomicBoolean();

        //複数スレッドで処理するからAtomicBoolean
        private void finish() {
            isFinished.set(true);
        }

        @Override
        public void run() {
            SurfaceHolder holder = getHolder();
            while (!isFinished.get()) {
                if (holder.isCreating()) {
                    continue;
                }
                Canvas canvas = holder.lockCanvas();
                if (canvas == null) {
                    continue;
                }
                score += 10;
                drawGame(canvas);
                holder.unlockCanvasAndPost(canvas);

                // threadの排他処理　処理する順番を制御する
                // https://cyzennt.co.jp/blog/2019/12/07/java%E3%81%A7%E3%81%AE%E3%82%B9%E3%83%AC%E3%83%83%E3%83%89%E5%88%B6%E5%BE%A1%EF%BC%88join%E3%81%A8synchronized%EF%BC%89/
                // joinが前のが完全に終わってから次、synchronizedが一個づつ交互
                synchronized (this) {
                    try {
                        wait(DRAW_INTERVAL);
                    } catch (InterruptedException e) {
                        //https://docs.oracle.com/javase/jp/6/api/java/lang/InterruptedException.html
                        //threadの処理に割り込みがあったときに発生する例外
                        //https://techacademy.jp/magazine/19517
                    }
                }
            }
        }
    }

    private void startDrawThread(){
        drawThread = new DrawThread();
        drawThread.start();
    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }

        drawThread.finish();
        drawThread = null;
        return true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //描画処理
        startDrawThread();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    protected void drawGame(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (player == null) {
            player = new Player(playerBitmap, width);
        }
//
        Obstacle lo = launchObstacle(playerBitmap, width);
        obstacleList.add(lo);
        drawObstacleList(canvas, obstacleList, playerBitmap, width, height);

        //
        player.draw(canvas, height);
        player.setPosition();
        canvas.drawText("Score: " + score, 0, SCORE_TEXT_SIZE, paintScore);
    }

    private Obstacle launchObstacle(Bitmap bitmap, int width) {
        int randomX1 = ran.nextInt(bitmap.getWidth() * 2);
        int randomX2 = ran.nextInt((width - bitmap.getWidth() * 2));
        return new Obstacle(randomX1, randomX2);
    }

    private void drawObstacleList(Canvas canvas, List<Obstacle> obstacleList, Bitmap bitmap, int width, int height) {
        for (int i = 0; i < obstacleList.size(); i++) {
            Obstacle ob = obstacleList.get(i);
            if (i % 78 == 0) {
                ob.draw1(canvas, bitmap, width, height);
                if (player.isHit1(ob, bitmap, width, height)) {
                    player.Hit();
                    vibrator.vibrate(VIBRATION_HIT);
                    handler.post(() -> eventCallBack.onGameOver(score));
                }
            } else  if (i % 78 == 26 || i % 78 == 52) {
                ob.draw2(canvas, bitmap, width, height);
                if (player.isHit2(ob, bitmap, width, height)) {
                    player.Hit();
                    vibrator.vibrate(VIBRATION_HIT);
                    handler.post(() -> eventCallBack.onGameOver(score));
                }
            }
            ob.move(bitmap);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                nowTouchX = event.getX();
                nowTouchY = event.getY();
                check();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
    private void check(){
        // 左フリック
        if(touchX > nowTouchX)
        {
            if(touchX - nowTouchX > adjustX)
            {
                getFlick(LEFT_FLICK);
                return;
            }
        }
        // 右フリック
        if(nowTouchX > touchX)
        {
            if(nowTouchX - touchX > adjustX)
            {
                getFlick(RIGHT_FLICK);
                return;
            }
        }
        // 上フリック
        if(touchY > nowTouchY)
        {
            if(touchY - nowTouchY > adjustY)
            {
                getFlick(UP_FLICK);
                return;
            }
        }
        // 下フリック
        if(nowTouchY > touchY)
        {
            if(nowTouchY - touchY > adjustY)
            {
                getFlick(DOWN_FLICK);
                return;
            }
        }
    }
    public void getFlick (int direct) {
        switch (direct){
            case LEFT_FLICK:
                player.setLeft_change(player.getLeft_change() - 180);
                break;
            case RIGHT_FLICK:
                player.setLeft_change(player.getLeft_change() + 180);
                break;
        }
    }
}