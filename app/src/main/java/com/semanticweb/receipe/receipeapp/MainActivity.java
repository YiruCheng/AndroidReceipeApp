package com.semanticweb.receipe.receipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static List<String> ingredientList = new ArrayList<>();
    static List<String> selectedIngredientList = new ArrayList<>();

    //UI Buttons
    private Button addIngredientButton;
    private Button showRecommendationButton;
    private ListView ingredientListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //just for testing
        MainActivity.ingredientList.add("butter");
        MainActivity.ingredientList.add("eggs");

        //get the view components
        addIngredientButton = (Button) findViewById(R.id.addButton);
        showRecommendationButton = (Button) findViewById(R.id.recommendButton);
        ingredientListView = (ListView)findViewById(R.id.list_ingredient);

        addIngredientButton.setOnClickListener(this);
        showRecommendationButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //we want to show ingredient list when ever view loads
        populateIngredientList();
    }

    // Have to implement with the OnClickListner
    // onClick is called when a view has been clicked.
    @Override
    public void onClick(View v) { // Parameter v stands for the view that was clicked.

        if (v == addIngredientButton) {
            Intent intent = new Intent(MainActivity.this, SelectIngredientActivity.class);
            startActivity(intent);
        }else if(v == showRecommendationButton){
            Intent intent = new Intent(MainActivity.this, ShowRecommendationActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //populates the ingredient List
    public void populateIngredientList(){
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                MainActivity.selectedIngredientList );
        ingredientListView.setAdapter(arrayAdapter);

      }
}