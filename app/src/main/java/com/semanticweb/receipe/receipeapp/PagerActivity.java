package com.semanticweb.receipe.receipeapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.semanticweb.receipe.receipeapp.Model.LanguageItem;
import com.semanticweb.receipe.receipeapp.Model.ReceipeAppModel;
import com.semanticweb.receipe.receipeapp.Model.SPARQLQueryEngine;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Pagers for information of ingredient in every language(if DBpedia has that data).
 * @author Yi-Ru
 *
 */
public class PagerActivity extends FragmentActivity {
	
	private String ingredient;
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
//		imageURL = intent.getStringExtra("imageURL");

		pager = (ViewPager) findViewById(R.id.viewpager);
		image = (ImageView) findViewById(R.id.ingre_img);
		addBtn = (Button) findViewById(R.id.addBtn);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);

//		add this ingredient into list
		addBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
            	final String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
				if(ReceipeAppModel.selectedIngredientList.contains(after_capitalized+":1") || ReceipeAppModel.selectedIngredientList.contains(after_capitalized+":0")){
                    Toast.makeText(PagerActivity.this, R.string.toast_ingredient_already_selected, Toast.LENGTH_SHORT).show();
                }else{
                	AlertDialog.Builder builder = new AlertDialog.Builder(PagerActivity.this);
                	builder.setTitle("Select Priority");
                	builder.setMessage("");
                	builder.setPositiveButton("High", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	ReceipeAppModel.selectedIngredientList.add(after_capitalized+":1");
                            PagerActivity.super.onBackPressed();
                        }
                    });

                    builder.setNegativeButton("Low", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	ReceipeAppModel.selectedIngredientList.add(after_capitalized+":0");
                            PagerActivity.super.onBackPressed();
                        }
                    });

                    builder.show();
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

	private Handler handler = new Handler();
	private Runnable detailFromDBpedia = new Runnable() {
		@Override
		public void run() {

//			set image
			bm = queryEngine.getImage(getApplicationContext(),ingredient);
			final List<Map<String, String>> data = queryEngine.getIngredientDetail(ingredient);
			final List<Fragment> fragments = setFragments(ingredient, data);
			
			handler.post(new Runnable() {
				@Override
				public void run() {
					IngredientPageAdapter adapter = new IngredientPageAdapter(getFragmentManager(), fragments);
					pager.setAdapter(adapter);
					image.setImageBitmap(bm);
					pager.setVisibility(View.VISIBLE);
					image.setVisibility(View.VISIBLE);
//					hidden add button
					if(Boolean.valueOf(data.get(0).get("btnFlag")))
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
