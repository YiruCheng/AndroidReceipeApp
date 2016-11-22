package com.semanticweb.receipe.receipeapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.semanticweb.receipe.receipeapp.Utilities.ParseImg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * show detail of a recipe.
 * @author Yi-Ru
 *
 */
public class RecipeDetailActivity extends Activity {
	
	private ProgressBar progressBar;
	private TextView recipeName;
	private ImageView recipeImg;
	private TextView description;
	private TextView ingredients;
	private TextView preparation;
	private ListView related;
	private JSONObject recipe;
	private ParseImg parseImg = new ParseImg();
	private String imgURL;
	private Iterator<String> keys;
	private String related_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_detail);
		
		Intent intent = getIntent();
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar_detail);
		recipeName = (TextView) findViewById(R.id.detail_recipeName);
		recipeImg = (ImageView) findViewById(R.id.detail_recipeImg);
		description = (TextView) findViewById(R.id.detail_description);
		ingredients = (TextView) findViewById(R.id.detail_ingredients);
		preparation = (TextView) findViewById(R.id.detail_preparation);
		related = (ListView) findViewById(R.id.detail_related);

		try {
			recipe = new JSONObject(intent.getStringExtra("recipe"));
			recipeName.setText(recipe.getString("recipeName"));
			description.setText(recipe.getString("description"));
		    imgURL = recipe.getString("imgURL");
	        
	        JSONArray ingredients_json = recipe.getJSONArray("ingredients");
	        String ingredients_content = parseJSONArray(ingredients_json);
		    ingredients.setText(ingredients_content);
	        
		    JSONArray preparation_json = recipe.getJSONArray("preparation");
	        String preparation_content = parseJSONArray(preparation_json);
		    preparation.setText(preparation_content);
		    
		    if(!recipe.get("related").equals("")){
		    	JSONObject related_json = recipe.getJSONObject("related");
			    keys = related_json.keys();
			    final List<String> relatedNameList = new ArrayList<String>();
			    final List<String> keysList = new ArrayList<String>();
			    while(keys.hasNext()){
			    	String k = keys.next();
//			    	System.out.println("related: ");
				    relatedNameList.add(related_json.getString(k));
				    keysList.add(k);
			    }
				ArrayAdapter<String> adapter_related = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, relatedNameList);
		        related.setAdapter(adapter_related);
		        
		        related.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
						
						Thread thrd = new Thread(new Runnable() {
							@Override
							public void run() {
								SocketConnection socket = new SocketConnection();
								try {
									socket.sendRecipeID(keysList.get(position));
									related_result = socket.receiver();
								} catch (IOException e) {
									e.printStackTrace();
								}
								System.out.println("item click: "+keysList.get(position)+": "+relatedNameList.get(position).toString());
								handler.post(new Runnable() {
									@Override
									public void run() {
										Intent intent = new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class);
										intent.putExtra("recipe", related_result);
										startActivity(intent);
									}
								});
							}
						});
						thrd.start();
					}
				});
		    }
	        
	        
		    Thread thread = new Thread(searchImg);
	        thread.start();
		    
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

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
	
	private Handler handler = new Handler();
    private Runnable searchImg = new Runnable() {

		@Override
		public void run() {
		    final Bitmap bm = parseImg.parseFromURL(getApplicationContext(), imgURL);
		    handler.post(new Runnable() {

				@Override
				public void run() {
				    recipeImg.setImageBitmap(bm);
					
					recipeName.setVisibility(View.VISIBLE);
					recipeImg.setVisibility(View.VISIBLE);
					description.setVisibility(View.VISIBLE);
					ingredients.setVisibility(View.VISIBLE);
					preparation.setVisibility(View.VISIBLE);
					related.setVisibility(View.VISIBLE);
				    progressBar.setVisibility(View.GONE);
				}
		    	
		    });
		}
    	
    };
    
//    private Runnable searchRelated = 
    
    private String parseJSONArray(JSONArray j_array) throws JSONException{
    	String result = "";
	    for(int i = 0; i < j_array.length(); i++){
	    	System.out.println(j_array.getString(i));
	    	result += j_array.getString(i);
	    	if(i != j_array.length()-1)
	    		result += "\n";
	    }
		return result;
    }
}
