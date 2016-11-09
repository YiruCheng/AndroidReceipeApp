package com.semanticweb.receipe.receipeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.semanticweb.receipe.receipeapp.Model.ReceipeAppModel;
import com.semanticweb.receipe.receipeapp.Utilities.IgnoreCaseComparator;

import org.hamcrest.Matchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.R.id.list;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;

public class SelectIngredientActivity extends AppCompatActivity {

    //ingredient List View
    private ListView ingredientListView;
    //search Recepe Button
    private Button searchReceipeButton;
    //resetButton
    private Button resetReceipeButton;
    //progress Bar while loading ingredients
    private ProgressDialog progress;
    private EditText textView;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //read file from Assets
        try {
            if (ReceipeAppModel.ingredientList.isEmpty())
                readFromAssets(this.getBaseContext(),"food_names.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //set the views
        ingredientListView = (ListView) findViewById(R.id.ingredientList);
        searchReceipeButton = (Button)findViewById(R.id.searchButton);
        resetReceipeButton = (Button)findViewById(R.id.resetButton);
        progress = new ProgressDialog(this);
        textView = (EditText) findViewById(R.id.etSearch);

        progress.setTitle("Loading Food");
        progress.setMessage("...Wait Please Wait...");
        progress.setCancelable(false);

//        textView.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//                // When user changed the Text
//               arrayAdapter.getFilter().filter(cs);
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                          int arg3) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                // TODO Auto-generated method stub
//            }
//        });

        searchReceipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String textToSearch = (String) textView.getText().toString();
                if (!textToSearch.isEmpty()) {
                    progress.show();
                 List filteredList =  select(ReceipeAppModel.ingredientList, having(on(String.class),
                            Matchers.containsString(textToSearch)));

                    populateIngredientList(filteredList);
                    progress.dismiss();
                    //arrayAdapter.getFilter().filter(textToSearch);
                }
            }
        });

        resetReceipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //populate all the list
                populateIngredientList();
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

    public void populateIngredientList(List ingredientSearchList){
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                ingredientSearchList );
        ingredientListView.setAdapter(arrayAdapter);

        //set items checked
        for(String ingredient : ReceipeAppModel.selectedIngredientList){
            int index = ReceipeAppModel.ingredientList.indexOf(ingredient);
            ingredientListView.setItemChecked(index,true);
        }
    }

    public void populateIngredientList(){
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        arrayAdapter = new ArrayAdapter<String>(
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


    public  String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();

        int count = 0;
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
            ReceipeAppModel.ingredientList.add(mLine);

            count++;

        }

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                //Start loading screen
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progress.show();
//                    }
//                });
//
//                //sorting the List
//                Collections.sort(ReceipeAppModel.ingredientList);
//
//                //Stop loading screen
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progress.dismiss();
//                    }
//                });
//            }
//        });

        reader.close();
        return sb.toString();
    }
}
