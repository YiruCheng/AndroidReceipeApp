package com.semanticweb.receipe.receipeapp;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.semanticweb.receipe.receipeapp.Model.ReceipeAppModel;
import com.semanticweb.receipe.receipeapp.Model.RecommendListAdapter;
import com.semanticweb.receipe.receipeapp.Utilities.ParseImg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

public class ShowRecommendationActivity extends AppCompatActivity {
	
	private ListView recommendList;
	private ProgressBar progressBar;
	
	private List<String> ingredientsList = ReceipeAppModel.selectedIngredientList;
	private List<Map<String, String>> data;
	private List<Bitmap> imgList;
	private ParseImg parseImg = new ParseImg();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recommendation);
		
		recommendList = (ListView) findViewById(R.id.recommendList);
		progressBar = (ProgressBar) findViewById(R.id.progressBar_recommend);
		ReceipeAppModel.allRecipesFromServer.clear();
        
        Thread thread = new Thread(connectToServer);
        thread.start();
        
        recommendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Intent intent = new Intent(ShowRecommendationActivity.this, RecipeDetailActivity.class);
				intent.putExtra("recipe", ReceipeAppModel.allRecipesFromServer.get(position).toString());
                startActivity(intent);
			}
		});
    }
    
    
    private Handler handler = new Handler();
    private Runnable connectToServer = new Runnable() {

		@Override
    	public void run() {
    		final SocketConnection socket = new SocketConnection(ingredientsList);
    		try {
				socket.send();
				String json_temp = socket.receiver();
				
//				InputStream is = getAssets().open("recipe.json");
//				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//				String json_temp = "";
//				while (br.ready()) {
//					json_temp += br.readLine();
//				}
//			    is.close();

			    data = new ArrayList<Map<String, String>>();
			    imgList = new ArrayList<Bitmap>();
			    JSONArray allRecipes = new JSONArray(json_temp);
			    for(int i = 0; i < allRecipes.length(); i++){
			    	JSONObject recipe = allRecipes.getJSONObject(i);
			    	ReceipeAppModel.allRecipesFromServer.add(recipe);
				    String name = recipe.getString("recipeName");
				    String description = recipe.getString("description");
				    String imgURL = recipe.getString("imgURL");
				    System.out.println("imgURL: "+imgURL);
				    Bitmap bm = parseImg.parseFromURL(getApplicationContext(), imgURL);
				    imgList.add(bm);
				    
				    Map<String, String> item = new HashMap<String, String>();
				    item.put("name", name);
				    item.put("description", description);
				    data.add(item);
			    }
			    
			    handler.post(new Runnable() {
			    	@Override
					public void run() {
			    		String[] from = new String[]{"name", "description"};
			    		RecommendListAdapter adapter = new RecommendListAdapter(ShowRecommendationActivity.this, data, imgList, from);
						recommendList.setAdapter(adapter);
						
						recommendList.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
			    	}
			    });
				
			} catch (IOException  | JSONException e) {
				e.printStackTrace();
			}
    	}
    };
}
