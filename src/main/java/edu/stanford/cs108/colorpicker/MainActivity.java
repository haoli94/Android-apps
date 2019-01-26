package edu.stanford.cs108.colorpicker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void setColor(View view){
        SeekBar red = (SeekBar) findViewById(R.id.redBar);
        int redVal = red.getProgress();
        SeekBar green = (SeekBar) findViewById(R.id.greenBar);
        int greenVal = green.getProgress();
        SeekBar blue = (SeekBar) findViewById(R.id.blueBar);
        int blueVal = blue.getProgress();

        View show = findViewById(R.id.colorWindow);
        show.setBackgroundColor(Color.rgb(redVal, greenVal, blueVal));
        String colInfo = "";
        colInfo += "Red: " + redVal + ", ";
        colInfo += "Green: " + greenVal + ", ";
        colInfo += "Blue: " + blueVal;
        TextView display = findViewById(R.id.colorInfo);
        display.setText(colInfo);

    }
}
