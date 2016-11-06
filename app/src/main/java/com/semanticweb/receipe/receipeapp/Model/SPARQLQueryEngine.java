package com.semanticweb.receipe.receipeapp.Model;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Omer on 10/29/2016.
 */

public class SPARQLQueryEngine {

    private static HttpTransport httpTransport = new NetHttpTransport();
    private static HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	private String queryDBpedia;
	private String httpResult;


    /*
    * Helper Methods for interacting with SPARQL Engine
    * */
    public List<Map<String, String>> getTranslation(final String ingredient, final String language){
//    	Query from DBpedia via HTTP request
		String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
		queryDBpedia = "";
		queryDBpedia = String.format("select distinct ?name, ?picURL where{ ?url rdfs:label \"%s\"@%s, ?name. ?url dbo:thumbnail ?picURL. filter (LANGMATCHES(LANG(?name), \"en\"))}", after_capitalized, language);
		System.out.println("query: "+queryDBpedia);
		GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
		urlCompany.put("format", "csv");
		urlCompany.put("query", queryDBpedia);
		httpResult = doHTTPRequest(urlCompany);
		String[] temp = httpResult.split("\n");
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for(int i = 1;i < temp.length;i++){
			Map<String, String> item = new HashMap<String, String>();
			String[] entry = temp[i].split("\",\"");
			item.put("imageURL", entry[1].replaceAll("\"", ""));
			String w = entry[0].replaceAll("\"", "").toLowerCase(Locale.ENGLISH);
//			check for duplicate result
			if(!data.isEmpty()){
				for(int j = 0; j < data.size();){
					if(!data.get(j).equals(w)){
						item.put("name", w);
						data.add(item);
						break;
					}else{
						break;
					}
				}
			}else{
				item.put("name", w);
				data.add(item);
			}
		}
        return data;
    }

    public List<Map<String, String>> getIngredientDetail(final String ingredient){
    	String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
    	queryDBpedia = "";
    	queryDBpedia = String.format("select ?picURL, LANG(?abstract), ?abstract where { ?ingredient dbo:thumbnail ?picURL. ?ingredient dbo:abstract ?abstract. ?ingredient rdfs:label \"%s\"@en, ?ingre_name. filter( LANGMATCHES(LANG(?ingre_name), \"en\") )}", after_capitalized);
    	GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
		urlCompany.put("format", "csv");
		urlCompany.put("query", queryDBpedia);
		httpResult = doHTTPRequest(urlCompany);
		String[] temp = httpResult.split("\n");
    	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    	for(int i = 1;i < temp.length;i++){
    		String[] t = temp[i].split("\",\"");
    		Map<String, String> item = new HashMap<String, String>();
    		item.put("language", t[1]);
    		item.put("picURL", t[0]);
    		item.put("info", t[2]);
    		data.add(item);
    	}
    	return data;
    }
    
    public String getImageURL(final String ingredient){
    	String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
    	queryDBpedia = "";
    	queryDBpedia = String.format("select ?picURL where{ ?ingredient rdfs:label \"%s\"@en, ?name. ?ingredient dbo:thumbnail ?picURL. filter (LANGMATCHES(LANG(?name), \"en\"))}", after_capitalized);
    	GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
		urlCompany.put("format", "csv");
		urlCompany.put("query", queryDBpedia);
		httpResult = doHTTPRequest(urlCompany);
		String[] temp = httpResult.split("\n");
		return temp[1];
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
