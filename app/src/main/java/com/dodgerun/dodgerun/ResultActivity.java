package com.dodgerun.dodgerun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = this.getIntent();
        long score = intent.getLongExtra("score", 0);
        TextView textView = this.findViewById(R.id.textView3);
        textView.setText(String.valueOf(score));
        TextView mes = this.findViewById(R.id.mes);
        if (score < 3000) {
            mes.setText("動体視力を鍛えた方が良いと思います");
        } else if (3000 <= score && score < 6000) {
            mes.setText("並の人間、といったところでしょうか");
        } else if (6000 <= score && score < 10000) {
            mes.setText("素晴らしい! 10000まであともう一息です");
        } else if (10000 <= score) {
            mes.setText("10000点をこえたけれどあなたは多くの時間を浪費しました");
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent2 = new Intent(this, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent2);
            //https://teratail.com/questions/120903
            //解説
        });

    }
    @Override
    public void onBackPressed() {}
}
