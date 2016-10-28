package com.semanticweb.receipe.receipeapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.semanticweb.receipe.receipeapp.entity.LanguageItem;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class TranslateActivity extends AppCompatActivity {
	
	private Button translateBtn;
	private Spinner languageList;
	private EditText inputIngredient;
	private ListView resultList;
	private String queryDBpedia;
	private String httpResult;
	
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
    	List<LanguageItem> languages = new ArrayList<LanguageItem>();
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
    			Thread thread = new Thread(sendToDBpedia);
                thread.start();
    		}
    	});
    	
    	resultList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
//				Copy translation
				String copiedString = resultList.getItemAtPosition(position).toString();
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
				ClipData clip = ClipData.newPlainText("WordKeeper", copiedString);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(TranslateActivity.this, copiedString+" has been copied!", Toast.LENGTH_SHORT).show();
			}
		});
    	
	}
	
	private Handler handler = new Handler();
	private Runnable sendToDBpedia = new Runnable() {
		
		@Override
		public void run() {
//			Query from DBpedia via HTTP request
			String ingredient = inputIngredient.getText().toString();
			String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
			String language = ((LanguageItem)languageList.getSelectedItem()).getAbbreviation();
			queryDBpedia = String.format("select ?name where{?url rdfs:label \"%s\"@%s, ?name. filter (LANGMATCHES(LANG(?name), \"en\")).}", after_capitalized, language);
			System.out.println("query: "+queryDBpedia);
			GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
			urlCompany.put("format", "csv");
			urlCompany.put("query", queryDBpedia);
			httpResult = doHTTPRequest(urlCompany);
//			System.out.println("Result split: " + temp[1]);
			String[] temp = httpResult.split("\n");
			final List<String> data = new ArrayList<String>();
			for(int i = 1;i < temp.length;i++){
				data.add(temp[i].replaceAll("\"", ""));
			}
			
			
//			Assign DBpedia result to TextView
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(TranslateActivity.this, android.R.layout.simple_list_item_1, data);
					resultList.setAdapter(adapter);
				}
			});
		}
	};
	
	private static HttpTransport httpTransport = new NetHttpTransport();
    private static HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	
	public static String doHTTPRequest(GenericUrl url) {
		String result = "";
		try {
			HttpRequest request = requestFactory.buildGetRequest(url);
		    HttpResponse httpResponse = request.execute();
		    
		    result = httpResponse.parseAsString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
