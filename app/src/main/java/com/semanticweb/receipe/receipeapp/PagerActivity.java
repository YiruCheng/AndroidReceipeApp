package com.semanticweb.receipe.receipeapp;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.semanticweb.receipe.receipeapp.Model.LanguageItem;
import com.semanticweb.receipe.receipeapp.Model.ReceipeAppModel;
import com.semanticweb.receipe.receipeapp.Model.SPARQLQueryEngine;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PagerActivity extends FragmentActivity {
	
	private String ingredient;
	private String imageURL;
	private SPARQLQueryEngine queryEngine = new SPARQLQueryEngine();
	private ImageView image;
	private ViewPager pager;
	private Bitmap bm;
	private Button addBtn;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pager);
		
		Intent intent = getIntent();
		ingredient = intent.getStringExtra("ingredient");
		imageURL = intent.getStringExtra("imageURL");

		pager = (ViewPager) findViewById(R.id.viewpager);
		image = (ImageView) findViewById(R.id.ingre_img);
		addBtn = (Button) findViewById(R.id.addBtn);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);

//		add this ingredient into list
		addBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(ReceipeAppModel.selectedIngredientList.contains(ingredient)){
                    Toast.makeText(PagerActivity.this, R.string.toast_ingredient_already_selected, Toast.LENGTH_SHORT).show();
                }else{
                    ReceipeAppModel.selectedIngredientList.add(ingredient);
                    PagerActivity.super.onBackPressed();
                }
			}
		});
		
		Thread thread = new Thread(detailFromDBpedia);
        thread.start();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu, menu);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
			return rootView;
		}
	}

//	parse URL from DBpedia, which stored in MediaWiki is not a actual URL link.
//	E.g. http://commons.wikimedia.org/wiki/Special:FilePath/Oeufs002b.jpg?width=300
//	and also create fragment for abstract in each language
	private final String USER_AGENT = "Mozilla/5.0";
	private Handler handler = new Handler();
	private Runnable detailFromDBpedia = new Runnable() {
		@Override
		public void run() {

			try {
//				query MediaWiki for the actual URL
				int start = imageURL.lastIndexOf("/");
				int end = imageURL.indexOf("?");
				String temp = imageURL.substring(start+1, end);
				String queryMediaWiki = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:"+temp;
				URL url = new URL(queryMediaWiki);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", USER_AGENT);
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

//				parse the response in JSON format
				JSONObject jObject0 = new JSONObject(response.toString());
				JSONObject jObject1 = new JSONObject(jObject0.getString("query"));
				jObject0 = new JSONObject(jObject1.getString("pages"));
				String pageID = jObject0.names().getString(0);
				jObject1 = new JSONObject(jObject0.getString(pageID));
				JSONArray jArray = new JSONArray(jObject1.getString("imageinfo"));
				String realURL = jArray.getJSONObject(0).getString("url");

//				set image
				url = new URL(realURL);
				conn = (HttpURLConnection) url.openConnection();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 10;
				bm = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<Map<String, String>> data = queryEngine.getIngredientDetail(ingredient);
			final List<Fragment> fragments = setFragments(ingredient, data);
			
			handler.post(new Runnable() {
				@Override
				public void run() {
					IngredientPageAdapter adapter = new IngredientPageAdapter(getFragmentManager(), fragments);
					pager.setAdapter(adapter);
					image.setImageBitmap(bm);
					pager.setVisibility(View.VISIBLE);
					image.setVisibility(View.VISIBLE);
					addBtn.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			});
		}
	};

	private List<Fragment> setFragments(String ingredient, List<Map<String, String>> result){
		List<Fragment> fragments = new ArrayList<Fragment>();
		List<LanguageItem> languages = TranslateActivity.languages;
		
		for(Map<String, String> item : result){
//			find corresponded language
			for(LanguageItem l : languages){
				if(item.get("language").equals(l.getAbbreviation()))
					item.put("language", l.getName());
			}
			fragments.add(IngredientFragment.newInstance(ingredient, item.get("info"), item.get("language")));
		}
		return fragments;
	}
}
