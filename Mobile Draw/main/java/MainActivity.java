package edu.stanford.cs108.mobiledraw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
    private CustomView canvas;
    RadioButton erase;
    RadioButton select;
    RadioButton oval;
    RadioButton rect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        erase = findViewById(R.id.erase);
        oval = findViewById(R.id.oval);
        rect = findViewById(R.id.rect);
        select = findViewById(R.id.select);
        canvas = findViewById(R.id.customView);
    }

    public void onUpdate(View view){
        canvas.UpdateGraph();
    }

}
