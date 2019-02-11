package edu.stanford.cs108.database;



import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;


@SuppressLint("Registered")
public class LookupActivity extends AppCompatActivity {
    SQLiteDatabase database;

    String[] fromArray = {"name","continent","population"};
    int[] toArray = {R.id.adapter_name,R.id.adapter_continent,R.id.adapter_population};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        database = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
    }


    public void onSearch(View view) {
        EditText editTextName = (EditText) findViewById(R.id.name);
        String queryName = editTextName.getText().toString().trim();


        EditText editTextContinent = (EditText) findViewById(R.id.continent);
        String queryContinent = editTextContinent.getText().toString().trim();


        EditText editTextPopulation = (EditText) findViewById(R.id.population);
        String queryPopulation = editTextPopulation.getText().toString().trim();


        RadioGroup radioGroup = this.findViewById(R.id.choices_group);
        int checkedId = radioGroup.getCheckedRadioButtonId();

        String query = "SELECT * FROM cities WHERE";
        String comparable = (checkedId == R.id.GorE) ? ">=" : "<";

        if (!queryName.equals("")){
            query += " name LIKE \'%" + queryName + "%\'";
        }


        if (!queryName.equals("")&&!queryContinent.equals("")){
            query += " and";
        }else if (!queryName.equals("")&&!queryPopulation.equals("")){
            query += " and";
        }

        if (!queryContinent.equals("")){
            query += " continent LIKE \'%" + queryContinent + "%\'";
        }


        if (!queryContinent.equals("")&&!queryPopulation.equals("")){
            query += " and";
        }
        if (!queryPopulation.equals("")){
            query += " population " + comparable + queryPopulation;
        }

        if (queryPopulation.equals("")&&queryContinent.equals("")&&queryName.equals("")){
            query = "SELECT * FROM cities";
        }

        query += ";";
        Cursor cursor = null;
        cursor = database.rawQuery(query, null);


        if (cursor != null) {

        ListAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.adapter, cursor, fromArray, toArray, 0);
        ListView lv = (ListView)findViewById(R.id.output_text);
        lv.setAdapter(adapter);
        }


    }


}

