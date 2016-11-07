package com.semanticweb.receipe.receipeapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.semanticweb.receipe.receipeapp.Model.LanguageItem;
import com.semanticweb.receipe.receipeapp.Model.SPARQLQueryEngine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class TranslateActivity extends AppCompatActivity {
	
	private Button translateBtn;
	private Spinner languageList;
	private EditText inputIngredient;
	private ListView resultList;
	private SPARQLQueryEngine queryEngine = new SPARQLQueryEngine();
	protected static List<LanguageItem> languages;
	private List<String> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        translateBtn = (Button) findViewById(R.id.translate);
    	languageList = (Spinner) findViewById(R.id.ori_language);
    	inputIngredient = (EditText) findViewById(R.id.input_ingredient);
    	resultList = (ListView) findViewById(R.id.resultList);
    	
//    	Set up language list
    	languages = new ArrayList<LanguageItem>();
		try {
			InputStream is = getAssets().open("languageList.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while (br.ready()) {
				LanguageItem item = new LanguageItem();
				String[] temp = br.readLine().split(",");
				item.setAbbreviation(temp[0]);
				item.setName(temp[1]);
				languages.add(item);
			}
		    is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	ArrayAdapter<LanguageItem> adapter = new ArrayAdapter<LanguageItem>(this, android.R.layout.simple_spinner_item, languages);
    	languageList.setAdapter(adapter);
    	
    	translateBtn.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {
    			Thread thread = new Thread(tranlationFromDBpedia);
                thread.start();
    		}
    	});

    	resultList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, final int position, long id) {
//				Map<String, String> items = (Map<String, String>) resultList.getItemAtPosition(position);
				Intent intent = new Intent(TranslateActivity.this, PagerActivity.class);
				intent.putExtra("ingredient", resultList.getItemAtPosition(position).toString());
                startActivity(intent);
                //check if the ingredient is already in the main list if not add it
//                if(ReceipeAppModel.selectedIngredientList.contains(ingredient)){
//                    Toast.makeText(TranslateActivity.this, R.string.toast_ingredient_already_selected, Toast.LENGTH_SHORT).show();
//                }else{
//                    ReceipeAppModel.selectedIngredientList.add(ingredient);
//                    TranslateActivity.super.onBackPressed();
//                }
//				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//				ClipData clip = ClipData.newPlainText("WordKeeper", copiedString);
//				clipboard.setPrimaryClip(clip);
//				Toast.makeText(TranslateActivity.this, copiedString+" has been copied!", Toast.LENGTH_SHORT).show();
			}
		});
    	
	}

	private Handler handler = new Handler();
	private Runnable tranlationFromDBpedia = new Runnable() {
		
		@Override
		public void run() {
			String ingredient = inputIngredient.getText().toString();
			String language = ((LanguageItem)languageList.getSelectedItem()).getAbbreviation();
			data = queryEngine.getTranslation(ingredient, language);
			
//			Assign DBpedia result to TextView
			handler.post(new Runnable() {
				@Override
				public void run() {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TranslateActivity.this, android.R.layout.simple_list_item_1, data);
//					String[] from = new String[]{"name"};
//					int[] to = new int[]{R.id.item};
//					SimpleAdapter adapter = new SimpleAdapter(TranslateActivity.this, data, R.layout.listitem_translate, from, to);
					resultList.setAdapter(adapter);
				}
			});
		}
	};
}
