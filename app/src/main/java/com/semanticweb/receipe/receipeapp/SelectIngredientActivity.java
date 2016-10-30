package com.semanticweb.receipe.receipeapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.semanticweb.receipe.receipeapp.Model.ReceipeAppModel;

import java.util.ArrayList;
import java.util.List;

public class SelectIngredientActivity extends AppCompatActivity {

    //ingredient List View
    private ListView ingredientListView;
    //search Recepe Button
    private Button searchReceipeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the views
        ingredientListView = (ListView) findViewById(R.id.ingredientList);
        searchReceipeButton = (Button)findViewById(R.id.searchButton);

        //search Button Clicked
        searchReceipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //:TODO send HTTP Request to Server for recepe
            }
        });

        ingredientListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //populate ingredient list
        populateIngredientList();

        ingredientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                      @Override
                                                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                          String ingredient = ReceipeAppModel.ingredientList.get(position);
                                                          if (ReceipeAppModel.selectedIngredientList.contains(ingredient)){
                                                              ReceipeAppModel.selectedIngredientList.remove(ingredient);
                                                          }else {
                                                              ReceipeAppModel.selectedIngredientList.add(ingredient);
                                                          }
                                                          populateIngredientList();
                                                      }
                                                  });
    }

    //:TODO check which items are in selected List View set them as selected

    //call it when you receive data from Server
    private void onIngredientsReceived(){
        //check if the ingredient is not in the list add
        //:TODO append in the list
    }

    public void populateIngredientList(){
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                ReceipeAppModel.ingredientList );
        ingredientListView.setAdapter(arrayAdapter);

        //set items checked
        for(String ingredient : ReceipeAppModel.selectedIngredientList){
            int index = ReceipeAppModel.ingredientList.indexOf(ingredient);
            ingredientListView.setItemChecked(index,true);
        }
    }

}
