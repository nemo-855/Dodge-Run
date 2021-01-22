package com.dodgerun.dodgerun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements GameView.EventCallBack{
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleScreen();
    }
    public void setGameScreen () {
        gameView = new GameView(this);
        gameView.setEventCallBack(this);
        setContentView(gameView);
    }
    public void setTitleScreen () {
        setContentView(R.layout.activity_main);
        Button startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(v -> setGameScreen());
        Button howTo = findViewById(R.id.howTo);
        howTo.setOnClickListener(v -> setHowToScreen());
    }
    public void setHowToScreen () {
        setContentView(R.layout.activity_main_howto);
        Button returnTitle = findViewById(R.id.returnTitle);
        returnTitle.setOnClickListener(v -> setTitleScreen());
    }
    @Override
    public void onGameOver (long score) {
        gameView.stopDrawThread();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}
}