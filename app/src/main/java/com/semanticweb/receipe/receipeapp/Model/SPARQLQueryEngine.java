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
    private static String ingredientDetailQuery = "select ?thumbnailURL,?abstract, LANG(?abstract), ?languageLabel where { ?fruit dbo:thumbnail ?thumbnailURL. ?fruit rdfs:label ?languageLabel. ?fruit dbo:abstract ?abstract. ?fruit rdfs:label \"%s\"@en}";


    private static HttpTransport httpTransport = new NetHttpTransport();
    private static HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
    private Handler handler = new Handler();
    private static String httpResult;


    /*
    * Helper Methods for interacting with SPARQL Engine
    * */
    public  static void getIngredeintDetail(final String ingredient){
        //create a Query
        //capitalize first letter of the string

        Runnable sendToDBpediaRUnnable = new Runnable() {
            @Override
            public void run() {
//			Query from DBpedia via HTTP request
                final String ingredientForQuery = Character.toUpperCase(ingredient.charAt(0))+ingredient.substring(1);
                String query = String.format(ingredientDetailQuery,ingredientForQuery);;
                GenericUrl urlCompany = new GenericUrl("http://dbpedia.org/sparsql");
                urlCompany.put("format", "csv");
                urlCompany.put("query", query);
                httpResult = doHTTPRequest(urlCompany);;
                System.out.println(httpResult);
            }
        };

        Thread thread = new Thread(sendToDBpediaRUnnable);
        thread.start();
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
