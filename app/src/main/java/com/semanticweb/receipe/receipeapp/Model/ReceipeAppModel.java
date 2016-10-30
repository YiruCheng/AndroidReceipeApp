package com.semanticweb.receipe.receipeapp.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omer on 10/29/2016.
 */

public class ReceipeAppModel {

    //contains the list of all ingredients which are fetched From Server
    public  static List<String> ingredientList = new ArrayList<>();

    //contains list of all ingredients which are selected by Server or Sparql Query
    //would be used for finding Receipes
    public  static List<String> selectedIngredientList = new ArrayList<>();

    //List of ingredients with details of Image, Name, and abstract in different Languages
    public  static List<IngredientDetailModel> ingredientDetailList = new ArrayList<>();
}
