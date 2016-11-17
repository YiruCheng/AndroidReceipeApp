package com.semanticweb.receipe.receipeapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
    //ingredeintSearchList
    List ingredientSearchListGlobal;
    //check whether search list is visible
    Boolean searchList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ingredient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //read file from Assets
        try {
            if (ReceipeAppModel.ingredientList.isEmpty())
                readFromAssets(this.getBaseContext(),"foodlist.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //set back button to true
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

                                                          //unselect the listview
                                                          ingredientListView.setItemChecked(position,false);


                                                          if (!searchList){

                                                              String ingredientSelected = ReceipeAppModel.ingredientList.get(position);
                                                              ingredientSelected = ingredientSelected.substring(0,ingredientSelected.indexOf(":"));

                                                              if((ReceipeAppModel.selectedIngredientList.indexOf(ingredientSelected+":0") == -1 )&&
                                                                      ( ReceipeAppModel.selectedIngredientList.indexOf(ingredientSelected+":1") == -1) )
                                                                showDialogAndAddIngredientWithPriority(SelectIngredientActivity.this,"Select Priority","",position);
                                                              else{
                                                                  if ((ReceipeAppModel.selectedIngredientList.indexOf(ingredientSelected+":0") != -1 )){
                                                                      //remove ingredient with low priority
                                                                      ReceipeAppModel.selectedIngredientList.remove(ingredientSelected+":0");
                                                                  }else{
                                                                      //remove ingredient with high priority
                                                                      ReceipeAppModel.selectedIngredientList.remove(ingredientSelected+":1");
                                                                  }
                                                                  populateIngredientList();
                                                              }
                                                          }else {

                                                              String ingredientSelected = (String) ingredientSearchListGlobal.get(position);
                                                              ingredientSelected = ingredientSelected.substring(0,ingredientSelected.indexOf(":"));

                                                              if((ReceipeAppModel.selectedIngredientList.indexOf(ingredientSelected+":0") == -1 )&&
                                                                      ( ReceipeAppModel.selectedIngredientList.indexOf(ingredientSelected+":1") == -1) )
                                                                  showDialogAndAddIngredientWithPriority(SelectIngredientActivity.this,"Select Priority","",position);
                                                              else{
                                                                  if ((ReceipeAppModel.selectedIngredientList.indexOf(ingredientSelected+":0") != -1 )){
                                                                      //remove ingredient with low priority
                                                                      ReceipeAppModel.selectedIngredientList.remove(ingredientSelected+":0");
                                                                  }else{
                                                                      //remove ingredient with high priority
                                                                      ReceipeAppModel.selectedIngredientList.remove(ingredientSelected+":1");
                                                                  }
                                                                  populateIngredientList(ingredientSearchListGlobal);
                                                              }
                                                          }


//                                                          String ingredient;
//                                                          if(!searchList){
//                                                              //search from ingredient List
//                                                              ingredient = ReceipeAppModel.ingredientList.get(position);
//
//                                                              if (ReceipeAppModel.selectedIngredientList.contains(ingredient)){
//                                                                  ReceipeAppModel.selectedIngredientList.remove(ingredient);
//                                                              }else {
//                                                                  ReceipeAppModel.selectedIngredientList.add(ingredient);
//                                                              }
//
//                                                              populateIngredientList();
//                                                          }else{
//                                                              ingredient = (String) ingredientSearchListGlobal.get(position);
//                                                              if (ReceipeAppModel.selectedIngredientList.contains(ingredient)){
//                                                                  ReceipeAppModel.selectedIngredientList.remove(ingredient);
//                                                              }else {
//                                                                  ReceipeAppModel.selectedIngredientList.add(ingredient);
//                                                              }
//
//                                                              populateIngredientList(ingredientSearchListGlobal);
//                                                          }
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
        ingredientSearchListGlobal = ingredientSearchList;

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_checked,
                ingredientSearchList );
        ingredientListView.setAdapter(arrayAdapter);

        //set items checked
        for(String ingredient : ReceipeAppModel.selectedIngredientList){
            //normalize value to zero because in ingredient list we have all zero values
            ingredient = ingredient.replace(":1",":0");
            if(ingredientSearchListGlobal.indexOf(ingredient) != -1) {
                int index = ingredientSearchListGlobal.indexOf(ingredient);
                ingredientListView.setItemChecked(index, true);
            }
        }
        searchList = true;
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
            //normalize value to zero because in ingredient list we have all zero values
            ingredient = ingredient.replace(":1",":0");
            if(ReceipeAppModel.ingredientList.indexOf(ingredient) != -1) {
                int index = ReceipeAppModel.ingredientList.indexOf(ingredient);
                ingredientListView.setItemChecked(index, true);
            }
        }
        searchList = false;
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
            //we set the priority to 0 for every object by append :0
            ReceipeAppModel.ingredientList.add(mLine+":0");

            count++;

        }

        reader.close();
        return sb.toString();
    }

    //for showring Alert View for Adding Priority
    public void showDialogAndAddIngredientWithPriority(Activity activity, String title, CharSequence message, final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);



        builder.setMessage(message);
        builder.setPositiveButton("High", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredient;
                if(!searchList){
                    //search from ingredient List
                    ingredient = ReceipeAppModel.ingredientList.get(index);
                    ingredient = ingredient.replace(":0",":1");
                    ReceipeAppModel.selectedIngredientList.add(ingredient);
                    populateIngredientList();
                }else{
                    ingredient = (String) ingredientSearchListGlobal.get(index);
                    //add high priority
                    ingredient = ingredient.replace(":0",":1");
                    ReceipeAppModel.selectedIngredientList.add(ingredient);
                    populateIngredientList(ingredientSearchListGlobal);
                }

            }
        });

        builder.setNegativeButton("Low", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredient;
                if(!searchList){
                    //search from ingredient List
                    ingredient = ReceipeAppModel.ingredientList.get(index);
                    ReceipeAppModel.selectedIngredientList.add(ingredient);
                    populateIngredientList();
                }else{
                    ingredient = (String) ingredientSearchListGlobal.get(index);
                        ReceipeAppModel.selectedIngredientList.add(ingredient);
                    populateIngredientList(ingredientSearchListGlobal);
                }

            }
        });

        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
