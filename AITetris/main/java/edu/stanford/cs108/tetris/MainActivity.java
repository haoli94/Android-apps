package edu.stanford.cs108.tetris;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TetrisUIInterface {

    BoardView boardView;
    TetrisBrainLogic TetrisBrainLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boardView = (BoardView) findViewById(R.id.board_view);
        TetrisBrainLogic = new TetrisBrainLogic(this);
        boardView.setLogic(TetrisBrainLogic);

    }

    boolean gameRunning = false;

    @Override
    public void boardUpdated() {
        boardView.invalidate();
    }

    @Override
    public void dataUpdated() {
        int score = TetrisBrainLogic.getScore();
        TextView scoreText = (TextView) findViewById(R.id.score_text);
        scoreText.setText("Score: " + score);
    }

    @Override
    public void rigGameOver() {
        gameRunning = false;
        handler.removeCallbacks(tetrisTick);
        Button startButton = (Button) findViewById(R.id.start_btn);
        startButton.setEnabled(true);
        Button stopButton = (Button) findViewById(R.id.stop_btn);
        stopButton.setEnabled(false);

        CheckBox testCheckBox = (CheckBox) findViewById(R.id.test_check);
        testCheckBox.setEnabled(true);
        CheckBox brainCheckBox = (CheckBox) findViewById(R.id.brian_check);
        brainCheckBox.setEnabled(true);
    }

    @Override
    public void rigGameInProgress() {
        Button startButton = (Button) findViewById(R.id.start_btn);
        startButton.setEnabled(false);
        Button stopButton = (Button) findViewById(R.id.stop_btn);
        stopButton.setEnabled(true);

        CheckBox testCheckBox = (CheckBox) findViewById(R.id.test_check);
        testCheckBox.setEnabled(false);
        CheckBox brainCheckBox = (CheckBox) findViewById(R.id.brian_check);
        brainCheckBox.setEnabled(false);
    }

    private class TetrisTick implements Runnable {

        @Override
        public void run() {
            if (gameRunning) {
                TetrisBrainLogic.onTick();
                handler.postDelayed(this, getTickDelay());
            }
        }
    }

    Handler handler = new Handler();
    Runnable tetrisTick = new TetrisTick();

    int tickDelay;

    int getTickDelay() {
        return tickDelay;
    }

    public void onStart(View view) {
        if (gameRunning) return;

        CheckBox testCheckBox = (CheckBox) findViewById(R.id.test_check);
        CheckBox brianCheckBox = (CheckBox) findViewById(R.id.brian_check);


        SeekBar speed = findViewById(R.id.speedBar);
        tickDelay = speed.getProgress();

        TetrisBrainLogic.setTestMode(testCheckBox.isChecked());
        TetrisBrainLogic.setBrianMode(brianCheckBox.isChecked());
        TetrisBrainLogic.onStartGame();
        gameRunning = true;
        handler.postDelayed(tetrisTick, getTickDelay());
    }

    public void onStop(View view) {
        TetrisBrainLogic.onStopGame();
        gameRunning = false;
        handler.removeCallbacks(tetrisTick);
    }

    public void handleLeft(View view) {
        if (!gameRunning) return;

        TetrisBrainLogic.onLeft();
    }
    public void handleRight(View view) {
        if (!gameRunning) return;

        TetrisBrainLogic.onRight();
    }
    public void handleRotate(View view) {
        if (!gameRunning) return;

        TetrisBrainLogic.onRotate();
    }
    public void handleDrop(View view) {
        if (!gameRunning) return;

        TetrisBrainLogic.onDrop();
    }

}
