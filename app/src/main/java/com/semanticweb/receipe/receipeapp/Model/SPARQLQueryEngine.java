package com.semanticweb.receipe.receipeapp.Model;

import android.os.Handler;
import android.widget.ArrayAdapter;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.semanticweb.receipe.receipeapp.TranslateActivity;
import com.semanticweb.receipe.receipeapp.entity.LanguageItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Omer on 10/29/2016.
 */

public class SPARQLQueryEngine {

    //Qureries
    private static String ingredientDetailQuery = "select ?thumbnailURL,?abstract, LANG(?abstract), ?languageLabel where {" +
                                            "  ?fruit dbo:thumbnail ?thumbnailURL." +
                                            "  ?fruit rdfs:label ?languageLabel." +
                                            "  ?fruit dbo:abstract ?abstract." +
                                            "  ?fruit rdfs:label \"%s\"@en" +
                                            "}";


    private static HttpTransport httpTransport = new NetHttpTransport();
    private static HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
    private Handler handler = new Handler();
    private static String httpResult;

    /*
    * Helper Methods for interacting with SPARQL Engine
    * */
    public  static void getIngredeintDetail(String ingredient){
        //create a Query
        String query = String.format(ingredientDetailQuery,ingredient);;
        GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparsql");
        urlCompany.put("format", "csv");
        urlCompany.put("query", query);
        httpResult = doHTTPRequest(urlCompany);;
    }

    private Runnable sendToDBpedia = new Runnable() {

        @Override
        public void run() {
////			Query from DBpedia via HTTP request
//            String ingredient = inputIngredient.getText().toString();
//            String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
//            String language = ((LanguageItem)languageList.getSelectedItem()).getAbbreviation();
//            queryDBpedia = String.format("select ?name where{?url rdfs:label \"%s\"@%s, ?name. filter (LANGMATCHES(LANG(?name), \"en\")).}", after_capitalized, language);
//            System.out.println("query: "+queryDBpedia);
//            GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
//            urlCompany.put("format", "csv");
//            urlCompany.put("query", queryDBpedia);
//            httpResult = doHTTPRequest(urlCompany);
////			System.out.println("Result split: " + temp[1]);
//            String[] temp = httpResult.split("\n");
//            final List<String> data = new ArrayList<String>();
//            for(int i = 1;i < temp.length;i++){
//                data.add(temp[i].replaceAll("\"", "").toLowerCase());
//            }
//
//            //littl pre processing
//            //convert all data to lower form
//            //remove data if same as Query
//            Set<String> uniqueValues = new TreeSet<>();
//            uniqueValues.addAll(data);
//
//            final List<String> uniqueValuesArray = new ArrayList<>(uniqueValues);
//
//            //remoave if if contains word same in English
//            if(uniqueValuesArray.contains(ingredient.toLowerCase()))
//                uniqueValuesArray.remove(ingredient.toLowerCase());
//
////			Assign DBpedia result to TextView
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(TranslateActivity.this, android.R.layout.simple_list_item_1, uniqueValuesArray);
//                    resultList.setAdapter(adapter);
//                }
//            });//			Query from DBpedia via HTTP request
//            String ingredient = inputIngredient.getText().toString();
//            String after_capitalized = ingredient.replace(ingredient.substring(0, 1), ingredient.substring(0, 1).toUpperCase(Locale.ENGLISH));
//            String language = ((LanguageItem)languageList.getSelectedItem()).getAbbreviation();
//            queryDBpedia = String.format("select ?name where{?url rdfs:label \"%s\"@%s, ?name. filter (LANGMATCHES(LANG(?name), \"en\")).}", after_capitalized, language);
//            System.out.println("query: "+queryDBpedia);
//            GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparql");
//            urlCompany.put("format", "csv");
//            urlCompany.put("query", queryDBpedia);
//            httpResult = doHTTPRequest(urlCompany);
////			System.out.println("Result split: " + temp[1]);
//            String[] temp = httpResult.split("\n");
//            final List<String> data = new ArrayList<String>();
//            for(int i = 1;i < temp.length;i++){
//                data.add(temp[i].replaceAll("\"", "").toLowerCase());
//            }
//
//            //littl pre processing
//            //convert all data to lower form
//            //remove data if same as Query
//            Set<String> uniqueValues = new TreeSet<>();
//            uniqueValues.addAll(data);
//
//            final List<String> uniqueValuesArray = new ArrayList<>(uniqueValues);
//
//            //remoave if if contains word same in English
//            if(uniqueValuesArray.contains(ingredient.toLowerCase()))
//                uniqueValuesArray.remove(ingredient.toLowerCase());
//
////			Assign DBpedia result to TextView
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(TranslateActivity.this, android.R.layout.simple_list_item_1, uniqueValuesArray);
//                    resultList.setAdapter(adapter);
//                }
//            });
        }
    };

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
