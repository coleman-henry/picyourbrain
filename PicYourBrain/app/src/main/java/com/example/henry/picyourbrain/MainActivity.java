package com.example.henry.picyourbrain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//TODO: create a gallery
//TODO: hook up the gallery to main activity

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {

    private Button mStartDrawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartDrawing = findViewById(R.id.button_start_draw_activity);
        mStartDrawing.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case(R.id.button_start_draw_activity):
                Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                startActivity(intent);
                break;
        }
    }
}
