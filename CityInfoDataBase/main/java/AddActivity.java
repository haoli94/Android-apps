package edu.stanford.cs108.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    SQLiteDatabase database;
    ContentValues values = new ContentValues();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        database = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);

    }
    public void onAdd(View view) {
        EditText nameEdit = (EditText) findViewById(R.id.name);
        EditText continentEdit = (EditText) findViewById(R.id.continent);
        EditText populationEdit = (EditText) findViewById(R.id.population);

        String name = nameEdit.getText().toString().trim();
        String continent = continentEdit.getText().toString().trim();
        String population = populationEdit.getText().toString().trim();

        values.put("name",name.equals("")?"NA":name);
        values.put("continent",continent.equals("") ? "NA":continent);
        values.put("population",population.equals("") ? "NA":population);

        database.insertWithOnConflict("cities", null, values, SQLiteDatabase.CONFLICT_REPLACE);

        values.clear();
        Toast.makeText(AddActivity.this, name+" Added", Toast.LENGTH_SHORT).show();

    }

}
