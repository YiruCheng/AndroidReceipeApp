package com.semanticweb.receipe.receipeapp.Model;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Omer on 10/29/2016.
 * 
 * Implement by Yi-Ru at 07/11/2016.
 */

public class SPARQLQueryEngine {

    private static HttpTransport httpTransport = new NetHttpTransport();
    private static HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	private String queryDBpedia;
	private String httpResult;


    /*
    * Helper Methods for interacting with SPARQL Engine
    * */
    public List<String> getTranslation(final String ingredient, final String language){
//    	Query from DBpedia via HTTP request
		String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
		queryDBpedia = null;
		queryDBpedia = String.format("select distinct ?name where{ ?url rdfs:label \"%s\"@%s, ?name. filter (LANGMATCHES(LANG(?name), \"en\"))}", after_capitalized, language);
		System.out.println("query: "+queryDBpedia);
		GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
		urlCompany.put("format", "csv");
		urlCompany.put("query", queryDBpedia);
		httpResult = doHTTPRequest(urlCompany);
		String[] temp = httpResult.split("\n");
//		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		final List<String> data = new ArrayList<String>();
		for(int i = 1;i < temp.length;i++){
			String w = temp[i].replaceAll("\"", "").toLowerCase(Locale.ENGLISH);
//			check for duplicate result
			if(!data.isEmpty()){
				for(String s : data){
					if(!s.equals(w)){
						data.add(w);
						break;
					}else{
						break;
					}
				}
			}else{
				data.add(w);
			}
		}
        return data;
    }

    public List<Map<String, String>> getIngredientDetail(final String ingredient){
    	String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
    	queryDBpedia = null;
    	queryDBpedia = String.format("select LANG(?abstract), ?abstract where { ?ingredient dbo:abstract ?abstract. ?ingredient rdfs:label \"%s\"@en, ?ingre_name. filter( LANGMATCHES(LANG(?ingre_name), \"en\") )}", after_capitalized);
    	GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
		urlCompany.put("format", "csv");
		urlCompany.put("query", queryDBpedia);
		httpResult = doHTTPRequest(urlCompany);
		String[] temp = httpResult.split("\n");
    	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    	System.out.println("length: "+temp.length);
    	if(temp.length > 1){
    		for(int i = 1;i < temp.length;i++){
        		String[] t = temp[i].split("\",\"");
        		Map<String, String> item = new HashMap<String, String>();
        		item.put("language", t[0].replaceAll("\"", ""));
        		item.put("info", t[1].replaceAll("\"", ""));
        		item.put("btnFlag", "true");
        		data.add(item);
        	}
    	}else{
    		Map<String, String> item = new HashMap<String, String>();
    		item.put("language", "en");
    		item.put("info", "Sorry, there is no abstract information in DBpedia.");
    		item.put("btnFlag", "false");
    		data.add(item);
    	}
    	
    	return data;
    }
    
//	parse URL from DBpedia, which stored in MediaWiki is not a actual URL link.
//	E.g. http://commons.wikimedia.org/wiki/Special:FilePath/Oeufs002b.jpg?width=300
//	and also create fragment for abstract in each language
    public Bitmap getImage(Context context, final String ingredient){
    	Bitmap bm = null;
    	String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
    	queryDBpedia = null;
    	queryDBpedia = String.format("select ?picURL where{ ?ingredient rdfs:label \"%s\"@en, ?name. ?ingredient dbo:thumbnail ?picURL. filter (LANGMATCHES(LANG(?name), \"en\"))}", after_capitalized);
    	GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
		urlCompany.put("format", "csv");
		urlCompany.put("query", queryDBpedia);
		httpResult = doHTTPRequest(urlCompany);
//		DBpedia has image
		if(httpResult.split("\n").length > 1){
			String DBpediaURL = httpResult.split("\n")[1];
			System.out.println("getURL DBpediaURL: "+DBpediaURL);

			String realURL = null;
			try {
//				query MediaWiki for the actual URL
				int start = DBpediaURL.lastIndexOf("/");
				int end = DBpediaURL.indexOf("?");
				String picName = DBpediaURL.substring(start+1, end);
				String queryMediaWiki = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:"+picName;
				URL url = new URL(queryMediaWiki);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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
				realURL = jArray.getJSONObject(0).getString("url");
				
				System.out.println("realURL: "+realURL);
				
				url = new URL(realURL);
				conn = (HttpURLConnection) url.openConnection();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 10;
				bm = BitmapFactory.decodeStream(conn.getInputStream(), null, options);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			AssetManager assetManager = context.getAssets();
			try {
				InputStream is = assetManager.open("noimage.gif");
				bm = BitmapFactory.decodeStream(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return bm;
    }
    
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
