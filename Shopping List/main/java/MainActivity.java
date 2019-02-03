package edu.stanford.cs108.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inventory = new ArrayList<>();
    }

    public void clearAll(View view){
        this.inventory.clear();
        TextView toshow = (TextView) findViewById(R.id.purchase);
        String display = "";
        toshow.setText(display);
    }
    public void addItem(View view){
        EditText editText =(EditText) findViewById (R.id.newvalue);
        String Text = editText.getText().toString();
        this.inventory.add(Text);

        StringBuilder display = new StringBuilder(this.inventory.get(0));
        for (int i=1;i<inventory.size();i++){
            display.append("\n");
            display.append(this.inventory.get(i));
        }

        TextView toshow = (TextView) findViewById(R.id.purchase);
        toshow.setText(display.toString());
        editText.setText("");
    }
}
