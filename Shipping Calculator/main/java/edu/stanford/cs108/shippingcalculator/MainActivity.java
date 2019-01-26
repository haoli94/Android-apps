package edu.stanford.cs108.shippingcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private double getSelectedInfo(int checkedId) {
        double factor = 0;
        switch (checkedId) {
            case R.id.next:
                factor = 10;
                break;
            case R.id.second:
                factor = 5;
                break;
            case R.id.stand:
                factor = 3;
                break;
            default:
                break;
        }
        return factor;
    }
    public void calculate(View view){
        RadioGroup radioGroup = this.findViewById(R.id.choices_group);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        Double transFactor = getSelectedInfo(checkedId);

        CheckBox insurChecked = findViewById(R.id.insuranceChecked);

        EditText inputVal = findViewById(R.id.input);
        double weight = Double.parseDouble(inputVal.getText().toString());
        double total = weight*transFactor;
        if (insurChecked.isChecked()) {
            total = 1.2*total;
        }
        Integer cost = (int)Math.round(total);

        TextView toshow = findViewById(R.id.window);

        String out = "Cost";
        out += ": $";
        out += cost.toString();
        toshow.setText(out);
    }
}
