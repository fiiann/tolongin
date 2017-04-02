package com.fian.root.simplifiedcodinglogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TimelineActivity extends AppCompatActivity {


    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();

        String apiKey = pref.getString("apiKey", null);

        Toast.makeText(TimelineActivity.this,apiKey,Toast.LENGTH_LONG).show();

    }
    public void fungsi(View view){
        Intent myintent = new Intent(TimelineActivity.this,MintaTolong.class);
        startActivity(myintent);
    }
}
